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
package com.windowtester.runtime.draw2d.internal.helpers;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Encapsulates bounds info for figures.
 */
public class Bounds {
	
	private final IFigure figure;
	private final BoundsCalculator calculator;
	
	private Bounds(IFigure figure) {
		this.figure = figure;
		this.calculator = BoundsCalculator.forFigure(figure);
	}
	
	public Rectangle asRectangle() {
		return calculator.getBounds(figure);
	}
	
	public static Bounds forFigure(IFigure figure) {
		return new Bounds(figure);
	}	

}
