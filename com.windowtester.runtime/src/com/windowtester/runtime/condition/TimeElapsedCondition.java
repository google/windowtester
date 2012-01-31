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
package com.windowtester.runtime.condition;

import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

/**
 * Tests if a unit of time has elapsed.
 * 
 * For example:
 * <p>
 * <code>ui.wait(new TimeElapsedCondition(TimeUnit.SECONDS, 1));</code>
 * </p>
 * specifies a wait of 1 second.
 * <p/>
 * Note that as a general rule, time periods should be replaced with 
 * conditions that test more specific properties of the application under test.
 * 
 * <p/>
 */
public class TimeElapsedCondition implements ICondition {

	private static final long UNSET = -1;
	
	private final TimeUnit unit;
	private final long duration;

	//NOTE in ms
	private long start = UNSET;
	
	
	/**
	 * Equivalent to: <code>new TimeElapsedCondition(TimeUnit.SECONDS, duration)</code>
	 */
	public static TimeElapsedCondition seconds(long duration){
		return new TimeElapsedCondition(TimeUnit.SECONDS, duration);
	}
	
	/**
	 * Equivalent to: <code>new TimeElapsedCondition(TimeUnit.MILLISECONDS, duration)</code>
	 */
	public static TimeElapsedCondition milliseconds(long duration){
		return new TimeElapsedCondition(TimeUnit.MILLISECONDS, duration);
	}
	
	/**
	 * Equivalent to: <code>new TimeElapsedCondition(TimeUnit.MINUTES, duration)</code>
	 */
	public static TimeElapsedCondition minutes(long duration){
		//TimeUnit.MINUTES was introduced in Java1.6 so we simulate:
		return new TimeElapsedCondition(TimeUnit.SECONDS, 60* duration);
	}
	
	/**
	 * Create a condition that can test if a period of time has elapsed.  
	 * @param unit - the unit of time
	 * @param duration - the duration
	 */
	public TimeElapsedCondition(TimeUnit unit, long duration){
		this.unit     = unit;
		this.duration = duration;
	}

	/**
	 * Test if the specified time duration has elapsed.
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		if (start == UNSET)
			start = currentTimeMillis();
		return currentTimeMillis() - start > unit.toMillis(duration);
	}
	
}
