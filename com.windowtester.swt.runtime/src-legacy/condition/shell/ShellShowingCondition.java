package com.windowtester.swt.condition.shell;

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
public class ShellShowingCondition
	extends com.windowtester.runtime.swt.condition.shell.ShellShowingCondition
	implements ICondition
{
	
	/**
	 * Construct a new instance checking for the specified shell to be shown.
	 * 
	 * @param title the title of the shell
	 */
	public ShellShowingCondition(String title) {
		super(title);
	}	
}
