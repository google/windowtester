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
package com.windowtester.test.screencapture;

import com.windowtester.runtime.IUIContext;

import junit.extensions.UITestCase;

/**
 *
 * @author Phil Quitslund
 *
 */
public class OldUIContextAdapterTest extends UITestCase {

	public void testOldToNewAdapts() throws Exception {
		com.windowtester.swt.IUIContext ui = getUIContext();
		com.windowtester.runtime.IUIContext adapter = (IUIContext) ui.getAdapter(com.windowtester.runtime.IUIContext.class);
		assertNotNull(adapter);
	}
	
}
