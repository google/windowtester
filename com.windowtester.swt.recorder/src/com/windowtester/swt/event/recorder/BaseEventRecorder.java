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
package com.windowtester.swt.event.recorder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.recorder.IEventFilter;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.recorder.event.user.SemanticFocusEvent;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.swt.event.model.IEventRecorderCallBack;
import com.windowtester.swt.event.model.SWTSemanticEventInterpreter;
import com.windowtester.swt.event.model.SWTSemanticEventParser;

public abstract class BaseEventRecorder implements IEventRecorder {
	
	/** The trace option for use in filtering debugging output*/
    protected static final String TRACE_OPTION = "com.windowtester.swt/BaseEventRecorder_DEBUG";	
	
	/** A back-pointer to the root display */
	private Display _display;
	
	/** A list of semantic event listeners */
	private List /*<ISemanticEventListener>*/ _listeners;
	
	/** A list of primitive event filters */
	private List /*<ISemanticEventListener>*/ _filters;
	
	/** An interpreter (and stateful parser) for parsing semantic events */
	private SWTSemanticEventInterpreter _interpreter = new SWTSemanticEventInterpreter();
	private SWTSemanticEventParser _parser = new SWTSemanticEventParser(_interpreter);
	
	/** A flag to indicate record state */
    protected boolean _isRecording;
    
    /** A flag to indicate pause state */
    protected boolean _isPaused;
    
    
    /**
     * Create an instance.
     */
    public BaseEventRecorder() {
    	//register the interpreter to receive recorder events
    	addListener(_interpreter);
    	//add a callback for dynamic updates to filters on the event stream
    	_interpreter.addEventRecorderCallBack(new EventRecorderCallBack());
    }
    
    
    /**
	 * Create an instance.
	 * @param display
	 */
	public BaseEventRecorder(Display display) {
		this();
		_display = display;
		//remove ourselves on disposal && signal the dispose Semantic Event
		_display.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event event) {
                removeFilters();
                /**
                 * To ensure that any buffered events are sent (notably here a CLOSE)
                 * we need to manually flush
                 */
                Tracer.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "Calling recorder terminate on display disposal");
                terminate();
                
                
                //handleDispose(); <--- handled by listening to debug events instead
                //System.out.println("dispose!");
            }
		});
		//start menu watcher 
//		MenuWatcher.getInstance(display).startWatching();
	}
    
    
	/**
	 * Filter out/in events of interest here 
	 * @return whether this event is significant
	 */
	protected boolean isSignificant(Event e) {
		//iterate through all of the registered filters and check for exclusion
		List filters = getEventFilters();
		for (Iterator iter = filters.iterator(); iter.hasNext();) {
			IEventFilter element = (IEventFilter) iter.next();
			if (!element.include(e)) {
				return false;
			}
		}
		return true;
	}
	
	
	/** The recording event listener */
	private Listener _recorder = new Listener(){
		public void handleEvent(org.eclipse.swt.widgets.Event e){
		    if (isSignificant(e))
		        logEvent(e);
		}
	};

	/**
	 * Report the given event to the registered listeners
	 */
	protected void logEvent(Event event) {
	    IUISemanticEvent semanticEvent = getSemanticEvent(event);
	    //some events don't have associated semantics; ignore these
	    if (semanticEvent != null)
	    	record(semanticEvent);	    
	}

	/**
	 * Report the given typed event to the registered listeners
	 */
	protected void logEvent(TypedEvent event) {
		/*
		 * Typed events interrupt our cached stream of untyped events.
		 * To ensure none get lost, we need to emty the buffer first.
		 */
		flushEventBuffer();
		
		IUISemanticEvent semanticEvent = getSemanticEvent(event);
		//some events don't have associated semantics; ignore these
	    if (semanticEvent != null)
	    	record(semanticEvent);	    
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#record(com.windowtester.swt.event.model.IUISemanticEvent)
	 */
	public void record(IUISemanticEvent semanticEvent) {
	    for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
	        ((ISemanticEventListener)iter.next()).notify(semanticEvent);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#reportError(com.windowtester.swt.event.model.RecorderErrorEvent)
	 */
	public void reportError(RecorderErrorEvent event) {
	    for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
	        ((ISemanticEventListener)iter.next()).notifyError(event);
	}
	
	/**
     * @return the associated semantic event (or <code>null</code>)
     * @see com.windowtester.swt.event.model.SWTSemanticEventInterpreter#interpret(Event)
     */
    private IUISemanticEvent getSemanticEvent(Event event) {
    	IUISemanticEvent semanticEvent = null;  	
    	/*
		 * For safety, catch any exceptions generated by parsing and move on
         */
    	try {
    		semanticEvent = _parser.parse(event);
		} catch(Throwable t) {
			TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "error caught in event parsing, event ignored (see log for details)");
			LogHandler.log(t);
    	}
		return semanticEvent;
    }
    
	/**
     * @return the associated semantic event (or <code>null</code>)
     */ 
    private IUISemanticEvent getSemanticEvent(TypedEvent event) {
    	IUISemanticEvent semanticEvent = null;  	
    	/*
		 * For safety, catch any exceptions generated by parsing and move on
         */
    	try {
    		semanticEvent = _parser.parse(event);
		} catch(Throwable t) {
			TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "error caught in event parsing, event ignored (see log for details)");
			LogHandler.log(t);
    	}
		return semanticEvent;
    }
    
    
    
    
	/**
	 * Notify all listeners of disposal event.
	 */
//	private void handleDispose() {
//	    for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
//	        ((ISemanticEventListener)iter.next()).notifyDispose();
//	}
	
	/**
	 * @return the display being watched
	 */
	public Display getDisplay() {
	    return _display;
	}
	
	/**
	 * Add event filters
	 */
	private void addFilters() {
		if (!_isRecording)
			return;
		int[] eventTypes = getEventTypes();
		for (int i = 0; i < eventTypes.length; i++) {
			_display.addFilter(eventTypes[i], _recorder);
		}
	}

	/**
	 * @return the event types of interest
	 */
	private int[] getEventTypes() {
		return com.windowtester.swt.event.model.EventModelConstants.EVENT_TYPES;
	}
	
	/**
	 * Remove event filters
	 */
	private void removeFilters() {
		if (!_isRecording)
			return;
		int[] eventTypes = getEventTypes();
		for (int i = 0; i < eventTypes.length; i++) {
			_display.removeFilter(eventTypes[i], _recorder);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.recorder.IEventRecorder#start()
	 */
	public void start() {
		_isRecording = true;
        _isPaused = false;
	    addFilters();
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyStart();
	}

	/**
	 * @see com.windowtester.recorder.IEventRecorder#pause()
	 */
	public void pause() {
        removeFilters();
        _isRecording = false;
        _isPaused = true;
        flushEventBuffer();
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyPause();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.event.recorder.IEventRecorder#stop()
	 */
	public void stop() {
		removeFilters();
        _isRecording = false;
        //first, flush buffer to ensure there are no undispatched events
        flushEventBuffer();
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyStop();
	}
    
	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#restart()
	 */
	public void restart() {
        //first, flush buffer to ensure there are no undispatched events
        flushEventBuffer();
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyRestart();
	}
	
    /* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#write()
     */
    public void write() {
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyWrite();
    }
    
    
    /**
     * @see com.windowtester.recorder.IEventRecorder#addHook(java.lang.String)
     */
    public void addHook(String hookName) {
    	for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyAssertionHookAdded(hookName);
    }
    
    
    /**
     * @see com.windowtester.recorder.IEventRecorder#terminate()
     */
    public void terminate() {
        //first, flush buffer to ensure there are no undispatched events
        flushEventBuffer();
         //notify dispose
         for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
             ((ISemanticEventListener)iter.next()).notifyDispose();
    	
    }

    /* (non-Javadoc)
     * @see com.windowtester.recorder.IEventRecorder#toggleSpyMode()
     */
    public void toggleSpyMode() {
    	//first, flush buffer to ensure there are no undispatched events
        flushEventBuffer();
         //notify dispose
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
             ((ISemanticEventListener)iter.next()).notifySpyModeToggle();
  
    }
    
    //surfaced to improve recorder interactivity
	public void flushEventBuffer() {
		IUISemanticEvent bufferedEvent = _parser.flush();
		//notice that focus events are ignored...  this is a kludge (they porpbably shouldn't be sent at all on close)
        if (bufferedEvent != null && !(bufferedEvent instanceof SemanticFocusEvent)) 
            record(bufferedEvent);
	}
    
    
    /* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#trace(com.windowtester.swt.event.model.RecorderTraceEvent)
	 */
	public void trace(RecorderTraceEvent event) {
        for (Iterator iter = getListeners().iterator(); iter.hasNext(); ) 
            ((ISemanticEventListener)iter.next()).notifyTrace(event);
	}
	
	/* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#addListener(com.windowtester.swt.event.model.ISemanticEventListener)
     */
    public void addListener(ISemanticEventListener listener) {
        List listeners = getListeners();
        if (listener == null)
        	return;
        if (!listeners.contains(listener)) //multiple adds simply ignored
            listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#removeListener(com.windowtester.swt.event.model.ISemanticEventListener)
     */
    public void removeListener(ISemanticEventListener listener) {
        List listeners = getListeners();
        if (listeners.contains(listener))
            debug("listener removed that was not registered: " + listener);
        else
            listeners.remove(listener);
    }
    
    /**
     * Get the registered event listeners.
     * @return a list of resgistered listeners
     */
    private List getListeners() {
        if (_listeners == null)
            _listeners = new ArrayList/*<ISemanticEventListener>*/();
        return _listeners;
    }

    /**
     * @see com.windowtester.recorder.IEventRecorder#addEventFilter(com.windowtester.recorder.IEventFilter)
     */
    public void addEventFilter(IEventFilter filter) {
        List filters = getEventFilters();
        if (filters.contains(filter))
            debug("multiple adds of filter: : " + filter);
        else
        	filters.add(filter);
    }
    
    /**
     * @see com.windowtester.recorder.IEventRecorder#removeEventFilter(com.windowtester.recorder.IEventFilter)
     */
    public void removeEventFilter(IEventFilter filter) {
        List filters = getEventFilters();
        if (filters.contains(filter))
            debug("filter removed that was not registered: " + filter);
        else
        	filters.remove(filter);
    }
    
    /**
     * @return the list of event filters
     */
    protected List getEventFilters() {
    	if (_filters == null)
    		_filters = new ArrayList/*<IEventFilter>*/();
    	return _filters;
    }
    
    /**
     * @return true if this recorder is currently recording
     */
    public boolean isRecording() {
        return _isRecording;
    }
    
    /**
     * Send this debug message to the tracer
     */
    private static void debug(String msg) {
        DebugHandler.trace(TRACE_OPTION, msg);
    }
    
    class EventRecorderCallBack extends DropTargetAdapter implements IEventRecorderCallBack {

		public void listenForDropEvents(DropTarget dropTarget) {
			dropTarget.addDropListener(this);
		}
		
		/**
		 * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
		 */
		public void drop(DropTargetEvent event) {
			logEvent(event);
		}
		
    }


    
}
