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
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.condition.LocatorClosingHandler;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Shell} widgets.
 * 
 */
public class ShellLocator extends SWTWidgetLocator {
	
	private static final long serialVersionUID = -6186081272378858556L;
	
	private class Closer implements ICloseableLocator {

		public void doClose(IUIContext ui) throws WidgetSearchException {
			IWidgetReference ref = (IWidgetReference) ui.find(ShellLocator.this);
			Shell shell = (Shell) ref.getWidget();
			UIProxy.closeShell(shell);
		}
		
	}
	
	
	private final boolean _isModal;

	/**
	 * Create a locator that locates a shell (that is optionally modal).
	 * @param shellTitle the title of the shell (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param isModal whether it is modal or not
	 */
	public ShellLocator(String shellTitle, boolean isModal) {
		super(Shell.class, shellTitle);
		_isModal = isModal;
	}
	
	/**
	 * Create a locator that locates a shell (modal).
	 */
	public ShellLocator(String shellTitle) {
		this(shellTitle, true);
	}
	
	/**
	 * Check whether the shell in question is modal.
	 * @return <code>true</code> if the shell in question is modal.
	 */
	public boolean isModal() {
		return _isModal;
	}
	

	/**
	 * Create a condition that tests if the given shell is showing.
	 */
	public ICondition isShowing() {
		return new ShellShowingCondition(getNameOrLabel());
	}

	/**
	 * Create a condition that tests if the given shell is disposed.
	 */
	public ICondition isDisposed() {
		return new ShellDisposedCondition(getNameOrLabel());
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == ICloseableLocator.class)
			return new Closer();
		return super.getAdapter(adapter);
	}
	
	/**
	 * Create a condition handler that ensures that this {@link Shell} is closed.
	 * 
	 * @since 5.0.0
	 */
	public IConditionHandler isClosed() {
		return new LocatorClosingHandler(this);
	}
}
