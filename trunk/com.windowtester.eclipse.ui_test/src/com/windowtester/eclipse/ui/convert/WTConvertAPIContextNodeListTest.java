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

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;

public class WTConvertAPIContextNodeListTest extends AbstractWTConvertAPIContextTest
{
	public void testInsert() throws Exception {
		testAPI("contextAPI/nodelist", "NewContactSwingTest.txt", "insert", new APITest() {
			public void before(WTConvertAPIContext context) {
			}

			@SuppressWarnings("unchecked")
			public void test(WTConvertAPIContext context, CompilationUnit compUnit) {
				Name newName = context.newName("com.windowtester.runtime.swing.locator.JButtonLocator", 7);
				ImportDeclaration newImport = compUnit.getAST().newImportDeclaration();
				newImport.setName(newName);
				newImport.setSourceRange(0, newImport.toString().trim().length());
				context.insert(compUnit, compUnit.imports(), 0, newImport);
			}

			public void after(WTConvertAPIContext context) {
			}
		});
	}
	
	public void testRemove() throws Exception {
		testAPI("contextAPI/nodelist", "NewContactSwingTest.txt", "remove", new APITest() {
			public void before(WTConvertAPIContext context) {
			}

			public void test(final WTConvertAPIContext context, final CompilationUnit compUnit) {
				compUnit.accept(new ASTVisitor() {
					@SuppressWarnings("unchecked")
					public boolean visit(ImportDeclaration node) {
						if (node.getName().getFullyQualifiedName().equals("com.windowtester.runtime.swing.locator.JButtonLocator"))
							context.remove(compUnit.imports(), node);
						return false;
					}
				});
			}

			public void after(WTConvertAPIContext context) {
			}
		});
	}
}
