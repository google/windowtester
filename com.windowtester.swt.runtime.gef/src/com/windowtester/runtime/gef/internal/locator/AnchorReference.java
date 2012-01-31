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
package com.windowtester.runtime.gef.internal.locator;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

public class AnchorReference implements IWidgetReference {

	public static AnchorReference forAnchor(ConnectionAnchor anchor) {
		return new AnchorReference(anchor);
	}
	
	private final ConnectionAnchor anchor;

	private AnchorReference(ConnectionAnchor anchor) {
		this.anchor = anchor;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public Object getWidget() {
		return anchor;
	}

	public ConnectionAnchor getAnchor() {
		return anchor;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new AnchorReference[]{ this };
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return widget == this;
	}

	public IFigure getOwner() {
		return anchor.getOwner();
	}

	
	
}
