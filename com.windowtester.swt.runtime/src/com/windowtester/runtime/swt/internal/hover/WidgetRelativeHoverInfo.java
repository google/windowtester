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
package com.windowtester.runtime.swt.internal.hover;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.WidgetLocator;

/**
 * Hover information that is relative to the location of a given widget.
 */
public class WidgetRelativeHoverInfo implements IHoverInfo {

	/** The associated widget */
	private final Widget _widget;
	/** The x, y offsets */
	private final int _x, _y;
	

	/**
	 * Create an instance.
	 * @param w the "parent" widget
	 * @param x the x offset
	 * @param y the y offset
	 */
	public WidgetRelativeHoverInfo(Widget w, int x, int y) {
		_widget = w;
		_x = x;
		_y = y;
	}
	
	
	/**
	 * Create an instance.
	 * @param w the "parent" widget
	 * @param offset the offset
	 */
	public WidgetRelativeHoverInfo(Widget w, Point offset) {
		this(w, offset.x, offset.y);
	}

	/**
	 * Get the widget target of this hover.
	 */
	public Widget getWidget() {
		return _widget;
	}
	
	/**
	 * Get the offset from the hover widget.
	 */
	public Point getOffset() {
		return new Point(_x, _y);
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.hover.IHoverInfo#getLocation()
	 */
	public Point getLocation() {
		Point location = getLocation(_widget);
		if (location == null)
			return null;
		return new Point(location.x + _x, location.y + _y);
	}
	
	
	///////////////////////////////////////////////////////////////////
	//
	// Location calculating helpers.
	//
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Get the absolute location of this widget.
	 * @param w - the widget in question
	 * @return the widget's point in space
	 */
	private static Point getLocation(final Widget w) {
		final Point[] point = new Point[1];
		
		/*
		 * Some conditions perform actions that cause the hover target to 
		 * be disposed.  In that case we pass back null.
		 */
		if (w == null || w.isDisposed())
			return null;
		//to be even more safe, we wrapper this call in case the widget gets
		//disposed in process
		try {
			w.getDisplay().syncExec(new Runnable() {
				public void run() {
					point[0] = WidgetLocator.getLocation(w);
				}
			});
		} catch (SWTError er) {
			// ignored -- null return will do
		} catch (SWTException ex) {
			// ignored -- null return will do
		}
		return point[0];
	}
	
}
