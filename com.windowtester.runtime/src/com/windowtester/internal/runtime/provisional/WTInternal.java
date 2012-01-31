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
package com.windowtester.internal.runtime.provisional;

import com.windowtester.runtime.WT;

/**
 * A place-holder for constants that are not API.  If they become API,
 * they should be moved to {@link WT}.
 */
public interface WTInternal {

	// Location constants (to be) used in XYLocator and SWTMouseOperation
	public static final int CENTER = 0;
	public static final int LEFT = 1 << 0;
	public static final int RIGHT = 1 << 1;
	public static final int TOP = 1 << 2;
	public static final int BOTTOM = 1 << 3;
	
	// Convenience constants
	public static final int TOPLEFT = TOP | LEFT;
	public static final int TOPRIGHT = TOP | RIGHT;
	public static final int BOTTOMLEFT = BOTTOM | LEFT;
	public static final int BOTTOMRIGHT = BOTTOM | RIGHT;
}
