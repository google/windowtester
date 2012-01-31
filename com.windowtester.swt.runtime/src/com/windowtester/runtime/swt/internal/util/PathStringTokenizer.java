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

import java.util.StringTokenizer;

/**
 * A tokenizer that handles escaped delimiters in path Strings.
 */
public class PathStringTokenizer extends StringTokenizer {

	private String[] _elements;
	private int _index;
	
	/**
	 * Create an instance.
	 * @param path
	 */
	public PathStringTokenizer(String path) {
		super(path);
		_elements = PathStringTokenizerUtil.tokenize(path);
	}

	/**
	 * @see java.util.StringTokenizer#countTokens()
	 */
	public int countTokens() {
		return _elements.length - _index;
	}
	
	/**
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}
	
	
	/**
	 * @see java.util.StringTokenizer#hasMoreTokens()
	 */
	public boolean hasMoreTokens() {
		return _index < _elements.length;
	}
	
	
	/**
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		return nextToken();
	}
	
	/**
	 * @see java.util.StringTokenizer#nextToken()
	 */
	public String nextToken() {
		return _elements[_index++];
	}
	
	/**
	 * NOTE: not intended to be used! (DELIMITER IS SET)
	 * @see java.util.StringTokenizer#nextToken(java.lang.String)
	 */
	public String nextToken(String delim) {
		return nextToken();
	}
	
	
}



