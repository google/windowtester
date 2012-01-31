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

import org.eclipse.gef.handles.ResizeHandle;

import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;

/**
 * A matcher for resize handles.
 */
public class ResizeHandleMatcher extends ByClassNameFigureMatcher  {

	private static final long serialVersionUID = 1L;

	public ResizeHandleMatcher() {
		super(ResizeHandle.class.getName());
	}


}
