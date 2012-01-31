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
package com.windowtester.internal.swing.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;
import abbot.script.ArgumentParser;
import abbot.tester.ComponentTester;
import abbot.util.AWT;


/**
 * A helper class for extracting info from components.
 */
public class ComponentAccessor {
	
	private static Hierarchy _hierarchy = AWTHierarchy.getDefault();
	private static ComponentTester _menuItemTester = ComponentTester.getTester(JMenuItem.class);
	/**
	 * Find the topmost parent menu item.
	 * @param child
	 */
	public static JMenu getRootMenu(JMenuItem child){
		Component parent = null;
		Component popup;
		popup = _hierarchy.getParent(child);
		if (popup instanceof JPopupMenu)
			parent = AWT.getInvoker(popup);
		if (parent instanceof JMenu)
			while ((!((JMenu)parent).isTopLevelMenu())){
				popup = parent.getParent();
				if (popup instanceof JPopupMenu)
					parent = AWT.getInvoker(popup);
		
			}
		return (JMenu)parent;
	}
	
	/**
	 *  get the path to the menu item
	 * @param child
	 * @return
	 */
	public static String extractMenuPath(JMenuItem child) {
		
		Component parent = null;
		Component popup;
		String path = "";
		
		popup = _hierarchy.getParent(child);
		if (popup instanceof JPopupMenu)
			parent = AWT.getInvoker(popup);
		
		if (parent != null)
			path = _menuItemTester.deriveTag(parent);
		if (parent instanceof JMenu)
			while ((!((JMenu)parent).isTopLevelMenu())){
				popup = parent.getParent();
				if (popup instanceof JPopupMenu)
					parent = AWT.getInvoker(popup);
				path =  _menuItemTester.deriveTag(parent) + "/" + path;
			}
		return path;
	}
	
	/**
     * Extract the menu item label string.
     * @param event - the underlying event
     * @return the menu item label string
     */
	public static String extractMenuItemLabel(JMenuItem item) {
        String label = null;
        label = TextUtils.escapeSlashes(_menuItemTester.deriveTag(item));
        label = TextUtils.fixTabs(label);
        return label;
    }
    
    /**
     * Extract the path to menu item, return string in the form of menu1/submenu/choice1
     * @param item
     * @return the path to the menu item
     */
    public static String extractPopupMenuPath(JMenuItem item){
    	String path = _menuItemTester.deriveTag(item);
    	// to get path to menuitem, first get the popup menu
		Component popup = item.getParent();
		Component parent = AWT.getInvoker(popup);
		while (parent instanceof JMenu){
			path = _menuItemTester.deriveTag(parent) + "/" + path;
			popup = parent.getParent();
			parent = AWT.getInvoker(popup);
		}		
		return path;
    }
    
    
    /**
     * Extract the widget label info.
     * @param event
     */
    public static String extractWidgetLabel(Component widget) {
    	String label = null;
    	if (widget instanceof AbstractButton){
    		label = ((AbstractButton)widget).getText();
    		if (label == null)
    			label = "";
    	}
    	return label;
    }
	
    /**
     * extract title from frame / dialog
     */
    public static String extractTitle(Window w){
    	String title = null;
    	if (w instanceof Frame)
    		title = ((Frame)w).getTitle();
    	if (w instanceof Dialog)
    		title = ((Dialog)w).getTitle();
    	
    	return title;
    }
    
    /**
     * Parse the string representation of Treepath which is of the form
     * [Root,node1, node2] to return an array of nodenames.
     * @param String input
     * @return String[] of nodenames for the tree
     */
    public static String[] parseTreePath(String input){
    	input = input.substring(1, input.length()-1);
        // Use our existing utility for parsing a comma-separated list
        String[] nodeNames = ArgumentParser.parseArgumentList(input);
        // Strip off leading space, if there is one
        for (int i=0;i < nodeNames.length;i++) {
            if (nodeNames[i] != null && nodeNames[i].startsWith(" "))
                nodeNames[i] = nodeNames[i].substring(1);
        }	
    	return nodeNames;
    }
    
    /**
     * Take the array of tree node names and assemble into string path 
     * like root / node1/node2
     * @param nodeNames
     * @return
     */
    public static String assemblePath(String[] nodeNames){
    	String path = "";
    	for (int i = 0; i < nodeNames.length -1; i++)    		
    		path = path + nodeNames[i] + "/";

    	path = path + nodeNames[nodeNames.length - 1];
    	return path;
    }

}
