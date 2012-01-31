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

import java.awt.Component;

import abbot.finder.Matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * An adapter from an {@link IWidgetMatcher} to an Abbot {@link Matcher}.
 * <p>
 * Created using the {@link AdapterFactory#adapt(IWidgetMatcher)} creation
 * method.
 *
 */
/*package */ class AbbotFinderMatcherAdapter implements Matcher {

    private final IWidgetMatcher _matcher;
    
    /**
     * Create an instance.
     * @param wm the matcher to adapt.
     */
    AbbotFinderMatcherAdapter(IWidgetMatcher wm) {
        _matcher = wm;
    }
    
	/* (non-Javadoc)
	 * @see abbot.finder.Matcher#matches(java.awt.Component)
	 */
	public boolean matches(Component c) {
		return _matcher.matches(c);
	}

}
