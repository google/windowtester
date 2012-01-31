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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.internal.widgets.ComboReference;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

public class ComboReferenceTest extends AbstractLocatorTest {

	private static final String DIALOG_TITLE = "Combo Event Responder";
	
	class DialogHandler extends ShellCondition implements IShellConditionHandler {
		public DialogHandler() {
			super(DIALOG_TITLE, true);
		}
		public void handle(IUIContext ui) throws WidgetSearchException {
			ui.click(new ButtonLocator("OK"));
		}
	}
	
	private Shell shell;
	private Combo combo;

	@Override
	public void uiSetup() {
			Display display = Display.getDefault();
			shell = new Shell(display);
			shell.setLayout(new GridLayout());
			
			combo = new Combo(shell, SWT.FLAT | SWT.BORDER);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			for (int i = 0; i < 5; i++) {
				combo.add("item" + i);
			}
			combo.setText("item0");

			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					selected = true;
				};
			});

			shell.pack();
			shell.open();
	}
	
	@Override
	public void uiTearDown() {
		shell.dispose();
	}
	
	boolean selected;
	
	public void testSelectionEventGenerated() throws Exception {
		new ComboReference(combo).click("item1");
		getUI().assertThat(new ICondition(){
			public boolean test() {
				return selected;
			}
		});
	}
	

	
	public void testListenerSyncExecDoesNotBlock() throws Exception {
		
		IShellMonitor sm = (IShellMonitor) getUI().getAdapter(IShellMonitor.class);
		sm.add(new DialogHandler());

		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				combo.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						System.out.println("Item selected");
						MessageDialog.openInformation(shell, DIALOG_TITLE, "Item selected");
						selected = true;
					};
				});
			}
		});
		
		System.out.println("pre select");
		new ComboReference(combo).click("item1");
		System.out.println("post select");
		getUI().assertThat(new ICondition(){
			public boolean test() {
				return selected;
			}
		});
	}
	
	
	
	
}
