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
package com.windowtester.swing.event.recorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.MenuComponent;

import abbot.editor.recorder.RecordingFailedException;
import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;
import abbot.script.Script;
import abbot.util.EventNormalizer;
import abbot.util.SingleThreadedEventListener;

import com.windowtester.recorder.IEventFilter;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.swing.event.spy.SpyEventListener;

/**
 *  The Swing Gui Test Recorder
 */
public class SwingGuiTestRecorder implements IEventRecorder {
	
	private static final long FIXTURE_EVENT_MASK =
        abbot.editor.recorder.EventRecorder.RECORDING_EVENT_MASK;

	private SwingEventRecorder _recorder;
	
	// the listener
	private static EventNormalizer normalizer = new EventNormalizer();
	
	// for compatibility with abbot code, nothing to do with windowtester
	private Script _script;
	private Hierarchy _hierarchy;
	
	/** A cache to store recorded events */
	private EventCachingListener _cache = new EventCachingListener();
	
	/** 
	 * Create an instance
	 *
	 */
	public SwingGuiTestRecorder(){
		_hierarchy = AWTHierarchy.getDefault();
		_script = new Script(_hierarchy);
		_recorder = new SwingEventRecorder(_script,true);
		// for debug purpose
	//	_recorder.addListener(new ConsoleReportingListener());
		_recorder.addListener(_cache);
		_recorder.addListener(new SpyEventListener());
		
	}
	
	public void start() {
		WindowTesterSecurityManager.install();
		startListening();
		_recorder.start();
	}

	public void stop() {
		_recorder.stop();
		
	}

	public void write() {
		_recorder.write();
	}

	public void restart() {
		_recorder.restart();
	}

	public void terminate() {
		_recorder.terminate();
	}

	public void toggleSpyMode() {
		_recorder.toggleSpyMode();
	}
	
	public void pause() {
		_recorder.pause();
	}

	public void addListener(ISemanticEventListener listener) {
		_recorder.addListener(listener);
		
	}

	public void removeListener(ISemanticEventListener listener) {
		_recorder.removeListener(listener);
		
	}

	public void record(IUISemanticEvent semanticEvent) {
		_recorder.record(semanticEvent);
		
	}

	public void reportError(RecorderErrorEvent event) {
		_recorder.reportError(event);
		
	}

	public void trace(RecorderTraceEvent event) {
		_recorder.trace(event);
		
	}

	public void addEventFilter(IEventFilter filter) {
		_recorder.addEventFilter(filter);
		
	}

	public void removeEventFilter(IEventFilter filter) {
		_recorder.removeEventFilter(filter);
		
	}

	public void addHook(String hookName) {
		_recorder.addHook(hookName);
		
	}
	/**
	 * start listening for events
	 *
	 */
	private void startListening() {	
		normalizer.startListening(new SingleThreadedEventListener() {
            protected void processEvent(final AWTEvent event) {
                    	startRecordingEvent(event); 
            }
        }, FIXTURE_EVENT_MASK);
	}
	
	
	/** 
	 * The  events are sent to the recorder.
     */
    public void startRecordingEvent(AWTEvent event) {
        Object src = event.getSource();
        boolean isComponent = src instanceof Component;
       
        // Allow only component events and AWT menu actions
        if (!isComponent && !(src instanceof MenuComponent)) {
        	System.out.println("Source not a Component or MenuComponent: " + event);
            return;
        }
        
        if ( _recorder != null) {
        	//System.out.println("recorder process event");
            try {
                _recorder.record(event);
            }
            catch(RecordingFailedException e) {
                // Stop recording, but keep what we've got so far
                _recorder.stop();
                e.printStackTrace();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.recorder.IEventRecorder#isRecording()
     */
    public boolean isRecording() {
    	return _recorder.isRecording();
    }
}
