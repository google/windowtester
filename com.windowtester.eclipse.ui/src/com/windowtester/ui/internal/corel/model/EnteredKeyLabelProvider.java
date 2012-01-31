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
package com.windowtester.ui.internal.corel.model;

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.swt.SWT;

import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticTextEntryEvent;

public class EnteredKeyLabelProvider {

	
	public static String getLabel(SemanticKeyDownEvent keyEvent) {
		
    	String key = keyEvent.getKey();
    	if (isTab(key))
    		return  "TAB";
    	if (isEnter(key))
    		return "CR";
    	if (isBackSpace(key))
    		return "BS";
    	
    	int keyCode = keyEvent.getKeyCode();
    	switch(keyCode) {
    		case SWT.ARROW_RIGHT :
    			return "ARROW_RIGHT";
    		case SWT.ARROW_LEFT :
    			return "ARROW_LEFT";
    		case SWT.ARROW_UP :
    			return "ARROW_UP";
    		case SWT.ARROW_DOWN :
    			return "ARROW_DOWN";
    		default:
    			return "'" + key +"'";
    	}
	}
	

	
    private static boolean isEnter(String key) {
		return key.charAt(0) == StringUtils.NEW_LINE.charAt(0) || key.charAt(0) == '\r'; // $codepro.audit.disable platformSpecificLineSeparator
	}

    private static boolean isBackSpace(String key) {
    	return key.charAt(0) == '\b';
    }
    	
    private static boolean isTab(String key) {
    	return key.charAt(0) == '\t';
    }



	public static String getLabel(SemanticTextEntryEvent textEvent) {
		
		SemanticKeyDownEvent[] chars = textEvent.getKeys();
		Stack keys = new Stack();
		
		StringBuffer sb = new StringBuffer("'");
		for (int i = 0; i < chars.length; i++) {
			String character = chars[i].getKey();
			if (isBackSpace(character) && !keys.isEmpty()) {
				keys.pop();
			} else {
				keys.push(character);
			}
		}
		for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
			String character = (String) iterator.next();
			sb.append(character);
		}
		sb.append("'");
		return sb.toString();
	}
    	
    	
}
