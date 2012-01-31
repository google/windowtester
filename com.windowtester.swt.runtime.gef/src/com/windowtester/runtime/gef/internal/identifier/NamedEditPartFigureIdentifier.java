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
package com.windowtester.runtime.gef.internal.identifier;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.finder.IGEFPartMapper;
import com.windowtester.runtime.gef.internal.reflect.GEFIdentifier;
import com.windowtester.runtime.gef.locator.NamedEditPartFigureLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * Identifier for named edit parts.
 */
public class NamedEditPartFigureIdentifier implements IFigureIdentifier {

	
	private final IGEFPartMapper partMapper;

	public NamedEditPartFigureIdentifier(IGEFPartMapper partMapper) {
		this.partMapper = partMapper;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		EditPart part = findPart(figure);
		if (part == null)
			return null;
		
		String id = GEFIdentifier.forPart(part);
		if (id == null)
			return null;
		return new NamedEditPartFigureLocator(id);
	}

	protected EditPart findPart(IFigure figure) {
		EditPart part = null;
		try {
			part = partMapper.findEditPart(figure);
		} catch( Throwable e) {
			//ignored -- this just means we'll skip this identifier
		}
		return part;
	}
	

}
