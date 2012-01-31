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

import com.windowtester.internal.runtime.Diagnostic;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.condition.ICondition;

/**
 * Wait utility.
 * 
 */
public class Timer {


	private long timeout = WT.getDefaultWaitTimeOut();
	private int interval = WT.getDefaultWaitInterval();

	public static Timer withTimeout(long timeout){
		Timer timer   = new Timer();
		timer.timeout = timeout;
		return timer;
	}
	
	public Timer withInterval(int interval){
		this.interval = interval;
		return this;
	}
	
	
	public void wait(ICondition condition) throws WaitTimedOutException {
		wait(condition, timeout, interval);
	}
	
	private void wait(ICondition condition, long timeout, int interval) throws WaitTimedOutException {
		long now = System.currentTimeMillis();
		while (!isTrue(condition)) {
			if (timedOut(now, timeout)) {
				handleTimeout(condition);
			}
			pause(interval);
		}
	}

	
	/**
	 * Default implementation that simply throws a {@link WaitTimedOutException}.
	 */
	protected void handleTimeout(ICondition condition) {
		// Build diagnostic information
		throw new WaitTimedOutException(Diagnostic.toString("Timed out waiting for condition", condition));
	
	}


	/**
	 * Default implementation that simply calls {@link Thread#sleep(long)}.
	 */
	public void pause(int interval) {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			//ignore interrupt
		}
	}

	/**
	 * Default implementation that simply calls {@link ICondition#test()}.
	 */
	protected boolean isTrue(ICondition condition) {
		return condition.test();
	}
	
	

	private boolean timedOut(long now, long timeout) {
		return System.currentTimeMillis() - now > timeout;
	}
	
}
