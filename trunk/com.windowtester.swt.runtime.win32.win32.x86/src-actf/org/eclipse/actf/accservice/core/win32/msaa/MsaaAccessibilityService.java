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





public class MsaaAccessibilityService {


//	private IWindowService _windowService = new MsaaWindowService();
//	private IAccessibilityEventService _accEventService = new MsaaAccessibilityEventService(_windowService);
	
	
	public MsaaAccessibilityService() throws InitializationException {
		MsaaLibraryManager.load();
	}
	
	public MsaaAccessible createAccessibleElement(Integer handle, Object[] params)
			throws Exception {
		MsaaAccessibilityService.internalCoInitialize();
		int hwnd = handle.intValue();
		int childId = -1;
		if (params != null) {
			int length = params.length;

			if (length == 1 && (params[0] instanceof Integer)) {
				childId = ((Integer) params[0]).intValue();
			}
		}
		return new MsaaAccessible(hwnd, childId);
	}

//	public IAccessibilityEventService getAccessibilityEventService() {
//		return _accEventService;
//	}
//
//	public IWindowService getWindowService() {
//		return _windowService;
//	}

	
	protected static native boolean internalCoInitialize();
	protected static native boolean internalCoUnInitialize();


}
