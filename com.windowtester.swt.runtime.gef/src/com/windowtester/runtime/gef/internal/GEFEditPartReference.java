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

import org.eclipse.gef.GraphicalEditPart;

import com.windowtester.runtime.locator.WidgetReference;

/**
 * Basic implementation of {@link IGEFEditPartReference}.
 */
public class GEFEditPartReference extends WidgetReference implements IGEFEditPartReference {
	
	public static IGEFEditPartReference[] emptyList() {
		return new IGEFEditPartReference[]{};
	}
	
	public static IGEFEditPartReference create(GraphicalEditPart part) {
		return new GEFEditPartReference(part);
	}
	
	
	public GEFEditPartReference(GraphicalEditPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.locator.IGEFEditPartReference#getPart()
	 */
	public GraphicalEditPart getPart() {
		//edit part stored in super
		return (GraphicalEditPart) super.getWidget();
	}

	/**
	 * Get the IFigure associated with this part.  This is the primary figure
	 * as returned by {@link GraphicalEditPart#getFigure()}.
	 * 
	 * @see com.windowtester.runtime.locator.WidgetReference#getWidget()
	 */
	public Object getWidget() {
		return getPart().getFigure();
	}
	
	
}
