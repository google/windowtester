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
package com.windowtester.internal.runtime.event;

/**
 * Event style bit constants.
 */
public class StyleBits {

	/**
	 * Pull down menu selection.
	 */
	public static final int PULL_DOWN = 1 << 4;

	public static boolean isPullDown(int style) {
		return (style & PULL_DOWN) == PULL_DOWN;
	}

}
