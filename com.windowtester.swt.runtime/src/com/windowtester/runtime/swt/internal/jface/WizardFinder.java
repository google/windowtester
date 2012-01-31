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
package com.windowtester.runtime.swt.internal.jface;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;

public class WizardFinder {

	public static IWizardPage getActiveWizardPage() throws WidgetSearchException {
		Shell activeShell = ShellFinder.getActiveShell(Display.getDefault());
		return getCurrentPage(activeShell);
	}
	
	public static WizardDialog getActiveWizardDialog() throws WidgetSearchException {
		Shell activeShell = ShellFinder.getActiveShell(Display.getDefault());
		return getWizardDialog(activeShell);
	}
	
	public static WizardDialog getWizardDialog(final Shell shell) throws WidgetSearchException {
		final Exception[] exception = new Exception[1];
		final WizardDialog[] wizardDialog    = new WizardDialog[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (shell == null) {
					exception[0] = new WidgetSearchException("No active shell");
					return;
				}
				Object dialog = shell.getData();
				if (!(dialog instanceof WizardDialog)) {
					exception[0] = new WidgetSearchException("Expected WizardDialog but found " + dialog);
					return;
				}
				wizardDialog[0] = (WizardDialog) dialog;
			}
		});
		if (exception[0] != null)
			throw ((WidgetSearchException) exception[0]);
		return wizardDialog[0];
	}
	
	public static IWizardPage getCurrentPage(final Shell shell) throws WidgetSearchException {
		final WizardDialog wizardDialog = getWizardDialog(shell);
		final Exception[] exception = new Exception[1];
		final IWizardPage[] page    = new IWizardPage[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				page[0] = wizardDialog.getCurrentPage();
				if (page[0] == null) {
					exception[0] = new WidgetSearchException("WizardDialog current page is null");
					return;
				}
			}
		});
		if (exception[0] != null)
			throw ((WidgetSearchException) exception[0]);
		return page[0];
		
	}
	
	
}
