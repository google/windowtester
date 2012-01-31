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
package com.windowtester.runtime.gef.internal.locator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.internal.runtime.locator.IAdaptableWidgetLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Finds figure matched by a given matcher that is nearest to a given orientation 
 * relative to a provided bounding box.
 */
public class ByOrientationLocator extends FigureLocatorDelegate implements PositionConstants  {

	private static final long serialVersionUID = 39201753344418894L;


	public static interface IBoundsProvider {
		Rectangle getBounds(IUIContext ui) throws WidgetSearchException;
	}

	
	public static class PositionHelper {
		
		static class DistanceComparator implements Comparator {
			public int compare(Object arg0, Object arg1) {
				return ((Point)arg0).getDistance2((Point)arg1);
			}
		}
		
		static class Orientation implements Comparable {
			
			final int locationConstant;
			private final double distance;
			private final Point toCompare;
			
			public Orientation(Point anchor, Point toCompare, int locationConstant) {
				this.toCompare = toCompare;
				this.locationConstant = locationConstant;
				this.distance = anchor.getDistance(toCompare);
				//System.out.println("anchor: " + anchor + " toCompare: " + toCompare + " " +  toString());
			}

			public int compareTo(Object arg0) {
				return Double.compare(this.distance , ((Orientation)arg0).distance);
			}
			
			public String toString() {
				return "Orientation(" + getOrientationString() + ") - " + distance;	
			}

			private String getOrientationString() {
				switch(locationConstant) {
					case NORTH      : return "N"; 
					case SOUTH      : return "S";
					case EAST       : return "E"; 
					case WEST       : return "W";
					case NORTH_EAST : return "NE"; 
					case SOUTH_EAST : return "SE";
					case NORTH_WEST : return "NW"; 
					case SOUTH_WEST : return "SW";
				}
				return "<NONE>";
			}
		}
		
		
		public static Orientation[] buildOrientations(Point point, Rectangle rectangle) {
			return new Orientation[]{
					new Orientation(rectangle.getTopRight(),    point, NORTH_EAST),
					new Orientation(rectangle.getBottomRight(), point, SOUTH_EAST),
					new Orientation(rectangle.getTopLeft(),     point, NORTH_WEST),
					new Orientation(rectangle.getBottomLeft(),  point, SOUTH_WEST),
					new Orientation(rectangle.getTop(),         point, NORTH),
					new Orientation(rectangle.getBottom(),      point, SOUTH),
					new Orientation(rectangle.getRight(),       point, EAST),
					new Orientation(rectangle.getLeft(),        point, WEST)
				};
		}
		
		public static int getNearestOrientationRelativeTo(Point point, Rectangle rectangle) {
			Orientation[] orientations = getRankedOrientations(point,rectangle);
			return firstOrientationConstant(orientations);
		}

		private static int firstOrientationConstant(Orientation[] orientations) {
			return orientations[0].locationConstant;
		}

		private static Orientation[] getRankedOrientations(Point point, Rectangle rectangle) {
			Orientation[] orientations = buildOrientations(point, rectangle);
			return sort(orientations);
		}

		private static Orientation[] sort(Orientation[] orientations) {
			Arrays.sort(orientations);
			return orientations;
		}
		
		
		public static Point getNearestPointToOrientation(Point[] points, Rectangle rectangle, int positionConstant) {
			Orientation[] closest = new Orientation[points.length];
			for (int i=0; i < closest.length; ++i) {
				closest[i] = getRankedOrientations(points[i], rectangle)[0];
			}
			return closestTo(closest, positionConstant);
		}

		
		
		
		private static Point closestTo(Orientation[] closest, int positionConstant) {
			List filtered = new ArrayList();
			for (int i = 0; i < closest.length; i++) {
				if (closest[i].locationConstant == positionConstant)
					filtered.add(closest[i]);
			}
			if (filtered.size() == 0)
				return null; //TODO: an exception?
			return sort((Orientation[]) filtered.toArray(new Orientation[]{}))[0].toCompare;
		}
		
	}
	
	
	private final IBoundsProvider boundsProvider;
	private final int positionConstant;
	private final FigureLocator figureLocator;
	
	
	public ByOrientationLocator(IBoundsProvider boundsProvider, IFigureMatcher figureMatcher, int positionConstant) {
		super(figureMatcher);
		this.boundsProvider   = boundsProvider;
		this.positionConstant = positionConstant;
		this.figureLocator = new FigureLocator(figureMatcher);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		IWidgetLocator[] allMatches = figureLocator.findAll(ui);
		Point[] points = getPoints(allMatches);
		Rectangle bounds = null;
		try {
			bounds = boundsProvider.getBounds(ui);
		} catch (WidgetSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bounds == null)
			return new IWidgetLocator[0];
		
		Point nearest = PositionHelper.getNearestPointToOrientation(points, bounds, positionConstant);
		if (nearest == null)
			return new IWidgetLocator[0];
		return new IWidgetLocator[]{allMatches[indexOf(nearest, points)]};
	}


	private int indexOf(Point nearest, Point[] points) {
		for (int i = 0; i < points.length; i++) {
			if (points[i].equals(nearest))
				return i;
		}
		return -1;
	}


	private Point[] getPoints(IWidgetLocator[] locators) {
		Point[] points = new Point[locators.length];
		for (int i=0; i < locators.length; ++i) {
			points[i] = getPoint(locators[i]);
		}
		return points;
	}


	public static Point getPoint(IWidgetLocator widgetLocator) {
		Rectangle bounds = getBounds(widgetLocator);
		if (bounds == null)
			return null;
		return bounds.getCenter();
	}


	private static Rectangle getBounds(IWidgetLocator widgetLocator) {
		if (!(widgetLocator instanceof IFigureReference))
			return null;
		IFigureReference ref = (IFigureReference)widgetLocator;
		IFigure figure = ref.getFigure();
		if (figure == null)
			return null;
		Rectangle bounds = figure.getBounds();
		return bounds;
	}


	public static IAdaptableWidgetLocator forPositionMatchInHost(int position, IFigureMatcher matcher, IFigureLocator hostFigure) {
		return new ByOrientationLocator(boundsFor(hostFigure), matcher, position);
	}

	public static IAdaptableWidgetLocator forPositionMatchInHost(Position position, IFigureMatcher matcher, IFigureLocator hostFigure) {
		return forPositionMatchInHost(com.windowtester.runtime.gef.internal.finder.position.PositionHelper.getPositionConstant(position), matcher, hostFigure);
	}
	
	
	
	
	private static IBoundsProvider boundsFor(final IFigureLocator hostFigure) {
		class BoundsProvider implements IBoundsProvider, Serializable {
			private static final long serialVersionUID = 1L;

			public Rectangle getBounds(IUIContext ui) throws WidgetSearchException {
				IWidgetLocator ref = ui.find(hostFigure);
				return ByOrientationLocator.getBounds(ref);
			}
		}	
		return new BoundsProvider();
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return figureLocator.matches(widget); //TODO: this is not quite right <-- this will over-eagerly match (pruning happens relative to otehr matches)
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return figureLocator.getAdapter(adapter);
	}



	

}
