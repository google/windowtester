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
package org.eclipse.actf.accservice.core.win32.msaa;


public class MsaaWindowService {

	public native static int[] internalGetWindowsList ();
	public static native boolean internalCoInitialize();
	public static native boolean internalCoUnInitialize();
	
	
	public MsaaWindowService() throws InitializationException {
		MsaaLibraryManager.load();
	}
	
	public int getProcessId (Object window) {
		int pid = -1;
		if (window instanceof Integer) {
			pid = internalGetProcessId(((Integer) window).intValue());
		}
		return pid;
	}

	protected native int internalGetProcessId (int hwnd);

	
	public int getCurrentProcessId(){
		return internalGetCurrentProcessId();
	}

	protected native int internalGetCurrentProcessId();
	
}
