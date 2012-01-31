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
package com.windowtester.eclipse.ui.views;

import org.eclipse.jface.action.IAction;

import com.windowtester.recorder.ui.remote.DashboardRemote;
import com.windowtester.recorder.ui.remote.IDashBoardRemote;

/**
 * Basic dash controller.
 */
public class DashboardController implements IDashboardController {

	private IDashBoardRemote dash;
	private final IRecorderDashActionProvider actionProvider;
	
	public static interface IRecorderDashActionProvider {
		IAction[] getActions();
	}
	
	
	public DashboardController(IRecorderDashActionProvider actionProvider) {
		this.actionProvider = actionProvider;
	}
	
	public IRecorderDashActionProvider getActionProvider() {
		return actionProvider;
	}
	
	public IDashBoardRemote getDash() {
		return dash;
	}
	
	public IAction[] getRecorderActions() {
		return getActionProvider().getActions();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.views.IDashboardController#dispose()
	 */
	public void dispose() {
		getDash().close();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.views.IDashboardController#open()
	 */
	public void open() {
		dash = DashboardRemote.forRecorderActions(getRecorderActions());
		dash.open();
	}

}
