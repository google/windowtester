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
package com.windowtester.runtime.swt.internal.abbot.matcher;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.runtime.swt.internal.debug.LogHandler;

import abbot.finder.matchers.swt.NameMatcher;
import abbot.finder.matchers.swt.TextMatcher;
import abbot.finder.swt.Matcher;

/**
 * 
 * A matcher that matches on names or labels.
 * 
 * Names are set using the <code>setData(..)</code> (<code>widget.setData("name", "someWidgetName");</code> 
 * method; labels are found by inspecting the widget's text attribute (this corresponds, 
 * for example, to the human readable text in a button).
 *
 */
public class NameOrLabelMatcher implements Matcher {

	/** The name matcher for matching on names */
	private final NameMatcher _nameMatcher;
	/** The label matcher for matching on labels */
	private final TextMatcher _labelMatcher;

	/**
	 * Create an instance.
	 * @param nameOrLabel the name or label on which to match.
	 */
	public NameOrLabelMatcher(String nameOrLabel) {
		_nameMatcher  = new NameMatcher(nameOrLabel);
		_labelMatcher =  buildTextMatcher(nameOrLabel);
	}

	/**
	 * The widget matches if it has the given name 
	 * (<code>widget.setData("name", "someWidgetName");</code> or label 
	 * (for example the human readable text in a button) that matches the 
	 * specified string.
	 * 
	 * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
	 */
	public boolean matches(Widget w) {
		try {
			return _nameMatcher.matches(w) || _labelMatcher.matches(w);
		} catch(SWTException e) {
			//TODO: push this up into Finder?
			LogHandler.log(e);
			LogHandler.log("SWT exception caught in name matching, defaulting to false in match");
			return false;
		}
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Name Or Label matcher (" + _labelMatcher.getText() + ")";
    }

	public static String trimMenuText(String actual) {
		return StringUtils.trimMenuText(actual);
	}

	/**
     * Static method provided to give access to legacy text matcher.
     */
    public static TextMatcher buildTextMatcher(String text) {
    	return new TextMatcher(text, false) {
			protected boolean stringsMatch(String expected, String actual) {
//				if (expected == null || actual == null)
//					return false;
//				
				//provisional work-around for Text case where we are ignoring text contents...
				if (expected == null || actual == null)
					return expected == actual; //they should both be null for a match

				// match against menu text with '&' and accelerator removed
				if (super.stringsMatch(expected, trimMenuText(actual)))
					return true;
				
				return super.stringsMatch(expected, actual);
			}
		};
    }
    

    
}
