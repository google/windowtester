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
package com.windowtester.codegen.generator.setup;

import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.recorder.event.ISemanticEvent;


/**
 * Setup Handler.
 */
public interface ISetupHandler {

	
	/*
	 * NB: A key contract is that implementers MUST support a public 0-arg 
	 * constructor in order to support reflective creation.
	 */
	
	String getBody();
	String getImport();
	
	String getStaticBody();
	String getStaticImport();
	
	/**
	 * Tests to see if this handler is applicable to any events in this stream.
	 */
	boolean appliesTo(IEventStream stream);
	
	/**
	 * Tests to see if this handler FULLY handles the given event.
	 * If it is fully handled, the event can safely be pruned from the test method
	 * event stream.
	 */
	boolean fullyHandles(ISemanticEvent event);
	
	/**
	 * Return a string representation of this handler suitable for presentation in the UI.
	 */
	String getDescription();
	
}
