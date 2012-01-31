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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.isVisible;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;
import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withText;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;
import com.windowtester.runtime.swt.condition.WidgetDisposedCondition;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTUIException;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests for the disposal of a shell.  Given the name of a shell, this condition finds that shell 
 * and waits until that shell has been disposed. If no such shell with the specified name is found, then the shell is
 * assumed to be disposed. To check for disposal of another type of widget, use
 * {@link WidgetDisposedCondition}
 * 
 */
public class ShellDisposedCondition extends TestAndWaitForIdleCondition
{
	/**
	 * The title of the shell
	 */
	private final String title;

	/**
	 * Construct a new instance checking for the specified shell to be disposed.
	 * 
	 * @param title the title of the shell (can be a regular expression as described in
	 *            the {@link StringComparator} utility)
	 */
	public ShellDisposedCondition(String title) {
		this.title = title;
	}

	protected void initTest() {
		TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "waiting for shell disposed: " + title);
	}

	/**
	 * Test all shells to see if a shell with the specified title is still visible
	 * 
	 * @see com.windowtester.runtime.swt.condition.shell.TestAndWaitForIdleCondition#test()
	 */
	public boolean test() {
		try {
			ISWTWidgetReference<?>[] shells = SWTWidgetFinder.forDisplay().findAll(ofClass(Shell.class),
				withText(title), isVisible());
			return shells.length == 0;
		}
		catch (UnableToFindActiveShellException e) {
			return false;
		}
		catch (SWTUIException e) {
			//we need to unwrap here
			Throwable cause = e.getCause();
			if (cause instanceof UnableToFindActiveShellException)
				return false;
			throw e;
		}
	}

	public String toString() {
		return "ShellDisposedCondition: '" + title + "'";
	}
}
