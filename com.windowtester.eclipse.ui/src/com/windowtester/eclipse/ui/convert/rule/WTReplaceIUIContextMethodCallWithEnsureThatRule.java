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
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;

public class WTReplaceIUIContextMethodCallWithEnsureThatRule extends WTReplaceMethodCallRule
{
	private final String conditionHandlerMethodName;

	public WTReplaceIUIContextMethodCallWithEnsureThatRule(String uiContextMethodName, String conditionHandlerMethodName) {
		super(IUIContext.class, uiContextMethodName, (Class<?>) null);
		this.conditionHandlerMethodName = conditionHandlerMethodName;
	}

	/**
	 * Called when an invocation is found that matches the signature
	 * 
	 * @param invocation the method invocation (not <code>null</code>)
	 */
	@SuppressWarnings("unchecked")
	protected void replaceMethod(MethodInvocation invocation) {
		List<ASTNode> arguments = invocation.arguments();
		Expression locator = (Expression) context.remove(arguments, 0);

		// If this is an IWidgetLocator, then attempt to substitute a more specific value
		String name = null;
		switch (locator.getNodeType()) {
			case ASTNode.ARRAY_ACCESS :
				if (!locator.toString().endsWith("[0]"))
					break;
				Expression array = ((ArrayAccess) locator).getArray();
				if (!(array instanceof SimpleName))
					break;
				name = ((SimpleName) array).getFullyQualifiedName();
				break;
			case ASTNode.SIMPLE_NAME :
				name = ((SimpleName) locator).getFullyQualifiedName();
				break;
			default :
				break;
		}
		if (name != null) {
			String typeName = context.resolve(getVarType(name));
			if (typeName != null) {
				if (typeName.endsWith("[]"))
					typeName = typeName.substring(0, typeName.length() - 2);
				if (typeName.equals(IWidgetLocator.class.getName())) {
					Expression value = (Expression) context.deepCopy(getVarValue(name));
					if (value != null)
						locator = value;
				}
			}
		}

		context.setMethodName(invocation, "ensureThat");
		context.insert(invocation, arguments, 0, context.newMethodInvocation(locator, conditionHandlerMethodName));
	}
}
