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

import java.util.ArrayList;
import java.util.List;

import com.windowtester.recorder.event.ISemanticEventListener;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.event.meta.RecorderErrorEvent;
import com.windowtester.recorder.event.meta.RecorderTraceEvent;

/**
 * A listener that caches recorded events to the console.
 * 
 */
public class EventCachingListener implements ISemanticEventListener {

		
		List events = new ArrayList();
		
		/** A describer used for stateful event descriptions */
		//private EventDescriber _describer = new EventDescriber();
		
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notify(com.windowtester.swt.event.model.IUISemanticEvent)
         */
        public void notify(IUISemanticEvent event) {
            events.add(event);
        }
        
        /**
         * @see com.windowtester.recorder.event.ISemanticEventListener#notifyAssertionHookAdded(java.lang.String)
         */
        public void notifyAssertionHookAdded(String hookName) {
        	events.add(new RecorderAssertionHookAddedEvent(hookName));
        }
        
        
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStart()
         */
        public void notifyStart() {
        }
        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyStop()
         */
        public void notifyStop() {
        }

        /* (non-Javadoc)
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyWrite()
         */
        public void notifyWrite() {
        }

        /*
         * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyPause()
         */
        public void notifyPause() {
        }
        
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyDispose()
		 */
		public void notifyDispose() {
		}
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyRestart()
		 */
		public void notifyRestart() {
		}

		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyError(com.windowtester.swt.event.model.RecorderErrorEvent)
		 */
		public void notifyError(RecorderErrorEvent event) {
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.swt.event.model.ISemanticEventListener#notifyTrace(com.windowtester.swt.event.model.RecorderTraceEvent)
		 */
		public void notifyTrace(RecorderTraceEvent event) {
		}

		/* (non-Javadoc)
		 * @see com.windowtester.recorder.event.ISemanticEventListener#notifySpyModeToggle()
		 */
		public void notifySpyModeToggle() {	
		}
		
		public IUISemanticEvent[] getEvents() {
			if (events == null)
				return new IUISemanticEvent[]{};
			return (IUISemanticEvent[]) events.toArray(new IUISemanticEvent[]{});
		}

		public void notifyControllerStart(int port) {
			// TODO Auto-generated method stub
			
		}

		public void notifyDisplayNotFound() {
			// TODO Auto-generated method stub
			
		}
	}