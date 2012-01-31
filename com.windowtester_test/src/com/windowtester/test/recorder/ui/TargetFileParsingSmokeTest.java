package com.windowtester.test.recorder.ui;

import java.io.InputStream;

import junit.framework.TestCase;

import com.windowtester.eclipse.ui.target.Target;

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
public class TargetFileParsingSmokeTest extends TestCase {

	public void testGetPlugins() throws Exception {
		String[] plugins = parser("test-0.target").getPlugins();
		assertEquals("com.windowtester.runtime", plugins[0]);
		assertEquals("com.windowtester.swt.runtime", plugins[1]);
		assertEquals(2, plugins.length);
	}
	
	public void testAddPlugin() throws Exception {
		String plugins[] = parser("test-0.target").addPlugin("org.eclipse.core.runtime").getPlugins();
		assertEquals("com.windowtester.runtime", plugins[0]);
		assertEquals("com.windowtester.swt.runtime", plugins[1]);
		assertEquals("org.eclipse.core.runtime", plugins[2]);
		assertEquals(3, plugins.length);
		//System.out.println(parser("test-0.target").addPlugin("org.eclipse.core.runtime").asString());
	}
	
	
	private Target parser(String fileName) {
		InputStream stream = getClass().getResourceAsStream(fileName);
		return Target.fromStream(stream);
	}
	
	
}
