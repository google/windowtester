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
package com.windowtester.runtime.gef.locator;

import java.io.Serializable;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jface.viewers.ILabelProvider;

import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.identifier.FigureCanvasIdentifier;
import com.windowtester.runtime.gef.internal.locator.FigureLabelProvider;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

/**
 * Locates XY coordinates relative to the {@link FigureCanvas} in a GEF Graphical Editor.
 * This is a convenience locator, fully equivalent to 
 * <code>new {@link XYLocator}(new {@link FigureLocator}(), x, y)</code>.
 * By default, search is scoped by the active editor.  If the active editor 
 * is not desired, another editor or part can be specified.
 */
public class FigureCanvasXYLocator extends XYLocator implements IAdaptable, Serializable
{
	
	private static final long serialVersionUID = -3037975190380340885L;

	private static final class IdentifierHints implements IIdentifierHintProvider {
		public boolean requiresXY() {
			return false;
		}
	}
	
	/**
	 * Create an instance that locates coordinates on the canvas in the active editor.
	 * @param x the x coordinate relative to the upper left of the canvas
	 * @param y the y coordinate relative to the upper left of the canvas
	 */
	public FigureCanvasXYLocator(int x, int y) {
		super(new FigureCanvasLocator(), x, y);
	}

	/**
	 * Create an instance that locates the canvas in the editor with the given name.
	 * 
	 * @param editorName the name of the target editor
	 * @param x the x coordinate relative to the upper left of the canvas
	 * @param y the y coordinate relative to the upper left of the canvas
	 * @see EditorLocator#EditorLocator(String)
	 */
	public FigureCanvasXYLocator(String editorName, int x, int y) {
		super(new FigureCanvasLocator(editorName), x, y);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == ILabelProvider.class)
			return new FigureLabelProvider();
		if (adapter == IFigureIdentifier.class)
			return new FigureCanvasIdentifier();
		if (adapter == IIdentifierHintProvider.class)
			return new IdentifierHints();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "FigureCanvasXYLocator(" + x() + ", " + y() +")";
	}
	
}
