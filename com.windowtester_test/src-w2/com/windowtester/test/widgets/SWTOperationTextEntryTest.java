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
package com.windowtester.test.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

public class SWTOperationTextEntryTest extends AbstractLocatorTest {

	
	private Shell shell;
	private Text text;

	@Override
	public void uiSetup() {
		shell = new Shell();
		final GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		shell.setSize(316, 67);
		shell.setText("Text Test");
		shell.open();

		text = new Text(shell, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text.setFocus();
		
		shell.layout();
	}
	
	
	public void testname() throws Exception {
		String txt = "KLASDKJASKLDJASKLDJKDJASLKDJASJKAKDJJJJAKKioqwoieuqioueiwoqueiqoueiowquioequoieqwoieuqw981209381209381902839018239081239081239081239812093890182392888888888888888";
		IUIContext ui = getUI();
		ui.enterText(txt);
		ui.assertThat(new SWTWidgetReference<Text>(text).hasText(txt));
		
	}
}
