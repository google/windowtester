package com.windowtester.test.locator.swt;

import static com.windowtester.runtime.swt.locator.SWTLocators.menuItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.swt.internal.matchers.IsVisibleMatcher;

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
public class MenuItemPathTest extends AbstractLocatorTest {

	private static final String RUN_ITEM_TEXT    = "Run...";
	private static final String RUN_AS_ITEM_TEXT = "R&un as";
	
	private static final String RUN_PATTERN = "Run...";

	private MenuItem runAs;
	private MenuItem run;
		
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiSetup()
	 */
	@Override
	public void uiSetup() {
		run = testableMenuItem(RUN_ITEM_TEXT);
		runAs = testableMenuItem(RUN_AS_ITEM_TEXT);		
//		VisibilityMatcher.TEST_MODE = true;
		IsVisibleMatcher.TEST_MODE = true;
	}

	private MenuItem testableMenuItem(String text) {
		MenuItem item = new MenuItem(new Menu(new Shell(Display.getDefault())), SWT.PUSH);
		item.setText(text);
//		VisibilityMatcher.setVisibleForTesting(item);
		IsVisibleMatcher.setVisibleForTesting(item);

		return item;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiTearDown()
	 */
	@Override
	public void uiTearDown() {
//		VisibilityMatcher.TEST_MODE = false;
		IsVisibleMatcher.TEST_MODE = false;

	}
	
	public void testRunPatternMatch() throws Exception {
		assertTrue(menuItem(RUN_PATTERN).matches(WTRuntimeManager.asReference(run)));
	}
	
	public void testRunAsPatternMatch() throws Exception {
		assertFalse(menuItem(RUN_PATTERN).matches(WTRuntimeManager.asReference(runAs)));
	}
	
	
	
	
}
