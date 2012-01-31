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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.IUIContext;

/**
 * Abstract superclass for conditions needing access to a {@link IUIContext}.  
 * This base class ensures that subclasses do not accidentally define test behavior 
 * in the {@link #test()} method by marking it <code>final</code> (moreover, it is 
 * implemented to throw an exception in the event that it is accidentally called).  
 * Clients requiring a more flexible implementation are encouraged to implement 
 * {@link IUICondition} instead.
 * 
 * <p>
 * NOTE: an early implementation of this class was deprecated in 2007 and replaced with its 
 * current version in February 2009.
 * </p>
 * 
 * <p/>
 */
public abstract class UICondition
	implements IUICondition {
	
	/**
	 * Unsupported method.  Call {@link #testUI(IUIContext)} instead.
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 * @throws UnsupportedOperationException
	 */
	public final boolean test() {
		throw new UnsupportedOperationException("unsupported method - should call testUI(IUIContext) instead");
	}

}
