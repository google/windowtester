package com.windowtester.test.prefpage;


import java.io.IOException;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.util.WTSettingsFileAccessor;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;


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
public class WTRuntimePreferenceSettingsSmokeTest extends UITestCaseSWT {

//	private static final String PREFERENCES_MENU_ITEM_PATH = "Window/&Preferences(...)?"; //3.4M7+-safe

	
	private static final String HIGHLIGHT_ON = "highlight.on";
	
	private String _originalHighlightingSetting;

	@Override
	protected void setUp() throws Exception {
		_originalHighlightingSetting = new WTSettingsFileAccessor().getSetting(HIGHLIGHT_ON);
		//ACTUALLY: if there is no settings file, this may be null and it's not an error
		//assertNotNull(_originalHighlightingSetting);
		if (_originalHighlightingSetting == null)
			_originalHighlightingSetting = "false";
	}
	
	public void testPageModifiesSettingsOnDisk() throws IOException, WidgetSearchException {
		boolean highLightOn = isHighlightingOn();
		System.out.println("(pre toggle) highlighting is on: " + highLightOn);
		toggleHighlight(highLightOn);
	}

	//TODO: implement and enable
	public void XtestPageReflectsSettingsOnDiskFails() throws IOException, WidgetSearchException {
		fail("unimplemented");
	}
	
	
	private void toggleHighlight(final boolean highLightOn) throws WidgetSearchException {
		IUIContext ui = getUI();
		//ui.click(new MenuItemLocator(PREFERENCES_MENU_ITEM_PATH));
		WorkBenchHelper.openPreferences(ui);
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new FilteredTreeItemLocator("WindowTester/Playback"));
		ui.pause(2000);
		ui.click(new ButtonLocator("Highlighting"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Preferences"));
		//wait for change to stick
		ui.wait(new ICondition() {
			public boolean test() {
				try {
					return highLightOn != isHighlightingOn();
				} catch (IOException e) {
					fail(e.getMessage());
					return false;
				} 
			}
			@Override
			public String toString() {
				return " highlighting to be set to: " + !highLightOn;
			}
		}, 3000);
		
		
	}

	@Override
	protected void tearDown() throws Exception {
		resetHighlighting();
	}
	
	
	
	
	private void resetHighlighting() throws IOException {
		assertNotNull(_originalHighlightingSetting);
		new WTSettingsFileAccessor().setSetting(HIGHLIGHT_ON, _originalHighlightingSetting);
	}

	private boolean isHighlightingOn() throws IOException {
		return new WTSettingsFileAccessor().getBooleanSetting(HIGHLIGHT_ON);
	}

	
}
