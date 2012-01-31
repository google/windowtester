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

import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;
import com.windowtester.runtime.gef.internal.reflect.GEFIdentifier;
import com.windowtester.runtime.util.StringComparator;

/**
 * A matcher that matches on user contributed names.
 */
public class NamedFigureMatcher extends BasicFigureMatcher implements Serializable {
	
	private static final long serialVersionUID = -8186200886001135736L;

	private final String figureId;

	public NamedFigureMatcher(String figureId) {
		this.figureId = figureId;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	protected boolean matches(IFigure figure) {
		if (figure == null)
			return false;
		String id = GEFIdentifier.forFigure(figure);
		if (id == null)
			return false;
		return StringComparator.matches(id, figureId);
	}


}
