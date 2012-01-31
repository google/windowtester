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
package com.windowtester.runtime.internal.properties;

/**
 * A key-value property holder object.
 */
public class Property {

	public static Property forKeyValue(Object key, Object value) {
		return new Property(key, value);
	}
	
	private final Object key;
	private final Object value;

	public Property(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
}
