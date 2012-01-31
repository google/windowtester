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
package com.windowtester.test.util;


import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.test.util.PlatformEventWatcherAndCodegenerator.API;



/**
 * A simple recording jig for platform/rcp recording/codegen debugging.
 * 
 * To use, launch in a JUnit Plug-in test launch config and set "main" to point to
 * your application under test.
 * 
 * @author Phil Quitslund
 *
 */
public class PlatformEventRecordingJig extends UITestCaseSWT {

	private static final boolean DISPLAY_EVENTS = true;

	
	public PlatformEventRecordingJig() {
		//super(demo.AddressBookUI.AddressBookUI.class);
	}
	
	
	@Override
	protected void setUp() throws Exception {
		//we need to override this, to make the runtime THINK it is in recording mode
		//this matters because in non-recording mode exceptions cause shells to be closed, menus to be 
		//dismissed etc...  (that is, they are treated as ERRORS, instead of part of daily life
		//as they are treated in recording)
		TestMonitor.getInstance().beginTest(null);
	}
	
	public void testDrive() throws Exception {
		doTestDrive();
	}


	//wrapper in subclasses to add extra behavior...
	protected void doTestDrive() {
		//REQUIRED FOR TESTING INSPECTOR 
		//SpyEventHandler.FORCE_ENABLE = true;
				
		//just watch!
		PlatformEventWatcherAndCodegenerator watcher = new PlatformEventWatcherAndCodegenerator(API.V2);//.withInspector();
		watcher.watch();
				
		waitForDisposeLoop(getShell());
		System.out.println(watcher.codegen());
			
		if (DISPLAY_EVENTS)	
			displayEvents(watcher.getEvents());
	}

	private Shell getShell() {
		final Display d = Display.getDefault();
		final Shell[] shell = new Shell[1];
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				shell[0] = d.getActiveShell();				
			}
		});
		return shell[0];
	}

	private void displayEvents(List<ISemanticEvent> events) {
		for (ISemanticEvent semanticEvent : events) {
			System.out.println(semanticEvent);
		}
	}

	private static void waitForDisposeLoop(final Shell shell) {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				while (!shell.isDisposed()) {
					if (!shell.getDisplay().readAndDispatch())
						shell.getDisplay().sleep();
				}
			}			
		});
	}
    
	
}