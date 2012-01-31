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

import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;

import swing.samples.SwingList;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JListLocator;

public class JListTest extends UITestCaseSwing {
	
	
	private IUIContext ui;
	private JList jlist;
	
	public JListTest(){
		super(SwingList.class);
		System.out.println(System.getProperty("java.class.path"));
	}
	
	protected void setUp() throws Exception {
			ui = getUI();
	}
	
	
	public void testRegularClicks() throws WidgetSearchException {

		IWidgetLocator locator;
	
		ui.wait(new WindowShowingCondition("Swing List"));
		locator = ui.click(new JListLocator("/three/two/one",new SwingWidgetLocator(JViewport.class,
							new SwingWidgetLocator(JScrollPane.class, 0, 
									new SwingWidgetLocator(Box.class)))));
		
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"/three/two/one"});
		
		
		locator = ui.click(new JListLocator("four",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 0, 
						new SwingWidgetLocator(Box.class)))));
		
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly( jlist.getSelectedValues(),new String[]{"four"});
		
			
		ui.click(new JListLocator("two",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 0, 
						new SwingWidgetLocator(Box.class)))));
		
		assertContainsExactly(jlist.getSelectedValues(), new String[]{"two"});
		
		ui.click(new JListLocator("seven",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 0, 
						new SwingWidgetLocator(Box.class)))));
		assertContainsExactly(jlist.getSelectedValues(), new String[]{"seven"});
	
//	}
	
	
//	public void testCtrlClicks() throws WidgetSearchException {
//		IWidgetLocator locator;
		
		ui.click(new JListLocator("one",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 1, 
						new SwingWidgetLocator(Box.class)))));
		
		ui.click(1,new JListLocator("seven",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 1, new SwingWidgetLocator(Box.class)))),
				InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
		
		locator = ui.click(1, new JListLocator("four",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 1, new SwingWidgetLocator(Box.class)))),
				InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
		
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly(jlist.getSelectedValues(), new String[]{"one", "seven", "four"});
//	}
	

//	public void testShiftClicks() throws WidgetSearchException {
//		IWidgetLocator locator;
		
		ui.click(new JListLocator( "five",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 2, 
						new SwingWidgetLocator(Box.class)))));
		
		locator = ui.click(1,new JListLocator("seven",new SwingWidgetLocator(JViewport.class,
				new SwingWidgetLocator(JScrollPane.class, 2, new SwingWidgetLocator(Box.class)))),
				InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
		
		jlist = (JList)((IWidgetReference)locator).getWidget();
		assertContainsExactly(jlist.getSelectedValues(), new String[]{"five", "six", "seven"});	
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
