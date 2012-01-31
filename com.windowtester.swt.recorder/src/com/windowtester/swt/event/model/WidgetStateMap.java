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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * A map associating Widgets with WidgetState objects
 */
public class WidgetStateMap {

    /** The backing map */
    private Map /*<Widget,IWidgetState>*/ _map = new HashMap();
    
    /**
     * @return the Widget state associated with this Widget.
     */
    public IWidgetState get(Widget c) {
        Object state = _map.get(c);
        //TODO: assert not null here
        return (IWidgetState)state;
    }
   
    /**
     * Put this Widget state mapping into the map
     * @param c - the Widget
     * @param state - the associated state
     * @return previous state associated with Widget, or null if there was no mapping.
     * @see java.util.Map.put(..) 
     */
    public IWidgetState put(Widget c, IWidgetState state) {
        return (IWidgetState)_map.put(c, state);
    }
    
    /**
     * Create a Widget state map from this display.
     */
    public static WidgetStateMap create(Display display) {
        WidgetStateMap map        = new WidgetStateMap();
        WidgetCollector collector = new WidgetCollector(display);
        Widget[] widgets = collector.getWidgets();
        for (int i = 0; i < widgets.length; i++) {
            map.put(widgets[i]);
        }
        return map;
    }

    /**
     * Put this widget and its current state into the map
     * @param widget
     */
    private IWidgetState put(Widget widget) {
          return put(widget, WidgetState.create(widget));
    }
}
