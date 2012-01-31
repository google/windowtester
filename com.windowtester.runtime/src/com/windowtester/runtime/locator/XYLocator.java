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
package com.windowtester.runtime.locator;

import java.io.Serializable;

import com.windowtester.internal.runtime.provisional.IAreaLocator;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.WT;

/**
 * A locator specifying an x,y position. If a locator is specified in 
 * the constructor and, during playback, this locator resolves to a
 * widget, the x, y coordinates are used as an offset from the top
 * left corner of the resolved widget.
 * <p>
 * If no locator is specified or the locator is null
 * then the x,y coordinates are relative to the screen itself. 
 */
public class XYLocator
	implements IXYLocator, Serializable
{
	
	/* Scratched old docs:
	 * 
	 * If a locator is specified in the constructor and, during playback, this
	 * locator resolves to an instance of {@link IAreaLocator}, then the
	 * relative flags are used to determine how the x,y coordinates are relative
	 * to the specified area. If locator resolves to an instance of IXYLocator
	 * rather than IAreaLocator, then the relative flags are ignored.
	 */
	
	
	private static final long serialVersionUID = -916067593367432955L;

	/**
	 * A locator to which the x,y coordinates are relative.
	 */
	private final ILocator locator;
	
	private final int x;
	private final int y;

	/**
	 * If, during playback, the locator resolves to an instance of {@link IAreaLocator},
	 * then these flags are used to determine how the x,y location is relative to the specified area.
	 * This can have values of
	 * 		{@link WT#LEFT}, {@link WT#CENTER} or {@link WT#RIGHT}
	 * or'd with
	 * 		{@link WT#TOP}, {@link WT#CENTER} or {@link WT#BOTTOM}
	 * indicating how the x, y coordinates are relative to the specified area.
	 * If locator is <code>null</code> or resolves to an instance of IXYLocator 
	 * rather than IAreaLocator, then this field is ignored.
	 * 
	 * NOTE: UNUSED in 2.0 
	 */
	//private final int relative;
	
	/**
	 * Construct a new locator specifying an x,y position relative to the screen itself
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public XYLocator(int x, int y) {
		this(null, x, y, 0);
	}
	
	/**
	 * Construct a new locator specifying an x,y position relative to the specified locator
	 * 
	 * @param locator A locator to which the x,y coordinates are relative
	 * 			or <code>null</code> if the x,y coordinates are relative to the screen
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param relative If, during playback, the locator resolves to an instance of {@link IAreaLocator},
	 * 			then these flags are used to determine how the x,y location is relative to the specified area.
	 * 			This can have values of
	 * 				{@link WT#LEFT}, {@link WT#CENTER} or {@link WT#RIGHT}
	 * 			or'd with
	 * 				{@link WT#TOP}, {@link WT#CENTER} or {@link WT#BOTTOM}
	 * 			indicating how the x, y coordinates are relative to the specified area.
	 * 
	 * NOTE: relative locators are being hidden for the 2.0 release (to be reviewed for later)
	 */
	private /* hidden */ XYLocator(ILocator locator, int x, int y, int relative) {
		this.locator = locator;
		this.x = x;
		this.y = y;
		//not used in 2.0
		//this.relative = relative;
	}

	
	/**
	 * Construct a new locator specifying an x,y position relative to the top left corner of the located widget.
     *
	 * @param locator A locator to which the x,y coordinates are relative
	 * 			or <code>null</code> if the x,y coordinates are relative to the screen
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public XYLocator(ILocator locator, int x, int y) {
		this(locator, x, y, WTInternal.TOP | WTInternal.LEFT);
	}
	
	
	public ILocator locator() {
		return locator;
	}
	
	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

//	public int relative() {
//		return relative;
//	}
}
