package com.windowtester.test.eclipse.locator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.*;

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
public class ViewLocatorSmokeTest extends BaseTest {

	public void testOpenAndCloseViews() throws Exception {
		openViews();
		closeViews();
	}

	@SuppressWarnings("deprecation")
	public void testOpenAndLegacyCloseViews() throws Exception {
		openViews();
		for (View view : views()){
			getUI().close(view.locator());
		}
	}
	
	public void testForName() throws Exception {
		IUIContext ui = getUI();
		openView(ui, View.JAVA_PACKAGEEXPLORER);
		ui.assertThat(ViewLocator.forName("Package Explorer").isVisible());	
		ui.assertThat(ViewLocator.forName("Package Explorer").isActive());	
		ui.assertThat(ViewLocator.forId(View.JAVA_PACKAGEEXPLORER.getViewID()).isVisible());	
		ui.assertThat(ViewLocator.forId(View.JAVA_PACKAGEEXPLORER.getViewID()).isActive());	
	}
	
	
	private void openViews() throws WidgetSearchException {
		for (View view : views())
			openView(getUI(), view);
	}
	
	private void closeViews() throws Exception {
		for (View view : views())
			closeView(getUI(), view);
	}

	private View[] views() {
//		return new View[]{View.BASIC_PROPERTIES};
		return View.values();
	}

	private void closeView(IUIContext ui, View view) throws Exception {
		ViewLocator viewLocator = view.locator();
		//System.out.println("closing view: " + view.getViewID());
//		ui.close(viewLocator);
		ui.ensureThat(viewLocator.isClosed());
		ui.assertThat(viewLocator.isVisible(false));
	}


	
}
