package com.windowtester.test.eclipse.condition;

import junit.framework.TestCase;

import org.eclipse.ui.IPerspectiveDescriptor;

import com.windowtester.runtime.swt.internal.finder.eclipse.PerspectiveFinder;

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
@SuppressWarnings("restriction")
public class PerspectiveFinderTest extends TestCase {


	public void testFindByName() throws Exception {
//		IPerspectiveDescriptor[] perspectives = PerspectiveFinder.getPerspectives();
//		for (IPerspectiveDescriptor perspectiveDescriptor : perspectives) {
//			System.out.println(perspectiveDescriptor.getId());
//			System.out.println(perspectiveDescriptor.getLabel());
//		}
		IPerspectiveDescriptor desc = PerspectiveFinder.findByNameInRegistry("Java");
		assertNotNull(desc);
	}
	
	public void testFindById() throws Exception {
		IPerspectiveDescriptor desc = PerspectiveFinder.findByIdInRegistry("org.eclipse.debug.ui.DebugPerspective");
		assertNotNull(desc);	
	}
	
	
}
