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

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.eclipse.ui.actions.LaunchRecorderViewAction;

import static com.windowtester.eclipse.ui.views.RecorderConsoleView.ACTION_TAG_PREFIX;

/**
 * A page to describe an empty list of recorded events.
 */
public class EmptyRecorderConsoleControl implements IShellProvider {

	/**
	 * 
	 */
	private static final String LAST_RECORDING_LABEL = "last recording";

	/**
	 * The id of the recorder launch group.
	 */
	private static final String RECORDING_LAUNCH_GROUP = "com.windowtester.ui.recorderLauchGroup";

	/**
	 * The id used to display the empty result control in the console panel.
	 */
	private static final String ID = "emptyResult";

	protected static final String RELAUNCH_LINK_ACTION_ID       = ACTION_TAG_PREFIX + "relaunch_link";
	protected static final String LAUNCH__CONFIG_LINK_ACTION_ID = ACTION_TAG_PREFIX + "launchConfig_link";
	
	private Composite control;

	private final LaunchRecorderViewAction launchAction;

	private Link link;

	public EmptyRecorderConsoleControl(LaunchRecorderViewAction launchAction) {
		this.launchAction = launchAction;
	}
	
	
	public void createControl(Composite parent) {
		Color background = parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(ID);
		
		composite.setBackground(background);

		link = new Link(composite, SWT.NONE);
		link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
				false));
		link.setBackground(background);
		
		updateLinkText();

		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String text = e.text;
				if (relaunch(text))
					relaunch();
				else
					openLaunchConfigDialog();
			}

			private boolean relaunch(String text) {
				if (text == null)
					return false;
				return text.equals(LAST_RECORDING_LABEL);	
			}
			
			private void relaunch() {
				launchAction.doRun();
			}
			
			private void openLaunchConfigDialog() {
				DebugUITools.openLaunchConfigurationDialogOnGroup(getShell(), null, RECORDING_LAUNCH_GROUP);
			}
			
			
		});

		control = composite;
	}


	private void updateLinkText() {
		String lastLaunch = getLastLaunchName();
		String text = "";
		if (lastLaunch != null)
			text = "Relaunch <a>" + LAST_RECORDING_LABEL + "</a> or start";
		else
			text = "Start";
		text += " a new recording from the <a>recording dialog</a>...";
		
		link.setText(text);
		
		//FIXME: need to get this to properly refresh.
		
	}


	private String getLastLaunchName() {
		return launchAction.getLastLaunchName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.IShellProvider#getShell()
	 */
	public Shell getShell() {
		return control.getShell();
	}


	public String getId() {
		return ID;
	}



	public void aboutToShow() {
		updateLinkText();
	}


	

}
