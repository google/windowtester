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

import java.util.Collection;
import java.util.Iterator;

import com.windowtester.codegen.assembly.unit.MethodUnit;


public class FreshMethodNameFinder {	

	private final Collection /*<MethodUnit>*/ _methods;

	public FreshMethodNameFinder(Collection methods) {
		_methods = methods;
	}
	
	public String find(String methodName) {
		for (; ;) {
			if (!containsMethodNamed(methodName))
				return methodName;
			methodName = increment(methodName);			
		}
	}
	
	
	
    public static String increment(String methodName) {
    	ParsedName name = parseName(methodName);
    	if (name.index == -1)
    		name.index = 1; //skip zero
    	else 
    		name.index++;
    	return name.toString();
	}

    
	private boolean containsMethodNamed(String name) {
    	for (Iterator iter = _methods.iterator(); iter.hasNext();) {
			MethodUnit element = (MethodUnit) iter.next();
			if (element.getName().equals(name))
				return true;
		}
    	return false;
	}

    
    
	/**
     * Parse this name into a name piece and an index
     * @param name - the name to parse
     * @return a ParsedName
     */
    public static ParsedName parseName(String name) {
        boolean done = false;
        StringBuffer sb = new StringBuffer();
        int i;
        for (i=name.length()-1; !done && i >= 0; --i) {
            char ch = name.charAt(i);
            if (Character.isDigit(ch))
                sb.append(ch);
            else
                   done = true;
        }
        ParsedName parsedName = new ParsedName();
        parsedName.index = sb.length() == 0 ? -1   : Integer.parseInt(sb.reverse().toString());
        parsedName.name  = sb.length() == 0 ? name : name.substring(0,i+2);
        return parsedName;
    }
	
	
	/**
     * A data holder class for parsed names. 
     */
    static class ParsedName {
        /** The name component */
        public String name;
        /** The integer index */
        public int index;
        
        public String toString() {
        	return name + index;
        }
    }
	
	
}
