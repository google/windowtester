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
 * A semantic event that corresponds to an underlying key down event.
 */
public class SemanticKeyDownEvent extends UISemanticEvent {

	//created by serialver
	static final long serialVersionUID = -3945340122627429093L;
	
    
    /** The pressed key 
     * @serial
     */
    private char _key;

    /** Whether this is a control char sequence.
     * @serial
     */
	private boolean _isControl;


	private int _keyCode;

    /**
     * Create an instance based on the given event info.
     * @param info
     */
    public SemanticKeyDownEvent(EventInfo info) {
        super(info);
    }


    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#toString()
     */
    public String toString() {
        return "KeyDown (" + getKey() +  ")";
    }
    
    

    /* (non-Javadoc)
     * @see com.windowtester.recorder.event.user.UISemanticEvent#accept(com.windowtester.recorder.event.ISemanticEventHandler)
     */
    public void accept(ISemanticEventHandler visitor) {
        visitor.handle(this);
    }
    
    /**
     * @return the key associated with this keydown event
     */
    public String getKey() {
        return Character.toString(_key);
    }
    
    /**
     * Set the key character.
	 * @param key - the key to set.
	 */
	public void setKey(char key) {
		_key = key;
	}
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
    	if (o == this)
    		return true;
    	if (!(o instanceof SemanticKeyDownEvent))
    		return false;
    	SemanticKeyDownEvent other = (SemanticKeyDownEvent)o;
    	return _key == other._key && _isControl == other.isControlSequence();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
		int result = 13;
		result = 37*(result + _key + (_isControl ? 2048 : 0));
		return result;
    }

    /**
     * Set whether this is a control character key sequence.
     * @param isControl
     */
	public void setIsControlSequence(boolean isControl) {
		_isControl = isControl;
	}

	/**
     * Check whether this is a control character key sequence.
     */
	public boolean isControlSequence() {
		return _isControl;
	}

	public void setKeyCode(int keyCode) {
		_keyCode = keyCode;
	}
	
	public int getKeyCode() {
		return _keyCode;
	}
	
	
}
