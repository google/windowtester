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
package com.windowtester.swt.event.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.SWTHierarchy;
import abbot.finder.swt.TestHierarchy;

/**
 * A helper class that accumulates all the widgets in a given hierarchy
 */
public class WidgetCollector {

    /** The underlying hieracrhy */
    private SWTHierarchy _hierarchy;
    
    /**
     * Create an instance.
     * @param hierarchy
     */
    public WidgetCollector(SWTHierarchy hierarchy) {
        _hierarchy = hierarchy;
    }
    
    /**
     * Create an instance.
     * @param display
     */
    public WidgetCollector(Display display) {
        this(new TestHierarchy(display));
    }

    /**
     * @return the controls contained in this hierarchy instance
     */
    public Widget[] getWidgets() {
        List collected   = new ArrayList(); 
        Collection roots = _hierarchy.getRoots();
        for (Iterator iter = roots.iterator(); iter.hasNext();) {
            Shell shell = (Shell) iter.next();
            Collection ws = _hierarchy.getWidgets(shell);
            for (Iterator iterator = ws.iterator(); iterator.hasNext();) {
                Widget widget = (Widget) iterator.next();
                collected.add(widget);
            }
        }
        return  (Widget[])collected.toArray(new Widget[]{});
    }
    
    
}
