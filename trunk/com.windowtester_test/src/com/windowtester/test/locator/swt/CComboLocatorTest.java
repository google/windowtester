package com.windowtester.test.locator.swt;

import org.eclipse.swt.custom.CCombo;

import abbot.tester.swt.CComboTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.locator.CComboItemLocator;
import com.windowtester.test.locator.swt.shells.CComboTestShell;

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
public class CComboLocatorTest extends AbstractLocatorTest {

	
	CComboTestShell _window;
	
	
	@Override
	public void uiSetup() {
		_window = new CComboTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	@SuppressWarnings("unchecked")
	public void XtestCComboLocator_basicSelections1_legacyRefs() throws WidgetSearchException {
		CCombo combo = _window.getCombo();
		
		IUIContext ui = getUI();
		
		//assert initial state
		assertNull(getSelection(combo));
	
		ui.click(new CComboItemLocator("ready", new WidgetReference(combo)));
		assertEquals("ready", getSelection(combo));
	
		ui.click(new CComboItemLocator("steady", new WidgetReference(combo)));
		assertEquals("steady", getSelection(combo));
	
		ui.click(new CComboItemLocator("go!", new WidgetReference(combo)));
		assertEquals("go!", getSelection(combo));
		
	}

	public void testCComboLocator_basicSelections1() throws WidgetSearchException {
		CCombo combo = _window.getCombo();
		
		IUIContext ui = getUI();
		
		//assert initial state
		assertNull(getSelection(combo));
	
		ui.click(new CComboItemLocator("ready"));
		assertEquals("ready", getSelection(combo));
	
		ui.click(new CComboItemLocator("steady"));
		assertEquals("steady", getSelection(combo));
	
		ui.click(new CComboItemLocator("go!"));
		assertEquals("go!", getSelection(combo));
		
	}

	
	public void testCComboLocator_basicSelections2() throws WidgetSearchException {
		CCombo combo = _window.getCombo();
		
		IUIContext ui = getUI();
		
		//assert initial state
		assertNull(getSelection(combo));
	
		ui.click(new CComboItemLocator("go!"));
		assertEquals("go!", getSelection(combo));
	
		ui.click(new CComboItemLocator("steady"));
		assertEquals("steady", getSelection(combo));
	
		ui.click(new CComboItemLocator("ready"));
		assertEquals("ready", getSelection(combo));
		
	}
	
	public void testComboLocator_advancedSelections1() throws WidgetSearchException  {

		IUIContext ui = getUI();		
		CCombo combo = _window.getCombo();
		
		//assert initial state
		assertNull(getSelection(combo));
		
		ui.click(new CComboItemLocator("five 5"));
		assertEquals("See Case 39540", "five 5", getSelection(combo));
		
		ui.click(new CComboItemLocator("many many many many words"));
		assertEquals("See Case 39540", "many many many many words", getSelection(combo));
		
		ui.click(new CComboItemLocator("Subtree OF"));
		assertEquals("See Case 39540", "Subtree OF", getSelection(combo));
		
		ui.click(new CComboItemLocator("tab\tconfusion"));
		assertEquals("See Case 39540", "tab\tconfusion", getSelection(combo));
		
		ui.click(new CComboItemLocator("tab	confusion 2"));
		assertEquals("See Case 39540", "tab	confusion 2", getSelection(combo));
	}
	
	// See Case 41110, attempted reproduction of this case
	public void testCComboLocator_advancedSelections2() throws WidgetSearchException {
		CCombo combo = _window.getCombo();
		
		IUIContext ui = getUI();
		
		//assert initial state
		assertNull(getSelection(combo));
	
		ui.click(new CComboItemLocator("!="));
		assertEquals("See Case 41110", "!=", getSelection(combo));
	
		ui.click(new CComboItemLocator("="));
		assertEquals("See Case 41110", "=", getSelection(combo));
	}
	
	public void testCComboLocator_assertionsTest() {
		IUIContext ui = getUI();
		CCombo ccombo = _window.getCombo();
		
		assertEquals(null, getSelection(ccombo));
		
		for (int i = 0; i < CComboTestShell.CCOMBO_TEST_SHELL_ITEMS.length; i++) {
			ui.assertThat(new CComboItemLocator(CComboTestShell.CCOMBO_TEST_SHELL_ITEMS[i]).isVisible());
			ui.assertThat(new CComboItemLocator(CComboTestShell.CCOMBO_TEST_SHELL_ITEMS[i]).isVisible(true));
			ui.assertThat(new CComboItemLocator(CComboTestShell.CCOMBO_TEST_SHELL_ITEMS[i]+"_").isVisible(false));
		}
	}

	private String getSelection(CCombo combo) {
		int index = new CComboTester().getSelectionIndex(combo);
		if (index == -1)
			return null;
		return new CComboTester().getItem(combo, index);
	}
	
}
