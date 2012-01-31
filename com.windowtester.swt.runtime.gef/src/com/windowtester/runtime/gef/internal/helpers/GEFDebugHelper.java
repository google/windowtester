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
package com.windowtester.runtime.gef.internal.helpers;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

public class GEFDebugHelper {


	public void printFigures(FigureCanvas canvas) {
		printFigures(canvas.getContents());
	}

	public void printFigures(IFigure figure) {
		printFigures(figure, 0);
	}

	private void printFigures(IFigure figure, int indent) {
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			output.append(" ");
		}

		String text = "";
		try {
			Method getText = figure.getClass().getMethod("getText",
					new Class[] {});
			if (getText != null) {
				Object obj = getText.invoke(figure, new Object[] {});
				if (obj instanceof String) {
					text = " - " + (String) obj;
				}
			}
		} catch (Exception e) { /* do nothing */
		}

		output.append("<");
		output.append(figure.toString());
		output.append(text);
		output.append(">");

		Rectangle bounds = figure.getBounds().getCopy();
		output.append(" [" + bounds +"]");
		figure.translateToAbsolute(bounds);
		output.append("  - [" + bounds +"]");
		
		
		System.out.println(output);

		List children = figure.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			printFigures((IFigure) iter.next(), indent + 3);
		}
	}

	
}
