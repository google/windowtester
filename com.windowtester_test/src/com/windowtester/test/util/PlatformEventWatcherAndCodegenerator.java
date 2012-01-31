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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.codegen.ICodeGenerator;
import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.swt.SWTV2TestGenerator;
import com.windowtester.eclipse.ui.inspector.Inspector;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;
import com.windowtester.recorder.event.user.IWidgetDescription;
import com.windowtester.recorder.event.user.SemanticEventAdapter;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.swt.event.model.SWTSemanticEventFactory;
import com.windowtester.swt.event.model.factory.SWTSemanticEventFactoryImplV2;
import com.windowtester.swt.event.recorder.SWTSemanticEventRecorder;


public class PlatformEventWatcherAndCodegenerator {

	
	public static enum API {
		V1,
		V2;
	}

	
	static class EventCachingListener implements ISemanticEventListener {

		List<ISemanticEvent> _events = new ArrayList<ISemanticEvent>();

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.ISWTSemanticEvent)
		 */
		public void notify(IUISemanticEvent event) {
			System.out.println(event);
			getEvents().add(event);
		}

		/**
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
		 */
		public void notifyAssertionHookAdded(String hookName) {
			getEvents().add(new RecorderAssertionHookAddedEvent(hookName));
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
		 */
		public void notifyStart() {
			System.out.println("recording started");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
		 */
		public void notifyStop() {
			System.out.println("recording stopped");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
		 */
		public void notifyWrite() {
			System.out.println("recording written");
		}

		/*
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyPause()
		 */
		public void notifyPause() {
			System.out.println("recording paused");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
		 */
		public void notifyDispose() {
			System.out.println("display disposed");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
		 */
		public void notifyRestart() {
			System.out.println("recording restarted");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
		 */
		public void notifyError(RecorderErrorEvent event) {
			System.out.println("an internal error occured: " + event);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
		 */
		public void notifyTrace(RecorderTraceEvent event) {
			System.out.println("a trace event was sent: " + event);
		}

		public List<ISemanticEvent> getEvents() {
			if (_events == null)
				_events = new ArrayList<ISemanticEvent>();
			return _events;
		}

		public void clear() {
			_events = null;
		}

		public void notifyControllerStart(int port) {
			//no-op
		}

		public void notifyDisplayNotFound() {
			//no-op
		}
		public void notifySpyModeToggle() {
			//no-op
		}
	}
	
	private Shell shell;
	private EventCachingListener cache;
	private final API api;

	//this recorder gets shared between test runs
	SWTSemanticEventRecorder recorder;
	
	private String testName;
	private String testPackageName;
	
	
	//for testing the inspector
	private boolean inspectorEnabled;
	
	
	/**
	 * Create an instance.
	 * @param shell - the main application shell to watch
	 */
	public PlatformEventWatcherAndCodegenerator(API version) {
		api = version;
		final Display d = Display.getDefault();
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				setShell(d.getActiveShell());				
			}
		});
	
	}
	
	/**
	 * Watch for events and cache them until {@link #codegen()} is called.
	 * Calling this method first clears the cache.
	 */
	public void watch() {
		//start by clearing the cache (each call starts a new session).
		getCache().clear();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				record();				
			}
		});
	}
	
	public String codegen() {
		return codegen(getCache());
	}
	
	public API getAPI() {
		return api;
	}
	
	public List<ISemanticEvent> getEvents() {
		if (getCache() == null)
			return new ArrayList<ISemanticEvent>();
		return getCache().getEvents();
	}

	public void stop() {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				getRecorder().stop();
			}
		});
	}
	

	private String codegen(EventCachingListener cache) {
		stop(); //make sure events are flushed
		List<ISemanticEvent> events = cache.getEvents();
		return getCodeGenerator().generate(new EventStream(events)); 
	}


	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Configuration options
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public PlatformEventWatcherAndCodegenerator withInspector() {
		inspectorEnabled = true;
		return this;
	}
	
	
	//get the codegenerator
	private ICodeGenerator getCodeGenerator() {		
//		return (getAPI() == API.V1) ? new CodeGenerator(new SWTTestCaseBuilder(getTestName(), getTestPackageName(), null, null))
//			: new SWTV2TestGenerator(getTestName(), getTestPackageName(), null, (String [])null);
		return new SWTV2TestGenerator(getTestName(), getTestPackageName(), null, (String [])null);
		
	}

	//get a configured recorder instance
	private SWTSemanticEventRecorder getRecorder() {
		if (recorder == null) {
			recorder = new SWTSemanticEventRecorder(Display.getDefault());
			if (getAPI() == API.V2) {
				// set new API strat <-- this is possibly unnecessary
				SWTSemanticEventFactory.setStrategy(new SWTSemanticEventFactoryImplV2());			}	
		}
		return recorder;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////

	private void record() {
		SWTSemanticEventRecorder recorder = getRecorder();
		recorder.addListener(getCache());
		if (inspectorEnabled) {
			recorder.addListener(new SemanticEventAdapter() {
				@Override
				public void notify(IUISemanticEvent event) {

					System.out.println("notified for: " + event);

					if (!(event instanceof SemanticWidgetInspectionEvent))
						return;
					final IWidgetDescription inspection = (IWidgetDescription) event;
					System.out.println(inspection);
					DisplayReference.getDefault().execute(new VoidCallable() {
						public void call() throws Exception {
							Inspector.openPopup(inspection);
						}
					});
				}
			});
		}
			
		
		recorder.start();
	}

    protected void setCache(EventCachingListener cache) {
		this.cache = cache;
	}

	private EventCachingListener getCache() {
		if (cache == null)
			cache = new EventCachingListener();
		return cache;
	}

	private void setShell(Shell shell) {
		this.shell = shell;
	}

	protected Shell getShell() {
		return shell;
	}




	/**
     * A listener that reports recorded events to the console.  (Useful for debugging.)
     * 
     */
	static class ConsoleReportingListener implements ISemanticEventListener {

		/** A describer used for stateful event descriptions */
		//private EventDescriber _describer = new EventDescriber();
		
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.ISWTSemanticEvent)
         */
        public void notify(IUISemanticEvent event) {
//            String description = _describer.describe(event);
//            if (description != null)
//                System.out.println(description);
            System.out.println("got event" + event);
        }
        
        /**
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
         */
        public void notifyAssertionHookAdded(String hookName) {
            System.out.println("hook added: " + hookName);
        }
        
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
         */
        public void notifyStart() {
            System.out.println("recording started");
        }
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
         */
        public void notifyStop() {
            System.out.println("recording stopped");
        }

        /**
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyPause()
         */
        public void notifyPause() {
        	 System.out.println("recording paused");
        }
        
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
         */
        public void notifyWrite() {
            System.out.println("recording written");
        }

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
		 */
		public void notifyDispose() {
			 System.out.println("display disposed");
		}
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
		 */
		public void notifyRestart() {
			System.out.println("recording restarted");
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
		 */
		public void notifyError(RecorderErrorEvent event) {
			System.out.println("an internal error occured: " + event);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
		 */
		public void notifyTrace(RecorderTraceEvent event) {
			System.out.println("a trace event was sent: " + event);
		}

		public void notifyControllerStart(int port) {
			System.out.println("controller started on: " + port);
		}

		public void notifyDisplayNotFound() {
			System.out.println("display not found");
		}
		
		public void notifySpyModeToggle() {
			System.out.println("spy mode toggled");	
		}
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public void setTestPackageName(String packageName) {
		this.testPackageName = packageName;
	}
	
	public String getTestPackageName() {
		return testPackageName;
	}
	
}
