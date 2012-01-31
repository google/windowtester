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
package com.windowtester.recorder.ui.remote;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

public class RecordingToolbar {

	
	private ToolBarManager toolbarManager;
	private ToolBar toolBar;

	private final IAction[] actionDelegates;

	private Composite composite;
	
	RecordingToolbar(Composite composite, IAction[] actionDelegates) {
		this.composite       = composite;
		this.actionDelegates = actionDelegates;
		createToolBarManager();
		addActions();
		createToolBar();
	}
	
	private void addActions() {
		for (int i = 0; i < actionDelegates.length; i++) {
			toolbarManager.add(actionDelegates[i]);
		}
//		addSeparator();
//		addMinMaxToggle();
	}
	
	private void createToolBarManager() {
		toolbarManager = new ToolBarManager(SWT.FLAT /* SWT.NONE */);
	}

	private void createToolBar() {
		toolBar = toolbarManager.createControl(composite);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalIndent = 3;
		toolBar.setLayoutData(gridData);
	}
}
