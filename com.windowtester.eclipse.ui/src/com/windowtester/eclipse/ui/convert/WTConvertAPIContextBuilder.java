/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.eclipse.ui.convert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.windowtester.ui.util.Logger;


/**
 * Call {@link #buildContext(CompilationUnit)} to build {@link WTConvertAPIContext} by
 * visiting the package statement and all import statements looking for references to
 * WindowTester types.
 */
public class WTConvertAPIContextBuilder extends ASTVisitor
{
	/**
	 * The compiler options used when parsing java source
	 */
	private static Hashtable<String, String> compilerOptions;

	/**
	 * Answer the lazily initialized compiler options used when parsing java source
	 */
	@SuppressWarnings("unchecked")
	private Hashtable<String, String> getCompilerOptions() {
		if (compilerOptions == null) {
			compilerOptions = new Hashtable<String, String>(JavaCore.getDefaultOptions());
			compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		}
		return compilerOptions;
	}

	private Set<String> wtTypeNames;
	private Set<String> wtStaticTypeNames;

	/**
	 * Traverse the AST structure and return a context containing the imported types and
	 * members. This method is preferred over {@link #buildContext(String)} because the
	 * compilation unit passed in carries the compiler options with it where as
	 * {@link #buildContext(String)} uses default compiler options.
	 * 
	 * @param compUnit the compilation unit's AST structure (not <code>null</code>)
	 * @return the context (not <code>null</code>)
	 */
	public WTConvertAPIContext buildContext(ICompilationUnit compUnit) throws JavaModelException {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(compUnit);
		return buildContext(parser, compUnit.getSource());
	}

	/**
	 * Traverse the AST structure and return a context containing the imported types and
	 * members. The {@link #buildContext(ICompilationUnit)} method is preferred because
	 * the compilation unit carries the compiler options where as this method uses default
	 * compiler options. This method is used for testing.
	 * 
	 * @param compUnit the compilation unit's AST structure (not <code>null</code>)
	 * @return the context (not <code>null</code>)
	 */
	public WTConvertAPIContext buildContext(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setCompilerOptions(getCompilerOptions());
		parser.setSource(source.toCharArray());
		return buildContext(parser, source);
	}

	private WTConvertAPIContext buildContext(ASTParser parser, String source) {

		// Parse the source

		loadWTInfo();
		CompilationUnit compUnit = (CompilationUnit) parser.createAST(new NullProgressMonitor());

		// Throw an exception if any syntax errors are detected

		for (IProblem problem : compUnit.getProblems())
			throw new WTConvertAPIParseException(problem);

		// Initialize the receiver and traverse the parse tree

		wtTypeNames = new HashSet<String>();
		wtStaticTypeNames = new HashSet<String>();
		compUnit.accept(this);

		// Return the context

		return new WTConvertAPIContext(source, compUnit, wtTypeNames, wtStaticTypeNames);
	}

	/**
	 * Collect the imported WindowTester types
	 */
	public boolean visit(PackageDeclaration node) {
		String fullyQualifiedName = node.getName().getFullyQualifiedName();
		Collection<String> typeNamesInPackage = WT_TYPE_NAME_MAP.get(fullyQualifiedName);
		if (typeNamesInPackage != null)
			wtTypeNames.addAll(typeNamesInPackage);
		return false;
	}

	/**
	 * Collect the imported WindowTester types
	 */
	public boolean visit(ImportDeclaration node) {
		String fullyQualifiedName = node.getName().getFullyQualifiedName();
		if (node.isStatic()) {
			if (node.isOnDemand()) {
				Collection<String> memberNamesInType = WT_MEMBER_NAME_MAP.get(fullyQualifiedName);
				if (memberNamesInType != null)
					wtStaticTypeNames.addAll(memberNamesInType);
			}
			else {
				wtStaticTypeNames.add(fullyQualifiedName);
			}
		}
		else {
			if (node.isOnDemand()) {
				Collection<String> typeNamesInPackage = WT_TYPE_NAME_MAP.get(fullyQualifiedName);
				if (typeNamesInPackage != null)
					wtTypeNames.addAll(typeNamesInPackage);
			}
			else {
				if (isWTType(fullyQualifiedName))
					wtTypeNames.add(fullyQualifiedName);
			}
		}
		return false;
	}

	public boolean visit(TypeDeclaration node) {
		return false;
	}

	//================================================================================
	// Global WindowTester type information

	/**
	 * A collection of fully qualified WindowTester type names
	 */
	private static Set<String> WT_TYPE_NAMES = null;

	/**
	 * A collection of fully qualified WindowTester member names
	 */
	private static Set<String> WT_MEMBER_NAMES = null;

	/**
	 * A mapping of package name to fully qualified WindowTester type names in that
	 * package
	 */
	private static Map<String, Collection<String>> WT_TYPE_NAME_MAP = null;

	/**
	 * A mapping of type name to fully qualified WindowTester member names in that
	 * type
	 */
	private static Map<String, Collection<String>> WT_MEMBER_NAME_MAP = null;

	/**
	 * Read the wt-types.txt and wt-static-members.txt files and cache that information in
	 * this class for rapid access.
	 */
	private static void loadWTInfo() {
		if (WT_TYPE_NAMES != null)
			return;

		WT_TYPE_NAMES = new HashSet<String>();
		WT_TYPE_NAME_MAP = new HashMap<String, Collection<String>>();
		loadWTInfo("wt-types.txt", WT_TYPE_NAMES, WT_TYPE_NAME_MAP);

		WT_MEMBER_NAMES = new HashSet<String>();
		WT_MEMBER_NAME_MAP = new HashMap<String, Collection<String>>();
		loadWTInfo("wt-static-members.txt", WT_MEMBER_NAMES, WT_MEMBER_NAME_MAP);
	}

	private static void loadWTInfo(String fileName, Set<String> names, Map<String, Collection<String>> nameMap) {
		InputStream stream = WTConvertAPIContextBuilder.class.getResourceAsStream(fileName);
		try {
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(stream)));
			while (true) {
				String typeName = reader.readLine();
				if (typeName == null)
					break;
				typeName = typeName.trim();
				if (typeName.length() == 0 || typeName.charAt(0) == '#')
					continue;
				names.add(typeName);
				int index = typeName.lastIndexOf('.');
				if (index == -1)
					continue;
				String packageName = typeName.substring(0, index);
				Collection<String> typeNamesInPackage = nameMap.get(packageName);
				if (typeNamesInPackage == null) {
					typeNamesInPackage = new ArrayList<String>();
					nameMap.put(packageName, typeNamesInPackage);
				}
				typeNamesInPackage.add(typeName);
			}
		}
		catch (IOException e) {
			Logger.log("Failed to read " + fileName + " stream", e);
		}
		finally {
			try {
				stream.close();
			}
			catch (IOException e) {
				Logger.log("Failed to close " + fileName + " stream", e);
			}
		}
	}

	/**
	 * Answer true if the specified fully qualified type name is a WindowTester type
	 * 
	 * @param fullyQualifiedTypeName the type name
	 * @return <code>true</code> if the type name references a WindowTester type
	 */
	public static boolean isWTType(String fullyQualifiedTypeName) {
		loadWTInfo();
		return WT_TYPE_NAMES.contains(fullyQualifiedTypeName);
	}

	/**
	 * Answer the fully qualified names of the WindowTester types in the specified package
	 * or <code>null</code> if the specified package is not part of the WindowTester
	 * product
	 */
	public static Collection<String> getWTTypesInPackage(String packageName) {
		loadWTInfo();
		return WT_TYPE_NAME_MAP.get(packageName);
	}
}
