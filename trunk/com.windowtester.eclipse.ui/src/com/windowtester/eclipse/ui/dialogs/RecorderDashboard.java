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
package com.windowtester.eclipse.ui.dialogs;

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

import com.windowtester.eclipse.ui.usage.ProfiledDelegateAction;
import com.windowtester.eclipse.ui.views.RecorderConsoleView;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IRecorderActionSource;
import com.windowtester.recorder.ui.remote.IDashBoardRemote;
import com.windowtester.recorder.ui.remote.StatusLine;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * The recorder dashboard controls actions on the {@link RecorderConsoleView}.  
 * The dashboard is only activated during active recording sessions.
 */
public class RecorderDashboard implements IDashBoardRemote {


	private static class RemoteAction extends ProfiledDelegateAction {
		public RemoteAction(IAction action) {
			super(action, "remote");
		}
	}
	
	private static final String CONTROL_GROUP_LABEL = "Recorder";
	private Shell shell;
	private Group controlGroup;
	private ToolBar toolBar;
	
	private ToolBarManager toolbarManager;
	private final IAction[] actionDelegates;
	
		
	private StatusLine statusLine;
	private IRecorderActionSource actionSource;
	
	public RecorderDashboard(IAction[] actionDelegates) {
		this.actionDelegates = new IAction[actionDelegates.length];
		for (int i = 0; i < actionDelegates.length; i++) {
			IAction action = actionDelegates[i];
			this.actionDelegates[i] = new RemoteAction(action);
		}	
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

	private void createStatusLine() {
		statusLine = new StatusLine(shell, actionSource);
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
		createStatusLine();
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
		RecorderDashboard dash = (RecorderDashboard) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				Action a = new Action("action", SWT.TOGGLE){
					public void run() {
						System.out.println("action!");
					}
				};
				a.setText("a");
				RecorderDashboard dashboard = new RecorderDashboard(new IAction[]{a, a, a, a});
				dashboard.open();
				return dashboard;
			}
		});
		
		final Display display = Display.getDefault();
		while (!dash.getShell().isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	
	}

	public void close() {
		dispose();
	}
	
	public void dispose() {
		DisplayExec.sync(new Runnable() {
			public void run() {
				statusLine.dispose();
				getShell().dispose();
			}
		});
	}
	
	public void addStatusSource(IRecorderActionSource actionSource) {
		this.actionSource = actionSource;
	}

	public IDashBoardRemote withModel(IEventSequenceModel sequenceModel) {
		//ignored
		return this;
	}
	
}

