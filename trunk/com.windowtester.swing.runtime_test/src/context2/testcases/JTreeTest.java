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

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreePath;

import swing.samples.SwingTree;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;
import com.windowtester.runtime.swing.UITestCaseSwing;
import com.windowtester.runtime.swing.condition.WindowShowingCondition;
import com.windowtester.runtime.swing.locator.JTreeItemLocator;

public class JTreeTest extends UITestCaseSwing {

	
	JTree tree;
	
	private IUIContext ui;
	
	public JTreeTest(){
		super(SwingTree.class);
	}
	
	protected void setUp() throws Exception {
			ui = getUI();
	}
	
	
/*	public void testTreeSelections() throws WidgetSearchException {
		
		IWidgetLocator locator;
		
		locator = ui.click(new JTreeItemLocator("Root/Parent1/Child10/grandChild102",
					new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane1"))));
	
		tree = (JTree)((IWidgetReference)locator).getWidget();
		TreePath path = tree.getSelectionPath();
		int[] items = tree.getSelectionRows();
		assertEquals(1, items.length);
		assertEquals("grandChild102", path.getLastPathComponent().toString());
		
		ui.click(new JTreeItemLocator("Root/Parent3/Child30",
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane1"))));
		path = tree.getSelectionPath();
		items = tree.getSelectionRows();
		assertEquals(1, items.length);
		assertEquals("Child30", path.getLastPathComponent().toString());

	}	
	
*/	
	public void testTreeShiftSelections() throws WidgetSearchException {
		
		ui.wait(new WindowShowingCondition("Swing Tree Example"));
		// tree selections
		tree = doTreeClick(1,"Root/Parent1/Child10/grandChild102","scrollPane1",InputEvent.BUTTON1_MASK);
		TreePath path = tree.getSelectionPath();
		int[] items = tree.getSelectionRows();
		assertEquals(1, items.length);
		assertEquals("grandChild102", path.getLastPathComponent().toString());
		
		doTreeClick(1,"Root/Parent3/Child30","scrollPane1",InputEvent.BUTTON1_MASK);
		path = tree.getSelectionPath();
		items = tree.getSelectionRows();
		assertEquals(1, items.length);
		assertEquals("Child30", path.getLastPathComponent().toString());
		
		
		// test shift clicks
		tree = doTreeClick(1,"Root/Item 0/Node 01","scrollPane2",InputEvent.BUTTON1_MASK);
		doTreeClick(1,"Root/Item 1/Node 10","scrollPane2",InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
		TreePath[] paths = tree.getSelectionPaths();
		assertEquals(3, paths.length);
		
		/*		IWidgetLocator locator;
		locator = ui.click(new JTreeItemLocator("Root/Item 0/Node 01",
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane2"))));
		ui.click(1,new JTreeItemLocator("Root/Item 1/Node 10",
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane2"))),
				InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
		
		tree = (JTree)((IWidgetReference)locator).getWidget();
		TreePath[] paths = tree.getSelectionPaths();
		assertEquals(3, paths.length);
*/		//fail("need to confirm items match too");
//	}
	
	
//	public void testTreeCtrlSelections() throws WidgetSearchException {
		
		// test cntrl clicks
//		IWidgetLocator locator;
		
/*		locator = ui.click(new JTreeItemLocator("Root/Parent1/Child10/grandChild100",
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane1"))));
		ui.click(1,new JTreeItemLocator("Root/Parent1/Child10/grandChild102",
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane1"))),
				InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);

		tree = (JTree)((IWidgetReference)locator).getWidget();
//		TreePath[] paths = tree.getSelectionPaths();
		paths = tree.getSelectionPaths();
		assertEquals(2, paths.length);		
*/		//fail("need to confirm items match too");
	}
	
	
/*	public void testContextMenuSelection() throws WidgetSearchException {
		
		
		ui.contextClick(new JTreeItemLocator("Root/Item 1/Node 11", 
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane2"))),	
			new JMenuItemLocator("choice1"));
	//	assertFalse(previousState == window.choice1);
		
		
		ui.contextClick(new JTreeItemLocator( "Root/Item 2/Node 21", 
				new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane2"))),	
			new JMenuItemLocator("choice2"));
	//	assertFalse(previousState == window.choice2);
	}
	
	
	public void testDoubleClicks() throws WidgetSearchException {
	
		IWidgetLocator locator;
		locator = ui.click(2,new JTreeItemLocator("Root/Parent2",
			new WidgetLocator(JViewport.class,new WidgetLocator(JScrollPane.class,"scrollPane1"))));
		
		tree = (JTree)((IWidgetReference)locator).getWidget();
		TreePath path = tree.getSelectionPath();
		assertTrue(tree.isExpanded(path));	
		
	}
*/	
	/** the test **/
	public JTree doTreeClick(int clickCount,String node,String scrollPane,int mask) throws WidgetSearchException{
		IWidgetLocator locator;
		locator = ui.click(clickCount,new JTreeItemLocator(node,
				new SwingWidgetLocator(JViewport.class,new SwingWidgetLocator(JScrollPane.class,scrollPane))),
				mask);
		JTree tree = (JTree)((IWidgetReference)locator).getWidget();
		return tree;
	}
	
	
}
