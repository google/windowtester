package com.windowtester.test.locator.swt;

import org.eclipse.swt.widgets.*;

import com.windowtester.runtime.*;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.locator.*;
import com.windowtester.runtime.swt.locator.eclipse.*;
import com.windowtester.test.locator.swt.shells.*;

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
public class ContributedToolItemLocatorTest extends AbstractLocatorTest {

	ToolBarTestShell _window;
	
	@Override
	public void uiSetup() {
		_window = new ToolBarTestShell();
		_window.open();
		_window.itemA.setEnabled(false);
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	public void testDisabled() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		ui.assertThat(locator(_window.itemA).isEnabled(false));
	}
	
	public void testEnabled() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		ui.assertThat(locator(_window.itemB).isEnabled(true));
		ui.assertThat(locator(_window.itemC).isEnabled(true));
	}
	
	public void testEnabled_DefaultAPI() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		ui.assertThat(locator(_window.itemB).isEnabled());
		ui.assertThat(locator(_window.itemC).isEnabled());
	}
	
	public void testSelectionOnEnabledItems() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		assertNull(_window.lastSelection);
		 
		ui.click(locator(_window.itemB));
		assertEquals(_window.itemB, _window.lastSelection);
		
		ui.click(locator(_window.itemC));
		assertEquals(_window.itemC, _window.lastSelection);
		
		ui.click(locator(_window.itemB));
		assertEquals(_window.itemB, _window.lastSelection);
	}
	
	public void testSelectionOnDisabledItem() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		assertNull(_window.lastSelection);
		 
		ui.click(locator(_window.itemB));
		assertEquals(_window.itemB, _window.lastSelection);
		
		// the selection will still be on itemB since itemA can't be selected
		ui.click(locator(_window.itemA));
		assertEquals(_window.itemB, _window.lastSelection);
	}
	
	private ContributedToolItemLocator locator(final ToolItem item) {
		return new ContributedToolItemLocator(""/*ignored*/){

			private static final long serialVersionUID = 1L;

			@Override
			public IWidgetLocator[] findAll(IUIContext ui)
			{
				return new IWidgetLocator[]{reference(item)};
			}
		};
	}

	private IWidgetReference reference(ToolItem item) {
		return WTRuntimeManager.asReference(item);
	}

}
