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
package com.windowtester.runtime.swt.internal.locator.jface;

import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;



/**
 * Dialog finder utility.
 */
public class DialogFinder {

	public static Dialog findActiveDialog() {
		Shell activeShell = ShellFinder.getActiveShell(Display.getDefault());
		return toDialog(activeShell);
	}
	
	public static Dialog toDialog(final Shell shell) {
		if (shell == null)
			return null;
		return DisplayReference.getDefault().execute(new Callable<Dialog>() {
			public Dialog call() throws Exception {
				Object data = shell.getData();
				if (data instanceof Dialog)
					return (Dialog) data;
				return null;
			}
		});
//		return (Dialog) DisplayExec.sync(new RunnableWithResult() {
//			public Object runWithResult() {
//				Object data = shell.getData();
//				if (data instanceof Dialog)
//					return data;
//				return null;
//			}			
//		});	
	}
	
	
	public static Control findActiveDialogMessageControl() {
		return DialogInspector.getMessageControl(findActiveDialog());
	}
	
	
}
