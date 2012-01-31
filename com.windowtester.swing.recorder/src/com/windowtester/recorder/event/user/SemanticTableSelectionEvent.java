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

import com.windowtester.recorder.event.ISemanticEventHandler;

public class SemanticTableSelectionEvent extends UISemanticEvent implements
		ISemanticSelectionEvent, IMaskable {

	
	private static final long serialVersionUID = 6702194273926013313L;

	/** The table item's label 
     * @serial
     */
    private String _label;
    
    /**
     * for context menu selections
     */
    private String _contextMenuPath;
    /**
     * row,col of the table item
     */
    private int row,col;
    
    /** The selection's mouse mask (e.g., InputEvent.CTRL_MASK or InputEvent.SHIFT_MASK) */
	private String _mask;
	
	public SemanticTableSelectionEvent(EventInfo info) {
		super(info);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#toString()
     */
    public String toString() {
    	if (_contextMenuPath != null)
    		return "Table Context Menu selection : " + getItemLabel() + "  Menu selection: " + getContextMenuSelectionPath();
        return "Table Item selection: " + getItemLabel();
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }

    /**
     * Get this item's label.
     * @return the table item's label
     */
    public String getItemLabel() {
        return _label;
    }
    
    /**
	 * Set the table item's label
	 * @param label - the label to set
	 */
	public void setItemLabel(String label) {
		_label = label;
	}

	public void setContextMenuSelectionPath(String menuPath) {
		_contextMenuPath = menuPath;
	}
	
	public String getContextMenuSelectionPath() {
		return _contextMenuPath;
	}
	
	public void setTableItemRow(int r){
		row = r;
	}
	
	public int getTableItemRow(){
		return row;
	}
    
	public void setTableItemCol(int c){
		col = c;
	}
	
	public int getTableItemCol(){
		return col;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IMaskable#getMask()
	 */
	public String getMask() {
		return _mask;
	}

	public void setMask(String _mask) {
		this._mask = _mask;
	}
	
}
