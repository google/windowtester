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
package com.windowtester.eclipse.ui.inspector.notifications;

import org.eclipse.swt.widgets.Display;

import com.windowtester.eclipse.ui.inspector.notifications.EventNotificationPopup.EventRecorded;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * Event notification service.
 */
public class EventNotification {

	
	private abstract static class Opener {
		EventNotificationPopup open() {
			EventNotificationPopup popup = create();
			popup.open();
			return popup;
		}
		
		abstract EventNotificationPopup create();
	}



	public static void main(String[] args) {	
		
		EventInfo info = new EventInfo();
		info.hierarchyInfo = new TreeItemLocator("foo/bar");
		
		
		EventNotificationPopup popup = popupForEvent(new SemanticWidgetSelectionEvent(info));
		final Display display = Display.getDefault();
		while (popup.getShell() != null && !popup.getShell().isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}	
	}
	
	
	
	public static EventNotificationPopup popupForEvent(final IUISemanticEvent event) {
		
		return openTimed(new Opener() {
			EventNotificationPopup create() {
				return new EventRecorded(event);
			}
		});

	}
	
	
	
	private static EventNotificationPopup openTimed(final Opener opener) {
		final EventNotificationPopup popup = (EventNotificationPopup)DisplayExec.sync(new RunnableWithResult(){
			public Object runWithResult() {
				return opener.open();			
			}
		});
		
		DisplayExec.sync(new Runnable() {
			public void run() {
				Display.getDefault().timerExec(3000, new Runnable() {
					public void run() {
						popup.close();
					}
				});
			}
		});
		return popup;
	}
}
