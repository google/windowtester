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
package com.windowtester.recorder.event.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An enum class for Tree type events.
 */
public class TreeEventType implements Serializable {

	//generated using serialver
	static final long serialVersionUID = 7222944620040858730L;
	
	/** This Event type's label 
	 * @serial
	 */
	private final String _label;

	/**
	 * Create an instance and give it a label.
	 * @param label - the label
	 */
	protected TreeEventType(String label) {
		_label = label;
	}

	/**
	 * Get this event's label.
	 * @return the event's label
	 */
	public String getLabel() {
		return _label;
	}
	
	/**
	 * A label-based factory.
	 * @param label - the label
	 * @return the associated event type, or null if there is none
	 */
	public static TreeEventType get(String label) {
		return (TreeEventType)_types.get(label);
	}

    /**
     * Replace the deserialized instance with its associated static object (required for proper serialization).
     * @return the associated static object
     */
    private Object readResolve () {
        return get(_label);
    }
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return "TreeEvent: " + _label;
    }
    
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Labels
	//
	////////////////////////////////////////////////////////////////////////////
	
	public static final String EXPAND_LABEL           = "expand";
	public static final String COLLAPSE_LABEL         = "collapse";
	public static final String SELECT_LABEL           = "select";
	public static final String SINGLE_CLICK_LABEL     = "single-click";
	public static final String DOUBLE_CLICK_LABEL     = "double-click";
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Event types
	//
	////////////////////////////////////////////////////////////////////////////
	
	public static final TreeEventType EXPAND       = new TreeEventType(EXPAND_LABEL);
	public static final TreeEventType COLLAPSE     = new TreeEventType(COLLAPSE_LABEL);
	public static final TreeEventType SELECT       = new TreeEventType(SELECT_LABEL);
	public static final TreeEventType SINGLE_CLICK = new TreeEventType(SINGLE_CLICK_LABEL);
	public static final TreeEventType DOUBLE_CLICK = new TreeEventType(DOUBLE_CLICK_LABEL);
		
	/** A map that contains mappings from labels to registered event types */
	private static final Map _types = new HashMap();
	static {
		_types.put(EXPAND_LABEL,       EXPAND);
		_types.put(COLLAPSE_LABEL,     COLLAPSE);
		_types.put(SELECT_LABEL,       SELECT);
		_types.put(SINGLE_CLICK_LABEL, SINGLE_CLICK);
		_types.put(DOUBLE_CLICK_LABEL, DOUBLE_CLICK);		
	}	
}
