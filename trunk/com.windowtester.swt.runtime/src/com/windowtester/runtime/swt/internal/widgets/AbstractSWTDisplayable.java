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
package com.windowtester.runtime.swt.internal.widgets;

import org.eclipse.swt.widgets.Display;

/**
 * A base class for SWT elements that have an associated display.
 */
public abstract class AbstractSWTDisplayable
{
	protected final DisplayReference displayRef;

	public AbstractSWTDisplayable(Display display) {
		this.displayRef = new DisplayReference(display);
	}
}
