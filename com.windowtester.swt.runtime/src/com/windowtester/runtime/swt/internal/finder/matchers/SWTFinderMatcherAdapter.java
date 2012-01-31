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
package com.windowtester.runtime.swt.internal.finder.matchers;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * An adapter from an {@link IWidgetMatcher} to an Abbot {@link Matcher}.
 *
 */
/*package */ class SWTFinderMatcherAdapter implements Matcher {

    private final IWidgetMatcher _matcher;
    
    /**
     * Create an instance.
     * @param wm the matcher to adapt.
     */
    SWTFinderMatcherAdapter(IWidgetMatcher wm) {
        _matcher = wm;
    }
    
    /* (non-Javadoc)
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(Widget w) {
        return _matcher.matches(w);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return "SWTMatcherAdapter[" + _matcher +"]";
    }
    
}
