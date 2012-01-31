package com.windowtester.test.eclipse.helpers;

import static junit.framework.Assert.assertNotNull;

import org.eclipse.core.runtime.IPath;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;

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
public class TestHelper {

    public static void ensureNotNull(Object ... args) {
		for (Object arg : args) {
			assertNotNull(arg);
		}
	}
	
	public static String getPathString(IPath path) {
		String str = path.toOSString();
		/* $codepro.preprocessor.if version >= 3.1 $ */
		str = path.toPortableString();
		/* $codepro.preprocessor.endif $ */
		return str;
	}
	
    protected static void selectAll(IUIContext ui) {
        int modKey = abbot.Platform.isOSX() ? WT.COMMAND : WT.CTRL;
        ui.keyClick(modKey, 'a');
    }
    
}
