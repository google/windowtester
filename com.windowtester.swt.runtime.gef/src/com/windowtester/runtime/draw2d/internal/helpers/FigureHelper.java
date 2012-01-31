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

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.internal.util.TextHelper;

public class FigureHelper {
	
	public static interface IFigureVisitor {
		/**
		 * Visit this figure.
		 * @param figure the figure to visit
		 * @return <code>true</code> if traversal should continue, <code><code>false</code> otherwise
		 */
		public boolean visit(IFigure figure);
	}
	
	public static void visit(IFigure figure, IFigureVisitor visitor) {
		//visit root
		doVisit(figure, visitor);

		//visit children
		for (Iterator iter = figure.getChildren().iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();
			visit(child, visitor);
		}
	}

	private static void doVisit(IFigure figure, IFigureVisitor visitor) {
		visitor.visit(figure);
	}

	public static String toString(IFigure figure) {
		return "Figure[" + figure.getClass().getName() + "] - text: " + getText(figure);
	}
	
	public static String getText(IFigure figure) {
		return TextHelper.getText(figure);
	}
	
}
