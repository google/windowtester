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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;


/** 
 * Provides matching of components by instance.
 * 
 */
public class InstanceMatcher implements Matcher {

	/** The object instance on which to match */
	private final Object _instance;
    
    /**
     * Create an instance.
     * @param instance - the object on which to match
     */
    public InstanceMatcher(Object instance) {
    	_instance      = instance;
    }
    /**
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(final Widget w) {
    	return w == _instance;
        	// TODO: add support for must be showing case
            //&& (!mustBeShowing || c.isShowing());
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	String instanceDesc = _instance == null ? "null" : _instance.toString();
        return "Instance matcher (" + instanceDesc + ")";
    }
    
}