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
package com.windowtester.runtime.swt.internal.finder;

/**
 * Handle on a shell.
 *
 */
public interface IShellHandle {

	/**
	 * Get the shell's title.
	 * @return a String representing the Shell's title
	 */
	String getTitle();
	
	/**
	 * Check whether the shell is modal.
	 * @return <code>true</code> if the shell is modal, <code>false</code> otherwise
	 */
	boolean isModal();
	
	
}
