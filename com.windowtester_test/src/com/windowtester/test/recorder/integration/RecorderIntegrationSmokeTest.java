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
package com.windowtester.test.recorder.integration;


import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.TimeElapsedCondition;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.EclipseUtil;

public class RecorderIntegrationSmokeTest extends BaseTest {

	

	
	
	private static final int RECORDER_BOOTSTRAP_TIMEOUT = 20000;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("recording.test", "true");
	}
	
	@Override
	protected void tearDown() throws Exception {
		//System.clearProperty("recording.test");
		super.tearDown();
	}
	
	
	public void testEclipseRecording() throws Exception {
		
		startRecordingSession();
		startRecording();
		codegen();
		
		
	}

	private void moveRecorderDialogOutOfTheWay() throws WidgetSearchException {
		final Shell shell = (Shell) ((IWidgetReference)getUI().find(new RecorderDialogLocator().shell())).getWidget();
		DisplayExec.sync(new Runnable() {
			public void run() {
				shell.setLocation(10, 600);
			}
		});
	}

	private void startRecording() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.wait(new RecorderDialogLocator().isShowing(), RECORDER_BOOTSTRAP_TIMEOUT);
		moveRecorderDialogOutOfTheWay(); //to make sure recorder dialog is out of the way
		ui.click(new RecorderDialogLocator().recordButton());
		
		//ui.wait(new RecorderDialogLocator().isDisposed());
		
	}
	
	
	private void codegen() {
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("New UI Test"));
		//...
		System.out.println("RecorderIntegrationSmokeTest.codegen()");
		ui.wait(TimeElapsedCondition.milliseconds(10000));
	}



	private void startRecordingSession() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PullDownMenuItemLocator(getRecordDialogMenuPath(), new ContributedToolItemLocator("org.eclipse.ui.recordAction")));
		ui.wait(new ShellShowingCondition("Record"));
		ui.click(new TreeItemLocator("Eclipse Application"));
		ui.click(new SWTWidgetLocator(ToolItem.class, "", 0, new SWTWidgetLocator(ToolBar.class)));
		ui.click(new ButtonLocator("Record"));
		ui.wait(new ShellDisposedCondition("Record"));
	}

	private String getRecordDialogMenuPath() {
		//ick: notice that we need to escape the periods?  --- this is because the periods form a pattern that
		//overeagerly matches (namely "Record As" in addition to the target "Record...")
		//another option would be to use the key accelerator to disambiguate
		if (EclipseUtil.isVersion_32())
			return "Record\\.\\.\\.";
		if (EclipseUtil.isVersion_33())
			return "Open Record Dialog...";
		return "Record Configurations..."; //3.4M4+
	}
	
	
}