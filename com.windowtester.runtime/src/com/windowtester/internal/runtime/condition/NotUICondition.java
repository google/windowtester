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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.UICondition;

/**
 * Simple Not condition.
 */
public class NotUICondition extends UICondition {

	
	private final IUICondition toNegate;

	public NotUICondition(IUICondition toNegate) {
		this.toNegate = toNegate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toNegate.toString()+ " to be FALSE";
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return !toNegate.testUI(ui);
	}
	
}
