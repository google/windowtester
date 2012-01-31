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
package com.windowtester.runtime.swt.internal.finder;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.util.ScreenCapture;

/**
 * 
 * A central service for perform actions that should be retried using
 * the global finder retry policy.
 *
 */
public class RetrySupport {

	public static interface ResultMatcher {
		boolean shouldRetry(Object result);
	}
	
	public static ResultMatcher NULL_RESULT_MATCHER = new ResultMatcher() {
		public boolean shouldRetry(Object result) {
			return result == null;
		}
	};
	
	public static ResultMatcher NON_NULL_RESULT_MATCHER = new ResultMatcher() {
		public boolean shouldRetry(Object result) {
			return result != null;
		}
	};
	
	
	public static ResultMatcher EMPTY_ARRAY_RESULT_MATCHER = new ResultMatcher() {
		public boolean shouldRetry(Object result) {
			if (!(result instanceof Object[]))
				return true;
			Object[] results = (Object[])result;
			return results.length == 0;
		}
	};
	
	
	public static Object retryUntilArrayResultIsNonEmpty(RunnableWithResult runnable) {
		return exec(runnable, EMPTY_ARRAY_RESULT_MATCHER);
	}
	
	public static Object retryUntilResultIsNonNull(RunnableWithResult runnable) {
		return exec(runnable, NULL_RESULT_MATCHER);
	}
	
	public static Object retryUntilResultIsNull(RunnableWithResult runnable) {
		return exec(runnable, NON_NULL_RESULT_MATCHER);
	}
	
	
	public static Object exec(RunnableWithResult retriable, ResultMatcher resultMatcher) {
		
		Object result = retriable.runWithResult();
		
		int tries = 0;
		while (resultMatcher.shouldRetry(result) && tries++ < getMaxRetries()) {
//			System.out.println("RetrySupport.exec(): " + tries);
			pause(getFinderRetryInterval());
			result = retriable.runWithResult();
		}
		return result;
	}


	public static interface Clickable {
		IWidgetLocator click() throws WidgetSearchException;
	}
	
	//NOTE: does screenshot
	public static IWidgetLocator performClickWithRetries(final Clickable clickAction) throws WidgetSearchException {
		final WidgetSearchException ex[] = new WidgetSearchException[1];
		
		IWidgetLocator clicked = (IWidgetLocator) retryUntilResultIsNonNull(new RunnableWithResult() {
			public Object runWithResult() {
				ex[0] = null;
				try {
					return clickAction.click();
				} catch (WidgetSearchException e) {
					ex[0] = e;
				}
				return null;
			}
		});
		
		if (ex[0] != null) {
			ScreenCapture.createScreenCapture();
			throw ex[0];
		}
		return clicked;
	}
	
	
	

	private static void pause(int ms) {
		try { Thread.sleep(ms); } catch(InterruptedException ie) { }
	}



	private static int getFinderRetryInterval() {
		return SWTWidgetFinder.getFinderRetryInterval();
	}



	private static int getMaxRetries() {
		return SWTWidgetFinder.getMaxFinderRetries();
	}

}
