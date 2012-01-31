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


/**
 * A semantic event that corresponds with a tree item selection.
 */
public class SemanticTreeItemSelectionEvent extends UISemanticEvent implements ISemanticSelectionEvent, IMaskable {
    
	//created by serialver
	static final long serialVersionUID = -4151543245642221111L;
	
    /** The tree item's label 
     * @serial
     */
    private String _label;
    
    /** The type of event 
     * @serial
     */
	private TreeEventType _type;

	/** The path String that identifies this tree item with respect to its parents 
	 * @serial
	 */
	private String _pathString;
	
	/** The (optional) mask for the selection of this widget
     * @serial 
     */
	private String _mask;

	private String _contextMenuPath;
	
    /**
     * Create an instance.
     * @param info - the underlying event
     * @param numberOfClicks - the number of clicks
     */
    public SemanticTreeItemSelectionEvent(EventInfo info, TreeEventType type) {
        super(info);
		_type = type;
    }


    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#toString()
     */
    public String toString() {
    	if( _contextMenuPath != null)
    		return "Tree Context Click : " + getItemLabel() + " Menu selection : " + getContextMenuSelectionPath();
        return "Tree Item selection: " + getItemLabel();
    }
    

    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }

    /**
     * Get this item's label.
     * @return the tree item's label
     */
    public String getItemLabel() {
        return _label;
    }

    /**
     * Get this event's type.
	 * @return the type.
	 */
	public TreeEventType getType() {
		return _type;
	}
	
	/**
	 * Get the String that describes this item's path info (position relative to its parents).
	 * @return the path String.
	 */
	public String getPathString() {
		return _pathString;
	}
	
	/**
	 * Set the tree item's label
	 * @param label - the label to set
	 */
	public void setItemLabel(String label) {
		_label = label;
	}

	/**
	 * Set the item's path
	 * @param path
	 */
	public void setItemPath(String path) {
		_pathString = path;
	}
	/**
	 * Set the item's type
	 * @param type
	 */
	public void setType(TreeEventType type) {
		_type = type;
	}
	
	/**
	 * Set the selection buton mask String.
	 * @param mask
	 */
	public void setMask(String mask) {
		_mask = mask;		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IMaskable#getMask()
	 */
	public String getMask() {
		return _mask;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.UISemanticEvent#setClicks(int)
	 */
	public void setClicks(int numClicks) {
		super.setClicks(numClicks);
		//also need to set value in type
		_type = (numClicks == 2) ? TreeEventType.DOUBLE_CLICK : TreeEventType.SINGLE_CLICK;
	}

	public void setContextMenuSelectionPath(String menuPath) {
		_contextMenuPath = menuPath;
	}
	
	public String getContextMenuSelectionPath() {
		return _contextMenuPath;
	}
	
	
}
