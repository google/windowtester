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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

/**
 * Matcher that matches widgets that are components of a shell identified by title.
 * 
 */
public class ShellComponentMatcher implements Matcher {

	/** The shell matching condition */
	private final ShellCondition _shellMatchCondition;
	
	/** The result of the match test (tested in a syncExec) */
	private boolean _result;

	/**
	 * Create an instance.
	 * @param shellTitle the title of the parent shell
	 * @param isModal whether it must be modal
	 */
	public ShellComponentMatcher(String shellTitle, boolean isModal) {
		_shellMatchCondition = new ShellCondition(shellTitle, isModal);
	}

	/**
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(final Widget w) {
		return hasParentMatch(w);
	}

	
	/**
	 * Check to see if the given widget has a parent that matches our criteria.
	 */
	private boolean hasParentMatch(Widget w) {

		SWTHierarchyHelper helper = new SWTHierarchyHelper(w.getDisplay());

		Widget parent = helper.getParent(w);
		while (parent != null) {
			if (isShellMatch(parent))
				return true;
			parent = helper.getParent(parent);
		}
		return false;
	}

	/**
	 * Test this widget against our shell matching criteria.
	 */
	public boolean isShellMatch(final Widget w) {
		if (!(w instanceof Shell))
			return false;

		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				_result = _shellMatchCondition.test((Shell) w);
			}
		});
		return _result;
	}

}
