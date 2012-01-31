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
package com.windowtester.runtime.swt.internal.condition;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Ensures locators are closed.
 */
public class LocatorClosingHandler extends UICondition implements IUIConditionHandler {

	private final SWTWidgetLocator locator;

	public LocatorClosingHandler(SWTWidgetLocator locator){
		this.locator = locator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ICloseableLocator closeable = (ICloseableLocator) locator.getAdapter(ICloseableLocator.class);
		if (closeable == null)
			throw new IllegalArgumentException("locator must adapt to ICloseableLocator");
		closeable.doClose(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return !locator.isVisible().testUI(ui);
	}

}
