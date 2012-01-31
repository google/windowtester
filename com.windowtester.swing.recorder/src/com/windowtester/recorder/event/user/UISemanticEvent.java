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

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.recorder.event.ISemanticEventHandler;
import com.windowtester.recorder.event.IUISemanticEvent;

/**
 * UISemanticEvents are the semantic counterparts to underlying raw OS UI events. 
 */
public class UISemanticEvent implements IUISemanticEvent, ICheckable {
    
	//created by serialver
	static final long serialVersionUID = -973337197177856958L;
	
    
    /** Cached toString info (depends on widget which may get disposed) 
     * @serial
     */
    private final String _toString;

    /** The associated widget's class name 
     * @serial
     */ 
    private final String _cls;

    /** Cached hierarchy info.
     * @serial
     */
	private IWidgetIdentifier _hierarchyInfo;

	/** The mouse button associated with this event. 
	 * @serial
	 */
	private int _button;
    
	private int _numClicks;

	/**
	 * An optional index for use in index-based selections (like Table Items).
	 * @serial
	 */
	private int _index = -1; //-1 is a sentinel value signalling NO INDEX
  
	
	
	/** Coordinates relative to bounding box for playback 
	 * @serial
	 */
	private int _x;
	private int _y;

	/** Does this event require x,y info for playback?
	 * @serial
	 */
	private boolean _requiresLocationInfo;
	
	/**
	 * Is this a check event?
	 * @serial
	 */
	private boolean _checked;
	
    /**
     * Create an instance.
     * @param event the underlying widget event.
     */
    public UISemanticEvent(EventInfo info) {
        _toString         = info.toString;
        _cls              = info.cls;
        _hierarchyInfo    = info.hierarchyInfo;
        _button           = info.button;
        _x                = info.x;
        _y                = info.y;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
    	if (o == this)
    		return true;
    	if (!(o instanceof UISemanticEvent))
    		return false;
    	UISemanticEvent other = (UISemanticEvent)o;
    	return _cls.equals(other._cls) && _button == other._button && _hierarchyInfo.equals(other.getHierarchyInfo()) && _x == other._x && _y == other._y;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
		int result = 13;
		result = 37*result + ((_cls == null) ? 0 : + _cls.hashCode());
		result = 37*result + _button;
		result = 37*result + ((_hierarchyInfo == null) ? 0 : + _hierarchyInfo.hashCode());
		return result;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return _toString;
    }
    

    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.ISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }


    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.IUISemanticEvent#getItemClass()
     */
    public String getItemClass() {
        return _cls;
    }
    

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#getHierarchyInfo()
	 */
	public IWidgetIdentifier getHierarchyInfo() {
		return _hierarchyInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#setHierarchyInfo(com.windowtester.internal.runtime.IWidgetIdentifier)
	 */
	public void setHierarchyInfo(IWidgetIdentifier id) {
		_hierarchyInfo = id;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#getButton()
	 */
	public int getButton() {
		return _button;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#getX()
	 */
	public int getX() {
		return _x;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#getY()
	 */
	public int getY() {
		return _y;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#isContext()
	 */
	public boolean isContext() {
		return getButton() == 3;
	}
	
	
     /**
     * A data-holder class for containing event info. 
     */
    public static class EventInfo {

    	/** The class name of the underlying widget */
		public String cls;
		/** A simple string representation of the event (for debugging) */
		public String toString;
		
		/** Widget hierarchy info */
		public IWidgetIdentifier hierarchyInfo;

		/** button mask info */
		public int button;
		
		/** Coordinates relative to bounding box for playback */
		public int x;
		public int y;
		
    }


	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#setRequiresLocationInfo(boolean)
	 */
	public void setRequiresLocationInfo(boolean requiresLocationInfo) {
		_requiresLocationInfo = requiresLocationInfo;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#requiresLocationInfo()
	 */
	public boolean requiresLocationInfo() {
		return _requiresLocationInfo;
	}

	/**
	 * Set the number of clicks associated with this event.
	 */
	public void setClicks(int numClicks) {
		_numClicks = numClicks;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.IUISemanticEvent#getClicks()
	 */
	public int getClicks() {
		return _numClicks;
	}
	
	public void setIndex(int index) {
		_index = index;
	}
	
	public int getIndex() {
		return _index;
	}

	public void setX(int x) {
		_x = x;
	}
	
	public void setY(int y) {
		_y = y;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.ICheckable#setChecked(boolean)
	 */
	public void setChecked(boolean checked) {
		_checked = checked;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.ICheckable#getChecked()
	 */
	public boolean getChecked() {
		return _checked;
	}
	
}
