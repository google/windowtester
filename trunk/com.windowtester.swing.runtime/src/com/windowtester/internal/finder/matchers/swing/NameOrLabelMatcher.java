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
package com.windowtester.internal.finder.matchers.swing;

import java.awt.Component;

import abbot.finder.matchers.AbstractMatcher;
import abbot.finder.matchers.NameMatcher;


/***
 * Provides matching with either name or label
 * 
 * Names are set using the <code>setName(..)</code> (<code>component.setName("name");</code> 
 * method; labels are found by inspecting the component's text attribute (this corresponds, 
 * for example, to the human readable text in a button).
 */


public class NameOrLabelMatcher extends AbstractMatcher {
	
	/** The name matcher for matching on names */
	private final NameMatcher _nameMatcher;
	/** The label matcher for matching on labels */
	private final TxtMatcher _labelMatcher;
	
	

    /** Construct a matcher that will match any component that has
        explicitly been assigned the given <code>name</code>.  Auto-generated
        names (e.g. <code>win0</code>, <code>frame3</code>, etc. for AWT
        (native) based components will not match.
    */
    public NameOrLabelMatcher(String nameOrLabel) {
    	_nameMatcher  = new NameMatcher(nameOrLabel);
		_labelMatcher =  new TxtMatcher(nameOrLabel, false){
		protected boolean stringsMatch(String expected, String actual) {
			if (expected == null || actual == null)
				return expected == actual; //they should both be null for a match
			String trimmed = actual;
			int index = trimmed.indexOf('\t');
			if (index != -1)
				trimmed = trimmed.substring(0, index);
			index = trimmed.indexOf('&');
			if (index != -1)
				trimmed = trimmed.substring(0, index) + trimmed.substring(index + 1);
			if (expected.equals(trimmed))
				return true;
			return super.stringsMatch(expected, actual);
		}
	}; 

    }

    /**
	 * The widget matches if it has the given name 
	 * (<code>widget.setName("name");</code> or label 
	 * (for example the human readable text in a button) that matches the 
	 * specified string.
	 * 
	 * @see abbot.finder.Matcher#matches(t)
	 */
	public boolean matches(Component w) {
		try {
			return _nameMatcher.matches(w) || _labelMatcher.matches(w);
		} catch(Exception e) {
			//TODO: push this up into Finder?
		//	LogHandler.log(e);
		//	LogHandler.log("Exception caught in name matching, defaulting to false in match");
			return false;
		}
	}

	
	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Name Or Label matcher (" + _labelMatcher.getText() + ")";
    }     

}
