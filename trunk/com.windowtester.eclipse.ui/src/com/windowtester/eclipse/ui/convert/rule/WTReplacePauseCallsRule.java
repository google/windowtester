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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.TimeElapsedCondition;

/**
 * Replace a pause invocation such as ui.pause(3000) with a wait invocation such as
 * ui.wait(milliseconds(3000))
 */
public class WTReplacePauseCallsRule extends WTReplaceMethodCallRule
{
	public WTReplacePauseCallsRule() {
		super(IUIContext.class, "pause", (Class<?>) null);
	}

	/**
	 * Called when an invocation is found that matches the signature
	 * 
	 * @param invocation the method invocation (not <code>null</code>)
	 */
	@SuppressWarnings("unchecked")
	protected void replaceMethod(MethodInvocation invocation) {
		List<ASTNode> arguments = invocation.arguments();
		Expression milliseconds = (Expression) context.remove(arguments, 0);

		context.addImport(TimeElapsedCondition.class.getName() + ".milliseconds", true);
		context.setMethodName(invocation, "wait");
		context.insert(invocation, arguments, 0, context.newMethodInvocation(null, "milliseconds", milliseconds));
	}
}
