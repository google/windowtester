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

public class AbsolutePointHoverInfo implements IHoverInfo {
	
	private final int _x;
	private final int _y;
	
	public AbsolutePointHoverInfo(int x, int y) {
		_x = x;
		_y = y;
	}

	public AbsolutePointHoverInfo(Point point) {
		this(point.x, point.y);
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.hover.IHoverInfo#getLocation()
	 */
	public Point getLocation() {
		return new Point(_x, _y);
	}
	
	
}
