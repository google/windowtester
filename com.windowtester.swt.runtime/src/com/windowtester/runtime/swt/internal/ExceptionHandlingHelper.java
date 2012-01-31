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
package com.windowtester.runtime.swt.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import abbot.script.Condition;
import abbot.tester.swt.ShellTester;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;

/**
 * Used to close open shells before throwing exceptions (required, else the
 * UI thread blocks).
 *
 */
public class ExceptionHandlingHelper {

	private static final int CALL_TO_NUCLEAR_CLOSE_INTERVAL = 1000;
	private static final int CLOSE_ALL_WAIT_INTERVAL = 200;
	private static final int CLOSE_ALL_WAIT_THRESHOLD = 10000;
	
	private final Display _display;
	private final boolean _captureScreens;
	private final ShellTester _shellTester = new ShellTester();

	//used to keep track of our last close call to guard against duplicates (needed since we're in an async exec)
	private Shell _lastDisposed;
	
	//used to track how many shells have been closed vs. requests so we know when to exit
	private int _disposedCount;
	private int _closeCalls;
	private IUIContext ui;

	
	class ShellCloser {

		private final boolean _screenCaptureOnFirst;
		/*
		 * flag to track first close
		 * (first does not generate a capture since it will have already been done)
		 * ---> this can be overrriden by setting <code>_closeOnFirst</code>
		 */
		private boolean _first = true;

		public ShellCloser(boolean screenCaptureOnFirst) {
			_screenCaptureOnFirst = screenCaptureOnFirst;
		}

		public void closeShells() {
			handleConditions();
			closeShells(getShells());
		}
				
		private void handleConditions() {
			if (ui == null)
				return;
			ui.handleConditions();
		}

		private void closeShells(final Shell[] shells) {
			
			//play it extra safe: 
			if (shells == null)
				return;
			
			Shell root = null;
			for (int i = 0; i < shells.length; i++) {
				root = shells[i];
				//if not disposed, close children
				if (root != null && !root.isDisposed()) {
					try {
						closeShells(getShells(root));
						// if the root is modal, close it
						if (isModal(root)) {
							if (!isLast(root)) // but only if we haven't already requested it!
								doClose(root);
						}
					} catch (SWTException e) {
						e.printStackTrace();
						/*
						 * Despite the fact that we test for disposal above, it
						 * may happen in the process of closing As a guard, we
						 * just consume the exception and continue forth...
						 */
					}
				}
			}
		}

		//is this the last shell we asked to close?
		private boolean isLast(Shell shell) {
			return shell == _lastDisposed;
		}

		private void doClose(Shell root) {
			_lastDisposed = root;
			String shellTitle = getText(root);
			LogHandler.log("closing shell [aysnc] " + shellTitle);
			if (captureScreens() && (!_first || _screenCaptureOnFirst))
				doScreenCapture("pre close of modal shell: " + shellTitle);
			closeShell(root);	
			_first = false; //not first anymore
		}		
	}

	
	public ExceptionHandlingHelper(Display display, boolean captureScreens) {
		_display        = display;
		_captureScreens = captureScreens;
	}

	//pass an optional UI context (used as a callback to handle conditions during shell closing)
	public ExceptionHandlingHelper(Display display, boolean captureScreens, IUIContext ui) {
		this(display, captureScreens);
		this.ui = ui;
	}
	
	/**
	 * Close all open modal shells, closing children first.
	 */
	public void closeOpenShells() {
		//screenshot will have been taken at cause of failure
		closeModalShellsNuclearOption(false);
		//we call this 5 times to handle nested shells
		for (int i=0; i < 5; ++i) {
			//NOTE: there is a bit of a race here...  we might consider a test for modal shells
			//but that would involve a race too.  Since this is a corner case, we just cross our
			//fingers that the number of retries and our waits will suffice
			UIDriver.pause(CALL_TO_NUCLEAR_CLOSE_INTERVAL);
			closeModalShellsNuclearOption(true); //subsequent closes should trigger a capture
		}
			
		//TODO: create a wait util that uses IConditions
		UIDriver.wait(new Condition() {
			public boolean test() {
				return _disposedCount == _closeCalls;
			}
		}, CLOSE_ALL_WAIT_THRESHOLD, CLOSE_ALL_WAIT_INTERVAL);
		
	}


	/**
	 * Forcefully close all modal shells.
	 * <p>
	 * Used as a last ditch effort to close open shells that won't seem to go away.  
	 * <p>
	 * Taken from {@link junit.extensions.UITestCase} and modified.
	 */
	private void closeModalShellsNuclearOption(final boolean closeOnFirst) {

		// guard against case where display is already disposed
		
		if (getDisplay().isDisposed())
			return; //shouldn't happen but let's be extra safe
		
		new ShellCloser(closeOnFirst).closeShells();
	}

	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	//////////////////////////////////////////////////////////////////////////////

	private boolean captureScreens() {
		return _captureScreens;
	}
	
	private Display getDisplay() {
		return _display;
	}
	
	private ShellTester getShellTester() {
		return _shellTester;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Predicates
	//
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Test this shell to see if it's modal.
	 * @return <code>true</code> if the shell is modal, <code>false</code> otherwise
	 */
	private boolean isModal(Shell shell) {
		int mask = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
		return ((getStyle(shell) & mask) != 0);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// UI Thread access proxies
	//
	//////////////////////////////////////////////////////////////////////////////
	
	private void closeShell(final Shell shell) {
		++_closeCalls;
		//notice this is an ASYNC -- this is because some shell closes can cause others to 
		//open which blocks the syncExec
		
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				shell.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						++_disposedCount;
					}
				});
				shell.close();
			}			
		});
	}
	
	private int getStyle(final Shell shell) {
		final int[] style = new int[1];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				style[0] = shell.getStyle();
			}
		});
		return style[0];
	}
	
	private Shell[] getShells() {
		final Shell[][] shells = new Shell[1][];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				shells[0] = getDisplay().getShells();
			}
		});
		return shells[0];
	}

	private Shell[] getShells(final Shell root) {
		final Shell[][] shells = new Shell[1][];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					if (!root.isDisposed())
						shells[0] = root.getShells();
				} catch(SWTException e) {
					/*
					 * Although we test for disposal BEFORE calling this,
					 * there is a race, as the shell may get disposed DURING
					 * this exec...  The solution is to just ignore the exception
					 * and return a null Shell that will get ignored by the call to 
					 * close.
					 * 
					 * 
					 */
				}
			}
		});
		return shells[0];
	}
	
	
	private String getText(final Shell shell) {
		final String[] text = new String[1];
		getDisplay().syncExec(new Runnable() {
			public void run() {
				text[0] = shell.getText();
			}
		});
		return text[0];
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Debugging Helpers
	//
	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Take a screenshot.
	 */
	public static void doScreenCapture(String desc) {
		String testcaseID = TestMonitor.getInstance().getCurrentTestCaseID();
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "Creating screenshot (" + desc + ") for testcase: " + testcaseID);
		ScreenCapture.createScreenCapture(testcaseID /*+ "_" + desc*/);
	}
	
	/**
	 * Debugging helper.
	 */
	class ShellStateDebuggingHelper {
		
		/**
		 * Build a descrption of the current shells.
		 */
		String getShellStateDump() {
			StringBuffer sb = new StringBuffer();
			sb.append("Open Shells:").append("\n\n");
			Shell[] shells = getDisplay().getShells();
			for (int i = 0; i < shells.length; i++) {
				sb.append(getState(shells[i])).append("\n");
			}
			return sb.toString();
		}

		String getState(Shell shell) {
			Composite parent = getShellTester().getParent(shell);
			return "Shell ("
					+ getShellTester().getText(shell)
					+ ") <"
					+ shell.hashCode()
					+ "> visible="
					+ getShellTester().isVisible(shell)
					+ " | modal="
					+ isModal(shell)
					+ " | parent=<"
					+ ((parent == null) ? "null" : Integer.toString(parent
							.hashCode())) + ">"
					+ (getShellTester().isDisposed(shell) ? " [* disposed *]" : "");
		}
	}
	
}
