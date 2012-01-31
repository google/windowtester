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
package com.windowtester.runtime.swt.internal.commands.eclipse;

/**
 * Indicates an exception in command execution.
 * 
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommandException(String msg) {
		super(msg);
	}
	
}
