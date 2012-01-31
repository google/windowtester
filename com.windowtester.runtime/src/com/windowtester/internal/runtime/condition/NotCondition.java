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
package com.windowtester.internal.runtime.condition;

import com.windowtester.runtime.condition.ICondition;

/**
 * Simple Not condition.
 */
public class NotCondition implements ICondition {
	
	private final ICondition toNegate;

	public NotCondition(ICondition toNegate) {
		this.toNegate = toNegate;
	}
	
	public boolean test() {
		return !toNegate.test();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toNegate.toString()+ " to be FALSE";
	}
	
}
