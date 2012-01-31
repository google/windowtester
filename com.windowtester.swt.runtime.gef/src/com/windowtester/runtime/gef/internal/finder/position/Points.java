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
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * A smart collection of points.
 */
public class Points {

	private final List points = new ArrayList();
	
	public static Points forArray(Point[] points) {
		Points pts = new Points();
		for (int i = 0; i < points.length; i++) {
			pts.add(points[i]);
		}
		return pts;
	}
	
	public Points add(Point point) {
		points.add(point);
		return this;
	}
	
	public Points excluding(Point point) {
		points.remove(point);
		return this;
	}
	
	public Point[] toArray() {
		return (Point[]) points.toArray(new Point[]{});
	}

	public static Points forAnchors(ConnectionAnchor[] anchors) {
		Points points = new Points();
		for (int i = 0; i < anchors.length; i++) {
			points.add(forAnchor(anchors[i]));
		}
		return points;
	}
	
	public static Point forAnchor(ConnectionAnchor anchor) {
		return anchor.getReferencePoint();
	}

	public static Point forFigure(IFigure figure) {
		return figure.getBounds().getCenter();
	}

	public static Points forFigures(IFigure[] figures) {
		Points points = new Points();
		for (int i = 0; i < figures.length; i++) {
			points.add(forFigure(figures[i]));
		}
		return points;
	}
	
	
}
