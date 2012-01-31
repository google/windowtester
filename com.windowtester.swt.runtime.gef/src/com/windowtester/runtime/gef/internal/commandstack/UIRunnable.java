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
package com.windowtester.runtime.gef.internal.commandstack;

import com.windowtester.runtime.WidgetSearchException;

/**
 * A special runnable that returns results and declares the appropriate exceptions.
 */
public abstract class UIRunnable {
	
	/**
	 * Contains the runnable coding returning a result.
	 * <p/>
	 * @return the result of the operation.
	 */
	public abstract Object runWithResult() throws WidgetSearchException;
	
}
