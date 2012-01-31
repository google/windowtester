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
import com.windowtester.runtime.gef.internal.FigureReference;

/**
 * Constructs an info hierarchy based on a given {@link IFigure}.
 * 
 * 
 * --- do we even need this class or is it all done by the info objects?
 * 
 * --- one reason: shared build context!
 * 
 */
public class GEFInfoBuilder {


	
	public IFigureReference build(IFigure root) {
		return FigureReference.create(root);
		
	}
	
}
