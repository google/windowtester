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
package com.windowtester.internal.runtime.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection utils.
 */
public class CollectionUtils {

	
	/**
	 * Create an array list of the given type of size 1.
	 */
	public static <T> ArrayList<T> listOfOne() {
		return new ArrayList<T>(1);
	}
	
	public static <T> List<T> listWith(T... values) {
		List<T> list = new ArrayList<T>();
		list.addAll(Arrays.asList(values));
		return list;
	}
	
    public static <T> List<T> emptyList() {
        return new ArrayList<T>();
    }
	
}
