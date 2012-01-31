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
package com.windowtester.runtime.gef.internal.hierarchy;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.IEditPartReference;
import com.windowtester.runtime.gef.internal.IFigureList;

public interface IFigureInfoBuilder {

	IFigureList getChildren(IFigure figure);

	IEditPartReference getPart(IFigure figure);

	IConnectionList getConnections(IFigure figure);

	IFigureReference getParent(IFigure figure);

}
