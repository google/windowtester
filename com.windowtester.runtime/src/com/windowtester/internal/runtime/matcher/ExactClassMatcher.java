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
package com.windowtester.internal.runtime.matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;


/** 
 * Provides matching of components by class.  Unlike ClassMatcher,
 * it does not check for asignablity, it checks for the exact class.
 */
public class ExactClassMatcher implements IWidgetMatcher {

	/** The class on which to match */
	private final Class cls;
	
    
    /**
     * Create an instance.
     * @param cls - the class on which to match
     */
    public ExactClassMatcher(Class cls) {
    	if (cls == null)
    		throw new IllegalArgumentException("Class must not be null");
        this.cls = cls;
    }
        
    /**
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(final Object w) {

    	//null check for sanity
    	if (w == null)
    		return false;
    	
    	return classMatches(w);
    }

	private boolean classMatches(final Object w) {
		return cls.isAssignableFrom(w.getClass()) && w.getClass().isAssignableFrom(cls);
	}
    


	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Exact Class matcher (" + cls.getName() + ")";
    }
    
}