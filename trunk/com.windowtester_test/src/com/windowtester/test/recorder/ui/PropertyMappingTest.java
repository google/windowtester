package com.windowtester.test.recorder.ui;

import com.windowtester.internal.runtime.PropertySet.PropertyMapping;

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
public class PropertyMappingTest extends TestCase {

	
	public void testAsString() {
		PropertyMapping enabled = PropertyMapping.ENABLED.withValue(true);
		
		String ref = enabled.asString();
		PropertyMapping result = PropertyMapping.fromString(ref);
		
		assertEquals(enabled.getKey(), result.getKey());
		assertEquals(enabled.getValue(), result.getValue());
	}
	
	public void testFromString() {	
		PropertyMapping mapping = PropertyMapping.fromString(PropertyMapping.ENABLED.getKey() + "=true");
		assertEquals(PropertyMapping.ENABLED.getKey(), mapping.getKey());
		assertEquals("true", mapping.getValue());
	}
	
	
	
}
