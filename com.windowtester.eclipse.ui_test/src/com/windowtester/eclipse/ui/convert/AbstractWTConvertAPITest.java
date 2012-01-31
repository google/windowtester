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
import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import junit.framework.TestCase;

public abstract class AbstractWTConvertAPITest extends TestCase
{
	/**
	 * Answer the content of the specified file in
	 * com/windowtester/eclipse/ui/convert/<dirName>. In the process of reading the file
	 * content, convert the line ends for this execution environment
	 * 
	 * @param dirName the subdirectory name (not <code>null</code>)
	 * @param fileName the file name (not <code>null</code>)
	 */
	protected String getSource(String dirName, String fileName) throws IOException {
		StringBuilder result = new StringBuilder(5000);
		InputStream stream = getClass().getResourceAsStream(dirName + "/" + fileName);
		if (stream == null) {
			String className = getClass().getName();
			String packageName = className.substring(0, className.lastIndexOf('.'));
			String relPath = packageName.replace('.', '/');
			return "Missing file: " + relPath + "/" + dirName + "/" + fileName;
		}
		try {
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(stream)));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				result.append(line);
				// Standardize on \n because the AST nodes all default to \n line endings
				result.append('\n');
			}
		}
		finally {
			stream.close();
		}
		return result.toString();
	}

	/**
	 * Assert that the class, start position, and length of each node in the compilation
	 * units match.
	 */
	public void assertEquals(CompilationUnit expected, CompilationUnit actual) {
		final Collection<ASTNode> expectedNodes = new ArrayList<ASTNode>();
		expected.accept(new ASTVisitor() {
			public void postVisit(ASTNode node) {
				expectedNodes.add(node);
			}
		});
		final Iterator<ASTNode> iter = expectedNodes.iterator();
		actual.accept(new ASTVisitor() {
			public void postVisit(ASTNode actualNode) {
				ASTNode expectedNode = iter.next();
				if (expectedNode.getClass() != actualNode.getClass()
					|| expectedNode.getStartPosition() != actualNode.getStartPosition()
					|| expectedNode.getLength() != actualNode.getLength()) {
					String errMsg = "Actual node does not equal expected node";
					errMsg += "\nExpected getClass() = " + expectedNode.getClass();
					errMsg += "\nActual getClass() = " + actualNode.getClass();
					errMsg += "\nExpected getStartPosition() = " + expectedNode.getStartPosition();
					errMsg += "\nActual getStartPosition() = " + actualNode.getStartPosition();
					errMsg += "\nExpected getLength() = " + expectedNode.getLength();
					errMsg += "\nActual getLength() = " + actualNode.getLength();
					errMsg += "\nExpected = " + getSourceSnippet(expectedNode, 7);
					errMsg += "\nActual = " + getSourceSnippet(actualNode, 7);
					fail(errMsg);
				}
			}

			private String getSourceSnippet(ASTNode node, int depth) {
				String snippet = node.toString();
				if (snippet.length() > 80)
					snippet = snippet.substring(0, 80) + "...";
				if (depth > 0) {
					ASTNode parent = node.getParent();
					if (parent != null)
						snippet += "\n   " + getSourceSnippet(parent, depth - 1);
				}
				return snippet;
			}
		});
	}

	/**
	 * Test the specified set of rules by applying them to the source in the file
	 * com/windowtester/eclipse/ui/convert/original/<fileName> and comparing the result to
	 * the content of the file
	 * com/windowtester/eclipse/ui/convert/<expectedDirName>/<fileName>
	 * 
	 * @param expectedDirName the subdirectory of com/windowtester/eclipse/ui/convert that
	 *            contains the file with the expected result (e.g. "expandedimports")
	 * @param fileName the name of both the original file and the expected result file
	 * @param rules the rules to be applied
	 */
	public void testRules(String expectedDirName, String fileName, WTConvertAPIRule[] rules) throws IOException {
		String original = getSource("original", fileName);
		WTConvertAPIRefactoring refactoring = new WTConvertAPIRefactoring(null);
		refactoring.setRules(rules);
		WTConvertAPIContext context = refactoring.convertCompilationUnitSource(original);
		String actualSource = context.getSource();
		CompilationUnit actualCompUnit = context.getCompilationUnit();

		String expectedSource = getSource(expectedDirName, fileName);
		assertEquals(expectedSource, actualSource);

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(expectedSource.toCharArray());
		CompilationUnit expectedCompUnit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		assertEquals(expectedCompUnit, actualCompUnit);
	}
}