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
package com.windowtester.runtime.swing.internal.junit;

import java.awt.Component;
import java.awt.Window;
import java.util.Collection;
import java.util.Iterator;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;

import com.windowtester.internal.runtime.junit.core.AbstractExecutionMonitor;
import com.windowtester.internal.runtime.junit.core.ITestIdentifier;
import com.windowtester.internal.swing.UIContextSwingFactory;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.monitor.IUIThreadMonitor;

/** 
 * Monitor for executing Swing tests.
 */
public class SwingExecutionMonitor extends AbstractExecutionMonitor {
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Instance Creation
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private IUIContext _ui;

	public SwingExecutionMonitor() {
		
		//build cleanup handler
//		setCleanupHandler(new SWTCleanupHandler(_environment, getState()));
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
		
		// TODO [author=Dan] Race condition workaround... better solution needed...
		// If a Swing test launches an application, but a Swing window is still
		// open from a previous test, then the "notShowing" loop below
		// will detect the window from the first application and exit immediately
		// without waiting for the 2nd application to open its window.
		// This hack sleeps for a moment, giving the Swing UI thread a chance
		// to open the window. A better solution would be to get a message 
		// before the 2nd application is launched to cache the active window
		// and wait until the active window has changed.		
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * wait for frame showing and active
		 * before starting the test
		 */ 
		boolean notShowing = true;
		while(notShowing){
			Hierarchy hierarchy = AWTHierarchy.getDefault();
			Iterator iterator = hierarchy.getRoots().iterator();
			while(iterator.hasNext()){
				Component c = (Component)iterator.next();
	        	if (((Window)c).isActive()){
	        		notShowing = false;
	        		break;
	        	}	
			}
		}
	
		
		//update settings scope
//		TestSettings.getInstance().push();
				
		//next inform listeners, etc.
		super.testStarting(identifier);
	}
	

	//@Override
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#testFinished()
	 */
	public void testFinished() {
		//notify listeners
		super.testFinished();
		//update settings scope
//		TestSettings.getInstance().pop();
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
		
	}

	
	
	//@Override
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.test.exec.AbstractExecutionMonitor#terminateWaitForFinish()
	 */
	protected boolean terminateWaitForFinish() {
		Hierarchy hierarchy = AWTHierarchy.getDefault();
		Collection c = hierarchy.getRoots();
		//System.out.println("roots: " + c.size());
		return (c.size() == 0);
		
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
	 * Get the UI Context.
	 */
	public IUIContext getUI() {
		if (_ui == null)
			_ui = (IUIContext) UIContextSwingFactory.createContext();
		return _ui;
	}
	
	
}
