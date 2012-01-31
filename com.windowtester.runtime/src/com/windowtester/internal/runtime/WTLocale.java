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
package com.windowtester.internal.runtime;

/**
 * Used by WT to indicate whether to use the current locale keyboard or
 * the US Keyboard for text entry.
 */
public class WTLocale {
	
	/**
	 * isCurrent = false - use US Keyboard (default)
	 * isCurrent = true - use current locale keyboard
	 */
	public static boolean isCurrent = false;

}
