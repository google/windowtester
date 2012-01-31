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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.selector.UIProxy;

/**
 * A factory for generating {@link com.windowtester.runtime.swt.internal.hover.IHoverInfo} instances.
 */
public class HoverInfo {

	/**
	 * Create an absolute hover info instance.
	 * @param x the absolute x location
	 * @param y the absolute y location
	 * @return the associated <code>IHoverInfo</code> instance
	 */
	public static IHoverInfo getAbsolute(int x, int y) {
		return new AbsolutePointHoverInfo(x,y);
	}

	/**
	 * Create an absolute hover info instance.
	 * <p>
	 * If the point is <code>null</code>, the current cursor position is 
	 * used.
	 * @return the associated <code>IHoverInfo</code> instance
	 */
	public static IHoverInfo getAbsolute(Point location) {
		if (location == null) {
			location = (Point) DisplayExec.sync(new RunnableWithResult() {
				public Object runWithResult() {
					return Display.getDefault().getCursorLocation();
				}
			});
		}
		//final fallback:
		if (location == null)
			location = new Point(0,0);
		
		return getAbsolute(location.x, location.y);
	}
	
	/**
	 * Create a widget-relative hover info instance.
	 * @param w the "parent" widget
	 * @param x the x offset
	 * @param y the y offset
	 * @return the associated <code>IHoverInfo</code> instance
	 */
	public static IHoverInfo getRelative(Widget w, int x, int y) {
		return new WidgetRelativeHoverInfo(w, x, y);
	}

	public static IHoverInfo getRelativeToCenter(Widget w) {
		Rectangle rect = UIProxy.getBounds(w);
		return getRelative(w, rect.width/2, rect.height/2);
	}

	

}
