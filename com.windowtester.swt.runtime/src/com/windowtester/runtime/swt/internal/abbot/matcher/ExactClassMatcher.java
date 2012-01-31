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

import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;


/** 
 * Provides matching of components by class.  Unlike ClassMatcher,
 * it does not check for asignablity, it checks for the exact class.
 *
 */
public class ExactClassMatcher implements Matcher {

	/** The class on which to match */
	private final Class _cls;
	
	/** A flag to indicate if the widget must be showing to match*/
    private final boolean _mustBeShowing;
    
    
    /**
     * Create an instance.
     * @param cls - the class on which to match
     */
    public ExactClassMatcher(Class cls) {
        this(cls, true);
    }
    
    /**
     * Create an instance.
     * @param cls- the class on which to match (must not be null)
     * @param mustBeShowing - whether the widget must be showing to match
     */
    public ExactClassMatcher(Class cls, boolean mustBeShowing) {
    	if (cls == null)
    		throw new IllegalArgumentException("Class must not be null");
        _cls            = cls;
        _mustBeShowing = mustBeShowing;
    }
    
    /**
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(final Widget w) {

    	//null check for sanity
    	if (w == null)
    		return false;
    	
    	boolean result = _cls.isAssignableFrom(w.getClass()) && w.getClass().isAssignableFrom(_cls);
        return result && visibilityCheck(w);
    }
    
    
    private boolean visibilityCheck(Widget w) {
    	if (!_mustBeShowing)
    		return true;
    	return SWTHierarchyHelper.isVisible(w);
	}

	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Exact Class matcher (" + _cls.getName() + ")";
    }
    
}