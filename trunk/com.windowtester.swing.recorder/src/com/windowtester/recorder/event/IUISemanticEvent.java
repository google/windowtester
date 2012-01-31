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
package com.windowtester.recorder.event;

import com.windowtester.internal.runtime.IWidgetIdentifier;

public interface IUISemanticEvent extends ISemanticEvent {
	
	/**
	 * Get the number of clicks associated with this event. 
	 */
	int getClicks();
	
	/**
	 * Get a relative index (optional: used for events that are indexed, such as TableItems with respect to columns)
	 */
	int getIndex();
	
	/**
	 * Set a relative index (optional: used for events that are indexed, such as TableItems with respect to columns)
	 */
	void setIndex(int index);
	
	
	/**
	 * @return associated Hierarchy info
	 */
	IWidgetIdentifier getHierarchyInfo();
	
	
	/**
	 * Set associated Hierarchy info
	 */
	void setHierarchyInfo(IWidgetIdentifier id);
	
	/**
	 * @return associated button info
	 */
	int getButton();
	
	/**
	 * @return x coordinate info relative to bounding box
	 */
	public int getX();
	
	/**
	 * @return y coordinate info relative to bounding box
	 */
	public int getY();
	
	/**
	 * @return true if this is a context selection
	 */
	boolean isContext();
	
    /**
     * @return item class (as a string)
     */
    String getItemClass();
	
    
	/**
	 * Set whether this event requires location info for playback.
	 */
	void setRequiresLocationInfo(boolean requiresLocationInfo);
	
	/**
	 * Check whether this event requires location info for playback.
	 */
	boolean requiresLocationInfo();
	
}
