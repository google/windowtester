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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import abbot.editor.recorder.RecordingFailedException;
import abbot.script.Resolver;
import abbot.script.Step;

import com.windowtester.recorder.IEventFilter;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.swing.event.spy.SpyEventHandler;
import com.windowtester.swing.recorder.ComponentRecorder;


/***
 *  Extend the Abbot EventRecorder for use with windowtester
 *  implement the IEventRecorder interface
 */
public class SwingEventRecorder extends abbot.editor.recorder.EventRecorder
	implements IEventRecorder{

	/** A flag to indicate record state */
    protected boolean _isRecording;
    
    /** A flag to indicate pause state */
    protected boolean _isPaused;
    
    /** A list of primitive event filters */
	private List /*<ISemanticEventListener>*/ _filters;
 
	private final SpyEventHandler spyHandler = new SpyEventHandler();
	
	public SwingEventRecorder(Resolver resolver, boolean captureMotion) {
		super(resolver, captureMotion);
		
	}
	
	
	 /**
     * Add the given semantic event listener to this recorder.  Event listeners
     * are notified of all semantic events.
     */
	public void addListener(ISemanticEventListener listener) {
		
        // Install existing semantic recorders
        for (int i=0;i < recorderClasses.length;i++) {
            ((ComponentRecorder)getSemanticRecorder(recorderClasses[i])).addListener(listener);
        }
	}

	/**
	 * Override 
	 *  point recorders to windowtester classes
	 */
	protected String getRecoderName(String cname) {
		return "com.windowtester.swing.recorder." + cname + "Recorder";
	}


	/**
	 *  Override 
	 *  notify listeners that recording has terminated
	 */
	public void terminate() throws RecordingFailedException {
		_isRecording = false;
		super.terminate();	
		//get the list of listeners
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyDispose();
	
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.IEventRecorder#toggleSpyMode()
	 */
	public void toggleSpyMode() {
		for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifySpyModeToggle();
	}
	

	public void stop() {
	//	System.out.println("Stopping recorder");
        try {
        	 _isRecording = false;
            super.terminate();
            List listeners = getListeners();
            for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyStop();
        }
        catch(RecordingFailedException e) {
            Throwable error = e.getReason();
            System.out.println("Recording stop failure: " + error.toString());
        }
        //note: this does a create...
        Step step = getStep();

		
	}

	

	public void start() {
		super.start();
		_isRecording = true;
        _isPaused = false;
		//	get the list of listeners
		List listeners = getListeners();
		for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyStart();
	}


	
	
	public void write() {
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyWrite();
	}


	public void restart() {
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyRestart();
	
	}


	public void pause() {
		_isRecording = false;
        _isPaused = true;
        List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyPause();		
	}


	public void removeListener(ISemanticEventListener listener) {
		// remove listener from semantic recorders
        for (int i=0;i < recorderClasses.length;i++) {
            ((ComponentRecorder)getSemanticRecorder(recorderClasses[i])).removeListener(listener);
        }
		
	}


	public void record(IUISemanticEvent semanticEvent) {
		//	get the list of listeners
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notify(semanticEvent);
	
		
	}


	public void reportError(RecorderErrorEvent event) {
		// get the list of listeners
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyError(event);
	
		
	}


	public void trace(RecorderTraceEvent event) {
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyTrace(event);
	
	}


	public void addEventFilter(IEventFilter filter) {
		List filters = getEventFilters();
        if (filters.contains(filter))
            debug("multiple adds of filter: : " + filter);
        else
        	filters.add(filter);
		
	}


	public void removeEventFilter(IEventFilter filter) {
		List filters = getEventFilters();
        if (filters.contains(filter))
            debug("filter removed that was not registered: " + filter);
        else
        	filters.remove(filter);
	}


	public void addHook(String hookName) {
		List listeners = getListeners();
		 for (Iterator iter = listeners.iterator(); iter.hasNext(); ) 
		        ((ISemanticEventListener)iter.next()).notifyAssertionHookAdded(hookName);
	
	}
	
	/**
	 * 
	 * @return list of listeners attached to the recorder
	 */
	private List getListeners(){
		return ((ComponentRecorder)getSemanticRecorder(recorderClasses[0])).getListeners();
	}
	

	public void notify(IUISemanticEvent semanticEvent) {
	    for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
		       ((ISemanticEventListener)iter.next()).notify(semanticEvent);
	}

	
	
	/**
     * @return the list of event filters
     */
    private List getEventFilters() {
    	if (_filters == null)
    		_filters = new ArrayList/*<IEventFilter>*/();
    	return _filters;
    }
    
    /**
     * Send this debug message to the tracer
     */
    private static void debug(String msg) {
//        DebugHandler.trace(TRACE_OPTION, msg);
    }
    
    
    /**
     * @return true if this recorder is currently recording
     */
    public boolean isRecording() {
        return _isRecording;
    }
	

    /** Override to generate spy events */
    public void insertStep(Step step) {
    	super.insertStep(step);
    	if (capturedEvent != null && capturedEvent.getID() == MouseEvent.MOUSE_ENTERED){
    		IUISemanticEvent event = spyHandler.interepretHover(capturedEvent);
    		if (event != null)
    			notify(event);
    	}
    	
    }


}
