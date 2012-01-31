package com.windowtester.test.locator.swt;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.test.locator.swt.shells.TableTestShell;


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
public class TableItemlLocatorTest extends AbstractLocatorTest {

	TableTestShell window;
	
	@Override
	public void uiSetup() {
		window = new TableTestShell();
		window.open();
	}

	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	public void testSelection() throws Exception {
		IUIContext ui = getUI();
		ui.click(new TableItemLocator("Item 1"));
//		boolean selected = new TableItemLocator("Item 1").isSelected(ui);
//		System.out.println(selected);
		ui.assertThat(new TableItemLocator("Item 1").isSelected());	
	}
	
	
}
