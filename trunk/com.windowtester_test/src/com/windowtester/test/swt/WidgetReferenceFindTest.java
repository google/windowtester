package com.windowtester.test.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.test.eclipse.BaseTest;

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
public class WidgetReferenceFindTest extends BaseTest {

	
	private static final String SHELL_TEXT = "Multi Button Test";
	private static String BUTTON_TEXT = "button";
	private static Shell _shell;
	
	public void testFindOfWidgetRefReturnsThatRef() throws WidgetSearchException {
		IUIContext ui = getUI();
			
		IWidgetLocator[] items = ui.findAll(new ButtonLocator(BUTTON_TEXT));
		assertEquals(4, items.length);
		// TODO[pq]: this contract is no longer valid in W2, nor probably should it be -- investigate
//		for (IWidgetLocator button : items) {
//			System.out.println(UIProxy.getToString((Widget) ((IWidgetReference)button).getWidget()));
//			assertEquals(button, ui.find(button));
//		}
		for (IWidgetLocator button : items) {
			ui.click(button); //failure would throw an exception
		}
		
	}

	@Override
	protected void setUp() throws Exception {
		//super.setUp();
		DisplayExec.sync(new Runnable() {
			public void run() {
				createButtonShell();
			}
		});
	}

	
	@Override
	protected void tearDown() throws Exception {
		DisplayExec.sync(new Runnable() {
			public void run() {
				if (_shell != null && !_shell.isDisposed())
					_shell.dispose();
			}
		});
	}
	
	private static void createButtonShell() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		_shell = new Shell();
		_shell.setLayout(gridLayout);
		_shell.setSize(400, 67);
		_shell.setText(SHELL_TEXT);

		for (int i=0; i < 4; ++i) {
			Button button1 = new Button(_shell, SWT.NONE);
			button1.setText(BUTTON_TEXT);			
		}
		_shell.open();
		_shell.layout();
	}
	
	
}
