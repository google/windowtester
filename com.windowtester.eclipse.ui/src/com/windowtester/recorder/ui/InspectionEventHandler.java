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
package com.windowtester.recorder.ui;

import com.windowtester.eclipse.ui.assertions.AssertionExpert;
import com.windowtester.eclipse.ui.assertions.AssertionExpertPopup.PopupClosedCallback;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.ui.RecordingSessionController.RecorderEventGateway;
import com.windowtester.runtime.swt.internal.display.DisplayExec;

/**
 * Helper to handle inspection request events.
 *
 */
public class InspectionEventHandler {

	private final RecorderEventGateway gateway;
	private SemanticWidgetInspectionEvent lastNotifiedEvent;
	
	public InspectionEventHandler(RecorderEventGateway gateway) {
		this.gateway = gateway;
	}
	
	void handleInspectionEvent(final SemanticWidgetInspectionEvent inspectionEvent) {
		
		DisplayExec.sync(new Runnable() {
			public void run() {
				//note: used to use workbench shell...
				AssertionExpert.openPopup(inspectionEvent, new PopupClosedCallback() {
					public void popupClosed() {
						notifyInspection(inspectionEvent);
					}
					public void assertionMade() {
						InspectionEventHandler.this.assertionMade();
					}
					public void popupDismissed() {
						InspectionEventHandler.this.expertDismissed();
					}
				});
			}
		});
	}


	private void notifyInspection(SemanticWidgetInspectionEvent inspectionEvent) {
		if (inspectionEvent == lastNotifiedEvent)
			return; //no need to re-notify
		doNotifyInspection(inspectionEvent);
	}

	private void doNotifyInspection(SemanticWidgetInspectionEvent inspectionEvent) {
		gateway.notify(inspectionEvent);
		lastNotifiedEvent = inspectionEvent;
	}
	
	//a hook to add behavior at assertion time
	protected void assertionMade() {
		
	}
	
	//a hook to add behavior at assertion time
	protected void expertDismissed() {
		// TODO Auto-generated method stub
		
	}
	
}
