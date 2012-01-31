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
package com.windowtester.internal.runtime.locator;

import java.io.Serializable;

import com.windowtester.internal.runtime.Adapter;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.locator.ILocator;

/**
 * Adapts a locator to an identifier (for playing nice with the legacy recorder).
 */
public class IdentifierAdapter implements IWidgetIdentifier, IAdaptable, ILocator, Serializable {
	
	private static final long serialVersionUID = -2449531209586204515L;

	private final ILocator _locator;

	public IdentifierAdapter(ILocator locator) {
		_locator = locator;
	}
	
	public String getNameOrLabel() {
		return getLocator().toString();
	}

	public Class<?> getTargetClass() {
		return null;
	}

	public String getTargetClassName() {
		return null;
	}

	public ILocator getLocator() {
		return _locator;
	}

	public Object getAdapter(Class<?> adapter) {
		if (adapter == ILocator.class)
			return getLocator();
		return Adapter.adapt(getLocator(), adapter);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "IdentifierAdapter[" + getLocator() + "]";
	}
	
}
