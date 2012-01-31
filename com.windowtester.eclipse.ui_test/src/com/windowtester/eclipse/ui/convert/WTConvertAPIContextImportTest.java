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

public class WTConvertAPIContextImportTest extends AbstractWTConvertAPIContextTest
{
	public void testAddImport() throws Exception {
		testAPI("contextAPI/imports", "NewContactSwingTest.txt", "addImport", new APITest() {
			final String typeName = "foo.bar";

			public void before(WTConvertAPIContext context) {
				assertFalse(context.getWTTypeNames().contains(typeName));
			}

			public void test(WTConvertAPIContext context, CompilationUnit node) {
				context.addImport(typeName, false);
			}

			public void after(WTConvertAPIContext context) {
				assertTrue(context.getWTTypeNames().contains(typeName));
			}
		});
	}
	
	public void testAddDuplicateImport() throws Exception {
		testAPI("contextAPI/imports", "NewContactSwingTest.txt", "addDuplicateImport", new APITest() {
			final String typeName = "com.windowtester.internal.swing.UIContextSwing";

			public void before(WTConvertAPIContext context) {
				assertTrue(context.getWTTypeNames().contains(typeName));
			}

			public void test(WTConvertAPIContext context, CompilationUnit node) {
				context.addImport(typeName, false);
			}

			public void after(WTConvertAPIContext context) {
				assertTrue(context.getWTTypeNames().contains(typeName));
			}
		});
	}

	public void testAddImportStatic() throws Exception {
		testAPI("contextAPI/imports", "NewContactSwingTest.txt", "addImportStatic", new APITest() {
			final String typeName = "foo.bar";

			public void before(WTConvertAPIContext context) {
				assertFalse(context.getWTTypeNames().contains(typeName));
			}

			public void test(WTConvertAPIContext context, CompilationUnit node) {
				context.addImport(typeName, true);
			}

			public void after(WTConvertAPIContext context) {
				assertFalse(context.getWTTypeNames().contains(typeName));
			}
		});
	}

	public void testReplaceImport() throws Exception {
		testAPI("contextAPI/imports", "NewContactSwingTest.txt", "replaceImport", new APITest() {
			final String oldTypeName = "com.windowtester.internal.swing.UIContextSwing";
			final String newTypeName = "foo.bar";

			public void before(WTConvertAPIContext context) {
				assertTrue(context.getWTTypeNames().contains(oldTypeName));
				assertFalse(context.getWTTypeNames().contains(newTypeName));
			}

			public void test(final WTConvertAPIContext context, CompilationUnit node) {
				node.accept(new ASTVisitor() {
					public boolean visit(ImportDeclaration node) {
						if (oldTypeName.equals(node.getName().getFullyQualifiedName()))
							context.replaceImport(node, newTypeName);
						return false;
					}
				});
			}

			public void after(WTConvertAPIContext context) {
				assertFalse(context.getWTTypeNames().contains(oldTypeName));
				assertTrue(context.getWTTypeNames().contains(newTypeName));
			}
		});
	}

	public void testRemoveImport() throws Exception {
		testAPI("contextAPI/imports", "NewContactSwingTest.txt", "removeImport", new APITest() {
			final String typeName = "com.windowtester.internal.swing.UIContextSwing";

			public void before(WTConvertAPIContext context) {
				assertTrue(context.getWTTypeNames().contains(typeName));
			}

			public void test(final WTConvertAPIContext context, CompilationUnit node) {
				node.accept(new ASTVisitor() {
					public boolean visit(ImportDeclaration node) {
						if (typeName.equals(node.getName().getFullyQualifiedName()))
							context.removeImport(node);
						return false;
					}
				});
			}

			public void after(WTConvertAPIContext context) {
				assertFalse(context.getWTTypeNames().contains(typeName));
			}
		});
	}
}
