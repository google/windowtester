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
package com.windowtester.runtime.swt.internal.widgets.cocoa32;

import com.windowtester.swt.macosx.cocoa.MacCocoa32;
import com.windowtester.swt.runtime.internal.macosx.MacExtensions;
import com.windowtester.swt.runtime.internal.macosx.OSX;


/**
 * Cocoa 32 support.
 */
public final class Cocoa32 extends OSX {

	public static Cocoa32 INSTANCE = new Cocoa32(new MacCocoa32());
	
	private Cocoa32(MacExtensions extensions) {
		super(extensions);
	}
}
