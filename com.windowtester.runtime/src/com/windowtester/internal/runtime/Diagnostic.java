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
 * Utility class for collecting diagnostic information
 */
public class Diagnostic
{
	/**
	 * Collect diagnostic information about the specified object
	 * 
	 * @return the diagnostic information collected
	 */
	public static String toString(String key, Object value) {
		IDiagnostic diagnostic = new DiagnosticWriter();
		diagnostic.diagnose(key, value);
		return diagnostic.toString();
	}
}
