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
package com.windowtester.internal.runtime.junit.core;

/**
 * Runs a sequnce of test steps (encapsulated in a runnable).
 */
public interface ISequenceRunner {

	/**
	 * Execute this runnable in the test thread.
	 */
	public abstract void exec(IRunnable runnable) throws Throwable;

	/**
	 * A runnable to be run in the test thread.
	 */
	public static interface IRunnable {
		void run() throws Throwable;
	}
}