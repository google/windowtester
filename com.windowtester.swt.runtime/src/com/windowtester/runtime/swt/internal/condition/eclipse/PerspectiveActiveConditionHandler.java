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
import com.windowtester.runtime.swt.internal.commands.eclipse.ShowPerspectiveCommand;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;

/**
 * A condition handler for ensuring that perspectives are showing.
 */
public class PerspectiveActiveConditionHandler extends UICondition implements IUIConditionHandler {

	public static PerspectiveActiveConditionHandler forPerspective(PerspectiveLocator perspective) {
		return new PerspectiveActiveConditionHandler(perspective);
	}

	private final PerspectiveLocator perspective;

	private PerspectiveActiveConditionHandler(PerspectiveLocator perspective) {
		this.perspective = perspective;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 */
	public void handle(IUIContext ui) throws Exception {
		ShowPerspectiveCommand.forPerspective(perspective).run();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		return perspective.isActive(true).test();
	}

}
