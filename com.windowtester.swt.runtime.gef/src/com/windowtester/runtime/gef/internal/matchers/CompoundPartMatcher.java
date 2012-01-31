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
package com.windowtester.runtime.gef.internal.matchers;

import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;

/**
 * A matcher for composing matchers.
 */
public class CompoundPartMatcher implements IGEFEditPartMatcher {

	private final IGEFEditPartMatcher _matcher2;
	private final IGEFEditPartMatcher _matcher1;

	public CompoundPartMatcher(IGEFEditPartMatcher matcher1, IGEFEditPartMatcher matcher2) {
		_matcher1 = matcher1;
		_matcher2 = matcher2;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFEditPartMatcher#matches(org.eclipse.gef.EditPart)
	 */
	public boolean matches(EditPart part) {
		return _matcher1.matches(part) && _matcher2.matches(part);
	}

}
