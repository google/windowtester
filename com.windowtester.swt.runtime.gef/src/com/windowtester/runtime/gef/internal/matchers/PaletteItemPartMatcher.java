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
package com.windowtester.runtime.gef.internal.matchers;

import java.io.Serializable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteStack;

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * A matcher for palette parts (tools and drawers).
 */
public class PaletteItemPartMatcher extends SafeGEFEditPartMatcher implements Serializable {

	private static final long serialVersionUID = -647028231605652878L;

	private final String path;

	public PaletteItemPartMatcher(String path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.matchers.SafeGEFEditPartMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public boolean matches(IFigureReference figure) {		
		if (!isPalettePartFigure(figure.getFigure()))
			return false;
		return super.matches(figure);
	}
	
	
	
	/**
	 * TODO: can we avoid doing this?
	 */
	private boolean isPalettePartFigure(IFigure figure) {
		/**
		 * TODO: this is scary...
		 * The issue is that there are a number of figures (5?) associated with the edit part and we need to identify
		 * the "primary" one
		 */
		return figure.getClass().getName().endsWith("DetailedLabelFigure") || isDrawerFigure(figure);
	}

	public static boolean isDrawerFigure(IFigure figure) {
		Class cls = figure.getClass();
		if (isDrawerFigureClass(cls))
			return true;
		//pop up to super as well to catch anonymous classes
		return isDrawerFigureClass(cls.getSuperclass());
	}

	private static boolean isDrawerFigureClass(Class cls) {
		return cls.getName().endsWith("DrawerFigure");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.matcher.SafeGEFEditPartMatcher#matchesSafely(org.eclipse.gef.EditPart)
	 */
	public boolean matchesSafely(EditPart partToTest) {
		//System.out.println("testing: " + partToTest);
		Object modelPart = partToTest.getModel();
		if (!(modelPart instanceof PaletteEntry))
			return false;
		if (modelPart instanceof PaletteStack)
			return false;
		if (!(partToTest instanceof GraphicalEditPart))
			return false;
		
		GraphicalEditPart part = (GraphicalEditPart)partToTest;
		IFigure figure = part.getFigure();
//		System.out.println("showing:" + figure.isShowing());
//		System.out.println("visible:" + figure.isShowing());
		if (!figure.isShowing() || !figure.isVisible())
			return false;
		return pathMatches(getPath(), getPath(modelPart));
	}

	protected boolean pathMatches(String expectedPath, String actualPath) {
		if (actualPath == null)
			return false;
		//System.out.print("testing actual: <" + actualPath + "> against expected: <" + expectedPath +">");
		boolean matches = StringComparator.matches(actualPath, expectedPath);
		//System.out.println(" matches: " + matches);
		return matches;
	}

	public final String getPath() {
		return path;
	}

	public static String getPath(EditPart part) {
		if (part == null)
			return null;
		return getPath(part.getModel());
	}
	
	private static String getPath(Object modelObject) {
		String text = null;
		if (modelObject instanceof PaletteDrawer)
			text = ((PaletteDrawer)modelObject).getLabel();
		else if (modelObject instanceof PaletteContainer) { //containers that are not drawers do not have visible labels
			text = getPath(((PaletteContainer)modelObject).getParent());
		} else if (modelObject instanceof PaletteEntry) {
			//look for a drawer parent
			PaletteEntry entry = (PaletteEntry)modelObject;
			String parentText = getPath(entry.getParent());
			if (parentText != null)
				text = parentText + "/" + entry.getLabel();
			else
				text = entry.getLabel();
		}
		return text;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PaletteItemPartMatcher("+ getPath() +")";
	}
	
}
