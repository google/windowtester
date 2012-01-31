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
package com.windowtester.eclipse.ui.assertions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.eclipse.ui.assertions.AssertionExpertPopup.PopupClosedCallback;
import com.windowtester.recorder.event.user.IWidgetDescription;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/** 
 * Manage the inspector popup.
 */ 
public class AssertionExpert {

	
	protected static final long POPUP_DELAY = 1000;

	private static AssertionExpertPopup inspector;

	private static Shell parentShell;
	
	
	//sanity: ensure we don't inspect multiple times for the same request
	private static IWidgetDescription lastEvent;

	private static Point cursorLocation;
	
	

	public static void openPopup(IWidgetDescription event, PopupClosedCallback callback) {
		if (eventIsInvalid(event))
			return;
		
		if (eventIsSeen(event))
			return;
		
		if (mouseIsOverOpenInspector())
			return;
		
		doOpen(event, callback);
	}
	
	public static void openPopup(IWidgetDescription event) {
		openPopup(event, null);
	}


	private static void doOpen(IWidgetDescription event, PopupClosedCallback callback) {
		cacheEvent(event);
		closeLastInspector();
		openNewInspectorForEvent(event, callback);
	}


	private static boolean mouseIsOverOpenInspector() {
		if (inspector == null)
			return false;
		
		Point cursorLocation = Display.getDefault().getCursorLocation();
		if (cursorLocation == null)
			return false;
		
		return inspector.contains(cursorLocation);
	}


	private static boolean eventIsInvalid(IWidgetDescription event) {
		if (event == null)
			return true;
		//for now we just ignore events with no locators -- in the future we may want to allow
		//users to debug them...
		return event.getLocator() == null;
	}


	private static void cacheEvent(IWidgetDescription event) {
		lastEvent = event;
	}


	private static boolean eventIsSeen(IWidgetDescription event) {
		return (event == lastEvent || event.isSame(lastEvent));
	}


	private static void openNewInspectorForEvent(final IWidgetDescription event, PopupClosedCallback callback) {
//		cacheCursorPosition();
//		
//		Runnable runnable = new Runnable() {
//			public void run() {
//				try {
//					Thread.sleep(POPUP_DELAY);
//				} catch (InterruptedException e) {
//				}
//				if (mouseMoved()) {
//					return;
//				}
//				
				inspector = new AssertionExpertPopup(getParent(), event, callback);
				inspector.open();
//			}
//		};
//		Thread t = new Thread(runnable);
//		t.start();
	}


	protected static boolean mouseMoved() {
		System.out.println(cursorLocation);
		System.out.println(getCurrentCursorLocation());
		
		//if either is null we just say true (shouldn't happen but playing safe)
		Point currentCursorLocation = getCurrentCursorLocation();
		if (currentCursorLocation == null || cursorLocation == null)
			return true;
		
		return !currentCursorLocation.equals(cursorLocation);
		
//		return cursorLocation != getCurrentCursorLocation();
	}


//	private static void cacheCursorPosition() {
//		cursorLocation = getCurrentCursorLocation();
//	}


	private static Point getCurrentCursorLocation() {
		try {
			return (Point) DisplayExec.sync(new RunnableWithResult() {
				public Object runWithResult() {
					return getParent().getDisplay().getCursorLocation();
				}
				
			});
			
		} catch(Throwable e) {
			//just null out the location
			return null;
		}
	}

	

	private static void closeLastInspector() {
		if (inspector != null)
			inspector.close();
	}


	private static Shell getParent() {
		if (parentShell == null)
			parentShell = new Shell(SWT.ON_TOP);
		return parentShell;
	}



	
	
}
