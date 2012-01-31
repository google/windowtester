package com.windowtester.test.gef.factories;


import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;

/**
 * Factory for common conditions.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ConditionFactory {

	public static ICondition shellDisposed(String title) {
		return new ShellDisposedCondition(title);
	}
	
	public static ICondition shellShowing(String title) {
		return new ShellShowingCondition(title);
	}
}
