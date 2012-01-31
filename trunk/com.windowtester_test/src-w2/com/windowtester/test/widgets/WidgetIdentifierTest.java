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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.WidgetIdentifier;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.JavaProjectHelper;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper;

@SuppressWarnings("restriction")
public class WidgetIdentifierTest extends BaseTest {

	WidgetIdentifier identifier = WidgetIdentifier.getInstance();
	
	public void testViewTabs() throws Exception {
		idAndDisplayAll(CTabItem.class);
	}
	
	public void testViewToolItems() throws Exception {
		idAndDisplayAll(ToolItem.class);
	}
	
	public void testTreeItems() throws Exception {
		WorkBenchHelper.openPreferences(getUI());
		idAndDisplayAll(TreeItem.class);
	}
	
	
	public void testPackageExplorerTreeItems() throws Exception {
		JavaProjectHelper.createJavaProject(getUI(), getClass().getName() + "Project");
		idAndDisplayAll(TreeItem.class);
	}
	

	private void idAndDisplayAll(Class<? extends Widget> cls) {
		List<? extends Widget> items = findAll(cls);
		for (Widget item : items) {
			idAndDisplay(item);
		}
	}

	private void idAndDisplay(Widget w) {
		IWidgetIdentifier loc = identify(w);
		String str = LocatorJavaStringFactory.toJavaString(loc);
		System.out.println(str);
	}

	IWidgetIdentifier identify(Widget w){
		return identifier.identify(w);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Class<T> cls) {
		List<T> widgets = new ArrayList<T>();
		IWidgetLocator[] refs = getUI().findAll(new SWTWidgetLocator(cls));
		for (IWidgetLocator ref : refs) {
			widgets.add((T)((IWidgetReference)ref).getWidget());
		}
		return widgets;
	}
	
	
}
