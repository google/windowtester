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
package com.windowtester.finder.matchers.swt;



/** 
 * Provides matching of components by class.  Unlike ClassMatcher,
 * it does not check for assignablity, it checks for the exact class.
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * 
 * @author Phil Quitslund
 *
 */
public class ExactClassMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.ExactClassMatcher {

	
    /**
     * Create an instance.
     * @param cls - the class on which to match
     */
    public ExactClassMatcher(Class cls) {
        super(cls);
    }
    
    /**
     * Create an instance.
     * @param cls- the class on which to match (must not be null)
     * @param mustBeShowing - whether the widget must be showing to match
     */
    public ExactClassMatcher(Class cls, boolean mustBeShowing) {
    	super(cls, mustBeShowing);
    }
       
}