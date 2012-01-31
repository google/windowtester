package com.windowtester.test.runtime;

import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
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
public class CloseNestedShellsTest extends UITestCaseSWT {

	
	private MessageDialog dialog;

	protected final void setUp() throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				uiSetup();
			}			
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.AbstractLocatorTest#uiSetup()
	 */
	public void uiSetup() {
		Shell shell = new Shell(Display.getDefault());
        dialog = new MessageDialog(shell, "First Shell", null,
                "message", MessageDialog.INFORMATION,
                new String[] { IDialogConstants.OK_LABEL }, 0) {
        	
        	/* (non-Javadoc)
        	 * @see org.eclipse.jface.dialogs.Dialog#close()
        	 */
        	@Override
        	public boolean close() {
        		boolean confirmed = MessageDialog.openConfirm(getShell(), "Nested", "Nested Shell");
        		if (confirmed)
        			return super.close();
        		return false;
        	}
        	
        };
        
        dialog.open();
	}
	
	
	static class Handler implements IShellConditionHandler {

		private final String title;
		private ShellCondition condition;

		public Handler(String title, boolean modal) {
			this.title = title;
			this.condition = new ShellCondition(title, modal);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
		 */
		public void handle(IUIContext ui) throws Exception {
			System.out.println("handling: " + title + " - " + new Date());
			ui.click(new ButtonLocator("OK"));
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.condition.shell.IShellCondition#test(org.eclipse.swt.widgets.Shell)
		 */
		public boolean test(Shell shell) {
			System.out.println("testing for: " + title + " against " + shell.getText() + "  - " + new Date());
			return condition.test(shell);
		}
		
	}
	
	public void testCloseNestedShells() throws Exception {
		IUIContext ui = getUI();
		IShellMonitor sm = (IShellMonitor)ui.getAdapter(IShellMonitor.class);
		sm.add(new Handler("Nested", true));

		//failure means a hang/infinite regress
		try {
			ui.find(new ButtonLocator("Bogus"));
		} catch (WidgetSearchException e) {
			//pass
		}
	}
	
}
