package com.windowtester.swt.locator;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.swt.WidgetLocator;

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
public class ShellLocator extends WidgetLocator {
	
	private static final long serialVersionUID = -6186081272378858556L;
	
	private final boolean _isModal;

	/**
	 * Create a locator that locates a shell (that is optionally modal).
	 * @param shellTitle the title of the shell
	 * @param isModal whether it is modal or not
	 */
	public ShellLocator(String shellTitle, boolean isModal) {
		super(Shell.class, shellTitle);
		_isModal = isModal;
	}
	
	/**
	 * Check wether the shell in question is modal.
	 * @return <code>true</code> if the shell in question is modal.
	 */
	public boolean isModal() {
		return _isModal;
	}
	
}
