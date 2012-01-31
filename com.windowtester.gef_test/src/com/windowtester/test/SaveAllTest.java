package com.windowtester.test;


import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

/**
 * A "test" to be included at the end of the GEF test suite to ensure all editors have
 * been saved so that the test does not hang on shutdown.
 * 
 * @author Dan Rubel
 */
public class SaveAllTest extends UITestCaseSWT
{
	public void testSaveAll() throws Exception {
		if (anyUnsavedChanges())
			getUI().click(new MenuItemLocator("File/Save All"));
	}

	private boolean anyUnsavedChanges() {
		return new DirtyEditorCondition().test();
	}



}