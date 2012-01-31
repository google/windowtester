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
package com.windowtester.runtime.swt.internal.display;

/**
 * Implements a runnable which is able to be queried for a result when finished.
 * <p>
 * Synchronizing on this object may lead to deadlocks. Access to the result is
 * thread safe.
 */
public abstract class RunnableWithResult implements Runnable {
	private Object result;
	/**
	 * Executes {@link #runWithResult()} and stores the result for later retrieval.
	 * @see java.lang.Runnable#run()
	 */
	public final void run() {
		this.setResult(this.runWithResult());
	}
	/**
	 * Sets the result.
	 * <p/>
	 * The method is thread safe.
	 * @param result
	 */
	private void setResult(Object result) {
		synchronized (this) {
			this.result = result;
		}
	}
	/**
	 * Retrieves the result of the executed operation.
	 * <p/>
	 * Access is thread safe and synchronized on this object.
	 * @return the result of the operation.
	 */
	public Object getResult() {
		synchronized (this) {
			return result;
		}
	}
	/**
	 * Contains the runnable coding returning a result.
	 * <p/>
	 * @return the result of the operation.
	 */
	public abstract Object runWithResult();
}
