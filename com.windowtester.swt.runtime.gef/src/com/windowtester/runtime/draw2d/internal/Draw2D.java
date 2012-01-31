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
package com.windowtester.runtime.draw2d.internal;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;

/**
 * A facade for access to core WindowTester Draw2D support features.
 */
public class Draw2D {

	public static IDraw2DFinder getFinder() {
		return Draw2DFinder.getDefault();
	}
	
}
