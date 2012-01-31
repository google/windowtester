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
package com.windowtester.eclipse.ui.session;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.recorder.ISemanticEventProvider;
import com.windowtester.ui.internal.core.recorder.NullRecorder;

public class SessionMonitor implements ISessionMonitor {

	public static final ISession NULL_SESSION = new ISession() {
		public ISemanticEventProvider getRecorder() {
			return new NullRecorder();
		}
		public ExecutionProfile getExecutionProfile() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	private ISession session     = nullSession(); 
	private final List<ISessionListener> listeners = new ArrayList<ISessionListener>();
	
	public ISession getCurrent() {
		return session;
	}

	public boolean inSession() {
		return getCurrent() != nullSession();
	}

	public void sessionEnded(ISession session) {
		for (ISessionListener listener: getListeners()) {
			listener.ended(session);
		}
		setSession(nullSession());
	}

	private static ISession nullSession() {
		return NULL_SESSION;
	}

	public void sessionStarted(ISession session) {
		setSession(session);
		for (ISessionListener listener: getListeners()) {
			listener.started(session);
		}
	}

	private void setSession(ISession session) {
		assertNotNull(session);
		this.session = session;
	}

	public void addListener(ISessionListener listener) {
		assertNotNull(listener);
		getListeners().add(listener);
	}

	private void assertNotNull(Object param) {
		if (param == null)
			throw new IllegalArgumentException("argument cannot be null");
	}

	public void removeListener(ISessionListener listener) {
		assertNotNull(listener);
		getListeners().remove(listener);
	}
	
	private List<ISessionListener> getListeners() {
		return listeners;
	}
	
}
