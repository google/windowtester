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

import static com.windowtester.eclipse.ui.convert.util.WTAPIUtil.simpleTypeName;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.windowtester.eclipse.ui.convert.util.WTAPIUtil;

public abstract class WTAPIUsageVisitor extends WTAPIAbstractVisitor
{
	public WTAPIUsageVisitor(WTConvertAPIContext context) {
		super(context);
	}

	public abstract void apiUsed(String signature);

	//=================================================================================
	// Visitor

	/**
	 * Record any referenced WindowTester classes
	 */
	public void endVisit(ImportDeclaration node) {
		super.endVisit(node);
		if (node.isStatic()) {
			String typeName = node.getName().getFullyQualifiedName();
			String methodName = "*";
			if (!node.isOnDemand()) {
				methodName = WTAPIUtil.simpleTypeName(typeName);
				typeName = WTAPIUtil.packageNameForType(typeName);
			}
			if (context.isWTType(typeName))
				apiUsed(typeName + "#" + methodName);
		}
	}

	/**
	 * Record any referenced WindowTester classes
	 */
	@SuppressWarnings("unchecked")
	public void endVisit(ClassInstanceCreation node) {
		super.endVisit(node);
		String typeName = getNodeType(node);
		if (context.isWTType(typeName))
			apiUsedWithArguments(typeName + "#" + simpleTypeName(typeName), node.arguments());
	}

	/**
	 * Record any referenced WindowTester classes
	 */
	public void endVisit(FieldDeclaration node) {
		super.endVisit(node);
		String typeName = getNodeType(node);
		if (context.isWTType(typeName))
			apiUsed(typeName + "#field");
	}

	/**
	 * Record any referenced WindowTester classes
	 */
	@SuppressWarnings("unchecked")
	public void endVisit(MethodInvocation node) {
		super.endVisit(node);
		String targetTypeName = getNodeType(node.getExpression());
		if (context.isWTType(targetTypeName))
			apiUsedWithArguments(targetTypeName + "#" + node.getName().getFullyQualifiedName(), node.arguments());
	}

	/**
	 * Record any WindowTester classes referenced as superclass or superinterfaces
	 */
	public void endVisit(TypeDeclaration node) {
		super.endVisit(node);
		Type type = node.getSuperclassType();
		if (type != null) {
			String typeName = getNodeType(type);
			if (context.isWTType(typeName))
				apiUsed(typeName + "#superclass");
		}
		for (Object each : node.superInterfaceTypes()) {
			type = (Type) each;
			String typeName = getNodeType(type);
			if (context.isWTType(typeName))
				apiUsed(typeName + "#superinterface");
		}
	}

	/**
	 * Record any referenced WindowTester classes
	 */
	public void endVisit(TypeLiteral node) {
		super.endVisit(node);
		String typeName = getNodeType(node);
		if (context.isWTType(typeName))
			apiUsed(typeName + "#class");
	}

	/**
	 * Record any referenced WindowTester classes
	 */
	public void endVisit(VariableDeclarationStatement node) {
		super.endVisit(node);
		String typeName = getNodeType(node);
		if (context.isWTType(typeName))
			apiUsed(typeName + "#localvar");
	}

	/**
	 * Record API usage with arguments
	 * 
	 * @param prefix the API signature prefix
	 * @param argTypeNames the type names for each API argument (not <code>null</code>,
	 *            but may contain <code>null</code>s)
	 * @param arguments the API arguments
	 */
	private void apiUsedWithArguments(String prefix, List<ASTNode> arguments) {
		String signature = prefix + "(";
		boolean first = true;
		for (ASTNode arg : arguments) {
			String typeName = getNodeType(arg);
			if (first)
				first = false;
			else
				signature += ",";
			if (typeName == null) {
				if (arg instanceof Name)
					typeName = ((Name) arg).getFullyQualifiedName();
				else
					typeName = "?" + arg.getClass().getName();
			}
			signature += typeName;
		}
		signature += ")";
		apiUsed(signature);
	}
}
