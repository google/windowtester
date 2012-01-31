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
package com.windowtester.internal.swing.locator;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import abbot.finder.AWTHierarchy;
import abbot.finder.ComponentFinder;
import abbot.finder.ComponentNotFoundException;
import abbot.finder.Hierarchy;
import abbot.finder.Matcher;
import abbot.finder.MultiMatcher;
import abbot.finder.MultipleComponentsFoundException;


/** Provides basic component lookup, examining each component in turn.
Searches all components of interest in a given hierarchy.
*/

public class BasicFinder2 implements ComponentFinder {
    private Hierarchy hierarchy;

    private static final ComponentFinder DEFAULT =
        new BasicFinder2(new AWTHierarchy());
    public static ComponentFinder getDefault() { return DEFAULT; }

    private class SingleComponentHierarchy implements Hierarchy {
        private Component root;
        private ArrayList list = new ArrayList();
        public SingleComponentHierarchy(Container root) {
            this.root = root;
            list.add(root);
        }
        public Collection getRoots() {
            return list;
        }
        public Collection getComponents(Component c) { 
            return getHierarchy().getComponents(c);
        }
        public Container getParent(Component c) {
            return getHierarchy().getParent(c);
        }
        public boolean contains(Component c) {
            return getHierarchy().contains(c)
                && SwingUtilities.isDescendingFrom(c, root);
        }
        public void dispose(Window w) { getHierarchy().dispose(w); }
    }

    public BasicFinder2() {
        this(AWTHierarchy.getDefault());
    }

    public BasicFinder2(Hierarchy h) {
        hierarchy = h;
    }

    protected Hierarchy getHierarchy() {
        return hierarchy;
    }

    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy under the given root is the desired
        one.
    */
    public Component find(Container root, Matcher m) 
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Hierarchy h = root != null
            ? new SingleComponentHierarchy(root) : getHierarchy();
        return find(h, m);
    }

    /** Find a Component, using the given Matcher to determine whether a given
        component in the hierarchy used by this ComponentFinder is the desired
        one.
    */
    public Component find(Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        return find(getHierarchy(), m);
    }

    protected Component find(Hierarchy h, Matcher m)
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Set found = new HashSet();
        Iterator iter = h.getRoots().iterator();
        while (iter.hasNext()) {
            findMatches(h, m, (Component)iter.next(), found);
        }
        if (found.size() == 0) {
            
            throw new ComponentNotFoundException("finder.not_found");
        }
        else if (found.size() > 1) {
            Component[] list = (Component[])
                found.toArray(new Component[found.size()]);
            if (!(m instanceof MultiMatcher)) {
                throw new MultipleComponentsFoundException("finder.multiple_found", list);
            	
            }
            return ((MultiMatcher)m).bestMatch(list);
        }
        return (Component)found.iterator().next();
    }
        
    protected void findMatches(Hierarchy h, Matcher m,
                               Component c, Set found) {
        if (found.size() == 1 && !(m instanceof MultiMatcher))
            return;

        Iterator iter = h.getComponents(c).iterator();
        while (iter.hasNext()) {
            findMatches(h, m, (Component)iter.next(), found);
        }
        if (m.matches(c)) {
            found.add(c);
        }
    }
    
    
    /** Find a Component, using the given Matcher to determine whether a given
    component in the hierarchy used by this ComponentFinder is the desired
    one.
	*/
	public int findAll(Matcher m){
	    return findAll(getHierarchy(), m);
	}
    
    
    protected int findAll(Hierarchy h, Matcher m) {
	    Set found = new HashSet();
	    Iterator iter = h.getRoots().iterator();
	    while (iter.hasNext()) {
	    	// 2/22/07 : kp check for match only in active window
	    	Component c = (Component)iter.next();
	    	if (((Window)c).isActive())
	    		findMatchesAll(h, m, c, found);
	    }
	    if (found.size() == 0) {
	    	// component not found ??
	    	return 0;
	    }
	    else if (found.size() > 1) {
	        // multiple components found for the locator
	    	return -1;
	    }
	    return 1;
	}
    
    
    
    
    
    protected void findMatchesAll(Hierarchy h, Matcher m,
            Component c, Set found) {
		
		Iterator iter = h.getComponents(c).iterator();
		while (iter.hasNext()) {
			findMatchesAll(h, m, (Component)iter.next(), found);
		}
		if (m.matches(c)) {
			found.add(c);
		}
    }
   
}

