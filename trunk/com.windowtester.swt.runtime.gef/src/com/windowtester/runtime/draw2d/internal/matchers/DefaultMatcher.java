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
package com.windowtester.runtime.draw2d.internal.matchers;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.matchers.BasicFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;

/**
 * Convenience factory for "null object" default matcher types.
 */
public class DefaultMatcher {

	private static final IFigureMatcher MATCH_NONE = new BasicFigureMatcher() {
		public boolean matches(IFigure figure) {
			return false;
		}
	};

	private static final IFigureMatcher MATCH_ALL = new BasicFigureMatcher() {
		public boolean matches(IFigure figure) {
			return true;
		}
	};
	
	public static IFigureMatcher matchNone() {
		return MATCH_NONE;
	}

	public static IFigureMatcher matchAll() {
		return MATCH_ALL;
	}
	
}
