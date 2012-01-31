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

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public abstract class AbstractWTConvertAPIContextTest extends AbstractWTConvertAPITest
{
	public interface APITest
	{
		void before(WTConvertAPIContext context);
	
		void test(WTConvertAPIContext context, CompilationUnit node);
	
		void after(WTConvertAPIContext context);
	}

	/**
	 * Test the specified rule by applying it to the source in the file
	 * com/windowtester/eclipse/ui/convert/original/<fileName> and comparing the result to
	 * the content of the file
	 * com/windowtester/eclipse/ui/convert/<expectedDirName>/<fileName>_<methodName>
	 * 
	 * @param expectedDirName the subdirectory of com/windowtester/eclipse/ui/convert that
	 *            contains the file with the expected result (e.g. "expandedimports")
	 * @param fileName the name of both the original file and the expected result file
	 * @param methodName the name of the method to be tested
	 * @param test the API test
	 */
	public void testAPI(String expectedDirName, String fileName, String methodName, final APITest test) throws IOException {
		String original = getSource("original", fileName);
		WTConvertAPIRefactoring refactoring = new WTConvertAPIRefactoring(null);
		refactoring.setRules(new WTConvertAPIRule[]{
			new WTConvertAPIRule() {
				public void convert(final WTConvertAPIContext context) {
					test.before(context);
					context.accept(new ASTVisitor() {
						public boolean visit(CompilationUnit node) {
							test.test(context, node);
							return false;
						}
					});
					test.after(context);
				}
			}
		});
		WTConvertAPIContext context = refactoring.convertCompilationUnitSource(original);
		String actualSource = context.getSource();
		CompilationUnit actualCompUnit = context.getCompilationUnit();
	
		int index = fileName.lastIndexOf('.');
		String expectedFileName = fileName.substring(0, index) + "_" + methodName + fileName.substring(index);
		String expectedSource = getSource(expectedDirName, expectedFileName);
		assertEquals(expectedSource, actualSource);
	
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(expectedSource.toCharArray());
		CompilationUnit expectedCompUnit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		assertEquals(expectedCompUnit, actualCompUnit);
	}

}