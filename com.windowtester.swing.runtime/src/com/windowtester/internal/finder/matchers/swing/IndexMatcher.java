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

import abbot.Log;
import abbot.finder.Matcher;

public class IndexMatcher implements Matcher{

	private Matcher _matcher;
	private int _index;
	private int _current = -1;
	
	public IndexMatcher(Matcher matcher, int index) {
		_index = index;
		_matcher = matcher;
	}
	
	public boolean matches(Component widget) {
		boolean matches = false;
		if(_matcher.matches(widget)) {
			_current++;
            Log.debug("Found match for matcher:\n"+_matcher+"\n Must check index:["+_current+"=="+_index+"]");
			if(_current == _index) {
				matches = true;
			}  
		}
		return matches;
	}
	
	 public String toString() {
	        //return "\nCould NOT MATCH INDEX:"+_index+"\nFor:\n"+_matcher.toString();
	    	return "Index Matcher (" + _matcher + ", " + _index +")";
	 }

}
