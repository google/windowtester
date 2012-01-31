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
package com.windowtester.internal.runtime.junit.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.monitor.IUIThreadMonitorListener;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;


/**
 * Base class for execution monitor implementations.
 * 
 */
public abstract class AbstractExecutionMonitor implements IExecutionMonitor, IUIThreadMonitorListener  {


	/**
	 * Manages and maintains running state information.
	 *
	 */
	public class RunningState {
		/**
		 * Flag used to signal when the execution is finished.
		 */
		private boolean _isRunning;
		
		private TestExceptionCache _exceptionCache = new TestExceptionCache();

		void setIsRunning(boolean isRunning) {
			if (_isRunning && isRunning)
				throw new IllegalStateException("test is already running");
			if (!_isRunning && !isRunning)
				throw new IllegalStateException("test is already stopped");
			_isRunning = isRunning;
		}

		public void setException(Throwable e) {
			_exceptionCache.cache(e);
		}
		
		TestExceptionCache getExceptions() {
			return _exceptionCache;
		}

		public boolean isRunning() {
			return _isRunning;
		}

		public boolean isExceptional() {
			return _exceptionCache.hasException();
		}

		public void throwException() throws Throwable {
			_exceptionCache.throwException();
		}
		
	}
	
	/**
	 * Used to track whether a test is already running or not.
	 */
	private final RunningState _runningState = new RunningState();
	
	/**
	 * Collection of execution listeners.
	 */
	private final List<ITestExecutionListener> _listeners = new ArrayList<ITestExecutionListener>();
	
	/**
	 * Used to cleanup post test run.
	 */
	private ITestCleanupHandler _cleanupHandler;
	
	/**
	 * Set the cleanup handler.
	 * @param handler the handler
	 */
	protected void setCleanupHandler(ITestCleanupHandler handler) {
		_cleanupHandler = handler;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.IExecutionMonitor#addListener(com.windowtester.runtime.test.exec.ITestExecutionListener)
	 */
	public void addListener(ITestExecutionListener listener) {
		_listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.IExecutionMonitor#removeListener(com.windowtester.runtime.test.exec.ITestExecutionListener)
	 */
	public void removeListener(ITestExecutionListener listener) {
		_listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testFinished()
	 */
	public void testFinished() {
		//notify listeners
		for (Iterator<ITestExecutionListener> iter = _listeners.iterator(); iter.hasNext();) {
			ITestExecutionListener listener = iter.next();
			listener.testFinished();
		}
		//update monitors
		stopTestMonitors(); //<-- note: could be listeners?
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testFinishing()
	 */
	public void testFinishing() {

		//notify listeners
		for (Iterator<ITestExecutionListener> iter = _listeners.iterator(); iter.hasNext();) {
			ITestExecutionListener listener = iter.next();
			listener.testFinishing();
		}
		
		//THEN: cleanup
		try {
			cleanUp();
		} finally {
			//finally, update state
			_runningState.setIsRunning(false);
		}
		
	}

	/**
	 * Cleanup test.  Called post test finish.
	 *
	 */
	protected void cleanUp() {
		if (_cleanupHandler != null)
			_cleanupHandler.cleanUp();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testStarting(com.windowtester.runtime.test.TestIdentifier)
	 */
	public void testStarting(ITestIdentifier identifier) {
		//update state
		_runningState.setIsRunning(true);
		
		//notify listeners
		for (Iterator<ITestExecutionListener> iter = _listeners.iterator(); iter.hasNext();) {
			ITestExecutionListener listener = iter.next();
			listener.testStarting(identifier);
		}
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#testStarted(com.windowtester.runtime.test.TestIdentifier)
	 */
	public void testStarted(ITestIdentifier identifier) {
		
		/*
		 * TODO[pq]: This is not getting called which means monitors are not being
		 * started here.  --- For Junit4 support to work, this will need to get remedied!
		 */
		
		//update monitors
		startTestMonitors(); //<-- note: could be listeners?
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.ITestExecutionListener#exceptionCaught(java.lang.Throwable)
	 */
	public void exceptionCaught(Throwable e) {
		//update state
		_runningState.setException(e);
		
		//notify listeners
		for (Iterator<ITestExecutionListener> iter = _listeners.iterator(); iter.hasNext();) {
			ITestExecutionListener listener = iter.next();
			listener.exceptionCaught(e);
		}
	}

	/**
	 * Get the active running state of this execution.
	 * @return the running state
	 */
	protected RunningState getState() {
		return _runningState;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#waitUntilFinished()
	 */
	public void waitUntilFinished() throws Throwable {
				
		RunningState state = getState();
		
		while (state.isRunning() && !state.isExceptional() &&!terminateWaitForFinish()/* && !display.isDisposed() */) {
			//System.out.println("calling doWait");
			doWaitForFinish();
		}
		
		//rethrow any caught exceptions
		state.throwException();
	}

	/**
	 * Do the wait in the <code>waitUntilFinished</code> loop.
	 * <p>
	 * To be provided by subclasses.
	 */
	protected abstract void doWaitForFinish();

	
	/**
	 * Signal that the wait loop should be terminated.  Used in exceptional conditions: for instance, the display is disposed.
	 * <p>
	 * To be provided by subclasses.
	 * @return <code>true</code> if the <code>waitUntilFinished</code> loop should be terminated.
	 */
	protected abstract boolean terminateWaitForFinish();
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Test Monitor Management
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Start test monitors.
	 */
	private void startTestMonitors() {
		//TODO ...move this monitor to runtime and refactor
		//TestMonitor.getInstance().beginTestCase(this);
		startUIMonitor();
	}

	/**
	 * Stop test monitors.
	 */
	private void stopTestMonitors() {
		//?::
		TestMonitor.getInstance().endTestCase();
		stopUIMonitor();	
	}
	
	/**
	 * Start monitoring the UI for responsiveness.
	 */
	protected void startUIMonitor() {
		getUIThreadMonitor().setListener(this);
	}

	/**
	 * Stop monitoring the UI for responsiveness.
	 */
	protected void stopUIMonitor() {
		getUIThreadMonitor().setListener(null);
	}
	
	/**
	 * Answer the user interface thread monitor used to determine if the user interface
	 * thread is idle or unresponsive longer than some expected time.
	 * 
	 * @return the user interface thread monitor (not <code>null</code>)
	 */
	protected abstract IUIThreadMonitor getUIThreadMonitor();


	//TODO: implement this!
	/* (non-Javadoc)
	 * @see com.windowtester.swt.monitor.IUIThreadMonitorListener#uiTimeout(boolean)
	 */
	public void uiTimeout(boolean isResponsive) {
		
		//System.out.println("timeout happened");
		
//from UITestCase:		
//		// do a screenshot before shutting down
//		ScreenCapture.createScreenCapture(getId());
//		// TODO [author=Dan] How can shells be closed or the test be stopped if UI thread is busy? 
//		// TODO [author=Dan] Can close shells be done by the closeUnexpectedShells method?
//		if (isIdle)
//			new ExceptionHandlingHelper(getDisplay(), true).closeOpenShells();
		
		//cache exception --- note that this will abort wait loop
		getState().setException(new InvocationTargetException(new WaitTimedOutException("UI thread idle timeout")));
		
		//do a screenshot before shutting down -- can we push this into the cleanup handler?
		ScreenCapture.createScreenCapture(); //we can do better than this...
		
		//NOTE: shouldn't need to do the closing of shells, it will be handled later....
		
	}
		
	/**
	 * Get the exception cache for this monitor.
	 */
	public TestExceptionCache getExceptionCache() {
		return getState().getExceptions();
	}
	
	
}