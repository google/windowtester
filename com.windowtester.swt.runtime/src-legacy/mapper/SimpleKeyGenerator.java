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
package com.windowtester.swt.mapper;

import com.windowtester.swt.WidgetLocator;

/**
 * A simple key generator that generates a fresh key for each call to generate.
 * 
 * @author Phil Quitslund
 *
 */
public class SimpleKeyGenerator implements IKeyGenerator {

	/** A global (incrementing) key index */
	private static int _keyIndex = 0;
	
	/**
	 * @see com.windowtester.swt.mapper.IKeyGenerator#generate(com.windowtester.swt.WidgetLocator)
	 */
	public synchronized String generate(WidgetLocator info) {
		return "key" + _keyIndex++;	
	}

}
