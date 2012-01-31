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
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.windowtester.eclipse.ui.convert.WTConvertAPIContext;
import com.windowtester.eclipse.ui.convert.WTConvertAPIRule;

/**
 * Expand the legacy WindowTester imports so that they can more easily be replaced by
 * later rules. This is used by the {@link WTReplaceTypeRule} which determines the imports
 * which this rule expands.
 */
public class WTExpandImportsRule extends ASTVisitor
	implements WTConvertAPIRule
{
	private final String packageToExpand;
	private WTConvertAPIContext context;

	/**
	 * Expand any demand imports of the specified package
	 * 
	 * @param packageToExpand the name of the package to be expanded
	 */
	public WTExpandImportsRule(String packageToExpand) {
		this.packageToExpand = packageToExpand;
	}

	public void convert(WTConvertAPIContext context) {
		this.context = context;
		context.accept(this);
	}

	/**
	 * Expand any on demand imports of WindowTester packages.
	 */
	public boolean visit(ImportDeclaration node) {
		String fullyQualifiedName = node.getName().getFullyQualifiedName();
		if (!node.isOnDemand() || !packageToExpand.equals(fullyQualifiedName))
			return false;
		if (node.isStatic()) {
			// TODO [Dan] Expand static imports?
		}
		else {
			context.expandImport(node);
		}
		return false;
	}

	public boolean visit(TypeDeclaration node) {
		return false;
	}
}
