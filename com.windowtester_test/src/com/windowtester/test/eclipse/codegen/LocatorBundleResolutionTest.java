package com.windowtester.test.eclipse.codegen;

import java.awt.Frame;
import java.io.IOException;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.internal.runtime.bundle.IBundleReference;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.test.PDETestCase;

import static com.windowtester.test.util.Serializer.serializeOutAndIn;

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
public class LocatorBundleResolutionTest extends PDETestCase {

	public void testWLAdaptsToBundleRef() {
		assertNotNull(new WidgetLocator(Widget.class).getAdapter(IBundleReference.class));
	}
	
	public void testSwingWLAdaptsToBundleRef() {
		assertNotNull(new SwingWidgetLocator(Frame.class).getAdapter(IBundleReference.class));
	}

	public void testSWTWLAdaptsToBundleRef() {
		assertNotNull(new SWTWidgetLocator(Widget.class).getAdapter(IBundleReference.class));
	}

	public void testSWTBundleResolves() throws IOException, ClassNotFoundException {
		assertEquals("org.eclipse.ui.forms", getBundleName(swtLocator(Section.class)));
	}

	private String getBundleName(IAdaptable bundleHost) {
		return ((IBundleReference)bundleHost.getAdapter(IBundleReference.class)).getBundleSymbolicName();
	}

	private SWTWidgetLocator swtLocator(Class<?> cls) throws IOException, ClassNotFoundException {
		return serializeOutAndIn(new SWTWidgetLocator(cls));
	}
	
}
