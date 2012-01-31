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

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.internal.concurrent.SafeCallable;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * The class <code>SWTUITest</code> contains tests for the classes
 * <code>{@link DisplayReference} and indirectly {@link SWTUIExecutor}</code>.
 */
public class DisplayReferenceTest extends UITestCaseSWT
{
	private boolean expected;
	private int totalCount;
	private long totalTime;
	private long peakTime;

	/**
	 * Test normal execution
	 */
	public void testCallable() throws Exception {
		Callable<Boolean> callable = new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return expected;
			}
		};
		Display display = Display.getDefault();
		totalCount = 1000;
		expected = true;
		totalTime = 0;
		peakTime = 0;
		for (int i = 0; i < totalCount; i++) {
			long startTime = System.currentTimeMillis();
			boolean actual = new DisplayReference(display).execute(callable);
			long delta = System.currentTimeMillis() - startTime;
			totalTime += delta;
			peakTime = Math.max(peakTime, delta);
			assertEquals(expected, actual);
			expected = !expected;
		}
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new Callable)");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}

	/**
	 * Test execution when the callable throws an exception
	 */
	public void testCallableExceptionHandling() throws Exception {
		final String simulatedExceptionMessage = "Simulated Exception";
		Callable<Boolean> callable = new Callable<Boolean>() {
			public Boolean call() throws Exception {
				throw new RuntimeException(simulatedExceptionMessage);
			}
		};
		Display display = Display.getDefault();
		totalCount = 1000;
		totalTime = 0;
		peakTime = 0;
		for (int i = 0; i < totalCount; i++) {
			long startTime = System.currentTimeMillis();
			try {
				new DisplayReference(display).execute(callable);
				fail("Expected exception");
			}
			catch (Exception e) {
				long delta = System.currentTimeMillis() - startTime;
				totalTime += delta;
				peakTime = Math.max(peakTime, delta);
				assertNotNull(e.getCause());
				assertEquals(simulatedExceptionMessage, e.getCause().getMessage());
			}
		}
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new Callable) with Exception");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}

	/**
	 * Test execution when called from the UI thread
	 */
	public void testCallableCalledFromUIThread() throws Exception {
		final Callable<Boolean> callable = new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return expected;
			}
		};
		final Display display = Display.getDefault();
		totalCount = 1000;
		totalTime = 0;
		peakTime = 0;
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				for (int i = 0; i < totalCount; i++) {
					long startTime = System.currentTimeMillis();
					boolean actual = new DisplayReference(display).execute(callable);
					long delta = System.currentTimeMillis() - startTime;
					totalTime += delta;
					peakTime = Math.max(peakTime, delta);
					assertEquals(expected, actual);
					expected = !expected;
				}
			}
		});
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new Callable) called from UI thread");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}

	/**
	 * Test normal execution
	 */
	public void testSafeCallable() throws Exception {
		SafeCallable<Boolean> callable = new SafeCallable<Boolean>() {
			public Boolean call() throws Exception {
				return expected;
			}
			public Boolean handleException(Throwable e) throws Throwable {
				fail("Should not be called");
				return false;
			}
		};
		Display display = Display.getDefault();
		totalCount = 1000;
		expected = true;
		totalTime = 0;
		peakTime = 0;
		for (int i = 0; i < totalCount; i++) {
			long startTime = System.currentTimeMillis();
			boolean actual = new DisplayReference(display).execute(callable);
			long delta = System.currentTimeMillis() - startTime;
			totalTime += delta;
			peakTime = Math.max(peakTime, delta);
			assertEquals(expected, actual);
			expected = !expected;
		}
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new SafeCallable)");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}

	/**
	 * Test execution when the callable throws an exception
	 * and handleException rethrows new exception
	 */
	public void testSafeCallableExceptionHandling() throws Exception {
		final String simulatedExceptionMessage = "Simulated Exception";
		SafeCallable<Boolean> callable = new SafeCallable<Boolean>() {
			public Boolean call() throws Exception {
				throw new RuntimeException("Planned exception that should be sent to handleException");
			}
			public Boolean handleException(Throwable e) throws Throwable {
				throw new RuntimeException(simulatedExceptionMessage);
			}
		};
		Display display = Display.getDefault();
		totalCount = 1000;
		totalTime = 0;
		peakTime = 0;
		for (int i = 0; i < totalCount; i++) {
			long startTime = System.currentTimeMillis();
			try {
				new DisplayReference(display).execute(callable);
				throw new RuntimeException("Expected exception");
			}
			catch (Exception e) {
				long delta = System.currentTimeMillis() - startTime;
				totalTime += delta;
				peakTime = Math.max(peakTime, delta);
				assertNotNull(e.getCause());
				assertEquals(simulatedExceptionMessage, e.getCause().getMessage());
			}
		}
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new SafeCallable) with Exception");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}

	/**
	 * Test execution when the callable throws an exception
	 * and handleException returns a default value
	 */
	public void testSafeCallableDefaultValue() throws Exception {
		SafeCallable<Boolean> callable = new SafeCallable<Boolean>() {
			public Boolean call() throws Exception {
				throw new RuntimeException("Planned exception that should be sent to handleException");
			}
			public Boolean handleException(Throwable e) throws Throwable {
				return expected;
			}
		};
		Display display = Display.getDefault();
		totalCount = 1000;
		expected = true;
		totalTime = 0;
		peakTime = 0;
		for (int i = 0; i < totalCount; i++) {
			long startTime = System.currentTimeMillis();
			boolean actual = new DisplayReference(display).execute(callable);
			long delta = System.currentTimeMillis() - startTime;
			totalTime += delta;
			peakTime = Math.max(peakTime, delta);
			assertEquals(expected, actual);
			expected = !expected;
		}
		long averageTime = totalTime / totalCount;
		System.out.println("SWTUI.run(new SafeCallable) with Default Value");
		System.out.println("   average: " + averageTime + " milliseconds.");
		System.out.println("   peak:    " + peakTime + " milliseconds.");
	}
}