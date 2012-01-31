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
package com.windowtester.runtime.draw2d.internal.condition;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.condition.ICondition;

/**
 * A condition that tests a figures XY location.
 */
public class FigureXYCondition implements ICondition, IDiagnosticParticipant {

	protected final IFigure figure;
	
	private final int x, y;
	
	public FigureXYCondition(IFigure figure, Point pt) {
		this.figure = figure;
		this.x = pt.x;
		this.y = pt.y;
	}
	
	public FigureXYCondition(IFigure figure, int x, int y) {
		this.figure = figure;
		this.x = x;
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		Rectangle bounds = getBounds();
		if (bounds == null)
			return false;
		return bounds.x == x && bounds.y == y;
	}

	protected Rectangle getBounds() {
		return figure.getBounds();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// IDiagnosticParticipant
	//
	////////////////////////////////////////////////////////////////////////////

	public void diagnose(IDiagnostic diagnostic) {
		Rectangle bounds = getBounds();
		diagnostic.attribute("class", getClass().getName());
		diagnostic.attribute("expected x", x);
		diagnostic.attribute("actual x", bounds == null ? "<null>" : new Integer(bounds.x).toString());
		diagnostic.attribute("expected y", x);
		diagnostic.attribute("actual y", bounds == null ? "<null>" : new Integer(bounds.y).toString());

	}
	
}
