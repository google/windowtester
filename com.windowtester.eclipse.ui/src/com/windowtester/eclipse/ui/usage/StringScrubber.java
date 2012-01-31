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
package com.windowtester.eclipse.ui.usage;

/**
 * Cleans strings for safe storage.
 */
public class StringScrubber {

	public static String clean(String str) {
		if (str == null)
			return str;

		if (str.indexOf(',') != -1)
			str = str.replace(',', '_');

		if (str.indexOf('\n') != -1)
			str = str.replace('\n', '_');

		if (str.indexOf('\r') != -1)
			str = str.replace('\r', '_');

		return str;
	}

}
