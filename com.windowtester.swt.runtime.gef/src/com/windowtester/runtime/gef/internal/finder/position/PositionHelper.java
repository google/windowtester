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

import java.io.Serializable;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;

import com.windowtester.runtime.gef.Position;


public class PositionHelper {
	

	public static final PositionImpl TOP          = new PositionImpl(PositionConstants.NORTH);
	public static final PositionImpl BOTTOM       = new PositionImpl(PositionConstants.SOUTH);
	public static final PositionImpl LEFT         = new PositionImpl(PositionConstants.WEST);
	public static final PositionImpl RIGHT        = new PositionImpl(PositionConstants.EAST);
	public static final PositionImpl TOP_RIGHT    = new PositionImpl(PositionConstants.NORTH_EAST);
	public static final PositionImpl TOP_LEFT     = new PositionImpl(PositionConstants.NORTH_WEST);
	public static final PositionImpl BOTTOM_RIGHT = new PositionImpl(PositionConstants.SOUTH_EAST);
	public static final PositionImpl BOTTOM_LEFT  = new PositionImpl(PositionConstants.SOUTH_WEST);
	
	public static final PositionImpl CENTER       = new PositionImpl(PositionConstants.CENTER);
	public static final PositionImpl NONE         = new PositionImpl(PositionConstants.NONE);
	
	
	public static final PositionImpl NORTH        = TOP;
	public static final PositionImpl SOUTH        = BOTTOM;
	public static final PositionImpl EAST         = RIGHT;
	public static final PositionImpl WEST         = LEFT;
	public static final PositionImpl NORTH_EAST   = TOP_RIGHT;
	public static final PositionImpl NORTH_WEST   = TOP_LEFT;
	public static final PositionImpl SOUTH_EAST   = BOTTOM_RIGHT;
	public static final PositionImpl SOUTH_WEST   = BOTTOM_LEFT;
	
	
	public static class PositionImpl implements Position, Serializable {
		private static final long serialVersionUID = 4537721238211331273L;
		private final int positionConstant;
		
		private PositionImpl(int positionConstant) {
			this.positionConstant = positionConstant;
		}
		
		public int getDirectionConstant() {
			return positionConstant;
		}

		public boolean describesPointRelativeTo(Point point, Point origin) {
			return isDirectionOf(point, getDirectionConstant(), origin);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getOrientationString(getDirectionConstant());
		}
	}
	
	
	public static boolean isDirectionOf(Point point, int positionConstant, Point origin) {
		
		int position = getPositionRelativeTo(point, origin);
		
		//System.out.println(point + " is " + getOrientationString(position) + " of " + origin);
		
		return position == positionConstant;
	}

	public static int getPositionRelativeTo(Point point, Point origin) {
		int position = getLeftRightPositionRelativeTo(point, origin) | getUpDownPositionRelativeTo(point, origin);
		if (position == PositionConstants.NONE)
			return PositionConstants.CENTER;
		return position;
	}

	protected static int getUpDownPositionRelativeTo(Point point, Point origin) {
		int dy = point.y - origin.y;
		if (dy == 0)
			return PositionConstants.NONE;
		if (dy < 0)
			return PositionConstants.NORTH;
		return PositionConstants.SOUTH;
	}

	protected static int getLeftRightPositionRelativeTo(Point point, Point origin) {
		int dx = point.x - origin.x;
		if (dx == 0)
			return PositionConstants.NONE;
		if (dx < 0)
			return PositionConstants.WEST;
		return PositionConstants.EAST;
	}


	public static Position getPositionForConstant(int locationConstant) {
		switch(locationConstant) {
			case PositionConstants.NORTH      : return TOP; 
			case PositionConstants.SOUTH      : return BOTTOM;
			case PositionConstants.EAST       : return RIGHT; 
			case PositionConstants.WEST       : return LEFT;
			case PositionConstants.NORTH_EAST : return TOP_RIGHT; 
			case PositionConstants.SOUTH_EAST : return BOTTOM_RIGHT;
			case PositionConstants.NORTH_WEST : return TOP_LEFT; 
			case PositionConstants.SOUTH_WEST : return BOTTOM_LEFT;
			case PositionConstants.CENTER     : return CENTER;
			case PositionConstants.NONE       : return NONE;
		}
		return null;
	}
	
	
	public static String getOrientationString(int locationConstant) {
		switch(locationConstant) {
			case PositionConstants.NORTH      : return "TOP"; 
			case PositionConstants.SOUTH      : return "BOTTOM";
			case PositionConstants.EAST       : return "RIGHT"; 
			case PositionConstants.WEST       : return "LEFT";
			case PositionConstants.NORTH_EAST : return "TOP_RIGHT"; 
			case PositionConstants.SOUTH_EAST : return "BOTTOM_RIGHT";
			case PositionConstants.NORTH_WEST : return "TOP_LEFT"; 
			case PositionConstants.SOUTH_WEST : return "BOTTOM_LEFT";
			case PositionConstants.CENTER     : return "CENTER";
			case PositionConstants.NONE       : return "NONE";
		}
		return "<unrecognized>";
	}
	

	public static int getPositionConstant(Position position) {
		Class cls = position.getClass();
		Class positionClass = PositionImpl.class;
		if (cls != positionClass) {
			throw new IllegalArgumentException("Position type: " + position + " not recognized ---- only instances of " + positionClass + " allowed");
		}
		return ((PositionImpl)position).getDirectionConstant();
	}
	
}
