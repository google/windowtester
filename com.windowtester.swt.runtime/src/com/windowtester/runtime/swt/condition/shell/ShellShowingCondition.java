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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.*;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.runtime.swt.UnableToFindActiveShellException;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTUIException;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests for the presence of a shell.  Given the name of a shell, this condition waits until that shell is visible.
 * 
 */
public class ShellShowingCondition extends TestAndWaitForIdleCondition
{
	/**
	 * The title of the shell
	 */
	private final String title;

	/**
	 * Construct a new instance checking for the specified shell to be shown.
	 * 
	 * @param title the title of the shell (can be a regular expression as described in
	 *            the {@link StringComparator} utility)
	 */
	public ShellShowingCondition(String title) {
		this.title = title;
	}

	protected void initTest() {
		TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "waiting for shell showing: " + title);
	}

	/**
	 * Test the active shell to see if it has a title matching the specified title
	 * 
	 * @see com.windowtester.runtime.swt.condition.shell.TestAndWaitForIdleCondition#test()
	 */
	public boolean test() {
		try {
			ISWTWidgetReference<?>[] shells = SWTWidgetFinder.forActiveShell().findAll(ofClass(Shell.class),
				withText(title), isVisible());
			return shells.length > 0;
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
		return "ShellShowingCondition: '" + title + "'";
	}
}
