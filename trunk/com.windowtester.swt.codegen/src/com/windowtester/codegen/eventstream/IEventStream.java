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
package com.windowtester.codegen.eventstream;

import com.windowtester.recorder.event.ISemanticEvent;

/**
 * An event stream is a stream of events ready for parsing by an EventStreamParser.
 */
public interface IEventStream {

    
    /**
     * @return true if there is a next token
     */
    boolean hasNext();
    
    /**
     * Advance to the next event in the stream.
     * @return the next event in the stream 
     */
    ISemanticEvent nextEvent();

    
	/**
	 * Look ahead to the next event but do not advance the cursor.
	 * @return the next event.
	 */
	ISemanticEvent peek();

	/**
	 * Back up the event stream cursor.
	 */
	void backUp();

	/**
	 * Create a copy of this stream (useful for traversals).
	 */
	IEventStream copy();
    
}
