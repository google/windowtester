package com.windowtester.swt.condition.shell;

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
public class ShellCondition extends com.windowtester.runtime.swt.condition.shell.ShellCondition
	implements IShellCondition
{
	/**
	 * Construct a new instance matching a shell with the specified conditions
	 * 
	 * @param title the expected shell title
	 * @param modal whether or not the expected shell is modal
	 */
	public ShellCondition(String title, boolean modal) {
		super(title, modal);
	}
}
