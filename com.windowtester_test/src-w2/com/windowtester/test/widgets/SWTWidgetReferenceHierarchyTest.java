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
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.ShellReference;
import com.windowtester.runtime.swt.internal.widgets.WidgetPrinter;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.util.TestCollection;
import com.windowtester.test.util.WidgetCollector;


public class SWTWidgetReferenceHierarchyTest extends BaseTest {

	
	static class Collector implements Visitor {

		List<SWTWidgetReference<?>> widgets = new ArrayList<SWTWidgetReference<?>>();
		
		public <W extends Widget> void visit(SWTWidgetReference<W> widget) {
			widgets.add(widget);
		}
		
		public <T extends SWTWidgetReference<?>> void visitEnter(T composite) {}
		public <T extends SWTWidgetReference<?>> void visitLeave(T composite) {	}	
	}
	
	DisplayReference displayRef = DisplayReference.getDefault();
	LegacyFinderUtil legacyFinder = new LegacyFinderUtil();

	
	@Override
	protected void oneTimeSetup() throws Exception {
		new WidgetPrinter().print();
	}
	
	/*			
	 * NOTE: Of special interest is the Menu here which is "orphaned"; see
	 * com.windowtester.test.widgets.LegacyFinderUtil.addOrphanedMenus(Decorations, ArrayList<Widget>)
	 * for some background.

				Expected:
				0: Menu {*Wrong Thread*}
				1: CBanner {*Wrong Thread*}
				2: StatusLine {*Wrong Thread*}
				3: FastViewBar$4 {*Wrong Thread*}
				4: ProgressRegion$1 {*Wrong Thread*}
				5: Composite {*Wrong Thread*}
				6: TrimCommonUIHandle {*Wrong Thread*}
				7: TrimCommonUIHandle {*Wrong Thread*}
	*/	
	public void testGetRootShellChildren() throws Exception {
		ShellReference shellRef = displayRef.getActiveShell();
		assertSameImmediateChildren(shellRef);
	}

	public void testCBannerChildren() throws Exception {
		List<CBanner> banners = new WidgetCollector(getUI()).all(CBanner.class);
		for (CBanner cBanner : banners) {
			assertSameImmediateChildren(cBanner);
		}
	}
	
	public void testAllWidgetsIncludingOrphanedMenus() throws Exception {
		ShellReference shellRef = displayRef.getActiveShell();
		assertContainsOnly(legacyWidgets(shellRef), widgets(shellRef));
	}
	
	public void testAllWidgetsIgnoringOrphanedMenus() throws Exception {
		ShellReference shellRef = displayRef.getActiveShell();
		assertContainsOnly(legacyWidgets(shellRef), new LegacyFinderUtil().ignoreOrphanedMenus().getAllWidgets(shellRef.getWidget()));
	}
	
	private void assertSameImmediateChildren(Widget w) {
		assertSameImmediateChildren(SWTWidgetReference.forWidget(w));
	}

	private void assertSameImmediateChildren(ISWTWidgetReference<?> ref) {
		assertContainsOnly(legacyImmediateChildren(ref), immediateChildren(ref));
	}

	private void assertContainsOnly(Collection<Widget> expected,
			Collection<Widget> actual) {
		System.out.println("---------------------------------");
		System.out.println("testing: ");
		System.out.println(expected);
		TestCollection.assertContainsOnly(expected, actual);
	}

	public Collection<Widget> legacyImmediateChildren(ISWTWidgetReference<?> ref) {
		return legacyFinder.getImmediateChildren(ref.getWidget());
	}
	
	public Collection<Widget> legacyWidgets(ISWTWidgetReference<?> ref) {
		return legacyFinder.getAllWidgets(ref.getWidget());
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Widget> immediateChildren(ISWTWidgetReference<?> ref) {
		List<Widget> widgets = new ArrayList<Widget>();
		for (ISWTWidgetReference child : ref.getChildren()) {
			widgets.add((Widget) child.getWidget());
		}
		return widgets;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Widget> widgets(SWTWidgetReference<?> ref) {
		Collector collector = new Collector();
		ref.accept(collector);		
		List<Widget> widgets = new ArrayList<Widget>();
		for (ISWTWidgetReference child : collector.widgets) {
			widgets.add((Widget) child.getWidget());
		}
		return widgets;
	}
	
	
}
