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
package com.windowtester.recorder.ui.remote.standalone;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import com.windowtester.eclipse.ui.dialogs.Mover;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

public class Remote {

	private static final String CONTROL_GROUP_LABEL = "Recorder";
	private Shell shell;
	private Group controlGroup;
	private ToolBar toolBar;
	
	private ToolBarManager toolbarManager;
	private final IAction[] actionDelegates;
	
		
	public static Remote forRecorder(RecorderGateway recorder) {
		RemoteActionFactory actions = new RemoteActionFactory(RemotePresenter.forRecorder(recorder));
		return new Remote(new IAction[]{actions.RECORD, actions.PAUSE, actions.SPY});
	}
	
	private Remote(IAction[] actionDelegates) {
		this.actionDelegates = actionDelegates;
	}
	
	public IAction[] getActionDelegates() {
		return actionDelegates;
	}
	
	public Shell getShell() {
		return shell;
	}
	
	
	public ToolBar getToolBar() {
		return toolBar;
	}
	
	public Group getControlGroup() {
		return controlGroup;
	}
	
	public ToolBarManager getToolbarManager() {
		return toolbarManager;
	}
	
	public void open() {
		createShell();
		createContents();
		openShell();
		addListeners();
	}

	private void addListeners() {
		Mover mover = Mover.forShell(getShell());
		getShell().addMouseListener(mover);
		getControlGroup().addMouseListener(mover);
	}

	private void createContents() {
		createControlGroup();
		createToolBarManager();
		addActions();
		createToolBar();
	}

	private void openShell() {
		shell.pack();
 		shell.open();
	}

	private void createShell() {
		shell = new Shell(SWT.ON_TOP);
		shell.setLayout(new GridLayout());
	}
	
	private void addActions() {
		IAction[] actions = getActionDelegates();
		for (int i = 0; i < actions.length; i++) {
			getToolbarManager().add(actions[i]);
		}
	}

	private void createControlGroup() {
		controlGroup = new Group(getShell(), SWT.NONE);		   
		controlGroup.setLayout(new GridLayout());
		controlGroup.setText(CONTROL_GROUP_LABEL);
	}
	
	private void createToolBarManager() {
		toolbarManager = new ToolBarManager(SWT.NONE);
	}

	private void createToolBar() {
		toolBar = getToolbarManager().createControl(getControlGroup());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		toolBar.setLayoutData(gridData);
	}

	
	public static void main(String[] args) throws InterruptedException {
		Remote dash = (Remote) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				Action a = new Action("action"){
					public void run() {
						System.out.println("action!");
					}
				};
				a.setText("a");
				Remote remote = new Remote(new IAction[]{a, a, a, a});
				remote.open();
				return remote;
			}
		});
		
		final Display display = Display.getDefault();
		while (!dash.getShell().isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
//		Thread.sleep(5000);
	
	}

	public void dispose() {
		Shell shell = getShell();
		if (shell == null || shell.isDisposed())
			return;
		shell.dispose();
	}
	
	
	
}
