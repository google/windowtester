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
package com.windowtester.recorder.event.user;

import com.windowtester.internal.runtime.event.StyleBits;
import com.windowtester.recorder.event.ISemanticEventHandler;


/**
 * A semantic event that corresponds to an menu selection event.
 */
public class SemanticMenuSelectionEvent extends UISemanticEvent implements ISemanticSelectionEvent {

	//created by serialver
	static final long serialVersionUID = 6027568405838659419L;
	
    /** The menu item's label
     * @serial 
     */
    private String _label;
    
    /** Whether the menu is a context menu
     * @serial 
     */
    private boolean _isContext;

    /** The selected menu item's path String
     * @serial
     */
	private String _pathString;

	/**
	 * An optional style bit.
	 * @serial
	 */
	private int _style = 0;
    
    
    /**
     * Create an instance.
     * @param info
     */
    public SemanticMenuSelectionEvent(EventInfo info) {
        super(info);
        _isContext = info.button == 3;
    }

    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#toString()
     */
    public String toString() {
        return "Menu selection: " + getHierarchyInfo() + "   Path: " + getPathString();
    }
    
 
    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }

    /**
     * @return the menu item's label
     */
    public String getItemLabel() {
        return _label;
    }

    /**
     * @return true if this is a context menu selection
     */
    public boolean isContextMenu() {
    	return _isContext;
    }
    
	/**
	 * Set this menu item's label.
	 * @param label - the label to set.
	 */
	public void setItemLabel(String label) {
		_label = label;
	}

	
	/**
	 * Set the selected menu item's path String.
	 * @param pathString
	 */
	public void setPath(String pathString) {
		_pathString = pathString;
	}
	
	/**
	 * @return the selected menu item's path String
	 */
	public String getPathString() {
		return _pathString;
	}

	/**
	 * Set an (optional) style bit hint.  (Used for identifying pull-downs.)
	 * @see StyleBits
	 */
	public void setStyle(int style) {
		_style |= style;
	}
	
	/**
	 * Get an (optionally set) style bit hint.  (Used for identifying pull-downs.)
	 * @see StyleBits
	 */
	public int getStyle() {
		return _style;
	}
	
	
}
