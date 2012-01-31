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
package com.windowtester.runtime.gef.internal;

import org.eclipse.gef.EditPart;

/**
 * A matcher for matching GEF edit parts.
 */
public interface IGEFEditPartMatcher extends IGEFMatcher {

	/**
	 * Check whether the given EditPart satisfies the specified criteria.
	 * @param part the part to test
	 * @return <code>true</code> if the part matches, 
	 * 		<code>false</code> otherwise
	 */
	boolean matches(EditPart part);
	
}
