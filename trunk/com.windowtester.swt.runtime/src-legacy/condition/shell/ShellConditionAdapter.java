package com.windowtester.swt.condition.shell;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.swt.condition.ICondition;

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
public class ShellConditionAdapter extends com.windowtester.runtime.swt.internal.condition.shell.ShellConditionAdapter
	implements ICondition
{
	/**
	 * Construct a new instance adapting the specified shell condition to a
	 * {@link ICondition}.
	 * 
	 * @param condition the shell condition to be adapted
	 */
	protected ShellConditionAdapter(IShellCondition condition) {
		super(condition);
	}
}
