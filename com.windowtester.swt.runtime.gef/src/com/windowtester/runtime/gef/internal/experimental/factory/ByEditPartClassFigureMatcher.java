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
package com.windowtester.runtime.gef.internal.experimental.factory;

import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.internal.matchers.SafeGEFEditPartMatcher;
import com.windowtester.runtime.internal.matcher.ClassByNameMatcher;

public class ByEditPartClassFigureMatcher extends SafeGEFEditPartMatcher {

	private final ClassByNameMatcher matcher;

	public ByEditPartClassFigureMatcher(String className) {
		matcher = new ClassByNameMatcher(className);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.matchers.SafeGEFEditPartMatcher#matchesSafely(org.eclipse.gef.EditPart)
	 */
	public boolean matchesSafely(EditPart partToTest) {
		return matcher.matches(partToTest);
	}

}
