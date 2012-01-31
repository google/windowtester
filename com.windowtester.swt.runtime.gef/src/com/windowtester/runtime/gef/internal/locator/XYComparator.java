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
import java.util.Comparator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.windowtester.runtime.gef.internal.FigureReference;

/**
 * A left=to-right scanning XY comparator.
 */
public class XYComparator implements Comparator, Serializable {
	
	
	private static final long serialVersionUID = 819516468638119743L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(Object figRef1, Object figRef2) {
		/*
		 * NOTE: not defensive.
		 */
		
		IFigure fig1 = ((FigureReference)figRef1).getFigure();
		IFigure fig2 = ((FigureReference)figRef2).getFigure();

		Point point1, point2;

		if (fig1 instanceof Figure && fig2 instanceof Figure) {
			point1 = ((Figure) fig1).getLocation();
			point2 = ((Figure) fig2).getLocation();
		} else {
			return 0;
		}

		if (point1.x < point2.x) {
			return -1;
		} else if (point1.x > point2.x) {
			return 1;
		} else if (point1.y < point2.y) {
			return -1;
		} else if (point1.x > point2.x) {
			return 1;
		} else {
			return 0;
		}
	}
}
