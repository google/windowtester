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

import org.eclipse.debug.core.ILaunchConfiguration;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.ui.core.model.ISemanticEvent;
import com.windowtester.ui.util.DateString;

public class SessionSummaryLabelProvider {

	
	public static String getSummary(RecorderConsoleView view) {
		ISemanticEvent[] events = view.getEvents();
		int numberOfEvents = events.length;
		String launchConfig = getLaunchConfigDescription();
		String numberDetail = getNumberDetail(numberOfEvents, launchConfig);
		String timeDetail = getTimeDetail(numberOfEvents, launchConfig);
		String recordingStatus = getSessionStatus(view.getPresenter());
		return launchConfig + timeDetail + numberDetail + recordingStatus;
	}


	private static String getSessionStatus(RecorderConsolePresenter presenter) {
		//no-op pending user feedback
		return "";
//		
//		if (presenter.isPauseEnabled())
//			return " (recording)";
//		if (presenter.isRecordEnabled())
//			return " (paused)";
//		return "";
	}


	private static String getTimeDetail(int numberOfEvents, String launchConfig) {
		if (isUnset(launchConfig))
			return "";
		return " at " + DateString.forNow() + " - ";
	}


	private static boolean isUnset(String launchConfig) {
		return launchConfig.length() == 0;
	}


	private static String getNumberDetail(int numberOfEvents,
			String launchConfig) {
		return (isUnset(launchConfig) && numberOfEvents == 0) ? "No recorded events" : getEventString(numberOfEvents);
	}


	private static String getEventString(int numberOfEvents) {
		String eventDetail = (numberOfEvents == 1) ? "event" : "events";
		return Integer.toString(numberOfEvents) + ' ' + eventDetail;
	}
	
	
	private static String getLaunchConfigDescription() {
		ILaunchConfiguration config = UiPlugin.getDefault().getCachedLaunchConfig();
		if (config == null)
			return "";
		return config.getName();
	}
}
