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
public class ShellHandle implements IShellHandle {

	private final String _title;
	private final boolean _isModal;

	public ShellHandle(String title, boolean isModal) {
		_title   = title;
		_isModal = isModal;
	}
	
	/**
	 * @see com.windowtester.runtime.swt.internal.finder.IShellHandle#getTitle()
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.finder.IShellHandle#isModal()
	 */
	public boolean isModal() {
		return _isModal;
	}

}
