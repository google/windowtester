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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.MenuComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;
import abbot.util.EventNormalizer;
import abbot.util.SingleThreadedEventListener;

import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.swing.SwingTestGenerator;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.user.SemanticEventAdapter;
import com.windowtester.swing.event.recorder.ConsoleReportingListener;
import com.windowtester.swing.event.recorder.EventCachingListener;
import com.windowtester.swing.event.recorder.SwingGuiTestRecorder;
import com.windowtester.swing.recorder.RecordingFailedException;


public class SwingEventRecordingWatcher  { 

	
	private static final long FIXTURE_EVENT_MASK =
                           abbot.editor.recorder.EventRecorder.RECORDING_EVENT_MASK;
	
	private static Hierarchy hierarchy;
	private static SwingGuiTestRecorder recorder;
	
	/** A cache to store recorded events */
	private EventCachingListener cache = new EventCachingListener();
	
	private static EventNormalizer normalizer = new EventNormalizer();
	
	static ActionListener recorderListener = new ActionListener() {
        public void actionPerformed(final ActionEvent event) {
            System.out.println(event.getActionCommand());
        }
    };
	
	public SwingEventRecordingWatcher(){
		
		hierarchy = AWTHierarchy.getDefault();
		recorder = new SwingGuiTestRecorder();
		recorder.addListener(new ConsoleReportingListener());
		recorder.addListener(cache);
		recorder.addListener(new SemanticEventAdapter() {
			public void notifyDispose() {
				codegen();
			}
		});
	
//		recorder.addActionListener(recorderListener);
		
	}

	/**
	 * Watch for events; 
	 */
	public void watch() {
	//	record();
		recorder.start();
	   	waitForDisposeLoop();
	}
	
	public void codegen() {
		IUISemanticEvent[] events = getEvents();
		System.out.println("cached events: ");
		for (int i = 0; i < events.length; i++) {
			System.out.println("\t" + events[i]);
		} 
		String src = new SwingTestGenerator("MockTest", "test", "MockApp", new String[]{}).generate(new EventStream(Arrays.asList(events)));
		//String src = new CodeGenerator(new SWTTestCaseBuilder("FooTest2", null, "com.windowtester.swt.tests.apps.InstrumentedApp", null)).generate(new EventStream(Arrays.asList(events))); 
		System.out.println(src);
	}
	

	public IUISemanticEvent[] getEvents() {
		return cache.getEvents();
	}
	
	private static void waitForDisposeLoop(){

		
	}
	
	private static void record() {	
		normalizer.startListening(new SingleThreadedEventListener() {
            protected void processEvent(final AWTEvent event) {
                    	startRecordingEvent(event); 
            }
        }, FIXTURE_EVENT_MASK);
	}
	
	
	/** 
	 * The  events are sent to the recorder.
     */
    public static void startRecordingEvent(AWTEvent event) {
        Object src = event.getSource();
        boolean isComponent = src instanceof Component;
       
        // Keep a log of all events we see on non-filtered components
   //      System.out.println("ED: " + Robot.toString(event)
   //                     + " (" + Thread.currentThread() + ")");
           
        // Allow only component events and AWT menu actions
        if (!isComponent && !(src instanceof MenuComponent)) {
        	System.out.println("Source not a Component or MenuComponent: " + event);
            return;
        }
        
        if ( recorder != null) {
        	//System.out.println("recorder process event");
            try {
                recorder.startRecordingEvent(event);
            }
            catch(RecordingFailedException e) {
                // Stop recording, but keep what we've got so far
            	recorder.stop();
                e.printStackTrace();
            }
        }
    }
    
    
    /** Stop recording and update the recorder actions' state. */
    public static void stopRecording(boolean discardRecording) {
        recorder.terminate();
    }

   
	
}
