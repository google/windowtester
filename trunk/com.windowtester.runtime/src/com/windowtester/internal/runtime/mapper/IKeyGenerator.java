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
package com.windowtester.internal.runtime.mapper;

import com.windowtester.runtime.WidgetLocator;

/**
 * A key-generation strategy.  Given an info object, IKeyGenerators 
 * return a describing key value.
 */
public interface IKeyGenerator {

	/**
	 * Generate a key value based on this info object.
	 * @param info - the object to describe.
	 * @return a key based on the info object
	 */
	String generate(WidgetLocator info);
		
}
