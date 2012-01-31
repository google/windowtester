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
 * A collector of diagnostic information via the
 * {@link IDiagnosticParticipant#diagnose(IDiagnostic)} method.
 */
public interface IDiagnostic
{
	/**
	 * Output diagnostic information.
	 * 
	 * @param key the diagnostic key associated with this value
	 * @param value the diagnostic value to be collected. If value implements
	 *            IDiagnosticWriter, then its
	 *            {@link IDiagnosticParticipant#diagnose(IDiagnostic)} method will be
	 *            called
	 */
	void diagnose(String key, Object value);
	
	/**
	 * Output diagnostic information in the form of a simple key/value pair.
	 * 
	 * @param key the diagnostic key associated with this value
	 * @param value the diagnostic value to be collected
	 */
	void attribute(String key, String value);
	
	/**
	 * Output diagnostic information in the form of a simple key/value pair.
	 * 
	 * @param key the diagnostic key associated with this value
	 * @param value the diagnostic value to be collected
	 */
	void attribute(String key, int value);
	
	/**
	 * Output diagnostic information in the form of a simple key/value pair.
	 * 
	 * @param key the diagnostic key associated with this value
	 * @param value the diagnostic value to be collected
	 */
	void attribute(String key, boolean value);
}
