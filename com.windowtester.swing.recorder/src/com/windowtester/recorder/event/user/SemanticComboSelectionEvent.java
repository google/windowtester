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


/**
 * A semantic event that corresponds to an underlying combo selection event.
 */
public class SemanticComboSelectionEvent extends UISemanticEvent implements ISemanticSelectionEvent {

	private static final long serialVersionUID = 5611877095452505923L;

	private String _selection;
	
	/**
	 * Create an instance.
	 * @param info
	 */
	public SemanticComboSelectionEvent(EventInfo info) {
		super(info);
	}

	public void setSelection(String selection) {
		_selection = selection;
	}
	
	public String getSelection() {
		return _selection;
	}
	
	public String toString(){
		return "ComboBox selection: " + getSelection();
	}
	
	
}
