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
package com.windowtester.runtime.gef.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;

/**
 * A smart list of figures.
 */
public class FigureList implements IFigureList {


	private final List figures = new ArrayList();
	
	public FigureList(IFigureReference[] figures) {
		for (int i = 0; i < figures.length; i++) {
			this.add(figures[i]);
		}
	}
	
	public FigureList() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.IFigureList#toArray()
	 */
	public IFigureReference[] toArray() {
		return (IFigureReference[]) figures.toArray(new IFigureReference[]{});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.IFigureContainer#accept(com.windowtester.runtime.gef.internal.IFigureInfoVisitor)
	 */
	protected void accept(IFigureInfoVisitor visitor) {
		IFigureReference[] figures = toArray();
		for (int i = 0; i < figures.length; i++) {
			visitor.visit(figures[i]);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.IFigureList#toFigureArray()
	 */
	public IFigure[] toFigureArray() {
		IFigureReference[] refs = toArray();
		IFigure[] figures = new IFigure[refs.length];
		for (int i = 0; i < figures.length; i++) {
			figures[i] = refs[i].getFigure();
		}
		return figures;
	}

	
	public FigureList select(IFigureMatcher matchCriteria) {
		
		FigureList selected = new FigureList();
		IFigureReference[] figures = this.toArray();
		for (int i = 0; i < figures.length; i++) {
			IFigureReference figure = figures[i];
			if (matchCriteria.matches(figure))
				selected.add(figure);
		}
		
		return selected;
	}
	
	
	public FigureList add(IFigureReference figure) {
		figures.add(figure);
		return this;
	}
	
	
}
