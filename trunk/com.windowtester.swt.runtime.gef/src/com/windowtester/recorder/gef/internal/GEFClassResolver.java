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
package com.windowtester.recorder.gef.internal;

import com.windowtester.internal.runtime.resolver.IClassResolver;

/**
 * Resolver support for GEF locators.
 */
public class GEFClassResolver implements IClassResolver {

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.IClassResolver#resolveClass(java.lang.String)
	 */
	public Class resolveClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
