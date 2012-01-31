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
package com.windowtester.runtime.swt.internal.junit;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.UIContextFactory;

/**
 * A holder for execution environment information.
 */
public class ExecutionEnvironment {
	
	/**
	 * The application display associated with this test or <code>null</code> if it has
	 * not been cached yet.
	 * 
	 * @see #cacheDisplay()
	 */
	private Display _display;

	/**
	 * The root shell associated with this test or <code>null</code> if none. At the end
	 * of the test, any decendent shells of this shell will be forcefully closed.
	 */
	private Shell _rootShell;
	
	/**
	 * The UIContext associated with this test.
	 */
	private IUIContext _ui;

	/**
	 * Update the cached environment to reflect the current state of the world.
	 */
	public void update() {
		cacheDisplay();
		cacheRootShell(); //probably not the right place
	}

	/**
	 * Sets the display.
	 * 
	 * @param display The display (not <code>null</code>)
	 * @throws {@link IllegalStateException} if the display has already been initialized
	 *             via the {@link #setDisplay(Display)} method.
	 */
	protected void setDisplay(Display display) {
		if (display == null)
			throw new IllegalArgumentException("Display cannot be null");
		_display = display;
	}

	/**
	 * Answers the display.
	 * 
	 * @return the display (not <code>null</code>)
	 * @throws IllegalStateException if the display has not been initialized
	 */
	public Display getDisplay() {
		if (_display == null)
			throw new IllegalStateException("Display has not been initialized.");
		return _display;
	}

	
	/**
	 * Answers the UIContext. (Notice this is acting as a context factory.)
	 * 
	 * @return the UIContext (not <code>null</code>)
	 */
	public IUIContext getUI() {
		if (_ui == null)
			_ui = createUI();
		return _ui;
	}
	
	/**
	 * Build a UIContext instance.
	 * @return
	 */
	private IUIContext createUI() {
		/* as it happens the returned instance also implements IUIContext
		 * -- this WILL change, but in the meantime, a cast suffices
		 */
		return (IUIContext) UIContextFactory.createContext(getDisplay());
	}

	/**
	 * Answer the root shell associated with this test or <code>null</code> if none has
	 * been specified.
	 * 
	 * @return the root shell or <code>null</code>
	 */
	public Shell getRootShell() {
		return _rootShell;
	}

	/**
	 * Cache the active shell so that any decendent shells can be forcefully closed at the
	 * end of the test.
	 */
	protected void cacheRootShell() {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				setRootShell(getDisplay().getActiveShell());
			}
		});
	}

	/**
	 * Set the root shell associated with the receiver. At the end of the test, any
	 * decendent shells of this shell that are still open will be forcefully closed.
	 * 
	 * @param shell the shell or <code>null</code>
	 */
	protected void setRootShell(Shell shell) {
		_rootShell = shell;
	}

	/**
	 * Cache the current {@link Display} if it has not already been cached by the
	 * {@link #launchApp()} method or a prior call to this method.
	 */
	protected void cacheDisplay() {
		if (_display == null) //should we just ALWAYS refresh here?
			setDisplay(Display.getDefault());
	}
}