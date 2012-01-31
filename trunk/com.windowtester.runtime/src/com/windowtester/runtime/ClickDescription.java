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
package com.windowtester.runtime;

import com.windowtester.internal.runtime.MouseConfig;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;

/**
 * Basic {@link IClickDescription} implementation.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ClickDescription implements IClickDescription {

	//in the future this may be retrieved from the XYLocator
	private static final int DEFAULT_RELATIVITY = WTInternal.TOP | WTInternal.LEFT;

	private int _clicks;
	private int _relative;
	private int _x;
	private int _y;
	private int _modifierMask;

	public static ClickDescription forClick(int clicks){
		return new ClickDescription(clicks);
	}
	
	public ClickDescription atXY(int x, int y){
		_x = x;
		_y = y;
		return this;
	}
	
	public ClickDescription relativeTo(int relativity){
		_relative = relativity;
		return this;
	}
	
	public ClickDescription withModifiers(int modifierMask){
		_modifierMask = modifierMask;
		return this;
	}
	
	
	public static IClickDescription singleClickAtXY(int x, int y) {
		return new ClickDescription(1, DEFAULT_RELATIVITY, x, y, MouseConfig.PRIMARY_BUTTON);
	}
	
	public static IClickDescription singleClick() {
		return new ClickDescription(1, DEFAULT_CENTER_CLICK, DEFAULT_CENTER_CLICK, DEFAULT_CENTER_CLICK, MouseConfig.PRIMARY_BUTTON);
	}
	
	public static IClickDescription create(int clicks, XYLocator xy, int modifierMask) {
		//sanity check mask
		modifierMask = fixMask(modifierMask);		
		return new ClickDescription(clicks, DEFAULT_RELATIVITY, xy.x(), xy.y(), modifierMask);
	}
	
	private static int fixMask(int modifierMask) {
		/*
		 * To make life easier for clients, omitted mouse buttons are assumed
		 * to be primary.
		 */
		if (MouseConfig.getButton(modifierMask) == MouseConfig.UNSPECIFIED)
			modifierMask |= WT.BUTTON1; // note this is the same as SWT.BUTTON1
		return modifierMask;
	}

	public static IClickDescription create(int clickCount, ILocator locator, int buttonMask) {
		//sanity check mask
		buttonMask = fixMask(buttonMask);
		if (locator instanceof XYLocator)
			return create(clickCount, (XYLocator)locator, buttonMask);
		if (locator instanceof IWidgetLocator) //TODO: change default sentinel to something more expressive than -1s...
			return new ClickDescription(clickCount, DEFAULT_CENTER_CLICK, DEFAULT_CENTER_CLICK, DEFAULT_CENTER_CLICK, buttonMask);
		throw new IllegalArgumentException();
	}

	public static ClickDescription copy(IClickDescription click){
		return new ClickDescription(click.clicks()).relativeTo(click.relative()).atXY(click.x(), click.y()).withModifiers(click.modifierMask());
	}
	
	private ClickDescription(int clicks) {
		_clicks = clicks;
	}
	
	ClickDescription(int clicks, int relative, int x, int y, int modifierMask) {
		_clicks = clicks;
		_relative = relative;
		_x = x;
		_y = y;
		_modifierMask = modifierMask;	
	}
	
	public int clicks() {
		return _clicks;
	}

	public int relative() {
		return _relative;
	}

	public int x() {
		return _x;
	}

	public int y() {
		return _y;
	}
	
	public int modifierMask() {
		return _modifierMask;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IClickDescription#isDefaultCenterClick()
	 */
	public boolean isDefaultCenterClick() {
		return _relative == DEFAULT_CENTER_CLICK;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		//TODO: mods and relativity
		return "ClickDescription[clicks: " + clicks() + ", x: " + x() + ", y: " + y() + "]";
	}

}
