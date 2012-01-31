package com.windowtester.test.recorder.ui;

import junit.framework.TestCase;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;

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
public class PropertySetTest extends TestCase {

	
	public void testFlagFocus() throws Exception {
		PropertySet props = new PropertySet.TestStub().withMapping(PropertyMapping.FOCUS);
		props.flag(PropertyMapping.FOCUS);
		assertTrue(props.toArray()[0].isFlagged());
	}
	
}
