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
package com.windowtester.runtime.gef;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;

import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * An accessor to information about figures (for use in matching). For more on
 * matching figures see {@link IFigureMatcher} and {@link IFigureMatcher#matches(IFigureReference)}.
 * @see IFigureMatcher
 */
public interface IFigureReference extends IWidgetLocator {

	/**
	 * Get the backing {@link IFigure}.
	 */
	IFigure getFigure();

	/**
	 * Get the {@link EditPart} associated with this figure, or
	 * <code>null</code> if there is none.
	 * <p>
	 * An edit part is considered associated with a figure <em>fig</em> if it
	 * is an instance of a {@link GraphicalEditPart} and it's
	 * {@link GraphicalEditPart#getFigure()} method returns <em>fig</em>.
	 */
	EditPart getEditPart();

	/**
	 * Get the children of this figure.
	 */
	IFigureReference[] getChildren();

	/**
	 * Get this figure's parent.
	 */
	IFigureReference getParent();


// ///////////////////////////////////////////////////////////////////////////
//
// Future API	
//	
/////////////////////////////////////////////////////////////////////////////	
	
//	/**
//	 * Get this figures connections.
//	 */
//	IConnectionInfo[] getConnections();
//	
//	/**
//	 * Get this figure's type.
//	 */
//	ClassReference getType(); //<---  this is an internal class
//	
//	/**
//	 * Get the {@link EditPart} associated with this figure, or null if there is none.
//	 */
//	IEditPartReference getEditPart();

}
