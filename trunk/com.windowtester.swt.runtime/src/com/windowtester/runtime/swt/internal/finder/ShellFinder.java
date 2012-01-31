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
package com.windowtester.runtime.swt.internal.finder;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.tester.swt.ShellTester;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * Find parent shells.
 */
public class ShellFinder {

	private static final ShellTester _shellTester = new ShellTester();
		
	protected static final int MODAL_SHELL_MASK = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;

    //a hook to override --- used by the inspector (NOT CLEAN!)
    public static Shell CURRENT_SHELL_HINT;
	
	
	/**
	 * Find the parent shell handle for the given widget.
	 * @param w the widget
	 * @return
	 */
	public static IShellHandle find(Widget w) {
		SWTHierarchyHelper helper = new SWTHierarchyHelper(w.getDisplay());
		do {
			w = helper.getParent(w);
			if (w instanceof Shell) {
				Shell shell = (Shell)w;
				return new ShellHandle(_shellTester.getText(shell), isModal(shell));
			}
		} while (w != null);
		//shouldn't happen!
		return null;
	}

	
	/**
	 * Check if the given shell is modal.
	 * 
	 * @param shell the shell in question (not <code>null</code>)
	 * @return <code>true</code> if the shell is modal, and <code>false</code>
	 *         otherwise
	 */
	public static boolean isModal(Shell shell) {
		return (_shellTester.getStyle(shell) & MODAL_SHELL_MASK) != 0;
	}

	/**
	 * Get the current modal shell or <code>null</code> if there is none.
	 */
	public static Shell getModalShell(final Display d) {
		final Shell[] focusShell = new Shell[1];
		d.syncExec(new Runnable() {
			public void run() {
				Shell[] shells = d.getShells();
				Shell shell;
				for (int i = 0; i < shells.length; i++) {
					shell = shells[i];
					/**
					 * Ensure: (1) shell is modal 
					 *         (2) shell is not parent of any of the other shells (need they be modal?)
					 */
					if (ShellFinder.isModal(shell) && !intersect(shell.getShells(), shells)) 
						focusShell[0] = shell;
				}
			}

			/**
			 * Test whether any of the items in array 1 are contained in array 2 
			 */
			private boolean intersect(Object[] items1, Object[] items2) {
				if (items1 == null)
					return false;
				for (int i=0; i < items1.length; ++i ) {
					for (int j = 0; j < items2.length; j++) {
						if (items1[i] == items2[j])
							return true;
					}
				}
				return false;
			}
		});
		return focusShell[0];
	}
	
	
	
	public static Shell getActiveShell(final Display display) {
		final Shell[] active = new Shell[1];
		display.syncExec(new Runnable() {
			public void run() {
				active[0] =  display.getActiveShell();
				//hook for inspector override
				if (active[0] == null) {
					try {
						if (CURRENT_SHELL_HINT != null && !CURRENT_SHELL_HINT.isDisposed())
							active[0] = CURRENT_SHELL_HINT;	
					} catch(Throwable th) {
						//being extra safe 
					}
				}
			}
		});
		return active[0];
		
		//backing out to address regressions
		
//		//1. get the active shell
//		Shell activeShell = display.getActiveShell();
//		
//		//2. check to see if there is more than one modal shell up
//		Shell[] allShells = display.getShells();
//		List modalShells  = getModalShells(allShells);
//		
//		//if only one
//		if (modalShells.size() == 1) {
//			//2a. verify that it agrees with the active one
//			Shell modal = (Shell)modalShells.get(0);
//			if (modal != activeShell) {
//				trace("single modal shell (" + modal.getText()+ ") does not agree with display's active shell (" + activeShell.getText() + ")updating to modal shell");
//				activeShell = modal;
//			}
//		}
//		//if more than one
//		if (modalShells.size() > 1) {
//			
//			// 2.b if the active shell is a modal child of another modal we
//			// assume it is the active one
//			if (isModal(activeShell)) {
//				boolean modalChild = false;
//				for (Iterator iter = modalShells.iterator(); iter.hasNext();) {
//					Shell modal = (Shell) iter.next();
//					if (!modal.isDisposed()) {
//						try {
//							Shell[] children = modal.getShells();
//							for (int i = 0; i < children.length; i++) {
//								if (children[i] == activeShell)
//									modalChild = true;
//							}
//						} catch (SWTException e) {
//							// ignore -- shell may be disposed
//						}
//					}
//				}
//				//2.c if not, check the ShellWatcher
//				if (!modalChild) {
//					activeShell = ShellWatcher.getInstance().getCurrent();
//					trace("falling back on ShellWatcher to infer active shell: " + activeShell);
//				}
//			}
//		}
//		return activeShell;
	}



	/**
	 * Bring the root display in front (if it is not already).
	 */
	public static void bringRootToFront(final Display d) {
		Shell activeShell = getActiveShell(d);
		if (activeShell != null)
			return;
		d.syncExec(new Runnable() {
			public void run() {
				Shell shell = getRootShell(d);
				if (shell == null)
					return; //TODO: consider a retry here?
				Logger.log("forcing root shell active and giving it focus");
				shell.forceActive();
				shell.setFocus();
				//TODO: should we wait here?
			}
		});
	}

	//note: called on the UI thread.
	protected static Shell getRootShell(Display d) {
		if (!Platform.isRunning())
			return null; //TODO: add support for non-platform case
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

	public static Shell getWorkbenchRoot(){
		return DisplayReference.getDefault().execute(new Callable<Shell>() {
			public Shell call() throws Exception {
				return getRootShell(Display.getDefault());
			}
		});
	}
	
	
//
//	private static void trace(String msg) {
//		TraceHandler.trace(IRuntimePluginTraceOptions.SHELL_FINDER, "(ShellFinder) - " + msg);
//	}
//
//
//	/**
//	 * Prune out the modal shells.
//	 */
//	private static List getModalShells(Shell[] shells) {
//		List modals = new ArrayList();
//		for (int i = 0; i < shells.length; i++) {
//			try {
//				if (!shells[i].isDisposed() && isModal(shells[i]))
//					modals.add(shells[i]);
//			} catch (SWTException e) {
//				// ignore -- shell may be disposed
//			}
//		}
//		return modals;
//	}
	
	
}
