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
package com.windowtester.runtime.draw2d.internal.matchers;

import java.io.Serializable;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;

public class CompositeMatcher implements IFigureMatcher, Serializable {

	private static final long serialVersionUID = -8745224822299680264L;

	//NOTE: these matchers must be serializable if this instance is to be serialized
	private final IFigureMatcher matcher1, matcher2;

	
	public CompositeMatcher(IFigureMatcher matcher1, IFigureMatcher matcher2) {
		this.matcher1 = matcher1;
		this.matcher2 = matcher2;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public boolean matches(IFigureReference figureRef) {
		return matcher1.matches(figureRef) && matcher2.matches(figureRef);
	}

}
