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

import org.eclipse.swt.widgets.Canvas;

/** 
 * A {@link Canvas} reference.
 * @param <T> the scrollable type
 */
public class CanvasReference<T extends Canvas>  extends CompositeReference<T> {

	public CanvasReference(T canvas) {
		super(canvas);
	}

}
