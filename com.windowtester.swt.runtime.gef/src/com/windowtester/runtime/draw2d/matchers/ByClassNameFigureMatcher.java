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
package com.windowtester.runtime.draw2d.matchers;

import java.io.Serializable;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.util.StringComparator;

/**
 * Matches figures by class.
 */
public class ByClassNameFigureMatcher extends BasicFigureMatcher implements Serializable {

	private static final long serialVersionUID = 2981559739007482193L;

	private final String className;

	
	public static ByClassNameFigureMatcher forName(String textOrPattern) {
		return new ByClassNameFigureMatcher(textOrPattern);
	}
	
	
	public ByClassNameFigureMatcher(String textOrPattern) {
		className = textOrPattern;
	}

	public final String getClassName() {
		return className;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	public boolean matches(IFigure figure) {
		return classNameEquals(figure.getClass().getName(), getClassName());
	}

	private boolean classNameEquals(String className, String classNameOrPattern) {
		return StringComparator.matches(className, classNameOrPattern);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ByClassNameFigureMatcher[" + getClassName() +"]";
	}
	
	
}
