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
package com.windowtester.internal.runtime;


/**
 * A class whose primary job is to keep track of whether the runtime is being run 
 * in the context of the platform or not.
 * 
 */
public final class Platform {

	/** A flag that indicates whether we are running in the context of the Eclipse Platform
	 */
	static boolean IS_RUNNING;
	
	/**
	 * Check whether the Eclipse Platform is running.
	 * @return true if we are running in the context of the Eclipse platform
	 */
	public static boolean isRunning() {
		return IS_RUNNING;
	}
	

	
	
}
