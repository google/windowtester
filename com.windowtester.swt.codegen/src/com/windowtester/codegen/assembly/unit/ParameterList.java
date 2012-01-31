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
 * A list of Member Parameters.
 */
public class ParameterList {

	/** The backing store of Parameters */
	private List /*<Parameter>*/ _parameters = new ArrayList/*<Parameter>*/();
	
	/**
	 * Add this Parameter to the list; return true if it was not already present.
	 * @param mod - the Parameter to add
	 * @return true if this list changed as a result of the call
	 */
	boolean add(Parameter mod) {
		return _parameters.add(mod);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Iterator iter = _parameters.iterator(); iter.hasNext();) {
			Parameter param = (Parameter) iter.next();
			sb.append(param.toString());
			if (iter.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}
}
