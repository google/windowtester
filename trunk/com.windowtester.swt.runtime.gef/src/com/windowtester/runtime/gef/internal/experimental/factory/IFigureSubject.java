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
package com.windowtester.runtime.gef.internal.experimental.factory;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * 
 * ...
 * 
 * The easiest way to make a figure identifiable is by giving it a
 * <em>name</em> property and assigning it a unique name at creation time 
 * (see {@link #hasName(String)}).  Alternatively, any property (or
 * combination of properties) can be used as an identifier (see the 
 * more generic {@link #hasProperty(String, String)}).
 * 
 * 
 * ...
 * 
 * 
 */
public interface IFigureSubject {

	IFigureFact hasProperty(String propertyName, String value);
	
	IFigureFact hasClass(String className);
	
	///////////////////////////////////////////////////////////////////////////////////
	//
	// Convenience
	//
	///////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * If a figure is designed to support the name protocol (e.g., has a "getName" method), a natural place
	 * to set the name is in the edit part at figure creation time ({@link AbstractGraphicalEditPart#createFigure}).
	 * <p>
	 * For example, one might set the name of a custom MyShapeFigure like so:
	 * <pre>
	 * class MyShapeEditPart extends AbstractGraphicalEditPart {
	 * 
	 *    IFigure createFigure() {
	 *       MyShapeFigure figure = new MyShapeFigure();
	 *       figure.setName(...);
	 *       return figure;
	 *    }
	 * 
	 * }
	 * </pre>
	 * 
	 * Note: this method is provided as a convenience.  It is equivalent to calling:
	 * <code>hasProperty("name", ...)</code>.
	 * </p>
	 * 
	 * 
	 * @param name the name to match
	 * @return a locator describing a matching figure
	 */
	IFigureFact hasName(String name);
	
	
	IEditPartSubject editPart();
	IModelObjectSubject modelObject();	
	
}
