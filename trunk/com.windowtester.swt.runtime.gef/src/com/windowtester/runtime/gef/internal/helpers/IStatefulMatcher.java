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
package com.windowtester.runtime.gef.internal.helpers;

/**
 * Matchers that implement this interface cache state to improve performance.
 * It is the client's responsibilty to clear the cache BEFORE performing fresh
 * searches (lest the search be invalidated by stale data).
 */
public interface IStatefulMatcher {
	/**
	 * Clear cached state.<p><b>NOTE:</b> it is the client's responsibility to call this
	 * method when cache may be invalid.
	 */
	void clearCache();
}