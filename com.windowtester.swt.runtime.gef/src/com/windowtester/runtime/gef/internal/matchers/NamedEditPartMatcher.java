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

import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.reflect.GEFIdentifier;
import com.windowtester.runtime.util.StringComparator;

/**
 * A matcher that matches on user contributed names.
 */
public class NamedEditPartMatcher implements IFigureMatcher, Serializable {
	
	private static final long serialVersionUID = 1001646790643354019L;

	private final String partId;

	public NamedEditPartMatcher(String figureId) {
		this.partId = figureId;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public boolean matches(IFigureReference figureRef) {
		EditPart editPart = figureRef.getEditPart();
		if (editPart == null)
			return false;
		String id = GEFIdentifier.forPart(editPart);
		if (id == null)
			return false;
		return StringComparator.matches(id, partId);
	}


}
