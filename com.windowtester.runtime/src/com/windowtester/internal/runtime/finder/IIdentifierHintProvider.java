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
package com.windowtester.internal.runtime.finder;

/**
 * A provider of hints that might be useful at widget identification
 * or location time.
 * <p>
 * NOTE: clients will tend to adapt to this interface.
 * <p>
 */
public interface IIdentifierHintProvider {

	/**
	 * Does this locator require XY location information to be complete?
	 */
	public boolean requiresXY();
	
}
