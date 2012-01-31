package com.windowtester.test.recorder.ui;

import junit.framework.TestCase;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.eclipse.ui.session.ISession;
import com.windowtester.eclipse.ui.session.ISessionMonitor;
import com.windowtester.eclipse.ui.session.SessionMonitor;
import com.windowtester.eclipse.ui.session.ISessionMonitor.ISessionListener;
import com.windowtester.recorder.ISemanticEventProvider;

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
public class SessionMonitorTest extends TestCase {

	
	public void testInitialState() {
		ISessionMonitor sm = getSM();
		assertOutOfSession(sm);
		sm.sessionStarted(stubSession());
		assertInSession(sm);
	}

	public void testSessionStart() {
		ISessionMonitor sm = getSM();
		sm.sessionStarted(stubSession());
		assertInSession(sm);		
	}
	
	public void testSessionEnd() {
		ISessionMonitor sm = getSM();
		sm.sessionStarted(stubSession());
		sm.sessionEnded(stubSession());
		assertOutOfSession(sm);
	}
	
	public void testSessionListenerGetstNotifications() {
		ISessionMonitor sm = getSM();
		final boolean[] started = new boolean[1];
		final boolean[] ended = new boolean[1];
		sm.addListener(new ISessionListener() {
			public void started(ISession session) {
				started[0] = true;
			}
			public void ended(ISession session) {
				ended[0] = true;
			}
		});
		assertFalse(started[0]);
		assertFalse(ended[0]);
		sm.sessionStarted(stubSession());
		assertTrue(started[0]);
		assertFalse(ended[0]);
		sm.sessionEnded(stubSession());
		assertTrue(started[0]);
		assertTrue(ended[0]);
	}
	
	
	
	private void assertInSession(ISessionMonitor sm) {
		assertTrue(sm.inSession());
	}

	private void assertOutOfSession(ISessionMonitor sm) {
		assertFalse(sm.inSession());
	}

	private ISession stubSession() {
		return new ISession() {
			public ISemanticEventProvider getRecorder() {
				return null;
			}
			public ExecutionProfile getExecutionProfile() {
				return null;
			}
		};
	}

	private ISessionMonitor getSM() {
		return new SessionMonitor();
	}
	
}
