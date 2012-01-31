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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.finder.position.PositionHelper.PositionImpl;

public class PositionFinder {

	
	public static PositionImpl findIdentifyingPosition(Point point, Rectangle rect, Points neighbors) {
		
		PositionSpec candidate = PositionSpec.forPointRelativeTo(point, rect);
		
		Point[] points =  neighbors.excluding(point).toArray();
		for (int i = 0; i < points.length; i++) {
			Point toTest = points[i];
			PositionSpec newSpec = PositionSpec.forPointRelativeTo(toTest, rect);
			if (candidate == newSpec)
				return PositionSpec.NONE.getPosition();
		}
		
		return candidate.getPosition();
	}
		
	public static Position findPositionRelativeTo(Point point, Rectangle rect) {
		return PositionSpec.forPointRelativeTo(point, rect).getPosition();
	}

	public static Position findPositionRelativeTo(Point point, IFigure figure) {
		return findPositionRelativeTo(point, figure.getBounds());
	}
	
}
