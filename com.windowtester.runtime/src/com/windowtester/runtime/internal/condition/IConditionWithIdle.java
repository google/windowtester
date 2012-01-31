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
package com.windowtester.runtime.internal.condition;

import com.windowtester.runtime.condition.ICondition;

/**
 * Marks conditions that perform a wait for idle post test.  The guaranteed call sequence is for
 * {@link ICondition#test()} to be called first and then {@link #testAndWaitForIdle()}. Clients who 
 * wish to simply test the property without the wait, can do so by calling {@link #test()}.  (Ensuring 
 * this invariant is the implementer's responsibility.
 */
public interface IConditionWithIdle extends ICondition {

	/**
	 * Call {@link #test()} and then perform a wait for idle before returning.
	 */
	boolean testAndWaitForIdle(); 
}
