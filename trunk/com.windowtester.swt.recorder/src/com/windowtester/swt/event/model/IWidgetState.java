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

import java.util.Map;

import org.eclipse.swt.widgets.Widget;

/** 
 * Widget state captures the state of a control at a given point in time
 * (used in conjunction with SemanticEvents).  Widget state is captured in a map, 
 * allowing for easy extension with new kinds of state.
 */
public interface IWidgetState {
 
    /**
     * @return the Widget associated with this state
     */
    Widget getWidget();
    
    /**
     * @return the data associated with this Widget
     */
    Object getData();

    /**
     * @return the state map associated with this widget
     */
    Map getStateMap();
    
    /**
     * Put this state vlaue at this state key in the state map
     * @param key
     * @param value
     */
    void put(Object key, Object value);
    
}
