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
package util;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swing.UITestCaseSwing;

//import contactmanager.ContactManagerSwing;

/**
 * A simple recording jig for swing recording/codegen debugging.
 * 
 * To use, launch in a JUnit test 
 * 
 * In the Arguments tab, for VM arguments, add the following
 * -Djava.library.path=c:\eclipse-SDK-3.2\swt
 * 
 * the path to the directory for the swt.jar and the dll files .
 * 
 * @author Phil Quitslund
 * @author keertip
 *
 */

public class SwingEventRecordingJig extends UITestCaseSwing {

	//private static final boolean DISPLAY_EVENTS = true;
	private static Object lock = new Object();
	private SwingEventRecordingWatcher watcher;
	
	WindowListener listener = new WindowAdapter() {
	      public void windowClosing(WindowEvent w) {
	        // remove lock
	    	  SwingEventRecordingWatcher.stopRecording(false);
	    	  synchronized(lock){
	    		  lock.notifyAll();
	    	  }
	      }
	    };
	
	public SwingEventRecordingJig(){

		//super(DialogDemo.class);
	//	super(ContactManagerSwing.class);
	//	super(EditorSample.class);
		
	//	super(swing.samples.SwingText.class);
	//	super(swing.samples.SwingMenus.class);
	//	super(swing.samples.TestTree.class);
		
		//super(swing.samples.DialogDemo.class); //test dialogs <-- TabPanes...
	//	super(TextInputDemo.class);	
		//DONE:
	//	super(swing.samples.SwingTree.class); //test trees
	//	super(swing.samples.SwingList.class); //test lists
	//	super(swing.samples.ComboBoxes.class); //test combos
	//	super(swing.samples.SimpleTable.class); //test tables
	//	super(swing.samples.SwingTables.class);
	//	super(DatePickerSample.class);
	//	super(DateFieldSample.class);
	//	super(swing.samples.TextComponentDemo.class);
	//	super(TextComponentDemo.class);
		super(swing.samples.UseTheSampleDialog.class);
	//	super(swing.samples.JListRendererDemo.class);
		System.out.println("Application opened");
	}
		
	
	public void testDrive() throws WidgetSearchException {
		// get the application frame and attach listener
		System.out.println("Checking for app");
		Frame f,frame = null;
		Hierarchy h = AWTHierarchy.getDefault();
		Iterator  i = h.getRoots().iterator();
		boolean done = false;
		while (i.hasNext()&& !done){
			f = (Frame)i.next();
			if (f.getTitle() != "Abbot Robot Verification"){
				frame = f;
				done = true;
			}
		}
		if (frame != null)
			frame.addWindowListener(listener);
		//just watch!
		watcher = new SwingEventRecordingWatcher();
		watcher.watch();
		System.out.println("watcher started");
		
		// wait for lock to be released
		try {
			synchronized (lock){
				lock.wait();
			}
			Thread.sleep(50000);
			System.out.println("waiting");
		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		}


	}


}
