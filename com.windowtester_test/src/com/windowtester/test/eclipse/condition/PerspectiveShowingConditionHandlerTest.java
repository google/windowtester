package com.windowtester.test.eclipse.condition;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.perspective;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.condition.eclipse.PerspectiveActiveConditionHandler;
import com.windowtester.test.eclipse.BaseTest;

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
@SuppressWarnings("restriction")
public class PerspectiveShowingConditionHandlerTest extends BaseTest {

	private static final int REPEATS = 5;

	/* (non-Javadoc)
	 * @see com.windowtester.test.eclipse.BaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp(); 
		getUI().ensureThat(perspective("Debug").isClosed());
	}
	
	public void testActive() throws Exception {
		IUIContext ui = getUI();
		for (int i=0; i < REPEATS; ++i) {
			ui.ensureThat(perspective("Debug").isActive());
			ui.assertThat(perspective("Debug").isActive());
			ui.ensureThat(perspective("Debug").isClosed());
			ui.assertThat(perspective("Debug").isClosed());
		}
	}
	
	
}
