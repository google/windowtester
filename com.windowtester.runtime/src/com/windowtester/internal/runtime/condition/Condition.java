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
import com.windowtester.runtime.condition.IUICondition;

/**
 * Factory for negated conditions.
 * 
 */
public class Condition {

	
	public static ICondition not(ICondition condition) {
		if (condition instanceof IUICondition)
			return new NotUICondition((IUICondition) condition);
		return new NotCondition(condition);
	}
	
	
	
}
