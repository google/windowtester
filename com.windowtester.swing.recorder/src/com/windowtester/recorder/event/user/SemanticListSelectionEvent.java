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
 * A semantic event that corresponds to an underlying list selection event.
 */
public class SemanticListSelectionEvent extends UISemanticEvent implements ISemanticSelectionEvent, IMaskable {

	private static final long serialVersionUID = 1546631055884987253L;
	
	/** The selected item's label*/
	private String _item;

	/** The selection's mouse mask (e.g., InputEvent.CTRL_MASK or InputEvent.SHIFT_MASK) */
	private String _mask;

	/**
	 * Create an instance.
	 * @param info
	 */
	public SemanticListSelectionEvent(EventInfo info) {
		super(info);
	}

	/**
	 * Set the selected item.
	 * @param item
	 */
	public void setItem(String item) {
		_item = item;
	}

	/**
	 * @return the selected item.
	 */
	public String getItem() {
		return _item;
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


	 public String toString() {
	        return "List Item selection: " + getItem() + " , " + getClicks();
	    }
	
}
