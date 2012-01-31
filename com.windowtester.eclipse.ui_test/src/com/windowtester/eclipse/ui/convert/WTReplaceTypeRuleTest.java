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
import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;

import com.windowtester.eclipse.ui.convert.rule.WTReplaceTypeRule;
import com.windowtester.internal.runtime.util.Filter;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;

/**
 * Test the expansion of imports
 * 
 * @author Dan Rubel
 */
public class WTReplaceTypeRuleTest extends AbstractWTConvertAPITest
{
	public void testConvertSuperclass_1() throws IOException {
		testConvertSuperclass("NewContactEmbeddedSwingTest.txt");
	}

	public void testConvertSuperclass_2() throws IOException {
		testConvertSuperclass("NewContactSwingTest.txt");
	}

	public void testConvertType_1() throws IOException {
		testConvertSuperclass("LostFocusTest.txt");
	}

	private void testConvertSuperclass(String fileName) throws IOException {
		testRules("replacetype", fileName, new WTConvertAPIRule[]{
			new WTReplaceTypeRule("junit.extensions.UITestCase", UITestCaseSWT.class),
			new WTReplaceTypeRule("junit.extensions.UITestCaseSWT", UITestCaseSWT.class),
			new WTReplaceTypeRule("com.windowtester.runtime.swt.finder.UnableToFindActiveShellException",
				UnableToFindActiveShellException.class),
			new WTReplaceTypeRule("com.windowtester.swt.util.PathStringTokenizerUtil",
				"com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil"),
			new WTReplaceTypeRule("com.windowtester.swt.util.TextUtils",
				"com.windowtester.runtime.swt.internal.util.TextUtils"),
		});
	}
	
	
	public static void main(String[] args) {
		
		dumpReplaceRuleTableContents();
		
		
	}

	
	private static Filter<WTConvertAPIRule> PUBLICS = new Filter<WTConvertAPIRule>() {
		@Override
		public boolean passes(WTConvertAPIRule rule) {
			if (!(rule instanceof WTReplaceTypeRule))
				return false;
			WTReplaceTypeRule replaceRule = (WTReplaceTypeRule)rule;
			return !isInternal(replaceRule);
		}
	};
	
	private static Filter<WTConvertAPIRule> INTERNALS = new Filter<WTConvertAPIRule>() {
		@Override
		public boolean passes(WTConvertAPIRule rule) {
			if (!(rule instanceof WTReplaceTypeRule))
				return false;
			WTReplaceTypeRule replaceRule = (WTReplaceTypeRule)rule;
			return isInternal(replaceRule);
		}
	};
	
	
	/**
	 * Contents for : /com.windowtester.eclipse.help/html/reference/API Migration.textile
	 */
	private static void dumpReplaceRuleTableContents() {
		WTConvertAPIRule[] rules = new WTConvertAPIRefactoring(new ArrayList<IJavaElement>()).getRules();
	
		System.out.println("public API types");
		boolean toColor = false;		
		for (WTConvertAPIRule rule : PUBLICS.filter(rules)) {
			System.out.println(rowAttr(toColor = !toColor) + asMarkup((WTReplaceTypeRule) rule));				
		}
		
		System.out.println("internal API types");
		toColor = false;
		for (WTConvertAPIRule rule : INTERNALS.filter(rules)) {
			System.out.println(rowAttr(toColor = !toColor) + asMarkup((WTReplaceTypeRule) rule));				
		}		
	}


	private static String rowAttr(boolean toColor) {
		return toColor ? "{background:#ddd}."  : ""; 
	}

	private static String asMarkup(WTReplaceTypeRule replaceRule) {
		return "| @" + replaceRule.getOldTypeName() + "@ | @" + replaceRule.getNewTypeName() + "@ |";
	}
	
	
	private static boolean isInternal(WTReplaceTypeRule replaceRule) {
		return replaceRule.getNewTypeName().contains(".internal.");
	}
}
