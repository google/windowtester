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

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class WidgetState implements IWidgetState {

    /** The state map */
    private final Map _map = new HashMap();
   
    /** The associated widget */
    private final Widget _widget;
    
    /** The data associated with the underlying Widget at time of state storing */
    private final Object _data;
   
    /**
     * Create an instance.
     * @param widget
     */
    public WidgetState(Widget widget) {
        put(IWidgetStateConstants.DISPOSED, widget.isDisposed());
        _data       = widget.getData();
        _widget     = widget;
    }
       
    /**
     * Create an instance -- a convenience constructor for controls.
     * @param w - the widget 
     * @param c - its control
     */
    public WidgetState(Widget w, Control c) {
        this(w);
        put(IWidgetStateConstants.ENABLED, c.isEnabled());
        put(IWidgetStateConstants.VISIBLE, c.isVisible());
    }

    /**
     * A factory method that captures and returns the Widget state for a given control.
     * 
     * TODO: state collection needs to be sanity-checked
     */
    public static WidgetState get(Widget w) {
        
        //A base state object, which will have state added or returned as the default
        WidgetState base = new WidgetState(w);
        
        if (w instanceof Control) {
            Control c = (Control)w;
            return new WidgetState(w, c);
        }
        
//- Items ---------------------------------------------------------------------------------------
        else if (w instanceof MenuItem) {
            MenuItem mi = (MenuItem)w;
            base.put(IWidgetStateConstants.ENABLED, mi.isEnabled());
        }
        else if (w instanceof CoolItem) {
            CoolItem ci = (CoolItem)w;
            Control c   = ci.getControl(); 
            return new WidgetState(w, c);           
        }
        else if (w instanceof CTabItem) {
            CTabItem cti = (CTabItem)w;
            Control c    = cti.getControl(); 
            return new WidgetState(w, c);           
        }
        else if (w instanceof TabItem) {
            TabItem ti = (TabItem)w;
            Control c   = ti.getControl(); 
            return new WidgetState(w, c);           
        }
        else if (w instanceof TableColumn) {
            TableColumn tc = (TableColumn)w; 
            base.put(IWidgetStateConstants.TEXT, tc.getText());
        }
        else if (w instanceof TableTreeItem) {
            TableTreeItem tti = (TableTreeItem)w;
            //but other stuff is: grayed, expanded, etc.
            base.put(IWidgetStateConstants.CHECKED,   tti.getChecked());
            base.put(IWidgetStateConstants.GRAYED,    tti.getGrayed());
            base.put(IWidgetStateConstants.EXPANDED,  tti.getExpanded());
            base.put(IWidgetStateConstants.TEXT,      tti.getText());
        }
        else if (w instanceof ToolItem) {
            ToolItem ti = (ToolItem)w;
            Control c   = ti.getControl();
            return new WidgetState(w, c);   
        }
        else if (w instanceof TrayItem) {
            TrayItem tri = (TrayItem)w;
            base.put(IWidgetStateConstants.TEXT, tri.getText()); 
        }
        else if (w instanceof TreeItem) {
            TreeItem tre = (TreeItem)w;
            base.put(IWidgetStateConstants.CHECKED,   tre.getChecked());
            base.put(IWidgetStateConstants.GRAYED,    tre.getGrayed());
            base.put(IWidgetStateConstants.EXPANDED,  tre.getExpanded());
            base.put(IWidgetStateConstants.TEXT,      tre.getText());
        }
//-----------------------------------------------------------------------------------------------

        else if (w instanceof Control) {
            Control c = (Control)w;
            return new WidgetState(w, c);
        }
        
        else if (w instanceof Menu) {
            Menu m = (Menu)w;
            base.put(IWidgetStateConstants.ENABLED, m.isEnabled());
            base.put(IWidgetStateConstants.VISIBLE, m.isVisible());
        }
        
        else if (w instanceof ScrollBar) {
            ScrollBar sb = (ScrollBar)w;
            base.put(IWidgetStateConstants.ENABLED, sb.isEnabled());
            base.put(IWidgetStateConstants.VISIBLE, sb.isVisible());
        }
        
        //a fall-through
        return new WidgetState(w);
        //ignoring (for now): Tray, Tracker, Caret, Drag{Source,Target}
    }
    

    /**
     * A convenience method that converts the boolean value to a Boolean
     */
    private void put(Object key, boolean value) {
        put(key, Boolean.valueOf(value));
    }

    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.IWidgetState#put(java.lang.Object,
     *      java.lang.Object)
     */
    public void put(Object key, Object value) {
        _map.put(key, value);
    }

    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.IWidgetState#getData()
     */
    public Object getData() {
        return _data;
    }

    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.IWidgetState#getWidget()
     */
    public Widget getWidget() {
        return _widget;
    }

    /* (non-Javadoc)
     * @see com.windowtester.swt.event.model.IWidgetState#getStateMap()
     */
    public Map getStateMap() {
        return _map;
    }

    /**
     * Create a state object for the given widget, capturing its current state data.
     * @param widget
     * @return
     */
    public static IWidgetState create(Widget widget) {
        return new WidgetState(widget);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer();
        sb.append(_widget.toString()).append(" data: ").append(_data).append(" state map: ").append(_map);
        return sb.toString();
    }
    
}
