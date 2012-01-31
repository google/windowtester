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
package com.windowtester.runtime.swing.locator;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JTabbedPane;

import com.windowtester.internal.runtime.matcher.CompoundMatcher;
import com.windowtester.internal.runtime.matcher.ExactClassMatcher;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.matcher.HierarchyMatcher;
import com.windowtester.internal.swing.matcher.IndexMatcher;
import com.windowtester.internal.swing.matcher.NameMatcher;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 *  A locator for JTabbedPane.
 */
public class JTabbedPaneLocator extends SwingWidgetLocator {
	
	private static final long serialVersionUID = 2471285225375825938L;
	
	
	/**
	 * Creates an instance of a locator for a JTabbedPane
	 * @param tabLabel the label of the selected tab
	 */
	public JTabbedPaneLocator(String tabLabel) {
		this(tabLabel,null);
	}
	
	
	/**
	 * Creates an instance of a locator for a JTabbedPane
	 * @param tabLabel the label of the selected tab
	 * @param parent locator for the parent
	 */
	public JTabbedPaneLocator(String tabLabel, SwingWidgetLocator parent) {
		this(tabLabel, -1, parent);
	}
	
	/**
	 * Creates an instance of a locator for a JTabbedPane
	 * @param tabLabel the label of the selected tab
	 * @param index the index of the JTabbedPane relative to it's parent
	 * @param parent locator for the parent 
	 */
	public JTabbedPaneLocator(String tabLabel, int index, SwingWidgetLocator parent) {
		this(JTabbedPane.class, tabLabel,index, parent);
		
	}
	
	public JTabbedPaneLocator(Class cls,String tabLabel,int index,SwingWidgetLocator parent){
		super(cls,tabLabel,index,parent);
		
		//create the matcher
		_matcher = new ExactClassMatcher(cls);
		
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		
		if (parent != null){
			if (parent instanceof com.windowtester.runtime.swing.locator.NamedWidgetLocator){
				_matcher = new CompoundMatcher(_matcher,NameMatcher.create(parent.getNameOrLabel()));
			}
			else {
				if (index != UNASSIGNED)
					_matcher = HierarchyMatcher.create(_matcher, parent.getMatcher(), index);
				else
					_matcher = HierarchyMatcher.create(_matcher,parent.getMatcher());
			}
		}
	}
	
	/**
	 * Perform the click.  
	 * @param clicks - the number of clicks
	 * @param w - the widget to click
	 * @param offset - the x,y offset (from top left corner)
	 * @param modifierMask - the mouse modifier mask
	 * @return the clicked widget
	 */
	protected Component doClick(IUIContext ui, int clicks, Component c, Point offset, int modifierMask) {
		return ((UIContextSwing)ui).getDriver().click(c,getNameOrLabel());
	}
	
	
}
