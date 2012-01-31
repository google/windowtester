package com.windowtester.test.eclipse.codegen;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.widgets.Form;

import com.windowtester.internal.runtime.bundle.BundleResolver;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.test.PDETestCase;

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
public class BundleResolverSmokeTest extends PDETestCase {

	
	public void testBasicBundleStatusDialog() throws WidgetSearchException {
		assertEquals("org.eclipse.jface", BundleResolver.bundleNameForClass(StatusDialog.class));
	}
	
	public void testBasicBundleResolutionFormToolkit() throws WidgetSearchException {
		assertEquals("org.eclipse.ui.forms", BundleResolver.bundleNameForClass(Form.class));
	}
	
	public void testBasicBundleResolutionFilteredTree() throws WidgetSearchException {
		assertEquals("org.eclipse.ui.workbench", BundleResolver.bundleNameForClass(FilteredTree.class));
	}

	public void testBasicBundleResolutionWidget() throws WidgetSearchException {
		assertEquals("org.eclipse.swt", BundleResolver.bundleNameForClass(Widget.class));
	}
	
	public void testBasicBundleResolutionIFile() throws WidgetSearchException {
		assertEquals("org.eclipse.core.resources", BundleResolver.bundleNameForClass(IFile.class));
	}	
	
}
