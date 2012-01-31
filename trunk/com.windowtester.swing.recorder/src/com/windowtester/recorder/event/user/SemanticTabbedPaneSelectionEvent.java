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


public class SemanticTabbedPaneSelectionEvent extends
		SemanticWidgetSelectionEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2957950844051215577L;

	/** The tabbed pane's selected tab title 
     * @serial
     */
    private String _tabLabel;
    
    /**
     *  The selected tab's index
     */
    private int index;
    
	public SemanticTabbedPaneSelectionEvent(EventInfo info) {
		super(info);
		// TODO Auto-generated constructor stub
	}

	public String getTabLabel() {
		return _tabLabel;
	}

	public void setTabLabel(String label) {
		_tabLabel = label;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	
}
