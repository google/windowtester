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

/**
 * Basic design by contract invariant utilities.
 */
public class Invariants {

		
	public static <T> void notNull(T ... o) {
		if (o == null)
			throw new IllegalArgumentException("array argument must not be null");
		for (int i = 0; i < o.length; i++) {
			if (o[i] == null)
				throw new IllegalArgumentException("array argument cannot contain nulls");				
		}
	}

	public static void notNegative(int i) {
		if (i < 0)
			throw new IllegalArgumentException("argument must not be negative [got: " + i + "]");
	}
	
}
