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

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;

import com.windowtester.eclipse.ui.convert.WTConvertAPIContext;
import com.windowtester.eclipse.ui.convert.WTConvertAPIRule;
import com.windowtester.eclipse.ui.convert.util.WTAPIUtil;

/**
 * Replace references to deprecated superclass with new API superclass. This rule assumes
 * that the {@link WTExpandImportsRule} has been run to expand any on demand imports of
 * deprecated packages.
 */
public class WTReplaceTypeRule extends ASTVisitor
	implements WTConvertAPIRule
{
	private final String oldTypeName;
	private final String newTypeName;
	private final WTExpandImportsRule expandImportsRule;

	private WTConvertAPIContext context;
	private boolean replacedImport;

	public WTReplaceTypeRule(String oldTypeName, Class<?> newType) {
		this(oldTypeName, newType.getName());
	}

	public WTReplaceTypeRule(String oldTypeName, String newTypeName) {
		this.oldTypeName = oldTypeName;
		this.newTypeName = newTypeName;
		expandImportsRule = new WTExpandImportsRule(WTAPIUtil.packageNameForType(oldTypeName));
	}

	/**
	 * @return the newTypeName
	 */
	public String getNewTypeName() {
		return newTypeName;
	}
	
	/**
	 * @return the oldTypeName
	 */
	public String getOldTypeName() {
		return oldTypeName;
	}
	
	public void convert(WTConvertAPIContext context) {
		this.context = context;
		this.replacedImport = false;
		expandImportsRule.convert(context);
		context.accept(this);
	}

	public boolean visit(ImportDeclaration node) {
		String typeName = node.getName().getFullyQualifiedName();
		if (node.isStatic()) {
			if (node.isOnDemand()) {
				if (oldTypeName.equals(typeName))
					context.replaceImport(node, newTypeName);
			}
			else {
				String methodName = WTAPIUtil.simpleTypeName(typeName);
				typeName = WTAPIUtil.packageNameForType(typeName);
				if (oldTypeName.equals(typeName)) {
					context.replaceImport(node, newTypeName + "." + methodName);
				}
			}
		}
		else {
			if (node.isOnDemand()) {
				// Nothing to do
			}
			else {
				if (oldTypeName.equals(typeName)) {
					if (replacedImport) {
						context.removeImport(node);
					}
					else {
						context.replaceImport(node, newTypeName);
						replacedImport = true;
					}
				}
				else if (newTypeName.equals(typeName)) {
					if (replacedImport) {
						context.removeImport(node);
					}
					else {
						replacedImport = true;
					}
				}
			}
		}
		return false;
	}

	public boolean visit(SimpleType type) {
		String unresolvedTypeName = type.getName().getFullyQualifiedName();
		String resolvedTypeName = context.resolve(unresolvedTypeName);
		if (oldTypeName.equals(resolvedTypeName)) {
			if (unresolvedTypeName.lastIndexOf('.') == -1)
				context.setTypeName(type, WTAPIUtil.simpleTypeName(newTypeName));
			else
				context.setTypeName(type, newTypeName);
		}
		return false;
	}
}
