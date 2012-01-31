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
package com.windowtester.internal.swing.util;


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
		return text.replaceAll("/", "\\\\\\\\/");
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
    
    
 /*   public static void main(String[] args) {
        String one = fixTabs("Foo    Bar");
        String two = "Foo\tBar";
        System.out.println(one.equals(two));        
        System.out.println(escapeSlashes("foo/bar"));
        
    }

*/

    
}
