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

import org.eclipse.swt.graphics.Point;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEventHandler;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.ILocator;


/**
 * A semantic event that corresponds to a request to inspect a widget.
 * 
 */
public class SemanticWidgetInspectionEvent extends UISemanticEvent implements IWidgetDescription {

	private static final long serialVersionUID = 4381028716485411126L;
		
	private PropertySet properties = PropertySet.empty();

	private Point hoverPoint;
	
	private int widgetHash;
	
	public SemanticWidgetInspectionEvent(EventInfo info) {
		super(info);
	}
		
	public SemanticWidgetInspectionEvent withProperties(PropertySet properties) {
		this.properties = properties;
		return this;
	}
	
	public SemanticWidgetInspectionEvent withWidgetHash(int hash) {
		this.widgetHash = hash;
		return this;
	}
	
	/**
	 * Create an instance.
	 * @param info
	 * @param context 
	 */
	public SemanticWidgetInspectionEvent(EventInfo info, IUIContext ui) {
		super(info);
		properties = PropertySet.forLocatorInContext(getLocator(), ui);
	}
	
	public SemanticWidgetInspectionEvent atHoverPoint(Point hoverPoint) {
		this.hoverPoint = hoverPoint;
		return this;
	}
	
	/**
	 * Get the cached hashcode of the inspected widget.  This is used to ensure that no more than one request
	 * for inspection be processed for a given widget at the same time.
    */
    public int getWidgetHash() {
    	return widgetHash;
    }
	
    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handleInspectionEvent(this);
    }

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IWidgetDescription#getLocator()
	 */
	public ILocator getLocator() {
		IWidgetIdentifier hierarchyInfo = getHierarchyInfo();
		if (hierarchyInfo instanceof IdentifierAdapter)
			return ((IdentifierAdapter)hierarchyInfo).getLocator();
		if (hierarchyInfo instanceof ILocator)
			return (ILocator)hierarchyInfo;
		return null; //TODO: introduce a null object here
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IWidgetDescription#getProperties()
	 */
	public PropertySet getProperties() {
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IWidgetDescription#getHoverPoint()
	 */
	public Point getHoverPoint() {
		return hoverPoint;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.IWidgetDescription#isSame(com.windowtester.recorder.event.user.IWidgetDescription)
	 */
	public boolean isSame(IWidgetDescription event) {
		if (!(event instanceof SemanticWidgetInspectionEvent))
			return false;
		SemanticWidgetInspectionEvent other = (SemanticWidgetInspectionEvent)event;
		return other.widgetHash == this.widgetHash;
	}
	
	/**
	 * Override in subclasses.
	 * 
	 * @see com.windowtester.recorder.event.user.IWidgetDescription#getDescriptionLabel()
	 */
	public String getDescriptionLabel() {
		return null;
	}
	
}
