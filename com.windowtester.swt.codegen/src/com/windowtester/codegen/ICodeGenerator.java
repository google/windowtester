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

import com.windowtester.codegen.eventstream.IEventStream;

/**
 * CodeGenerators parse an event stream and produce a String of generated code.
 */
public interface ICodeGenerator {
	
	/**
	 * Generate code from this event stream.
	 * @param stream - the event stream
	 * @return generated code as a String
	 */
	String generate(IEventStream stream);
}