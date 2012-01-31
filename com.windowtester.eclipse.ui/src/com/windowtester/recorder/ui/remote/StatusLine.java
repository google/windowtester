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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.windowtester.recorder.ui.IRecorderActionSource;
import com.windowtester.recorder.ui.RecorderConsoleActionAdapter;

public class StatusLine extends RecorderConsoleActionAdapter {

	private static final String RECORDING_STATUS = "recording";
	private static final String PAUSED_STATUS    = "paused";
	private static final String INITIAL_STATUS   = "idle";
	private static final String SPY_MODE_STATUS  = "inspecting";
	
	private final Label statusLine;
	private final IRecorderActionSource actionSource;

	private final Composite composite;
	private boolean spying;

	public StatusLine(Composite composite, IRecorderActionSource actionSource) {
		this.composite = composite;
		this.actionSource = actionSource;
		if (actionSource != null)
			actionSource.addHandler(this);
		statusLine = new Label(composite, SWT.NONE);
		statusLine.setText(INITIAL_STATUS);
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.horizontalSpan = 2;
		statusLine.setLayoutData(gridData);	 
	}

	public void dispose() {
		if (actionSource != null)
			actionSource.removeHandler(this);
		statusLine.dispose();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.RecorderConsoleActionAdapter#clickPause()
	 */
	public void clickPause() {
		updateStatusText(PAUSED_STATUS);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.RecorderConsoleActionAdapter#clickRecord()
	 */
	public void clickRecord() {
		updateStatusText(RECORDING_STATUS);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.ui.RecorderConsoleActionAdapter#clickSpyMode()
	 */
	public void clickSpyMode() {
		/*
		 * TODO: this local management of state sucks...
		 */
		spying = !spying;
		String msg = spying ? SPY_MODE_STATUS : RECORDING_STATUS;
		updateStatusText(msg);
	}


	
	private void updateStatusText(String text) {
		statusLine.setText(text);
		composite.layout();
	}

	
}
