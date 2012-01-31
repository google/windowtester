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

public class InstanceOfByClassNameFigureMatcher extends BasicFigureMatcher {

	private final String _className;
	private Class _cls;

	
	public InstanceOfByClassNameFigureMatcher(String fullyQualifiedClassName) {
		_className = fullyQualifiedClassName;
	}

	public final String getClassName() {
		return _className;
	}
	
	public final Class resolveClass() {
		if (_cls == null) {
			try {
				_cls = Class.forName(getClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return _cls;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.draw2d.internal.IFigureMatcher#matches(org.eclipse.draw2d.IFigure)
	 */
	public boolean matches(IFigure figure) {
		Class cls = resolveClass();
		if (cls == null)
			return false;
		return cls.isInstance(figure);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ByClassNameFigureMatcher[" + getClassName() +"]";
	}
	
	
}
