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
package com.windowtester.eclipse.ui.actions;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.windowtester.eclipse.ui.UiPlugin;


public class RecordToolbarAction extends AbstractLaunchToolbarAction {

	private IWorkbenchWindow window;
	private static RecordToolbarAction INSTANCE;


	/**
	 * @param launchGroupIdentifier
	 */
	public RecordToolbarAction() {
		super(UiPlugin.ID_RUN_LAUNCH_GROUP);
		INSTANCE = this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction#getToolTip(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	protected String getToolTip(ILaunchConfiguration lastLaunched) {
		return "Record " + lastLaunched.getName();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		//NOTE: usage data reported in delegate action
		
		/*
		 * Overriding super since the default action short-circuits trying to resolve a launch mode for the current active selection
		 */
		ILaunchConfiguration configuration = getLastLaunch();
		if (configuration == null) {
			DebugUITools.openLaunchConfigurationDialogOnGroup(getShell(), new StructuredSelection(), getLaunchGroupIdentifier());
		} else {
			DebugUITools.launch(configuration, getMode());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		super.init(window);
	}
	
	protected Shell getShell() {
		return window.getShell();
	}
	
	public String getLastLaunchName() {
		ILaunchConfiguration lastLaunch = getLastLaunch();
		if (lastLaunch == null)
			return null;
		return lastLaunch.getName();
	}


	public static RecordToolbarAction getInstance() {
		return INSTANCE;
	}
	
}
