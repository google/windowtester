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
package com.windowtester.ui.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A date string provider.
 */
public class DateString {

	private static final String DATE_FORMAT = "M/d/yy h:mm aaa";

	public static String forNow() {
		return forNow(DATE_FORMAT);
	}

	public static String forNow(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(cal.getTime());
	}

}
