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
 * A description of a list of thrown exceptions.
 */
public class ThrowsList {

	/** The backing store of exceptions */
	private List /*<String>*/ _exceptions = new ArrayList/*<String>*/();
	
	/**
	 * Add this exception to the list; return true if it was not already present.
	 * @param ex - the exception to add
	 * @return true if this list changed as a result of the call
	 */
	boolean add(String ex) {
		return _exceptions.add(ex);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (_exceptions.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer(" throws "); //hokey but we need a leading space
		for (Iterator iter = _exceptions.iterator(); iter.hasNext();) {
			String ex = (String) iter.next();
			sb.append(ex);
			if (iter.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}
	
	
	
}
