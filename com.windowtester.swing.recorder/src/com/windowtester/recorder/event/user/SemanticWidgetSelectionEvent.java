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

import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEventHandler;
import com.windowtester.runtime.locator.ILocator;



/**
 * A semantic event that corresponds with a widget selection event.
 */
public class SemanticWidgetSelectionEvent extends UISemanticEvent implements ISemanticSelectionEvent, IMaskable {

	//created by serialver
	static final long serialVersionUID = 571223044720450774L;
	
    /** The label of this widget
     * @serial 
     */
    private String _label;

    /** The (optional) mask for the selection of this widget
     * @serial 
     */
	private String _mask;
	
	
    /**
     * Create an instance.
     * @param info
     */
    public SemanticWidgetSelectionEvent(EventInfo info) {
        super(info);
    }
    

    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }

    /**
     * @return the label for this widget
     */
    public String getItemLabel() {
       return _label;
    }

	/**
	 * Set the widget's label.
	 * @param label
	 */
	public void setItemLabel(String label) {
		_label = label;
	}

	public boolean isContext() {
		return getButton() == 3;
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
	
	
	public String toString(){
		return "Widget Selection event: " + getHierarchyInfo();
//		if (_label != null)
//			return ("Widget Selection event " + getItemLabel() );
//		return "Widget Selection event " + getItemClass();
	}
	
	
	public SemanticWidgetSelectionEvent copy() {
		
		EventInfo info = new EventInfo();
		info.x = getX();
		info.y = getY();
		info.button = getButton();
		info.cls = getItemClass();
		info.toString = super.toString();
		
		SemanticWidgetSelectionEvent cloned = new SemanticWidgetSelectionEvent(info);
		cloned.setChecked(getChecked());
		cloned.setClicks(getClicks());
		cloned.setIndex(getIndex());
		cloned.setItemLabel(getItemLabel());
		cloned.setHierarchyInfo(getHierarchyInfo());
		cloned.setMask(getMask());
		cloned.setRequiresLocationInfo(requiresLocationInfo());
		
		return cloned;
	}


	public static SemanticWidgetSelectionEvent forLocator(ILocator locator) {
		EventInfo info = new EventInfo();
		info.hierarchyInfo = new IdentifierAdapter(locator);
		return new SemanticWidgetSelectionEvent(info);
	}

	
}

