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
package com.windowtester.runtime.swt.internal.operation;



import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * Represents a widget relative location
 */
public class SWTWidgetLocation<T extends ISWTWidgetReference<?>> extends SWTLocation
{
	protected final T widgetRef;

	/**
	 * Construct a new instance representing a location relative to the specified widget
	 * 
	 * @param widgetRef the reference to the SWT widget to which the location is relative
	 *            (not <code>null</code>)
	 * @param relative how the the location is relative to the widget's bounding box (
	 *            {@link WTInternal#TOPLEFT}, {@link WTInternal#RIGHT}, ...)
	 */
	public SWTWidgetLocation(T widgetRef, int relative) {
		super(relative);
		this.widgetRef = widgetRef;
	}

	/**
	 * Construct a new instance representing a location relative to the specified widget
	 * with a default location in the center of the widget.
	 * 
	 * @param widgetRef the reference to the SWT widget to which the location is relative
	 *            (not <code>null</code>)
	 * @param click the click description (not <code>null</code>)
	 */
	public static SWTWidgetLocation<ISWTWidgetReference<?>> withDefaultCenter(ISWTWidgetReference<?> widgetRef, IClickDescription click)
	{
		SWTWidgetLocation<ISWTWidgetReference<?>> location = new SWTWidgetLocation<ISWTWidgetReference<?>>(widgetRef,
			click.isDefaultCenterClick() ? WTInternal.CENTER : WTInternal.TOPLEFT);
		if (!click.isDefaultCenterClick())
			location.offset(click.x(), click.y());
		return location;
	}

	
	/**
	 * Construct a new instance representing a location relative to the specified widget
	 * with a default location in the center of the widget.
	 * 
	 * @param widgetRef the reference to the SWT widget to which the location is relative
	 *            (not <code>null</code>)
	 * @param click the click description (not <code>null</code>)
	 */
	public static SWTWidgetLocation<ISWTWidgetReference<?>> withDefaultCenter(ISWTWidgetReference<?> widgetRef, IClickDescription click,Point offset)
	{
		SWTWidgetLocation<ISWTWidgetReference<?>> location = new SWTWidgetLocation<ISWTWidgetReference<?>>(widgetRef,
			click.isDefaultCenterClick() ? WTInternal.CENTER : WTInternal.TOPLEFT);
		if (!click.isDefaultCenterClick())
			location.offset(click.x()+ offset.x, click.y()+ offset.y);
		return location;
	}
	
	
	/**
	 * Construct a new instance representing a location relative to the specified widget
	 * with a default location offset 3 pixels in both dimensions from the widget's top
	 * left corner.
	 * 
	 * @param widgetRef the reference to the SWT widget to which the location is relative
	 *            (not <code>null</code>)
	 * @param click the click description (not <code>null</code>)
	 */
	public static SWTWidgetLocation<ISWTWidgetReference<?>> withDefaultTopLeft33(IWidgetReference widget, IClickDescription click)
	{
		SWTWidgetLocation<ISWTWidgetReference<?>> location2 = new SWTWidgetLocation<ISWTWidgetReference<?>>(
			(ISWTWidgetReference<?>) widget, WTInternal.TOPLEFT);
		if (click.isDefaultCenterClick())
			location2.offset(3, 3);
		else
			location2.offset(click.x(), click.y());
		return location2;
	}

	//=======================================================================
	// Internal

	/**
	 * Answer the reference to the widget to which the location is relative
	 * 
	 * @return the widget reference (not <code>null</code>)
	 */
	protected T getWidgetRef() {
		return widgetRef;
	}

	/**
	 * Calculate the client area of the widget and convert that from local coordinates to
	 * global coordinates (also known as display coordinates).
	 * 
	 * @return the client area of the widget in display coordinates
	 */
	protected Rectangle getDisplayBounds() {
		return widgetRef.getDisplayBounds();
	}

	public String toString() {
		return getClass().getName() + "{" + relative + "," + widgetRef + "}";
	}
}
