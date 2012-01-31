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
package com.windowtester.runtime.gef.internal.finder.scope;

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * A base class for scope objects.
 */
public abstract class AbstractScope implements IAdaptable {

	protected IWidgetLocator[] noMatches() {
		return new IWidgetLocator[]{};
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		return null;
	}
	
	
}
