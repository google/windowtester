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
package com.windowtester.runtime.internal;

import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;

/**
 * Assertion handling.
 */
public interface IAssertionHandler {

	void assertThat(ICondition condition);

	void ensureThat(IConditionHandler conditionHandler) throws Exception;

	void assertThat(String message, ICondition condition) throws WaitTimedOutException;

}