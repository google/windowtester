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

import com.windowtester.recorder.event.ISemanticEvent;




/**
 * A semantic event that corresponds to a text entry event.
 */
public class SemanticTextEntryEvent extends UISemanticEvent implements ICompositeEvent {

	private static final long serialVersionUID = -5072454186777054746L;
	private final SemanticKeyDownEvent[] keys;
	

	public SemanticTextEntryEvent(SemanticKeyDownEvent[] keys) {
		super(new EventInfo()); //NOTE: ignored
		this.keys = keys;
	}

	/**
	 * Get the sequence of keys that make up this text entry event.
	 */
	public SemanticKeyDownEvent[] getKeys() {
		return keys;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.ICompositeEvent#getComponents()
	 */
	public ISemanticEvent[] getComponents() {
		return getKeys();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.recorder.event.user.UISemanticEvent#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i].toString());
			if (i+1 < keys.length)
				sb.append(", ");
		}
		return "TextEntry [" + sb.toString() + "]"; 
	}
	
}
