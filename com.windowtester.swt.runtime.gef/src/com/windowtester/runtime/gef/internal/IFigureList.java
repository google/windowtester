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

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;

public interface IFigureList {

	IFigureReference[] toArray();
	
	FigureList add(IFigureReference figure);
	
	FigureList select(IFigureMatcher matchCriteria);

	IFigure[] toFigureArray();
	
	

}