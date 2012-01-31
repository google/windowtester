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
package com.windowtester.runtime.swt.internal.junit;


import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.junit.core.AbstractExecutionMonitor;
import com.windowtester.internal.runtime.junit.core.ITestIdentifier;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.swt.internal.settings.TestSettings;
import com.windowtester.runtime.util.ScreenCapture;

/** 
 * Monitor for executing SWT tests.
 *
 */
public class SWTExecutionMonitor extends AbstractExecutionMonitor {
	
	private class ScreenShotHandler {
		private  void takeScreenshotIfNecessary(Throwable e) {
			if (!previouslyHandled(e))
				ScreenCapture.createScreenCapture();
		}
		//widget search exceptions trigger screen shots before reaching here
		//TODO: a thought: we could tag exceptions as having an associated screen shot...
		private boolean previouslyHandled(Throwable e) {
			return e instanceof WidgetSearchException;
		}
	}
	
	private final ScreenShotHandler screenShotHandler = new ScreenShotHandler();
	
	//used to cache test environment details
	private final ExecutionEnvironment environment;
	//a holder for cached automation values -- should probably get moved
	private boolean automatedMode;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public SWTExecutionMonitor() {
		//create environment info holder
		this.environment = new ExecutionEnvironment();
		//build cleanup handler
		setCleanupHandler(new SWTCleanupHandler(environment, getState()));
	}	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Test Lifecycle
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testStarting(com.windowtester.runtime.test.TestIdentifier)
	 */
	public void testStarting(ITestIdentifier identifier) {

		//System.out.println("monitor starting test: " + identifier);
		
		//first cache environment
		environment.update();
		
		//TODO: this should probably get moved to a listener
		setAutomationFlags();
		
		
		//update settings scope
		TestSettings.getInstance().push();
				
		//next inform listeners, etc.
		super.testStarting(identifier);
	}
	

	/**
	 * Set automation flags.
	 */
	private void setAutomationFlags() {
		// wrapped call in syncExec, to prevent NPE while
		// setting flag
		Display.getDefault().syncExec(new Runnable(){
			public void run(){
				automatedMode = ErrorDialog.AUTOMATED_MODE;
				ErrorDialog.AUTOMATED_MODE = false;
			}
		});
		
		
	}
	
	/**
	 * Reset automation flags (to previous value).
	 */
	private void resetAutomationFlags() {
		ErrorDialog.AUTOMATED_MODE = automatedMode;
	}

	//@Override
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#testFinished()
	 */
	public void testFinished() {
		//notify listeners
		super.testFinished();
		//update settings scope
		TestSettings.getInstance().pop();
		//reset automation mode
		resetAutomationFlags();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.junit.core.AbstractExecutionMonitor#exceptionCaught(java.lang.Throwable)
	 */
	public void exceptionCaught(Throwable e) {		
		takeScreenShotIfNecessary(e);
		cleanUp(); //cleanup so that the exception gets properly propagated
		super.exceptionCaught(e);
	}

	private void takeScreenShotIfNecessary(Throwable e) {
		screenShotHandler.takeScreenshotIfNecessary(e);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Wait Loop Management
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//@Override
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#doWaitForFinish()
	 */
	protected void doWaitForFinish() {
		Display display = getDisplay();
		
//		if (noUI())
//			return;
		
		try {
			if (!display.isDisposed() && !display.readAndDispatch()) {
				doSleep(display);
			}
		} catch (SWTException ex) {
			/* ignore device/widget disposal errors -- these are just unavoidable */
			if (ex.code == SWT.ERROR_DEVICE_DISPOSED || ex.code == SWT.ERROR_WIDGET_DISPOSED)
				return;
			// Do nothing: rethrowing errors blocks the display thread.
			// One must rely on the fact of proper error handling of
			// display thread users.
			//!pq: this was the abbot assumption, but is it right?  why not cache and fail later?
			LogHandler.log("Exception caught in SWTExecutionMonitor.waitUntilFinished():");
			LogHandler.log(ex);
		} catch (Throwable t) {
			LogHandler.log("Exception caught in SWTExecutionMonitor.waitUntilFinished():");
			LogHandler.log(t);
		}
	}

	private void doSleep(Display display) throws InterruptedException {
		//display.sleep is not safe in the pure SWT case
		if (!Platform.isRunning())
			Thread.sleep(100); 
		else
			display.sleep();
	}

//TODO: investigate
//	private boolean noUI() {
//		final Display display = getDisplay();
//		Shell[] shells = (Shell[]) DisplayExec.sync(new RunnableWithResult(){
//			public Object runWithResult() {
//				return display.getShells();
//			}
//		});
//		return shells.length == 0;
//	}

	//@Override
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#terminateWaitForFinish()
	 */
	protected boolean terminateWaitForFinish() {
		Display display = getDisplay();
		return display == null || display.isDisposed();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Test Monitor Instantiation
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Answer the user interface thread monitor used to determine if the user interface
	 * thread is idle or unresponsive longer than some expected time.
	 * 
	 * @return the user interface thread monitor (not <code>null</code>)
	 */
	protected IUIThreadMonitor getUIThreadMonitor() {
		return (IUIThreadMonitor) getUI().getAdapter(IUIThreadMonitor.class);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get the current display.
	 * @return the current display
	 */
	private Display getDisplay() {
		return environment.getDisplay();
	}
	
	/**
	 * Get the UI Context.
	 */
	public IUIContext getUI() {
		return environment.getUI();
	}
	
	
}
