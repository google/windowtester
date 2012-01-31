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
package com.windowtester.test.widgets;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTreeTests extends TestCase {

	public static Test suite() {
		
		TestSuite suite = new TestSuite(
				"TreeItem Reference Tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TreeItemReferenceStaticTreeTest.class);
		suite.addTestSuite(TreeItemReferenceStaticTreeWithImagesTest.class);
		suite.addTestSuite(TreeItemReferenceStaticTreeWithChecksTest.class);
		suite.addTestSuite(TreeItemReferenceStaticTreeWithImagesAndChecksTest.class);
		suite.addTestSuite(TreeItemReferenceLazyTreeTest.class);
		suite.addTestSuite(TreeItemReferenceVirtualTreeTest.class);
		
		//$JUnit-END$
		return suite;
	}

}
