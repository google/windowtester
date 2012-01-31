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
package com.windowtester.recorder.event.user;

/**
 * An interface to mark selection events that can be masked with a mouse mask modifier
 * such as a SHIFT or CTRL.
 *
 */
public interface IMaskable {

	/**
	 * Get the mouse mask represented as a String.  This should really be an int but it
	 * is a String for legacy reasons.
	 */
	String getMask();
	
}
