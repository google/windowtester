package com.windowtester.test.runtime;

import static com.windowtester.runtime.swt.internal.util.PathStringTokenizerUtil.tokenize;
import junit.framework.TestCase;

import static com.windowtester.test.util.TestCollection.assertContainsOnly;

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
public class PathStringTokenizerTest extends TestCase {
	
	public void testNoEscape() throws Exception {
		assertTokenizesTo("foo/bar/baz", "foo", "bar", "baz");
	}
	
	public void testEscape() throws Exception {
		assertTokenizesTo("foo\\bar", "foo\\bar");
	}
	
	public void testEscape1() throws Exception {
		assertTokenizesTo("foo/bar\\baz", "foo", "bar\\baz");
	}	
	
	//http://fogbugz.instantiations.com//default.php?13961
	public void testEscape2() throws Exception {
		assertTokenizesTo("ProjectName [svn\\/annotation]/src", "ProjectName [svn/annotation]", "src");
	}
	
	public void testEscape3() throws Exception {
		assertTokenizesTo("File/Search\\/Replace", "File", "Search/Replace");
	}


	public void testEscape4() throws Exception {
		assertTokenizesTo("parent/child 2/grand\\/children...", "parent", "child 2", "grand/children...");
	}		
	
	public void testDoubleEscape() throws Exception {
		assertTokenizesTo("foo\\\\bar", "foo\\\\bar");
	}
	
	public void testDoubleEscape1() throws Exception {
		assertTokenizesTo("foo/bar\\\\baz", "foo", "bar\\\\baz");
	}
	
	public void testTripleEscape() throws Exception {
		assertTokenizesTo("foo\\\\\\bar", "foo\\\\\\bar");
	}
	
	public void testTripleEscape1() throws Exception {
		assertTokenizesTo("foo/bar\\\\\\baz", "foo", "bar\\\\\\baz");
	}
	
	private void assertTokenizesTo(String path, String ... expectedTokens) {
		String[] actualTokens = tokenize(path);
		assertContainsOnly(expectedTokens, actualTokens);
	}

	
}
