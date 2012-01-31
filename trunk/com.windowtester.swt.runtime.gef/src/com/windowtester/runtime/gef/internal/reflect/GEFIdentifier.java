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
package com.windowtester.runtime.gef.internal.reflect;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

/**
 * Service for querying parts and figures for user specified identifier tags. 
 */
public class GEFIdentifier {

	private static String DEFAULT_PART_ID_KEY   = "getEditPartId";
	private static String DEFAULT_FIGURE_ID_KEY = "getFigureId";
	
	private static IdIntrospector PART   = IdIntrospector.forName(DEFAULT_PART_ID_KEY);
	private static IdIntrospector FIGURE = IdIntrospector.forName(DEFAULT_FIGURE_ID_KEY);
	
	public static String forPart(EditPart part) {
		return PART.getId(part);
	}

	public static String forFigure(IFigure figure) {
		return FIGURE.getId(figure);
	}
	
	
//one way we might open this for user intervention	
//	public static void overridePartIdKey(String newPartIdKey) {
//		...		
//	}
	
}
