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
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import com.windowtester.eclipse.ui.convert.WTAPIAbstractVisitor;
import com.windowtester.eclipse.ui.convert.WTConvertAPIContext;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Replace some SWTWidgetLocator instances with more specific locator instances.</br> For
 * example,
 * 
 * <pre>
 * new SWTWidgetLocator(Shell.class, &quot;Java - Eclipse SDK&quot;)
 * </pre>
 * 
 * should be replaced with
 * 
 * <pre>
 * new ShellLocator(&quot;Java - Eclipse SDK&quot;)
 * </pre>
 */
public class WTReplaceSWTWidgetLocatorRule extends WTAPIAbstractVisitor
{
	private final Class<?> locatorClass;
	private final Class<?> widgetClass;

	public WTReplaceSWTWidgetLocatorRule(Class<?> widgetClass, Class<?> locatorClass) {
		super(null);
		this.widgetClass = widgetClass;
		this.locatorClass = locatorClass;
		WTConvertAPIContext.addCommonType(widgetClass);
	}

	/**
	 * Detect specific SWTWidgetLocator constructor references and replace them with more
	 * specific locator instances.
	 */
	@SuppressWarnings("unchecked")
	public void endVisit(ClassInstanceCreation node) {
		super.endVisit(node);
		String typeName = getNodeType(node);
		if (!SWTWidgetLocator.class.getName().equals(typeName))
			return;
		List<ASTNode> arguments = node.arguments();
		if (arguments.size() != 2)
			return;
		if (!(widgetClass.getName() + ".class").equals(getNodeType(arguments.get(0))))
			return;
		final Type type = node.getType();
		if (!type.isSimpleType())
			return;
		context.addImport(locatorClass.getName(), false);
		context.setTypeName((SimpleType) type, locatorClass.getSimpleName());
		context.remove(arguments, 0);
	}
}
