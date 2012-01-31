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
package com.windowtester.runtime.swt.condition.shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * An implementation of {@link IShellCondition} suitable for subclassing or use as
 * it exists. This implements typical shell matching conditions such as matching a modal
 * dialog with a particular title.
 * 
 */
public class ShellCondition
	implements IShellCondition
{
	protected static final int MODAL_SHELL_MASK = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;

	/**
	 * The expected shell title
	 */
	private final String _title;
	
	/**
	 * Whether or not the expected shell is modal
	 */
	private final boolean _modal;

	/**
	 * Construct a new instance matching a shell with the specified conditions
	 * 
	 * @param title the expected shell title
	 * @param modal whether or not the expected shell is modal
	 */
	public ShellCondition(String title, boolean modal) {
		_title = title != null ? title : "";
		_modal = modal;
	}

	/**
	 * Determine if the condition has been satisfied by checking the shell's title and
	 * modality. Subclasses may override and extend as necessary.
	 * 
	 * @param shell the shell to be tested (not <code>null</code>)
	 * @return <code>true</code> if the condition is satisfied, else <code>false</code>
	 */
	public boolean test(Shell shell) {
		return testModal(shell) && testTitle(shell);
	}

	/**
	 * Determine whether the shell's title matches what is expected.
	 * 
	 * @param shell the shell to test (not <code>null</code>)
	 * @return <code>true</code> if matching, else <code>false</code>
	 */
	protected boolean testTitle(Shell shell) {
		return _title.equals(shell.getText());
	}

	/**
	 * Determine whether the shell's modality matches what is expected.
	 * 
	 * @param shell the shell to test (not <code>null</code>)
	 * @return <code>true</code> if matching, else <code>false</code>
	 */
	protected boolean testModal(Shell shell) {
		return _modal == isModal(shell);
	}

	/**
	 * Check if the given shell is modal.
	 * 
	 * @param shell the shell in question (not <code>null</code>)
	 * @return <code>true</code> if the shell is modal, and <code>false</code>
	 *         otherwise
	 */
	protected boolean isModal(Shell shell) {
		return (shell.getStyle() & MODAL_SHELL_MASK) != 0;
	}
}
