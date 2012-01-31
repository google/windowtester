package com.windowtester.test.runtime;


import junit.framework.TestCase;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.internal.KeyStrokeDecoder;
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
public class KeyStrokeDecodingSmokeTest extends TestCase {

	public void testKeyIdentity() {
		for (int cons : KeyStrokeDecoder.KEY_CONSTANTS) {
			int[] keys = extractKeys(cons);
			assertEquals(1, keys.length);
			assertEquals(cons, keys[0]);
		}
	}

	public void testModifierIdentity() {
		for (int cons : KeyStrokeDecoder.KEY_MODS) {
			int[] keys = extractKeys(cons);
			assertEquals(1, keys.length);
			assertEquals(cons, keys[0]);
		}
	}
	
	
	public void testIdentity1() {
		int composed = WT.ALT;
		int[] keys = extractKeys(composed);
		assertEquals(1, keys.length);
		assertEquals(WT.ALT, keys[0]);
	}
	
	public void testIdentity2() {
		int composed = WT.ARROW_DOWN;
		int[] keys = extractKeys(composed);
		assertEquals(1, keys.length);
		assertEquals(WT.ARROW_DOWN, keys[0]);
	}
	
	public void testIdentity3() {
		int composed = WT.F8;
		int[] keys = extractKeys(composed);
		assertEquals(1, keys.length);
		assertEquals(WT.F8, keys[0]);
	}
		
	public void testIdentity4() {
		int composed = WT.END;
		int[] keys = extractKeys(composed);
		assertEquals(1, keys.length);
		assertEquals(WT.END, keys[0]);
	}
	
	
	public void testSingleModKeyComposites() {
		for (int key : KeyStrokeDecoder.KEY_CONSTANTS) {
			for (int mod: KeyStrokeDecoder.KEY_MODS) {
				int composed = key | mod;
				int[] keys = extractKeys(composed);
				assertEquals(2, keys.length);
				assertContains(key, keys);
				assertContains(mod, keys);				
			}
		}
	}
	
	
	public void testComposite1() {
		int composed = WT.ALT | WT.SHIFT;
		int[] keys = extractKeys(composed);
		assertEquals(2, keys.length);
		assertContains(WT.ALT, keys);
		assertContains(WT.SHIFT, keys);
	}
	
	public void testComposite2() {
		int composed = WT.CTRL | WT.ALT | WT.SHIFT;
		int[] keys = extractKeys(composed);
		assertEquals(3, keys.length);
		assertContains(WT.ALT, keys);
		assertContains(WT.SHIFT, keys);
		assertContains(WT.CTRL, keys);
	}
	
	public void testComposite3() {
		int composed = WT.ALT | WT.SHIFT | WT.TAB;
		int[] keys = extractKeys(composed);
		assertEquals(3, keys.length);
		assertContains(WT.ALT, keys);
		assertContains(WT.SHIFT, keys);
		assertContains(WT.TAB, keys);
	}	
	
	public void testComposite4() {
		int composed = WT.ALT | WT.CR;
		int[] keys = extractKeys(composed);
		assertEquals(2, keys.length);
		assertContains(WT.ALT, keys);
		assertContains(WT.CR, keys);
	}
	
	//http://fogbugz.instantiations.com/default.php?36788
	public void testComposite5() {
		int composed = WT.SHIFT | WT.END;
		int[] keys = extractKeys(composed);
		assertEquals(2, keys.length);
		assertContains(WT.SHIFT, keys);
		assertContains(WT.END, keys);
	}
	
	//http://fogbugz.instantiations.com/default.php?36788
	public void testComposite6() {
		int composed = WT.CTRL | WT.SHIFT | WT.END;
		int[] keys = extractKeys(composed);
		assertEquals(3, keys.length);
		assertContains(WT.CTRL, keys);
		assertContains(WT.SHIFT, keys);
		assertContains(WT.END, keys);
	}
	
	
	private void assertContains(int i, int[] ints) {
		for (int j : ints) {
			if (i == j)
				return;
		}
		fail("array: " + toString(ints) + " expected to include " + i + " but didn't");
	}

	private CharSequence toString(int[] ints) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < ints.length; i++) {
			sb.append(ints[i]);
			if (i+1 < ints.length)
				sb.append(", ");
		}
		sb.append(']');
		return sb;
	}

	private int[] extractKeys(int composed) {
		return KeyStrokeDecoder.extractModifiers(composed);
	}
	
}
