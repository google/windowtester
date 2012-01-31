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
package com.windowtester.codegen.util;

import java.util.StringTokenizer;

import com.windowtester.codegen.ICodeGenPluginTraceOptions;
import com.windowtester.internal.debug.Tracer;
import com.windowtester.recorder.event.user.SemanticTreeItemSelectionEvent;
import com.windowtester.recorder.event.user.TreeEventType;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A helper class for creating useful code snippets that drive a given UIContext
 * variable/field instance.
 */
public class CodeGenSnippetBuilder implements ICodeGenConstants {

	/** The name of the UIContextInstance*/
	private final String _instanceName;

	/**
	 * Create builder.
	 * @param instanceName - name of the UIContext instance variable
	 */
	public CodeGenSnippetBuilder(String instanceName) {
		_instanceName = instanceName;
	}

    /** 
     * Generate a text String snippet that enters a given bit of text. 
	 * @param text  - the text to enter
	 * @return the String snippet
	 */
	public String enterTextSnippet(String text) {
		text = handleEscapes(text);
		StringBuffer sb = new StringBuffer();
		sb.append(_instanceName).append(".enterText(\"").
		  	append(text).append("\");").append(NEW_LINE);
		return sb.toString();
	}

	/*
	 * Fix escape characters in this String of text.
	 */
    public static String handleEscapes(String text) {
    	
    	
    	return handleQuotes(handleSlashes(text));
    	
 
	}

	private static String handleSlashes(String text) {
	   	StringBuffer sb = new StringBuffer();
    	/**
    	 * Regexps are giving me fits.  Doing it the old fashioned way.
    	 */
    	StringTokenizer tok = new StringTokenizer(text, "\\");
    	if (tok.countTokens() < 2) {
    		//check for trailing case:
    		if (text.charAt(text.length()-1) == '\\') {
    			sb.append(text.subSequence(0, text.length()-1)).append("\\\\");
    			return sb.toString();
    		}
    		return text;
    	}
    		
    	sb.append(tok.nextToken());
    	while(tok.hasMoreElements()) {
    		//System.out.println();
    		sb.append("\\\\").append(tok.nextToken());
    	}
    	
		//check for trailing case:
		if (text.charAt(text.length()-1) == '\\') {
			sb.append("\\\\");
		}
    	
    	return sb.toString();
	}

	private static String handleQuotes(String text) {
		return text.replaceAll("\"", "\\\\\"");
	}

	/** 
     * Generate a text String snippet that enters a given key. 
	 * @param text  - the key to enter
	 * @return the String snippet
	 */
	public String enterKeySnippet(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append(_instanceName).append(".keyClick(").
		  	append(text).append(");").append(NEW_LINE);
		return sb.toString();
	}
	
    /** 
     * Generate a text String snippet that enters a given key w/modifier.
     * @param modifier - the key modifier (e.g., "SWT.CTRL") 
	 * @param key  - the key to enter
	 * @return the String snippet
	 */	
	public String enterKeySnippet(String modifier, String key) {
		StringBuffer sb = new StringBuffer();
		sb.append(_instanceName).append(".keyClick(").append(modifier).append(", \'").
		  	append(key).append("\');").append(NEW_LINE);
		return sb.toString();
	}
	
    /**
     * Generate a text String snippet that clicks a button with a given label. 
	 * @param label - the label of the button to click
	 * @return the String snippet
	 */
	public String clickButtonSnippet(String label) {
		return genericWidgetSelection(label, -1, null, 1);
	}

	
    /**
     * Generate a text String snippet that closes a shell with a given label. 
	 * @param label - the label of the widget to close
	 * @return the String snippet
	 */
	public String closeShellSnippet(String label) {
		StringBuffer sb = new StringBuffer();
		sb.append(_instanceName).append(".close(\"").
		  	append(label).append("\");").append(NEW_LINE);
		return sb.toString();
	}
	
	
    /**
     * Generate a text String snippet that clicks a widget with a given label. 
	 * @param label - the label of the widget to click
     * @param index - a relative index (such as a column)
     * @param mask - the button mask
     * @param numClicks - the number of button clicks
	 * @return the String snippet
	 */
	public String genericWidgetSelection(String label, int index, String mask, int numClicks) {
		StringBuffer sb = new StringBuffer();
		String cmd = (numClicks == 2) ? "doubleClick" : "click"; 
		sb.append(_instanceName).append(".").append(cmd).append("(\"").append(label).append("\"");
        if (index != -1)
        	sb.append(", \"").append(index).append("\"");
		if (mask != null)
        	sb.append(", ").append(mask);
        sb.append(");").append(NEW_LINE);
		return sb.toString();
	}
	
    /**
     * Generate a text String snippet that clicks a widget with a given label. 
	 * @param label - the label of the widget to click
     * @param index - a relative index (-1 means none)
     * @param mask - the button mask
     * @param numClicks - the number of button clicks
     * @param x - x coordinate info
     * @param y - y coordinate info
	 * @return the String snippet
	 */
	public String genericWidgetSelection(String label, int index, String mask, int numClicks, int x, int y) {
		StringBuffer sb = new StringBuffer();
		String cmd = (numClicks == 2) ? "doubleClick" : "click"; 
		sb.append(_instanceName).append(".").append(cmd).append("(\"").append(label).append("\", ");
        if (index != -1)
        	sb.append(", \"").append(index).append("\"");
		sb.append(x).append(", ").append(y);
		
		if (mask != null)
        	sb.append(", ").append(mask);
        sb.append(");").append(NEW_LINE);
		return sb.toString();
	}
	
	
	
	public String listSelection(String label, String item, String mask, int numClicks) {
		StringBuffer sb = new StringBuffer();
		String cmd = (numClicks == 2) ? "doubleClick" : "click"; 
        sb.append(_instanceName).append(".").append(cmd).append("(\"").append(label).append("\", \"").append(item).append("\"");
        if (mask != null)
        	sb.append(", ").append(mask);
        sb.append(");").append(NEW_LINE);
		return sb.toString();
	}
	
	public String comboSelection(String label, String item) {
		StringBuffer sb = new StringBuffer();
        sb.append(_instanceName).append(".click(\"").append(label).append("\", \"").append(item).append("\");").append(NEW_LINE);
		return sb.toString();
	}
	
	
	public String moveSnippet(String label, int x, int y) {
		StringBuffer sb = new StringBuffer();
        sb.append(_instanceName).append(".move(\"").append(label).append("\", ").append(x).append(", ").append(y).append(");").append(NEW_LINE);
		return sb.toString();
	}
	
	public String mouseMoveToSnippet(String label, int x, int y) {
		StringBuffer sb = new StringBuffer();
        sb.append(_instanceName).append(".mouseMove(\"").append(label).append("\", ").append(x).append(", ").append(y).append(");").append(NEW_LINE);
		return sb.toString();
	}
	
	public String dragToSnippet(String label, String path, int x, int y) {
		StringBuffer sb = new StringBuffer();
        sb.append(_instanceName).append(".dragTo(\"").append(label).append(", ");
        if (path != null)
        	sb.append("\"").append(path).append("\", ");
        sb.append(x).append(", ").append(y).append(");").append(NEW_LINE);
		return sb.toString();
	}
	
	public String resizeSnippet(String label, int width, int height) {
		StringBuffer sb = new StringBuffer();
        sb.append(_instanceName).append(".resize(\"").append(label).append("\", ").append(width).append(", ").append(height).append(");").append(NEW_LINE);
		return sb.toString();
	}

	public String methodInvocation(String method) {
		StringBuffer sb = new StringBuffer();
        sb.append(method).append("();").append(NEW_LINE);
		return sb.toString();
	
	}
	
//	public String setFocus(String label) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(_instanceName).append(".setFocus(\"").append(label).append("\");").append(NEW_LINE);
//        return sb.toString();
//	}
	
	
	
	/**
	 * Generate a text String snippet that invokes a menu with a given path. 
	 * @param index 
	 * @param path - the menu's path
	 * @param button 
	 * @param string 
	 * @return the String snippet
	 */
	public String invokeMenuItemSnippet(String handle, int index, String path, int button) {
		StringBuffer sb = new StringBuffer();
		path = escapeTabs(path);
		String cmd = button == 3 ? "contextClick" : "click";
        sb.append(_instanceName).append('.').append(cmd).append("(\"").append(handle).append("\", \"");
        if (index != -1)
        	sb.append(index).append("\", ");
        sb.append(path).append("\");").append(NEW_LINE);
		return sb.toString();
		
	}


	/**
	 * Generate a text String snippet that selects a tree item.... 
	 * @param path - the menu's path
	 * @param shellTitle - the title of the containing shell
	 * @param type - the type of selection event
	 * @return the String snippet
	 */
	public String treeItemSelectSnippet(String label, SemanticTreeItemSelectionEvent event) {
		TreeEventType type = event.getType();
		String path           = event.getPathString();
		
		StringBuffer sb = new StringBuffer();
		//expands/collapses
		if (type == TreeEventType.EXPAND || type == TreeEventType.COLLAPSE) {
			//FIXME
            Tracer.trace(ICodeGenPluginTraceOptions.CODEGEN, "Expand/collapse events unsupported...");
			sb.append("//***expand/collapse events unsupported...").append(NEW_LINE);
		//clicks
		//click(find(FILE_TYPE_TREE), "File Type/Plain Text");
		} else if (type == TreeEventType.SINGLE_CLICK || type == TreeEventType.DOUBLE_CLICK) {
			
			//TODO: triple+ clicks
			String cmd = type == TreeEventType.SINGLE_CLICK ? "click" : "doubleClick";
			
			//context clicks (override clicks/double-clicks)
			if (event.getButton() == 3)
				cmd = "contextClick";
			
			sb.append(_instanceName).append('.').append(cmd).append("(\"").append(label).append("\", \"").
				append(path);
			if (event.getButton() == 3) {
				sb.append("\", \"").append(escapeTabs(event.getContextMenuSelectionPath()));
			}
			if (event.getChecked()) {
				String button = "SWT.BUTTON" + event.getButton(); 
				sb.append("\", ").append(button).append(" | SWT.CHECK").append(");").append(NEW_LINE);
			} else
				sb.append("\");").append(NEW_LINE);
		}	
		return sb.toString();
	}

	
	/**
	 * Escape all tabs '\t' in this string ("\\t").  
	 * @param str - the string to escape
	 * @return a tab-escaped string
	 */
	private static String escapeTabs(String str) {	
		return str.replaceAll("\t", "\\\\t");	
	}

}
