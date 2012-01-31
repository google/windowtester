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
import com.windowtester.runtime.swt.internal.commands.eclipse.ZoomPartCommand;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewCondition.Zoomed;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A condition handler for ensuring that views are showing.
 */
public class ViewZoomedConditionHandler extends UICondition implements IUIConditionHandler {

	public static ViewZoomedConditionHandler forView(ViewLocator view, IViewMatcher matcher) {
		return new ViewZoomedConditionHandler(view, matcher);
	}

	private final ViewLocator view;
	private Zoomed zoomCondtion;

	private ViewZoomedConditionHandler(ViewLocator view, IViewMatcher matcher) {
		this.view = view;
		this.zoomCondtion = ViewCondition.isZoomed(matcher);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ZoomPartCommand.forView(view).run();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return zoomCondtion.test();
	}

}
