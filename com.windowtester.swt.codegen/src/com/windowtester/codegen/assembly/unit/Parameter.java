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
package com.windowtester.codegen.assembly.unit;

/**
 * A representation of a named parameter for a MethodUnit
 * 
 */
public final class Parameter {

	/** A String describes this Parameter's type */
	private final String _type;
	
	/** A String describes this Parameter's name */
	private final String _name;
	
	/**
	 * Create an instance.
	 * @param type - a String that describes this parameter's type
	 * @param name - a String that describes this parameter's name
	 */
	public Parameter(String type, String name) {
		_type = type;
		_name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _type + " " + _name;
	}
		
}
