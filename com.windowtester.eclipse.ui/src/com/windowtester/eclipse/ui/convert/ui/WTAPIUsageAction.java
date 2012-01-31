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
package com.windowtester.eclipse.ui.convert.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.eclipse.ui.convert.WTAPIUsage;
import com.windowtester.eclipse.ui.dialogs.ExceptionDetailsDialog;
import com.windowtester.ui.util.Logger;

/**
 * Show a text based report containing a list of all WindowTester API used by the selected
 * project/package/class.
 * 
 */
public class WTAPIUsageAction
	implements IActionDelegate
{
	private List<IJavaElement> selected;

	/**
	 * Called when the selection in the workbench has changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selected = new ArrayList<IJavaElement>();
		if (!(selection instanceof IStructuredSelection))
			return;
		for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
			Object elem = (Object) iter.next();
			if ((elem instanceof IJavaElement))
				selected.add((IJavaElement) elem);
		}
	}

	/**
	 * Called to perform the conversion.
	 */
	public void run(IAction action) {
		if (selected == null || selected.size() == 0)
			return;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		try {
			final WTAPIUsage usage = new WTAPIUsage();
			new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						usage.scan(selected, monitor);
					}
					catch (Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			});
			window.getActivePage().openEditor(new WTAPIUsageEditorInput(usage), "org.eclipse.ui.DefaultTextEditor");
		}
		catch (OperationCanceledException e) {
			// Ignored... Operation canceled by user
		}
		catch (Exception e) {
			// TODO integrate the Bug Submission form
			if (e instanceof InvocationTargetException)
				e = (Exception) ((InvocationTargetException) e).getCause();
			String errMsg = "WindowTester API scanning exception: " + e;
			Logger.log(errMsg, e);
			new ExceptionDetailsDialog(shell, "Code Conversion Exception", errMsg, e).open();
		}
	}
}
