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
package com.windowtester.eclipse.ui.usage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

/**
 * Base action class for instrumented actions.
 *
 */
public abstract class ProfiledAction extends Action implements IActionDelegate {

	public ProfiledAction() {
		setIdToDefault();
	}
	
	public ProfiledAction(String text) {
		super(text);
		setIdToDefault();
	}

	public ProfiledAction(String text, int style) {
		super(text, style);
		setIdToDefault();
	}
	
	public ProfiledAction(String text, ImageDescriptor image) {
		super(text, image);
		setIdToDefault();
	}

	private void setIdToDefault() {
		setId(getDefaultId());
	}
	
	/**
	 * Return a sensible default id value (in case it is not set using {@link ProfiledAction#setId(String)}).
	 */
	protected String getDefaultId() {
		return getClass().getName();
	}

	public ProfiledAction withId(String id) {
		setId(id);
		return this;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//
	// Run templates
	//
	////////////////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public final void run() {
		doRun();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public final void run(IAction action) {
		doRun(action);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		//override in subclasses if needed
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//
	// Subclass hooks
	//
	////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * Perform the action (and DO NOT call the usage profiler). 
	 */
	public void doRun() { }
	public void doRun(IAction action) {}

	////////////////////////////////////////////////////////////////////////////////////
	//
	// Profiler notification
	//
	////////////////////////////////////////////////////////////////////////////////////
	
	private void notifyUsageProfiler(IAction action) {
	}
	
	
}
