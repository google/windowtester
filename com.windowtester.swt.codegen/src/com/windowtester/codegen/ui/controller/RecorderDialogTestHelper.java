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
package com.windowtester.codegen.ui.controller;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;


/**
 * Helper for making the recorder dialog test-friendly.
 *
 */
public class RecorderDialogTestHelper {

	
	public static class RecorderDialogShellLocator extends NamedWidgetLocator {

		private static final long serialVersionUID = -7911082464406739917L;

		public RecorderDialogShellLocator() {
			super(RecorderDialogTestHelper.RECORDER_SHELL_NAME);
		}
	}
	
	public static class RecorderDialogStartButtonLocator extends NamedWidgetLocator {

		private static final long serialVersionUID = -7911082464406739917L;

		public RecorderDialogStartButtonLocator() {
			super(RecorderDialogTestHelper.RECORDER_RECORD_BUTTON_NAME);
		}
	}
	
	
	public static final String RECORDER_SHELL_NAME    = "recorder.shell";
	public static final String RECORDER_RECORD_BUTTON_NAME = "recorder.record.button";
	
	public static void tagAsRecorderShell(Shell shell) {
		shell.setData("name", RECORDER_SHELL_NAME);
	}
	
	public static boolean isRecorderShell(Shell shell) {
		return RECORDER_SHELL_NAME.equals(shell.getData("name"));
	}

	public static void tagAsRecorderStartButton(Widget button) {
		button.setData("name", RECORDER_RECORD_BUTTON_NAME);
	}
	
	public static boolean isRecorderStartButton(Widget button) {
		return RECORDER_RECORD_BUTTON_NAME.equals(button.getData("name"));
	}
	
	public static RecorderDialogShellLocator shellLocator() {
		return new RecorderDialogShellLocator();
	}
	
	public static IWidgetLocator startButtonLocator() {
		return new RecorderDialogStartButtonLocator();
	}
	
	
	
}
