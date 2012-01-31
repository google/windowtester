package com.windowtester.test.runtime;

import static com.windowtester.runtime.swt.internal.util.TextUtils.*;
import junit.framework.TestCase;

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
public class TextUtilsTest extends TestCase {

	
	public void testHandleEscapesShouldNot() throws Exception {
		assertEquals("foo\\/bar", escapeSlashes("foo\\/bar"));
	}

	public void testHandleEscapesShould() throws Exception {
		assertEquals("foo\\\\/bar", escapeSlashes("foo/bar"));
	}

	
}
