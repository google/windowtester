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
package com.windowtester.runtime.swt.internal.drivers;

import java.util.Iterator;

import com.windowtester.internal.swing.util.PathStringTokenizerUtil;

public class PathString implements Iterable<String>, Iterator<String> {

	private String[] nodes;
	private int currentIndex;
	private final String pathString;

	public PathString(String pathString) {
		this.pathString = pathString;
		this.nodes = PathStringTokenizerUtil.tokenize(pathString);
	}

	public Iterator<String> iterator() {
		return this;
	}

	public boolean hasNext() {
		return currentIndex < nodes.length;
	}
	
	public boolean hasLast() {
		return currentIndex-1 >= 0;
	}
	
	public String last() {
		return nodes[--currentIndex];
	}

	public String next() {
		return nodes[currentIndex++];
	}
	
	public void remove() {
		throw new UnsupportedOperationException("cannot remove items from a path string");
	}

	@Override
	public String toString() {
		return "Path(" + pathString + ")";
	}
	
	public static void main(String[] args) {
		PathString path = new PathString("foo/bar/baz");
		//for (String node : path) {
		//	System.out.println(node);
		//}
		path.next();
		path.last();
		for (String node : path) {
			System.out.println(node);
		}
		
		
	}
	
	
}
