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

import com.windowtester.internal.runtime.matcher.CompoundMatcher;
import com.windowtester.internal.runtime.matcher.ExactClassMatcher;
import com.windowtester.internal.swing.UIContextSwing;
import com.windowtester.internal.swing.matcher.ClassMatcher;
import com.windowtester.internal.swing.matcher.HierarchyMatcher;
import com.windowtester.internal.swing.matcher.IndexMatcher;
import com.windowtester.internal.swing.matcher.NameMatcher;
import com.windowtester.internal.swing.matcher.NameOrTextMatcher;
import com.windowtester.internal.swing.util.PathStringTokenizerUtil;
import com.windowtester.internal.swing.util.TextUtils;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IPathLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swing.SwingWidgetLocator;

/**
 * A base class for locators that have a path.
 */
public abstract class AbstractPathLocator extends SwingWidgetLocator implements IPathLocator{

	private static final long serialVersionUID = 4463096378279721331L;

	private String _path;

	
	public AbstractPathLocator(Class cls, String path) {
		this(cls, getLabel(path), path);
	}
	
	
	public AbstractPathLocator(Class cls, String itemText, String path ) {
		this(cls, itemText, path, SwingWidgetLocator.UNASSIGNED, null);
	}

	
	public AbstractPathLocator(Class cls, String path, SwingWidgetLocator parentInfo) {
		this(cls, getLabel(path), path, parentInfo);
	}
	
	
	public AbstractPathLocator(Class cls, String itemText, String path, SwingWidgetLocator parentInfo) {
		this(cls, itemText, path, SwingWidgetLocator.UNASSIGNED, parentInfo);
	}

	
	public AbstractPathLocator(Class cls, String path, int index, SwingWidgetLocator parentInfo) {
		this(cls, getLabel(path), path, index, parentInfo);
	}
	
	
	//TODO: improve chaining 
	public AbstractPathLocator(Class cls, String itemText, String path, int index, SwingWidgetLocator parentInfo) {
		super(cls, itemText, index, parentInfo);
		_path = path;
		
		//	create the matcher
		if (this instanceof JListLocator || this instanceof JTreeItemLocator)
			_matcher = ClassMatcher.create(cls);
		else _matcher = new ExactClassMatcher(cls);
		
		
		if (this instanceof JMenuItemLocator)
			_matcher = new CompoundMatcher(_matcher, NameOrTextMatcher.create(getItemText()));
		
		if (index != UNASSIGNED)
			_matcher = IndexMatcher.create(_matcher, index);
		
		if (parentInfo != null){
			
			// if parent is NamedWidgetLocator, then component has a name
			if (parentInfo instanceof com.windowtester.runtime.swing.locator.NamedWidgetLocator){
				if (!(this instanceof JMenuItemLocator)){
					_matcher = new CompoundMatcher(_matcher,NameMatcher.create(parentInfo.getNameOrLabel()));
				}
			}
			else {
				if (index != UNASSIGNED)
					_matcher = HierarchyMatcher.create(_matcher, parentInfo.getMatcher(), index);
				else
					_matcher = HierarchyMatcher.create(_matcher,parentInfo.getMatcher());
			
			}
		}
	
		
	}

	
	public String getPath() {
		return _path;
	}

	public String getItemText() {
		if (this instanceof JListLocator || this instanceof JComboBoxLocator)
			return _path;
		return getNameOrLabel();
	}
	
	protected static String getLabel(String path) {
		if (path == null)
			return null;
		String[] items = PathStringTokenizerUtil.tokenize(path);
		return items[items.length - 1];
		
	}
	
		
	//not ideal to have this mutable but it fits best with current id inference scheme
	public void setPath(String pathString) {
		_path = pathString;
	}
	
	/**
	 * Perform the context click.
	 * @param ui the UI context 
	 * @param widget the widget reference to click
	 * @param menuItemPath the path to the menu item to select
	 * @return the clicked widget (as a reference)
	 * @throws WidgetSearchException
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath) 
			throws WidgetSearchException {
		
		Component component = (Component)widget.getWidget();
		Component clicked = ((UIContextSwing)ui).getDriver().contextClick(component, menuItemPath);
		return WidgetReference.create(clicked, this);
	}
}
