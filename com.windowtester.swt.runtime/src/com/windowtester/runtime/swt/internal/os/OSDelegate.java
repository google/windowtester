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
package com.windowtester.runtime.swt.internal.os;


/**
 * Service for accessing the OS delegate appropriate for the current OS.
 */
public class OSDelegate {

	
	private static IOSDelegate current;
	
	public static IOSDelegate getCurrent() {
		if (current == null)
			current = doGetCurrent();
		return current;
	}

	private static IOSDelegate doGetCurrent() {
		IOSDelegate[] delegates = OSDelegateManager.getInstance().getContributedDelegates();
		if (delegates.length == 0)
			return new UnsupportedOSDelegate();
		if (delegates.length == 1)
			return delegates[0];
		return new MultipleRegisteredOSDelegates(delegates);
	}


	
}
