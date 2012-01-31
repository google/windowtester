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
package com.windowtester.runtime.swt.condition;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.os.OSDelegate;

/**
 * Tests for the presence of a native shell.
 *
 */
public class NativeShellShowingCondition implements ICondition {

	private ICondition delegate;

	public NativeShellShowingCondition(String windowTitle) {
		delegate = OSDelegate.getCurrent().getConditionFactory().nativeDialogShowing(windowTitle);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return delegate.test();
	}

}
