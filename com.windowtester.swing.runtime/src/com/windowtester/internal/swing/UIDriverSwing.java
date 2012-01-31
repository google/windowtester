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
package com.windowtester.internal.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import abbot.finder.AWTHierarchy;
import abbot.script.Condition;
import abbot.tester.ActionFailedException;
import abbot.tester.ComponentLocation;
import abbot.tester.ComponentMissingException;
import abbot.tester.ComponentTester;
import abbot.tester.JComboBoxTester;
import abbot.tester.JListLocation;
import abbot.tester.JTabbedPaneLocation;
import abbot.tester.JTabbedPaneTester;
import abbot.tester.JTableLocation;
import abbot.tester.JTextComponentTester;
import abbot.tester.JTreeLocation;
import abbot.tester.LocationUnavailableException;
import abbot.util.Properties;

import com.windowtester.internal.runtime.preferences.PlaybackSettings;
import com.windowtester.internal.swing.util.KeyStrokeDecoder;
import com.windowtester.internal.tester.swing.JListTester;
import com.windowtester.internal.tester.swing.JTableTester;
import com.windowtester.internal.tester.swing.JTreeTester;
import com.windowtester.runtime.WidgetSearchException;


/**
 * A service that drives UI events.
 * <br>
 * The prefered way to access these functions is through a <code>UIContextSwing</code> instance.
 * 
 * @see com.windowtester.internal.swing.UIContextSwing
 * 
 */
public class UIDriverSwing {

	
	/** Base delay setting. */
    public static int defaultTimeout =
        Properties.getProperty("abbot.robot.default_delay", 30000, 0, 60000);
	
    private static final int SLEEP_INTERVAL = 10;
    
    
    /** A settings object that manages playback-related settings */
	protected PlaybackSettings _settings;
	
    
    public static int getDefaultTimeout() {
		return defaultTimeout;
	}
	
    public static int getDefaultSleepInterval() {
		return SLEEP_INTERVAL;
	}
    
    private Component dragSource;
    private int dragSrcX;
    private int dragSrcY;
	
    
    ////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	// //////////////////////////////////////////////////////////////////////////

    
	public UIDriverSwing(){
		ComponentTester.setTester(JList.class,new JListTester());
		ComponentTester.setTester(JTree.class,new JTreeTester());
		ComponentTester.setTester(JTable.class,new JTableTester());
	}

	///////////////////////////////////////////////////////////////////////////
	//
	// Click actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	
	public Component click(int clickCount,Component w,int x,int y,int mask){
		ComponentTester tester =  ComponentTester.getTester(w);
		dragSource = w;
		dragSrcX = x; 
		dragSrcY = y;
		tester.actionClick(w, x, y, mask, clickCount);
		
		return w;
	}
	
	public Component click(Component owner, String labelOrPath) throws ComponentMissingException, ActionFailedException {  

		// not checking for awt.MenuItem, not a subclass of Component
		ComponentTester tester =  ComponentTester.getTester(owner);
		dragSource = owner;
		
		if (owner instanceof JTable){
			JTableLocation location = new JTableLocation(labelOrPath);
			Point cellLocation = location.getPoint(owner);
			dragSrcX = cellLocation.x; 
			dragSrcY = cellLocation.y;
			((JTableTester)tester).actionSelectCell(owner,new JTableLocation(labelOrPath));
			
		}
		if (owner instanceof JTabbedPane)
			((JTabbedPaneTester)tester).actionSelectTab(owner, new JTabbedPaneLocation(labelOrPath));
		return owner;
	}
	
	public Component clickTreeItem(int clickCount,Component owner,String path,int mask){
//		 convert string to TreePath
		String[] nodeNames = path.split("/");
		TreePath treePath = new TreePath(nodeNames);
		dragSource = owner;
		JTreeLocation location = new JTreeLocation(treePath);
		
		ComponentTester tester =  ComponentTester.getTester(owner);
		((JTreeTester)tester).actionMakeVisible(owner,treePath);
		try {
			Point point = location.getPoint(owner);
			dragSrcX = point.x;
			dragSrcY = point.y;			
		} catch (LocationUnavailableException e) {
				// do nothing
			System.out.println("Caught location unaviable exception");
		}
		((JTreeTester)tester).actionSelectPath(clickCount,owner,treePath,mask);
		return owner;
	}
	
	/**
	 *  click on table cell selection based on row and col
	 * @param owner
	 * @param row
	 * @param col
	 * @return
	 * TODO: check with given string, if any whether we have the right cell
	 */
	public Component clickTable(int clickCount,JTable owner,int row,int col,int mask){
		ComponentTester tester =  ComponentTester.getTester(owner);
		JTableLocation location = new JTableLocation(row,col);
		Point cellLocation = location.getPoint(owner);
		dragSrcX = cellLocation.x; 
		dragSrcY = cellLocation.y;
		dragSource = owner;
		((JTableTester)tester).actionSelectCell(clickCount,owner,new JTableLocation(row,col),mask);
		return owner;
	}
	
	public Component clickMenuItem(JMenuItem owner){
		ComponentTester tester =  ComponentTester.getTester(owner);
		tester.actionSelectMenuItem(owner);
		return owner;
	}
	
	
	public Component clickListItem(int clickCount,JList owner,String labelOrPath, int mask) throws ActionFailedException {
		ComponentTester tester = ComponentTester.getTester(owner);
		dragSource = owner;
		JListLocation location = new JListLocation(labelOrPath);
		Point point = location.getPoint(owner);
		dragSrcX = point.x;
		dragSrcY = point.y;
		((JListTester)tester).actionMultipleClick(owner,clickCount,labelOrPath,mask);
		return owner;
	}
	
	
	public Component clickComboBox(JComboBox owner,String labelOrPath,int clickCount) throws ActionFailedException {
		ComponentTester tester = ComponentTester.getTester(owner);
		if (labelOrPath != null)
			((JComboBoxTester)tester).actionSelectItem(owner,labelOrPath);
		else
			tester.actionClick(owner, new ComponentLocation(), InputEvent.BUTTON1_MASK, clickCount);
		return owner;
	}
	
	/**
	 * Click at a particular position in a text component
	 * @param owner
	 * @param caret
	 * @return
	 */
	public Component clickTextComponent(JTextComponent owner,int caret){
		ComponentTester tester = ComponentTester.getTester(owner);
		try {
			Rectangle rect = owner.modelToView(caret);
			dragSource = owner;
			dragSrcX = rect.x + rect.width/2;
			dragSrcY = rect.y + rect.height/2;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((JTextComponentTester)tester).actionClick(owner,caret);
		return owner;
	}
	
	public Component contextClick(Component widget, String path) throws WidgetSearchException, ActionFailedException{
			ComponentTester tester =  ComponentTester.getTester(widget);
			try {
				tester.actionSelectPopupMenuItem(widget,path);
			} catch (ComponentMissingException e) {
				throw new WidgetSearchException("Cant find menu item"+ path);
			}
			return widget;
	}
		
	
	public Component contextClick(Component widget, int x, int y, String path) throws WidgetSearchException, ActionFailedException {
			ComponentTester tester =  ComponentTester.getTester(widget);
			try {
				tester.actionSelectPopupMenuItem(widget,x,y,path);
			} catch (ComponentMissingException e) {
				throw new WidgetSearchException("Cant find menu item"+ path);
			}
			return widget;
	}
	
	/**
	 * Context click on tree node
	 * @param widget
	 * @param itemPath
	 * @param menuPath
	 * @return
	 * @throws ComponentMissingException
	 * @throws ActionFailedException
	 */
	public Component contextClickTree(JTree widget,String itemPath, String menuPath) throws ComponentMissingException, ActionFailedException {
		String[] nodeNames = itemPath.split("/");
		TreePath path = new TreePath(nodeNames);
		ComponentTester tester = ComponentTester.getTester(widget);
		((JTreeTester)tester).actionMakeVisible(widget,path);
		tester.actionSelectPopupMenuItem(widget,new JTreeLocation(path),menuPath);
		return widget;
	}
	
	/**
	 * Context click on a table
	 * @param widget
	 * @param itemPath
	 * @param menuPath
	 * @return
	 * @throws ComponentMissingException
	 * @throws ActionFailedException
	 */
	public Component contextClickTable(JTable widget, int row,int col, String menuPath) throws ComponentMissingException, ActionFailedException {
		ComponentTester tester = ComponentTester.getTester(widget);
		JTableLocation location = new JTableLocation(row,col);
		tester.actionShowPopupMenu(widget,location);
		tester.actionSelectPopupMenuItem(widget,location,menuPath);
		return widget;
	}
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Primitive mouse action commands
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	public void mouseMove(Component w) {
		mouseMove(w, w.getWidth() / 2, w.getHeight() / 2);
	}
	
	
	public void mouseMove(Component w, int x, int y) {
		ComponentTester tester = ComponentTester.getTester(w);
		tester.mouseMove(w,x,y);
	}
	
	public void mouseMove(int x,int y) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.mouseMove(x,y);
		
	}
	
	/**
	 * Press the mouse.
	 * @param accel - the mouse accelerator.
	 * @since 3.8.1
	 */
	public void mouseDown(int accel) {
		ComponentTester tester =  ComponentTester.getTester(Component.class);
		tester.mousePress(accel);
	}
	
	/**
	 * Release the mouse
	 * @param accel - the mouse accelerator.
	 * @since 3.8.1
	 */
	public void mouseUp(int accel) {
		ComponentTester tester =  ComponentTester.getTester(Component.class);
		tester.mouseRelease(accel);
	}
	
	
    ///////////////////////////////////////////////////////////////////////////
	//
	// Drag and drop actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * @since 3.8.1
	 */
	public void doDragTo(Component target,int x,int y){
		ComponentTester tester = ComponentTester.getTester(Component.class);
        tester.drag(dragSource, dragSrcX, dragSrcY, InputEvent.BUTTON1_MASK);
        tester.mouseMove(target, x, y);
        tester.drop(target,x,y);
        waitForIdle();
		

	}
	
	/**
	 * @since 3.8.1
	 */
	public Point getLocation(Component owner, String path){
		if (owner instanceof JList){
			JListLocation location = new JListLocation(path);
			return location.getPoint(owner);
		}
		if (owner instanceof JTree){
			String[] nodeNames = path.split("/");
			JTreeLocation location = new JTreeLocation(new TreePath(nodeNames));
			return location.getPoint(owner);
		}
		ComponentLocation location = new ComponentLocation();
		return location.getPoint(owner);
		
	}
	
	/**
	 * @since 3.8.1
	 */
	public Point getLocation(Component owner,int row,int col){
		JTableLocation location = new JTableLocation(row,col);
		return location.getPoint(owner);
	}
	
	/**
	 * @since 3.8.1
	 */
	public Point getLocation(Component owner, int index){
		JTextComponentTester tester = (JTextComponentTester) ComponentTester.getTester(JTextComponent.class);
		return tester.scrollToVisible(owner, index);
	}
	
	/**
	 * @since 3.8.1
	 */
	public Point getLocation(Component owner){
		ComponentLocation location = new ComponentLocation();
		return location.getPoint(owner);
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Text entry actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	public void enterText(String txt) throws ActionFailedException  {
		// get the component and the tester
		ComponentTester tester = ComponentTester.getTester(Component.class);
		Component widget = tester.findFocusOwner();
		ComponentTester testerw = ComponentTester.getTester(widget);
//		if(widget instanceof JTextComponent){
//			((JTextComponentTester) testerw).actionEnterText(widget,txt);
//		}
//		else
			tester.actionKeyString(widget,txt);
	}
	
	
	
	// TODO: this needs to be fixed
	public void keyClick(int key) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		
		int[] keys = KeyStrokeDecoder.extractKeys(key);
		int modifiers = KeyStrokeDecoder.extractModifiers(key);
		for (int i=0;i < keys.length;i++)
			tester.actionKeyStroke(keys[i], modifiers);
		
		
	}
		
	public void keyClick(char key) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.keyStroke(key);
	}
		
	public void keyClick(int modifiers, char c) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		boolean shift = (modifiers & InputEvent.SHIFT_MASK) != 0 || 
							(modifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
	    boolean alt = (modifiers & InputEvent.ALT_MASK) != 0 || 
	    					(modifiers & InputEvent.ALT_DOWN_MASK) != 0;
	    boolean ctrl = (modifiers & InputEvent.CTRL_MASK) != 0 ||
	    					(modifiers & InputEvent.CTRL_DOWN_MASK) != 0;
	  
		if(shift)
			tester.actionKeyPress(KeyEvent.VK_SHIFT);
		if (alt)
			tester.actionKeyPress(KeyEvent.VK_ALT);
		if (ctrl)
			tester.actionKeyPress(KeyEvent.VK_CONTROL);
		tester.keyStroke(c);
		waitForIdle();
		
		if (ctrl)
			tester.actionKeyRelease(KeyEvent.VK_CONTROL);
		if (alt)
			tester.actionKeyRelease(KeyEvent.VK_ALT);
		if (shift)
			tester.actionKeyRelease(KeyEvent.VK_SHIFT);
	}	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// "Meta" actions
	//
	///////////////////////////////////////////////////////////////////////////
	
	/** Set the focus on to the given component. */
	
	public void setFocus(Component widget) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.actionFocus(widget);
	}
	
	public void close(Window window ) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.close(window);
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Timing
	//
	///////////////////////////////////////////////////////////////////////////
	
	
	public void waitForIdle() {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.actionWaitForIdle();
	}
	
	
	public void waitForWindowShowing(final String windowName, final int timeout) {
		
		wait(new Condition() {
			public boolean test() {
				return assertComponentShowing(windowName);
			}
			public String toString() { return windowName + " to show"; }
		}, timeout);
		
	}
	
	boolean boolT;
	
	private synchronized boolean assertComponentShowing(final String title){
	
		boolT = false;
//		Collection f = WindowTracker.getTracker().getRootWindows();
		ArrayList componentList = new ArrayList();
		AWTHierarchy h = new AWTHierarchy();
		final Iterator rootIter = h.getRoots().iterator();
		while (rootIter.hasNext()) {
			componentList.addAll(h.getComponents((Component)rootIter.next()));
		}
		componentList.addAll(h.getRoots());
//		f = h.getRoots();
		final Iterator compIter = componentList.iterator();
		while (compIter.hasNext()) {
			Component c = (Component)compIter.next();
			if (c instanceof Frame){
				Frame w = (Frame)c;
				if ( w.isDisplayable() && (w.getTitle().equals(title))) {
					boolT = w.isVisible();
				}
			}
			if (c instanceof Dialog){
				Dialog d = (Dialog)c;
				if ( d.isDisplayable() && (d.getTitle().equals(title))) {
					boolT = d.isVisible();
				}
			}
		}
		return boolT;
	}
	
	
	public void pause(int ms) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.actionDelay(ms);
	}
	
	
	public void wait(Condition condition) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.wait(condition);
	}
	
	public void wait(Condition condition, long timeout) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.wait(condition,timeout);
	}
	
	public void wait(Condition condition, long timeout, int interval) {
		ComponentTester tester = ComponentTester.getTester(Component.class);
		tester.wait(condition,timeout,interval);
	}
	
	
	
}
