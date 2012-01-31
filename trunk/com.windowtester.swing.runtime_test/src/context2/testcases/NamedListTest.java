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
package context2.testcases;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.locator.JListLocator;
import com.windowtester.runtime.swing.locator.NamedWidgetLocator;

public class NamedListTest extends UITestCaseSwing {
	
	private IUIContext ui;
	private JList jlist;
	
/*	public NamedListTest(){
		super(SwingList.class);
	}
	
	protected void setUp() throws Exception {
		ui = getUI();
	}
*/	
	
	public void testNamedLists() throws WidgetSearchException{
		IWidgetLocator locator;
		
		ui = getUI();
		// named locator
		locator = ui.click(new JListLocator("one",new NamedWidgetLocator("list1")));
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"one"});
		
		locator = ui.click(new JListLocator("four",new NamedWidgetLocator("list2")));
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"four"});
		
		locator = ui.click(new JListLocator("seven",new NamedWidgetLocator("list3")));
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"seven"});
		
		locator = ui.click(new JListLocator("five",new NamedWidgetLocator("list1")));
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"five"});
	}
	
	
    ////////////////////////////////////////////////////////////////////////
	//
	// Assertion helpers
	//
	////////////////////////////////////////////////////////////////////////
	
	public void assertContainsExactly(IStructuredSelection selection, Object[] elems) {
		assertContainsExactly(selection.toList(), Arrays.asList(elems));
	}
	
	public void assertContainsExactly(Collection host, Collection elems) {
		assertTrue(host.containsAll(elems));
	}
	

	public void assertContainsExactly(Object[] hosts, Object[] elems) {
		assertContainsExactly(Arrays.asList(hosts), Arrays.asList(elems));
	}

	public void assertTextEquals(String expected, StyledText text) {
		String result = text.getText();
		assertEquals(expected, result);
	}
	

}
