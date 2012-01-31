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
package com.windowtester.internal.runtime.junit.core.launcher;

/**
 * A listener on a {@link IApplicationLauncher}.
 *
 */
public interface ILaunchListener {

	/**
	 * Called before launch.
	 */
	void preFlight();

	/**
	 * Called after launch.
	 */
	void postFlight();
	
}
