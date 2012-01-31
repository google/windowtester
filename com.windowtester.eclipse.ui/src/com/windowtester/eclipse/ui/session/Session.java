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

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.recorder.ISemanticEventProvider;

/**
 * A factory for sessions.
 */
public class Session {

	static interface ILocalSession extends ISession{}
	static interface IRemoteSession extends ISession {}	
	
	private static class LocalSession implements ILocalSession {
		public ISemanticEventProvider getRecorder() {
			// TODO Auto-generated method stub
			return null;
		}
		public ExecutionProfile getExecutionProfile() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private static class RemoteSession implements IRemoteSession {
		
		private final ISemanticEventProvider _provider;
		private final ExecutionProfile _executionProfile;

		private RemoteSession(ISemanticEventProvider provider, ExecutionProfile executionProfile) {
			_provider = provider;
			_executionProfile = executionProfile;
		}
		
		public ISemanticEventProvider getRecorder() {
			return _provider;
		}
		
		public ExecutionProfile getExecutionProfile() {
			return _executionProfile;
		}
	}
	
	
	public static ISession newLocal() {
		return new LocalSession();
	}
	
	public static ISession newRemote(ISemanticEventProvider semanticEventProvider, ExecutionProfile executionProfile) {
		return new RemoteSession(semanticEventProvider, executionProfile); 
	}
	
	public static boolean isLocal(ISession session) {
		return session instanceof ILocalSession;
	}
	
	public static boolean isRemote(ISession session) {
		return session instanceof IRemoteSession;
	}
	
	
}
