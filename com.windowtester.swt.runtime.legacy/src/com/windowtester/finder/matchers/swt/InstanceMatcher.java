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
 * Provides matching of components by instance.
 * 
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * 
 * @author Phil Quitslund
 *
 */
public class InstanceMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.InstanceMatcher {

    /**
     * Create an instance.
     * @param instance - the object on which to match
     */
    public InstanceMatcher(Object instance) {
        super(instance);
    }
    
}