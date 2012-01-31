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
package com.windowtester.runtime.gef;

import com.windowtester.runtime.gef.internal.finder.position.PositionHelper;



/**
 * Type constants representing cardinal directions and relative positions.
 * <p>
 * NOTE: this is not meant to be subclassed by clients.
 * @noimplement
 */
public interface Position {

	//TODO: should we push this up into core runtime?
	
	
	public static final Position TOP          = PositionHelper.TOP;
	public static final Position BOTTOM       = PositionHelper.BOTTOM;
	public static final Position LEFT         = PositionHelper.LEFT;
	public static final Position RIGHT        = PositionHelper.RIGHT;
	public static final Position TOP_RIGHT    = PositionHelper.TOP_RIGHT;
	public static final Position TOP_LEFT     = PositionHelper.TOP_LEFT;
	public static final Position BOTTOM_RIGHT = PositionHelper.BOTTOM_RIGHT;
	public static final Position BOTTOM_LEFT  = PositionHelper.BOTTOM_LEFT;
	
	public static final Position CENTER       = PositionHelper.CENTER;
	
	public static final Position NORTH        = TOP;
	public static final Position SOUTH        = BOTTOM;
	public static final Position EAST         = RIGHT;
	public static final Position WEST         = LEFT;
	public static final Position NORTH_EAST   = TOP_RIGHT;
	public static final Position NORTH_WEST   = TOP_LEFT;
	public static final Position SOUTH_EAST   = BOTTOM_RIGHT;
	public static final Position SOUTH_WEST   = BOTTOM_LEFT;
}
