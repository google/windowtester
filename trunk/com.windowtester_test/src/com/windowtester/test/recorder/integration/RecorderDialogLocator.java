package com.windowtester.test.recorder.integration;

import com.windowtester.codegen.ui.controller.RecorderDialogTestHelper;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IsVisibleCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;

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
public class RecorderDialogLocator {

	
	static final ICondition SHELL_SHOWING  = new IsVisibleCondition(RecorderDialogTestHelper.shellLocator()) {
		public boolean testUI(IUIContext ui) {
			//while recording is bootstrapping, there will be not active shell so we need to iognore this upstream exception
			try {
				return super.testUI(ui);
			} catch (UnableToFindActiveShellException e) {
				return false;
			}
		}
	};
	static final ICondition SHELL_DISPOSED = new IsVisibleCondition(RecorderDialogTestHelper.shellLocator(), false);
	
	public ICondition isShowing() {
		return SHELL_SHOWING;
	}
	
	public ICondition isDisposed() {
		return SHELL_DISPOSED;
	}
	
	public IWidgetLocator recordButton() {
		return RecorderDialogTestHelper.startButtonLocator();
	}
	
	public IWidgetLocator shell() {
		return RecorderDialogTestHelper.shellLocator();
	}
	
	
}
