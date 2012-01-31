package com.windowtester.test.eclipse;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.TimeElapsedCondition;
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
public class ErrorNotificationHandlingTest extends UITestCaseSWT {

	protected static final String ERROR_TITLE = "DR348DFEEGDKJKILSHA"; // A unique title
	protected static final String ERROR_MSG   = "Whoops!";

	private boolean _handled; //flag to track when the handler is called
	
	class ErrorHandledCondition implements ICondition {
		public boolean test() {
			return _handled;
		}
	}
	
	class ErrorShellHandler extends ShellCondition implements IShellConditionHandler {
		
		public ErrorShellHandler() {
			super(ERROR_TITLE, true);
		}
		public void handle(IUIContext ui) throws Exception {
			_handled = true;
			try {
				ui.click(new ButtonLocator("OK"));
			}
			catch (Throwable e) {
				System.err.println("ErrorNotificationHandlingTest exception in ErrorShellHandler#handle(...)");
				e.printStackTrace();
			}
		}
	}
	
	public void testHandleErrorDiaog() {
		
		IShellMonitor sm = (IShellMonitor) getUI().getAdapter(IShellMonitor.class);
		sm.add(new ErrorShellHandler());
		
		openDialog();
		getUI().wait(TimeElapsedCondition.milliseconds(250)); // brief delay to allow dialog to show
		getUI().wait(new ErrorHandledCondition(), 30000); //fail if not handled in 30 seconds
	}

	private void openDialog() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ERROR_TITLE, ERROR_MSG);
			}
		});
	}
	
}
