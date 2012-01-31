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

import java.util.List;

import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.IAdaptable;

/**
 * An event stream is a stream of events ready for parsing by an EventStreamParser.
 */
public class EventStream implements IEventStream {

    /** A pointer to the current index*/
    int cursor;
    
    /** The backing list of interactions */
    private List interactions;

    /**
     * Create an instance based on a list of interactions.
     * @param interactions - the list of interactions
     */
    public EventStream(List interactions) {
        this.interactions = interactions;
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.eventstream.IEventStream#nextEvent()
     */
    public ISemanticEvent nextEvent() {
    	Object next = interactions.get(cursor++);
        return adaptToSemanticEvent(next);
        
    }

	private ISemanticEvent adaptToSemanticEvent(Object object) {
		if (object instanceof ISemanticEvent)
        	return (ISemanticEvent)object;
        if (object instanceof IAdaptable) {
        	IAdaptable adapted = (IAdaptable)object;
        	return (ISemanticEvent) adapted.getAdapter(ISemanticEvent.class);
        }
        //TODO: return a null object that gets codegened and includes diagnostic info
        return null;
	}

    /* (non-Javadoc)
     * @see com.windowtester.codegen.eventstream.IEventStream#hasToken()
     */
    public boolean hasNext() {
        return cursor <= interactions.size()-1;
    }
    
    /**
     * @see com.windowtester.codegen.eventstream.IEventStream#peek()
     */
    public ISemanticEvent peek() {
    	Object next = interactions.get(cursor);
    	return adaptToSemanticEvent(next);
    }
    
    /**
     * @see com.windowtester.codegen.eventstream.IEventStream#backUp()
     */
    public void backUp() {
    	--cursor;
    }
    
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.eventstream.IEventStream#copy()
     */
    public IEventStream copy() {
    	return new EventStream(interactions);
    }
    
}
