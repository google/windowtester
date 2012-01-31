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
package com.windowtester.codegen;

/**
 * Trace option flag constants for use when running in trace mode.
 */
public interface ICodeGenPluginTraceOptions {

	/**
	 * The <code>BASIC</code> option enables basic lifecycle event tracing.
	 */
	static String BASIC = CodeGenPlugin.getPluginId() + "/basic";
	
	/**
	 * The <code>SWT_EVENTS</code> option enables verbose tracing of generated output.
	 */
	static String CODEGEN =  CodeGenPlugin.getPluginId() + "/codegen";
	
	/**
	 * The <code>RECORDER_EVENTS</code> option enables tracing of received recorder events.
	 */
	static String RECORDER_EVENTS =  CodeGenPlugin.getPluginId() + "/recorder/events";
	
}
