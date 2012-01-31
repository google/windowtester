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
 * An enum class for member modifiers
 * 
 */
public final class Modifier {

	/** A String label that describes this Modifier */
	private final String _label;

	/**
	 * Create an instance.
	 * @param label - the String label that describes this Modifier
	 */
	private Modifier(String label) {
		_label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _label;
	}
	
	public static final Modifier PUBLIC       = new Modifier("public");
	public static final Modifier PRIVATE      = new Modifier("private");
	public static final Modifier PROTECTED    = new Modifier("protected");
	public static final Modifier STATIC       = new Modifier("static");
	public static final Modifier FINAL        = new Modifier("final");
	public static final Modifier SYNCHRONIZED = new Modifier("public");
	//etc...
	
}
