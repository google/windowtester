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
package com.windowtester.internal.runtime;

import com.windowtester.runtime.IAdaptable;

/**
 * Adaptation service provider.
 */
public class Adapter {

	
	public static Object adapt(Object o, Class<?> cls) {
		if (o.getClass() == cls)
			return o;
		if (!(o instanceof IAdaptable))
			return null;
		return ((IAdaptable)o).getAdapter(cls);
	}
	
}
