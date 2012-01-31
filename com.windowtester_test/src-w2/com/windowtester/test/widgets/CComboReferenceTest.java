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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.widgets.CComboReference;
import com.windowtester.test.locator.swt.AbstractLocatorTest;

public class CComboReferenceTest extends AbstractLocatorTest {

	private Shell shell;
	boolean selected;
	private CCombo combo;
	
	@Override
	public void uiSetup() {
			Display display = Display.getDefault();
			shell = new Shell(display);
			shell.setLayout(new GridLayout());
			
			combo = new CCombo(shell, SWT.FLAT | SWT.BORDER);
			combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			for (int i = 0; i < 5; i++) {
				combo.add("item" + i);
			}
			combo.setText("item0");

			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Item selected");
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
	
	public void testSelectionEventGenerated() throws Exception {
		new CComboReference(combo).click("item1");
		getUI().assertThat(new ICondition(){
			public boolean test() {
				return selected;
			}
		});
	}
	
	
}
