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

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;

/**
 * An edit part matcher that ensures that the tested part is not <code>null</code>.
 */
public abstract class SafeGEFEditPartMatcher implements IFigureMatcher, IGEFEditPartMatcher {

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
	 */
	public boolean matches(IFigureReference figure) {
		return matches(figure.getEditPart());
	}
	
	/**
	 * Method made final to prevent accidental override. If you need to override this, implement the IMatcher interface directly.
	 * 
	 * @see com.windowtester.runtime.gef.internal.IGEFEditPartMatcher#matches(org.eclipse.gef.EditPart)
	 */
	public final boolean matches(EditPart part) {
		if (part == null)
			return false;
		return matchesSafely((EditPart)part);
	}
	
	/**
	 * Subclasses should implement this. The part is guaranteed not to be <code>null</code>.
	 * @param partToTest the part to test
	 * @return <code>true</code> if part matches, <code>false</code> otherwise.
	 */
	public abstract boolean matchesSafely(EditPart partToTest);
		
	protected static void assertNotNull(Object arg) throws IllegalArgumentException {
		if (arg == null)
			throw new IllegalArgumentException("Argument cannot be null");
	}
}
