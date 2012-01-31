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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.eclipse.ui.dialogs.Mover;
import com.windowtester.eclipse.ui.dialogs.RecorderDashboard;
import com.windowtester.eclipse.ui.views.RecorderConsoleView;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.recorder.ui.IRecorderActionSource;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * The dashboard remote controls actions on the {@link RecorderConsoleView}.  
 * The dashboard is only activated during active recording sessions.
 */
public class DashboardRemote implements IDashBoardRemote {
	
	
	private static final boolean USE_LEGACY_REMOTE = true;
	
	private Shell shell;
	
	private final IAction[] recordingActions;
	
	EventViewPane eventViewer;
	
	Composite mainControlComposite;
	
	private RecordingToolbar recordingToolbar;
	private MinMaxToggleGroup minMaxToggleGroup;

	private StatusLine statusLine;

	private IRecorderActionSource actionSource;

	private IEventSequenceModel sequenceModel;
	
	public static IDashBoardRemote forRecorderActions(IAction[] recordingActionDelegates) {
		if (USE_LEGACY_REMOTE)		
			return new RecorderDashboard(recordingActionDelegates);
		return new DashboardRemote(recordingActionDelegates);
	}
	
	public DashboardRemote(IAction[] recordingActionDelegates) {
		this.recordingActions = recordingActionDelegates;
	}

	
	public Shell getShell() {
		return shell;
	}
	
	
	public void open() {
		createShell();
		createContents();
		openShell();
		addListeners();
	}

	void addListeners() {
		Mover mover = Mover.forShell(getShell());
		getShell().addMouseListener(mover);
		mainControlComposite.addMouseListener(mover);
	}

	void createContents() {
		
		createMainComposite();

		createRecordingToolBar();
		
		createMinMaxToggleGroup();
		
		//createSeparator(mainControlComposite);
		
		createViewPane();
		//createSeparator(getShell());
		createStatusLine();		
		
		mainControlComposite.setFocus(); //to avoid focus being on the first item whihc looks bad...
	}

	private void createStatusLine() {
		statusLine = new StatusLine(getShell(), actionSource);
	}

	private void createRecordingToolBar() {
		recordingToolbar = new RecordingToolbar(mainControlComposite, recordingActions);
	}

	private void createMinMaxToggleGroup() {
		minMaxToggleGroup = new MinMaxToggleGroup(this);
	}

	private void createMainComposite() {
		mainControlComposite = new Composite(getShell(), SWT.NONE);
		
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalIndent = 0;
		mainControlComposite.setLayoutData(layoutData);
		
		GridLayout layout = new GridLayout(2, false);
		mainControlComposite.setLayout(layout);
	}

	private void createSeparator(Composite parent) {
		Label separator = new Label (parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT);
		GridData separatorData = new GridData();
		separatorData.verticalAlignment = SWT.TOP;
		separatorData.horizontalAlignment = SWT.FILL;
		separatorData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(separatorData);
	}

	private void createViewPane() {
		eventViewer = new EventViewPane(mainControlComposite, sequenceModel).hidden();	
	}

	void openShell() {
		shell.pack();
 		shell.open();
	}

	void createShell() {
		shell = new Shell(SWT.ON_TOP);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing   = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = -4;
		gridLayout.marginHeight = 2;
		
		shell.setLayout(gridLayout);
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		DashboardRemote dash = (DashboardRemote) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				Action a = new Action(/*"action"*/){
					public void run() {
						System.out.println("action!");
					}
				};
				DashboardRemote dashboard = new DashboardRemote(new IAction[]{a, a, a, a /*, toggle */});
				dashboard.open();
				return dashboard;
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
		DisplayExec.sync(new Runnable() {
			public void run() {
				statusLine.dispose();
				getShell().dispose();
			}
		});
	}

	public void close() {
		dispose();
	}
	
	void pack() {
		shell.pack();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.remote.IDashBoardRemote#addStatusSource(com.windowtester.recorder.ui.IRecorderActionSource)
	 */
	public void addStatusSource(IRecorderActionSource actionSource) {
		this.actionSource = actionSource;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.remote.IDashBoardRemote#withModel(com.windowtester.recorder.ui.IEventSequenceModel)
	 */
	public IDashBoardRemote withModel(IEventSequenceModel sequenceModel) {
		this.sequenceModel = sequenceModel;
		return this;
	}
	
	
}
