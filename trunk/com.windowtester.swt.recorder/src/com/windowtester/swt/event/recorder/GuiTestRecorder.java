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

import org.eclipse.swt.widgets.Display;

import com.windowtester.recorder.IEventFilter;
import com.windowtester.recorder.IEventRecorder;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.swt.event.model.SWTSemanticEventFactory;
import com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV2;
import com.windowtester.internal.debug.TraceHandler;

/**
 * The main GUI test recorder.
 */
public class GuiTestRecorder implements IEventRecorder {

	/** A reference to an event watcher instance */
	private SWTSemanticEventRecorder _recorder;
	
	/** A console reporting event listener (for debugging purposes) */
	private ISemanticEventListener _consoleReportingListener = new ConsoleReportingListener();
	
     
	/** The watched display */
	private Display _display;
	
	/**
	 * Create an instance.
	 * @param display
	 */
	public GuiTestRecorder(Display display, int apiVersion) {
		_display  = display;
		_recorder = initRecorder(apiVersion);
		_recorder.addListener(_consoleReportingListener);
	}

	/**
	 * Create a recorder for the given api version.
	 */
	private SWTSemanticEventRecorder initRecorder(int apiVersion) {
		SWTSemanticEventRecorder recorder = new SWTSemanticEventRecorder(_display);
		if (apiVersion == 2) {
			SWTSemanticEventFactory.setStrategy(new SWTSemanticEventFactoryImplV2());
		}
		return recorder;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.recorder.ISWTEventRecorder#start()
	 */
	public void start() {
	    _recorder.start();
	}

	/**
	 * @see com.windowtester.recorder.IEventRecorder#pause()
	 */
	public void pause() {
		_recorder.pause();
	}
	
	/**
	 * @see com.windowtester.recorder.IEventRecorder#addHook(java.lang.String)
	 */
	public void addHook(String hookName) {
		_recorder.addHook(hookName);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#restart()
	 */
	public void restart() {
		_recorder.restart();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.recorder.ISWTEventRecorder#stop()
	 */
	public void stop() {
	    _recorder.stop();
	}

    /* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#write()
     */
    public void write() {
        _recorder.write();
    }
    
    /**
     * @see com.windowtester.recorder.IEventRecorder#terminate()
     */
    public void terminate() {
    	_recorder.terminate();
    }
    
    /* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#record(com.windowtester.swt.event.model.UISemanticEvent)
	 */
	public void record(IUISemanticEvent semanticEvent) {
		_recorder.record(semanticEvent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#trace(com.windowtester.swt.event.model.RecorderTraceEvent)
	 */
	public void trace(RecorderTraceEvent event) {
		_recorder.trace(event);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.event.model.IEventRecorder#reportError(com.windowtester.swt.event.model.RecorderErrorEvent)
	 */
	public void reportError(RecorderErrorEvent event) {
		_recorder.reportError(event);
	}
	
    /* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#addListener(com.windowtester.swt.event.model.ISemanticEventListener)
     */
    public void addListener(ISemanticEventListener listener) {
       _recorder.addListener(listener);
    }

    /* (non-Javadoc)
     * @see com.windowtester.event.model.IEventRecorder#removeListener(com.windowtester.swt.event.model.ISemanticEventListener)
     */
    public void removeListener(ISemanticEventListener listener) {
        _recorder.removeListener(listener);
        //_recorder.removeListener(_socketStreamingListener); //PQ: why are we removing here?
    }
        
    /* (non-Javadoc)
     * @see com.windowtester.recorder.IEventRecorder#toggleSpyMode()
     */
    public void toggleSpyMode() {
    	_recorder.toggleSpyMode();
    }
       
    /**
     * A listener that reports recorded events to the console.  (Useful for debugging.)
     * 
     */
	static class ConsoleReportingListener implements ISemanticEventListener {

		/** A describer used for stateful event descrptions */
		//private EventDescriber _describer = new EventDescriber();
		
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.IUISemanticEvent)
         */
        public void notify(IUISemanticEvent event) {
//            String description = _describer.describe(event);
//            if (description != null)
//                System.out.println(description);
        	String msg = event == null ? "null event" : event.toString();
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, msg);
        }
        
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
         */
        public void notifyStart() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS,"recording started");
        }
        
        /**
         * @see com.windowtester.recorder.event.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
         */
        public void notifyAssertionHookAdded(String hookName) {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "hook added: " + hookName);
        }
        
        
        /*
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyPause()
         */
        public void notifyPause() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "recording paused");
        }
        
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
         */
        public void notifyStop() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "recording stopped");
        }

        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
         */
        public void notifyWrite() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "recording written");
        }

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
		 */
		public void notifyDispose() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "display disposed");
		}
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
		 */
		public void notifyRestart() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "recording restarted");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
		 */
		public void notifyError(RecorderErrorEvent event) {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "an internal error occured: " + event);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
		 */
		public void notifyTrace(RecorderTraceEvent event) {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "a trace event was sent: " + event);
		}

		public void notifyControllerStart(int port) {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "controller started on port " + port);
		}

		public void notifyDisplayNotFound() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "display disposed");
		}
		
		public void notifySpyModeToggle() {
            TraceHandler.trace(IEventRecorderPluginTraceOptions.RECORDER_EVENTS, "spy mode toggled");	
		}
	}

	/**
	 * @see com.windowtester.recorder.IEventRecorder#addEventFilter(com.windowtester.recorder.IEventFilter)
	 */
	public void addEventFilter(IEventFilter filter) {
		_recorder.addEventFilter(filter);
	}

	/**
	 * @see com.windowtester.recorder.IEventRecorder#removeEventFilter(com.windowtester.recorder.IEventFilter)
	 */
	public void removeEventFilter(IEventFilter filter) {
		_recorder.removeEventFilter(filter);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.IEventRecorder#isRecording()
	 */
	public boolean isRecording() {
		return _recorder.isRecording();
	}

    
}
