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
package com.windowtester.runtime.swt.internal;

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.Diagnostic;
import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.IApplicationContextAdvisor;
import com.windowtester.runtime.swt.internal.debug.LogHandler;

/**
 * Manages conditions for the {@link UIContextSWT} instance.
 *
 */
public class ConditionManager {
	
	private final UIContextSWT ui;

	ConditionManager(UIContextSWT ui) {
		this.ui = ui;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IUIContext#wait(com.windowtester.runtime.condition.ICondition, long, int)
	 */
	public void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
		startingTest(condition);
		updateExpectedDelay(timeout);
		try {
			handleConditions();
			doWait(condition, timeout, interval);
		} finally {
			endingTest(condition);
		}
	}

	private void endingTest(ICondition condition) {
		if (condition instanceof IApplicationContextAdvisor)
			((IApplicationContextAdvisor)condition).postFlight(ui.applicationContext);
	}

	private void startingTest(ICondition condition) {
		if (condition instanceof IApplicationContextAdvisor)
			((IApplicationContextAdvisor)condition).preFlight(ui.applicationContext);		
	}

	private void doWait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
		long now = System.currentTimeMillis();
		while (!isTrue(condition)) {
			if (timedOut(now, timeout)) {
				handleTimeout(condition);
			}
			pause(interval);
		}
	}


	private void pause(int interval) {
		//note conditions are handled in the pause
		ui.pause(interval);
	}

	private boolean isTrue(ICondition condition) {
		return ConditionMonitor.test(ui, condition);
	}

	private void handleConditions() {
		ui.handleConditions();
	}

	private void updateExpectedDelay(long timeout) {
		ui.expectDelay(timeout);
	}

	private void handleTimeout(ICondition condition) throws WaitTimedOutException {
		Display display = ui.getDisplay();
		// If the display is valid, then capture the screen and close open shells
		if (isValid(display)) {
			ui.doScreenCapture("on timeout");
			new ExceptionHandlingHelper(display, true).closeOpenShells();
		}
		
		// Build diagnostic information
		throw new WaitTimedOutException(Diagnostic.toString("Timed out waiting for condition", condition));
	}

	private boolean isValid(Display display) {
		if (display == null) {
			LogHandler.log("failed to get current display in wait timeout handling");
			return false;
		} else if (display.isDisposed()) {
			LogHandler.log("current display is disposed in wait timeout handling");
			return false;
		} 
		return true;
	}

	private boolean timedOut(long now, long timeout) {
		return System.currentTimeMillis() - now > timeout;
	}

}
