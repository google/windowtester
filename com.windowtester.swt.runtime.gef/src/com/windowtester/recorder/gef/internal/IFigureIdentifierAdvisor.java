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
package com.windowtester.recorder.gef.internal;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.locator.ILocator;

/**
 * Implementers map draw2D <code>IFigure</code>s to <code>ILocator<code>s.
 */
public interface IFigureIdentifierAdvisor {

	ILocator identify(IFigure figure);
	
}
