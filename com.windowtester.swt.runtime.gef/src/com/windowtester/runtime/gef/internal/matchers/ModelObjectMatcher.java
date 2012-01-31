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

import com.windowtester.runtime.internal.IMatcher;

/**
 * An EditPart matcher that tests associated model objects against provided criteria.  Model objects
 * are retrieved from the {@link EditPart} via the {@link EditPart#getModel()} method.
 */
public class ModelObjectMatcher extends SafeGEFEditPartMatcher {

	private final IMatcher _modelObjectMatcher;

	public ModelObjectMatcher(IMatcher modelObjectMatcher) {
		assertNotNull(modelObjectMatcher);
		_modelObjectMatcher = modelObjectMatcher;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.matcher.SafeGEFEditPartMatcher#matchesSafely(org.eclipse.gef.EditPart)
	 */
	public boolean matchesSafely(EditPart partToTest) {
		return _modelObjectMatcher.matches(partToTest.getModel());
	}
	
}
