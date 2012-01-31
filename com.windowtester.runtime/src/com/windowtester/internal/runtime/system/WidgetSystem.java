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
package com.windowtester.internal.runtime.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.internal.runtime.locator.IDefaultUISelectorFactory;
import com.windowtester.internal.runtime.locator.IUISelector;

/**
 * A global registry for widget system details.
 */
public class WidgetSystem {

	static transient List _factories = new ArrayList();
	
	/**
	 * Add a widget reference factory that generates widget references for appropriate widgets.
	 */
	public static void addDefaultSelector(IDefaultUISelectorFactory factory) {
		if (factory == null)
			throw new IllegalArgumentException("factory must not be null");
		_factories.add(factory);
	}

	public static IUISelector getDefaultSelector(Object widget) {
		IUISelector selector;
		for (Iterator iter = _factories.iterator(); iter.hasNext();) {
			IDefaultUISelectorFactory factory = (IDefaultUISelectorFactory) iter.next();
			selector = factory.create(widget);
			if (selector != null)
				return selector;
		}
		return null;
	}

}
