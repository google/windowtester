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
package com.windowtester.runtime.gef.internal.finder.position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.gef.internal.finder.position.PositionHelper.PositionImpl;


public class PositionSpec {

	private static final Collection POSITIONS = new ArrayList();
	
	
	public static class CenterRegion {
		
		private static final double RADIUS_FACTOR = 0.3;
		private final Rectangle rectangle;

		public CenterRegion(Rectangle rect) {
			this.rectangle = rect;
		}

		public static CenterRegion forRect(Rectangle rect) {
			return new CenterRegion(rect);
		}
		
		public boolean contains(Point pt) {
			Point center = rectangle.getCenter();
			double maxDist = deriveRadiusForCenter(rectangle);
			return center.getDistance(pt) <= maxDist;
		}
		
		public static double deriveRadiusForCenter(Rectangle rect) {
			int minDimension = Math.min(rect.height, rect.width);
			return Math.floor(minDimension * RADIUS_FACTOR);
		}
		
	}

	public static final PositionSpec TOP          = new PositionSpec(PositionHelper.TOP);
	public static final PositionSpec BOTTOM       = new PositionSpec(PositionHelper.BOTTOM);
	public static final PositionSpec LEFT         = new PositionSpec(PositionHelper.LEFT);
	public static final PositionSpec RIGHT        = new PositionSpec(PositionHelper.RIGHT);
	public static final PositionSpec TOP_RIGHT    = new PositionSpec(PositionHelper.TOP_RIGHT);
	public static final PositionSpec BOTTOM_RIGHT = new PositionSpec(PositionHelper.BOTTOM_RIGHT);
	public static final PositionSpec TOP_LEFT     = new PositionSpec(PositionHelper.TOP_LEFT);
	public static final PositionSpec BOTTOM_LEFT  = new PositionSpec(PositionHelper.BOTTOM_LEFT);
	public static final PositionSpec NONE         = new PositionSpec(PositionHelper.NONE);
	
	public static final PositionSpec CENTER       = new PositionSpec(PositionHelper.CENTER) {
		public boolean describesPointRelativeTo(Point point, Rectangle rect) {
			return inCenterRegion(point, rect);
		}
	};
	
	
	private final PositionImpl position;
	
	public PositionSpec(PositionImpl position) {
		this.position = position;
		POSITIONS.add(this);
	}
	
	public PositionImpl getPosition() {
		return position;
	}
	
	
	public boolean describesPointRelativeTo(Point point, Rectangle rect) {
		return !inCenterRegion(point, rect) && position.describesPointRelativeTo(point, rect.getCenter());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PositionSpec("+ position.toString() +")";
	}
	
	public static PositionSpec forPointRelativeTo(Point point, Rectangle rect) {
		for (Iterator iter = iterator(); iter.hasNext(); ) {
			PositionSpec spec = (PositionSpec)iter.next();
			if (spec.describesPointRelativeTo(point, rect))
				return spec;
		}
		return NONE;
	}
	
	public static boolean inCenterRegion(Point pt, Rectangle rect) {
		return CenterRegion.forRect(rect).contains(pt);
	}
	
	private static Iterator iterator() {
		return POSITIONS.iterator();
	}
	
}
