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
package com.windowtester.runtime.swt.internal.util;


/**
 * Some basic utilities related to parsing and modifying text.
 */
public class TextUtils {

    /**
     * Replace all tabs that have been converted to spaces with a literal tab ("\t") 
     * (for use in Widget labels).  Note: assumes Eclipse default of 4 spaces to a tab. 
     * @param text - the text to fix
     * @return the text re-tabbed 
     */
    public static String fixTabs(String text) {
        return text.replaceAll("    ", "\\t");
    }
    
    
	/**
	 * Escape slashes so that they are not misinterpreted as path delimiters.
	 * <p><p>
	 * For example "Find/Replace" -> "Find\\/Replace"
	 * @param text - the text to fix
	 * @return the text with slashes escaped
	 */
	public static String escapeSlashes(String text) {
		if (isAlreadyEscaped(text))
			return text;
		return doEscape(text);
	}

	private static boolean isAlreadyEscaped(String text) {
		return text.indexOf("\\/") != -1;
	}

	private static String doEscape(String text) {
		return text.replaceAll("/", "\\\\\\\\/");
	}


    
	
	/**
	 * Perform quote and forward slash escaping.
	 * @param text - the text to fix
	 * @return the text properly escaped
	 */
	public static String escapeText(String text) {
		return escapeQuotes(escapeForwardSlashes((text)));
	}
	
	/**
	 * Escape quotes ("), so that the generated string will be legal Java.
	 * <p><p>
	 * For example Say "Hello" -> Say \\"Hello\\"
	 * @param text - the text to fix
	 * @return the text with slashes escaped
	 */
	public static String escapeQuotes(String text) {
		return text.replaceAll("\"", "\\\\\"");
	}
	
	
	/**
	 * Escape forward slashes '\', so that the generated string will be legal Java.
	 * <p><p>
	 * For example "c:/drive" -> "c:\\/drive"
	 * @param text - the text to fix
	 * @return the text with slashes escaped
	 */
	public static String escapeForwardSlashes(String text) {
		return text.replaceAll("\\u005c", "\\\\\\\\");
	}
	
	
	
//!pq: removed for now to remove jdt dependency    
//    /**
//     * Queries the Java core preference store for the default indent character
//     * (tab or space)
//     * @return
//     */
//    public static char getDefinedIndentCharacter() {
//        Hashtable options = JavaCore.getDefaultOptions();
//        Object option = options.get("org.eclipse.jdt.core.formatter.tabulation.char");
//        return "tab".equals(option) ? '\t' : ' '; 
//    }
    
    
    public static void main(String[] args) {
        String one = fixTabs("Foo    Bar");
        String two = "Foo\tBar";
        System.out.println(one.equals(two));        
        System.out.println(escapeSlashes("foo/bar"));
        System.out.println(escapeQuotes("Say \"hello\""));
        System.out.println(escapeText("Say \"hello\" to c:\\dev\\null"));
    }






    
}
