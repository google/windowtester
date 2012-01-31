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
package com.windowtester.eclipse.ui.convert.rule;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.windowtester.eclipse.ui.convert.WTAPIAbstractVisitor;

public abstract class WTReplaceMethodCallRule extends WTAPIAbstractVisitor
{
	private final String expectedTypeName;
	private final String expectedMethodName;
	private final String[] expectedArgTypeNames;

	/**
	 * Construct a new instance representing a method call
	 * 
	 * @param type the receiver's type
	 * @param methodName the method name
	 * @param argTypes the argument types (not <code>null</code>, but may contain
	 *            <code>null</code>s indicating a wildcard)
	 */
	public WTReplaceMethodCallRule(Class<?> type, String methodName, Class<?>... argTypes) {
		this(type.getName(), methodName, getTypeNames(argTypes));
	}

	private static String[] getTypeNames(Class<?>[] types) {
		String[] typeNames = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if (type != null)
				typeNames[i] = type.getName();
		}
		return typeNames;
	}

	/**
	 * Construct a new instance representing a method call
	 * 
	 * @param typeName the receiver's type
	 * @param expectedMethodName the method name
	 * @param argTypeNames the argument types (not <code>null</code>, but may contain
	 *            <code>null</code>s indicating a wildcard)
	 */
	public WTReplaceMethodCallRule(String typeName, String methodName, String... argTypeNames) {
		super(null);
		expectedTypeName = typeName;
		expectedMethodName = methodName;
		expectedArgTypeNames = argTypeNames;
	}

	/**
	 * Called when an invocation is visited to determine if the method invocation should
	 * be replaced.
	 * 
	 * @param node the method invocation (not <code>null</code>)
	 * @return true if the receiver should visit the target and each argument of the
	 *         invocation
	 */
	@SuppressWarnings("unchecked")
	public void endVisit(MethodInvocation node) {
		super.endVisit(node);
		String targetTypeName = getNodeType(node.getExpression());
		List<ASTNode> arguments = node.arguments();
		if (!expectedTypeName.equals(targetTypeName)
			|| !expectedMethodName.equals(node.getName().getFullyQualifiedName())
			|| expectedArgTypeNames.length != arguments.size())
			return;
		int index = 0;
		for (ASTNode arg : arguments) {
			String expected = expectedArgTypeNames[index++];
			if (expected != null && !expected.equals(getNodeType(arg)))
				return;
		}
		replaceMethod(node);
	}

	/**
	 * Called when an invocation is found that matches the signature
	 * 
	 * @param invocation the method invocation (not <code>null</code>)
	 */
	protected abstract void replaceMethod(MethodInvocation invocation);
}