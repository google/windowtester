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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of Member Modifiers.
 */
public class ModifierList {

	/** The backing store of modifiers */
	private List /*<Modifier>*/ _modifiers = new ArrayList/*<Modifier>*/();
	
	/**
	 * Add this modifier to the list; return true if it was not already present.
	 * @param mod - the modifier to add
	 * @return true if this list changed as a result of the call
	 */
	boolean add(Modifier mod) {
		return _modifiers.add(mod);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator iter = _modifiers.iterator(); iter.hasNext();) {
			Modifier mod = (Modifier) iter.next();
			sb.append(mod.toString());
			if (iter.hasNext())
				sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * Return wether this modifier list is empty.
	 * @return true if this modifier list is empty
	 */
	public boolean isEmpty() {
		return _modifiers.isEmpty();
	}
}
