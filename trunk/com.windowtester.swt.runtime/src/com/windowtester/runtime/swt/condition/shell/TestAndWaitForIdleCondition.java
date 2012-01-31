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

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.internal.condition.IConditionWithIdle;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * Waits for a test condition to be true and then waits for the display to
 * finish processing all asynchronous messages.
 */
public abstract class TestAndWaitForIdleCondition
	implements IConditionWithIdle
{
	private enum TestStates {
		INIT, TEST, WAIT, DONE
	}

	/**
	 * The current state. Synchronize against {@link #LOCK} before accessing this field
	 */
	private TestStates state = TestStates.INIT;

	/**
	 * Synchronize against this field before accessing {@link #state}
	 */
	private final Object LOCK = new Object();

	private int waitForIdleCount = 0;

	/**
	 * Determine if the condition has been satisfied and if the display has finished
	 * processing all asynchronous messages.
	 */
	public final boolean testAndWaitForIdle() {
		switch (getState()) {

			case INIT :
				initTest();
				setState(TestStates.TEST);

			case TEST :
				if (test()) {
					setState(TestStates.WAIT);
					startWaitForIdle();
				}
				return false;

			case WAIT :
				return false;

			case DONE :
				return true;

			default :
				throw new IllegalStateException();
		}
	}

	/**
	 * Subclasses may override to perform first time condition testing initialization or
	 * to log a one time trace message.
	 */
	protected void initTest() {
		// Do nothing
	}

	/**
	 * Determine if the condition has been satisfied.
	 * <p>
	 * Note that this method is NOT guaranteed to be executed on the UI thread. In fact,
	 * it is most likely to be executed on the test thread. If you want to conveniently
	 * execute a test on the SWT UI thread, subclass
	 * com.windowtester.runtime.swt.condition.SWTUIConditionAdapter.
	 * 
	 * @return <code>true</code> if the condition is satisfied, else <code>false</code>
	 * 
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public abstract boolean test();

	/**
	 * Start the wait for idle process by queuing a {@link Display#asyncExec(Runnable)}
	 * runnable to detect when the display has finished the shell activation process. If
	 * this is called from the UI thread, then wait for idle won't work so don't wait and
	 * simply set the {@link #state} to {@link TestStates#DONE}
	 */
	private void startWaitForIdle() {
		final Display display = DisplayReference.getDefault().getDisplay();
		if (display.getThread() == Thread.currentThread())
			setState(TestStates.DONE);
		else
			waitForIdle(display);
	}

	/**
	 * Queue a {@link Display#asyncExec(Runnable)} runnable to detect when the display has
	 * finished the shell activation process
	 */
	private void waitForIdle(final Display display) {
		if (++waitForIdleCount > 5)
			System.out.println(getClass().getSimpleName() + " waiting for idle " + waitForIdleCount);
		display.asyncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed() && display.readAndDispatch())
					waitForIdle(display);
				else
					setState(TestStates.DONE);
			}
		});
	}

	private TestStates getState() {
		synchronized (LOCK) {
			return state;
		}
	}

	private void setState(TestStates state) {
		synchronized (LOCK) {
			this.state = state;
		}
	}

}