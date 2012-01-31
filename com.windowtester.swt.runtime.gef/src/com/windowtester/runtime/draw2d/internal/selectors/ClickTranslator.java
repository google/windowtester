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
package com.windowtester.runtime.draw2d.internal.selectors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Point;

import com.windowtester.runtime.IClickDescription;

/**
 * Click translation helper.
 */
public class ClickTranslator {


	public static Point makeRelativeToCenter(IClickDescription click, IFigure figure) {
		if (isCentered(click))
			return new Point(0,0);	
		return translateRelativeToCenter(click, figure);
	}

	private static boolean isCentered(IClickDescription click) {
		return click.isDefaultCenterClick();
	}

	private static Point translateRelativeToCenter(IClickDescription click,
			IFigure figure) {
		Rectangle bounds = figure.getBounds();
		org.eclipse.draw2d.geometry.Point topLeft = bounds.getTopLeft();
		org.eclipse.draw2d.geometry.Point center = figure.getBounds().getCenter();
		Dimension relativized = new org.eclipse.draw2d.geometry.Point(click.x(),click.y()).getDifference(center);
		return new Point(relativized.width + topLeft.x, relativized.height + topLeft.y);
	}
}
