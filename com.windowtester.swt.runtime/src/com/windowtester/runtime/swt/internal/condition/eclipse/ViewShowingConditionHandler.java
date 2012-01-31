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
package com.windowtester.runtime.swt.internal.condition.eclipse;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IUIConditionHandler;
import com.windowtester.runtime.condition.UICondition;
import com.windowtester.runtime.swt.internal.commands.eclipse.ShowViewCommand;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A condition handler for ensuring that views are showing.
 * 
 */
public class ViewShowingConditionHandler extends UICondition implements IUIConditionHandler {

	public static ViewShowingConditionHandler forView(ViewLocator view) {
		return new ViewShowingConditionHandler(view);
	}

	private final ViewLocator view;

	private ViewShowingConditionHandler(ViewLocator view) {
		this.view = view;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ShowViewCommand.forView(view).run();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return view.isVisible().testUI(ui);
	}

}
