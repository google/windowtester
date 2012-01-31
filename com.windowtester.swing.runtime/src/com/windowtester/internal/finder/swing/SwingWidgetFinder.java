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
package com.windowtester.internal.finder.swing;

import java.awt.Component;
import java.awt.Window;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;
import abbot.finder.Matcher;

import com.windowtester.internal.runtime.finder.IWidgetFinder;
import com.windowtester.internal.runtime.matcher.AdapterFactory;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;


/**
 * A Swing Widget Finder.
 */
public class SwingWidgetFinder implements IWidgetFinder {

	private Hierarchy hierarchy;
	 
	private static final IWidgetFinder DEFAULT =
        new SwingWidgetFinder(new AWTHierarchy());
	
    public static IWidgetFinder getDefault() { return DEFAULT; }

	
	public SwingWidgetFinder() {
        this(AWTHierarchy.getDefault());
    }
	
	 public SwingWidgetFinder(Hierarchy h) {
	        hierarchy = h;
	    }

	public IWidgetLocator[] findAll(IWidgetLocator locator) {
		
		Set found = new HashSet();
		Iterator iter = hierarchy.getRoots().iterator();
		Matcher m = new AdapterFactory().adapt(locator);
        while (iter.hasNext()) {
        	// match only if window has focus
        	Component c = (Component)iter.next();
        	//  System.out.println(c);
	       	if (((Window)c).isActive()){
	        		findMatches(m, c, found);
	       	}
	       	else if (((Window)c).getOwnedWindows().length != 0) { 
	       		// check to see whether (ALL) frame owns any windows
	        	
	        	//if(c.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame")){
	    			Window[] windows = ((Window)c).getOwnedWindows();
	    			for (int i = 0; i< windows.length; i++){
	    				if (windows[i].isActive())
	    					findMatches(m,windows[i],found);
	    				// fix for Verify text in tooltip
	    				else if (windows[i].isShowing() && (c.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame")))
	    					findMatches(m,windows[i],found);
	    				
	    			}
	        }
	      
	    	// Embedded Frames are not accessible in Apple's Java5+
	       	// 12/3/09 : added WEmbeddedFrame
	       	else if (c.getClass().getName().equals("sun.awt.EmbeddedFrame") ||
	       			(c.getClass().getName().equals("sun.awt.windows.WEmbeddedFrame"))){
	      		findMatches(m,c,found);
	       	}
	       	
        }
        	
        WidgetReference[] locators = new WidgetReference[found.size()];
        int i = 0;
        Iterator foundIterator = found.iterator(); 
        while (foundIterator.hasNext()){
        	locators[i] = new WidgetReference(foundIterator.next());
        	i++;
        }
        	
		return locators;
	}
	
	
	protected void findMatches(Matcher m,
            Component c, Set found) {
	
		Iterator iter = hierarchy.getComponents(c).iterator();
		while (iter.hasNext()) {
			Component component = (Component)iter.next();
			findMatches(m, component, found);
		}
		
		if (m.matches(c)) {
			found.add(c);
		}
	}
	

}
