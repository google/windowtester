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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.internal.concurrent.VoidCallable;

/** 
 * A {@link Shell} reference.
 * @param <T> the shell type
 */
public class ShellReference extends DecorationsReference<Shell> {

	public ShellReference(Shell shell) {
		super(shell);
	}
	
	public ShellReference [] getShells (){
		return displayRef.execute(new Callable<ShellReference[]>() {
			public ShellReference[] call() throws Exception {
				return asReferencesOfType(widget.getShells(), ShellReference.class);
			}
		});
	}

	/**
	 * Test if this shell is active.
	 * @return true if this shell is active, false otherwise
	 */
	public boolean isActive() {
		displayRef.getActiveShell();
		return displayRef.getActiveShell().getWidget() == widget;
	}

	/**
	 * If the receiver is visible, moves it to the top of the drawing order for the
	 * display on which it was created (so that all other shells on that display, which
	 * are not the receiver's children will be drawn behind it) and asks the window
	 * manager to make the shell active
	 */
	public void setActive() {
		displayRef.execute(new VoidCallable() {
			public void call() throws Exception {
				widget.setActive();
			}
		});
	}

	
	public static ShellReference forShell(Shell shell) {
		if (shell == null)
			return null;
		return new ShellReference(shell);
	}

	public static ShellReference[] forShells(Shell[] shells) {
		ShellReference[] refs = new ShellReference[shells.length];
		for (int i = 0; i < refs.length; i++) {
			refs[i] = new ShellReference(shells[i]);
		}
		return refs;
	}
	
}
