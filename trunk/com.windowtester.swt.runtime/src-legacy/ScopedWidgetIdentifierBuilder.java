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
package com.windowtester.swt.locator;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import abbot.finder.swt.Matcher;
import abbot.finder.swt.SWTHierarchy;
import abbot.tester.swt.ButtonTester;
import abbot.tester.swt.WidgetTester;

import com.windowtester.finder.swt.SearchScopeHelper;
import com.windowtester.finder.swt.WidgetFinder;
import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.swt.WidgetLocator;
import com.windowtester.swt.locator.eclipse.ViewLocator;

public class ScopedWidgetIdentifierBuilder implements IWidgetIdentifierStrategy {

	/** A list of keys which we want to propagate to locators */
	private static final String[] INTERESTING_KEYS = { "name" };

	/** For use in checking for unique matches */
	private final WidgetFinder _finder = new WidgetFinder();

	/** For use in elaboration (created once per call it identify) */
	private SWTHierarchyHelper _hierarchyHelper;
	private SearchScopeHelper _searchScopeHelper;

	
	/* (non-Javadoc)
	 * @see com.windowtester.swt.locator.IWidgetIdentifierStrategy#identify(org.eclipse.swt.widgets.Widget, org.eclipse.swt.widgets.Event)
	 */
	public IWidgetIdentifier identify(Widget w, Event event) {
		return identify(w);
	}
	
	/**
	 * Generates a <code>WidgetLocator</code> that uniquely identifies this widget
	 * relative to the current widget hierarchy.  If no uniquely identifying locator is found
	 * <code>null</code> is returned.
	 * 
	 * @see com.windowtester.swt.locator.IWidgetIdentifierStrategy#identify(org.eclipse.swt.widgets.Widget)
	 */
	public IWidgetIdentifier identify(Widget w) {

		Display display = w.getDisplay();
		//cache the helpers for use in elaboration
		_hierarchyHelper   = new SWTHierarchyHelper(display);
       	_searchScopeHelper = new SearchScopeHelper(new SWTHierarchy(display));
		
		//get top-level scope
		WidgetLocator scope = findTopLevelScope(w);
		//get locator describing the target widget itself
		WidgetLocator locator = getLocator(w);
		//attach scope
		locator.setParentInfo(scope); //note: it can be null

		
		Matcher matcher        = MatcherFactory.getMatcher(locator);
		Shell shellSearchScope = _searchScopeHelper.getShellSearchScope(matcher); 
		
//		int count = 0;
		
		//elaborate until done (notice: null locator indicates a failure)
		while(!isUniquelyIdentifying(matcher, shellSearchScope) && locator != null) {
			locator = elaborate(locator, w);
			if (locator != null)
				matcher = MatcherFactory.getMatcher(locator);
//			if (++count == 3)
//				new SWTHierarchy(shellSearchScope.getDisplay()).dbPrintWidgets();
		}
		
		return locator;
	}

	/**
	 * Find top-level scope (Shell | View) -- might be <code>null</code>.
	 */
	private WidgetLocator findTopLevelScope(Widget w) {
		
		if (!PlatformUI.isWorkbenchRunning())
			return null; //bail out if the platform is not running
		
		//as a final sanity check, wrap this, in case we get a failure:
		try {
			// 1 check for view scope
			IViewHandle handle = ViewFinder.find(w);
			if (handle != null)
				return new ViewLocator(handle.getId());
		} catch (IllegalStateException e) {
			LogHandler.log(e);
		} // TODO: remove this wrapper post 2.0
			
		
		//2 check for shell scope

		//TODO: when to use Shell Scope?
		
//		IShellHandle shellHandle = ShellFinder.find(w);
//		if (shellHandle != null && shellHandle.isModal())
//			return new ShellLocator(shellHandle.getTitle(), shellHandle.isModal());
		
		//handle other cases here...
		return null;
	}
	
	/**
	 * Takes a WidgetLocator object and elaborates on it until is uniquely identifying.
	 * If no uniquely identifying locator can be inferred, a <code>null</code> value 
	 * is returned.
	 * 
	 */
	private WidgetLocator elaborate(WidgetLocator info, Widget w) {
		
		//a pointer to the original locator for returning (in the success case)
		WidgetLocator root = info;
		
		TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, "elaborating on: " + info + " widget=" + w);

		boolean elaborated = false;
		WidgetLocator parentInfo = null;
		
		while(!elaborated) {

			//get parent info of the current (top-most) locator 
			parentInfo = info.getParentInfo();
			//get the parent of the current (top-most) widget in the target's hierarchy
			Widget parent = _hierarchyHelper.getParent(w);
			/*
			 * if the parent is null at this point, we've failed to elaborate and we
			 * need to just return
			 */
			if (parent == null) {
				TraceHandler.trace(IRuntimePluginTraceOptions.HIERARCHY_INFO, UIProxy.getToString(w) + " has null parent, aborting elaboration");
				return null;
			}

			//if the parent is a scope locator, connect to it
			if (isScopeLocator(parentInfo)) {
				handleScopeLocatorCase(info, parentInfo, w, parent);
				elaborated = true;
			//if the parent is null, create a new parent and attach it	
			} else if (parentInfo == null) {
				info.setParentInfo(getLocator(parent));
				setIndex(info, w, parent);
				elaborated = true;
			}
			
			/*
			 * setup for next iteration
			 */
			w    = parent;
			info = parentInfo;
		} 
		
		return root;
	}

	/**
	 * Check to see if the given locator is a scope locator.
	 */
	private boolean isScopeLocator(WidgetLocator locator) {
		//TODO: ideally this will be an interface: IScopeLocator?
		return locator instanceof ViewLocator || locator instanceof ShellLocator;
	}

	/**
	 * Handle case where parent locator is a scoping locator.
	 */
	private void handleScopeLocatorCase(WidgetLocator currentTopLocator, WidgetLocator scopeLocator, Widget currentWidget, Widget widgetParent) {

			//1. create a new parent
			WidgetLocator newParent = getLocator(widgetParent);
			//attatch it to our old top locator
			currentTopLocator.setParentInfo(newParent);
			setIndex(currentTopLocator, currentWidget, widgetParent);
			
			int scopeRelativeIndex = _hierarchyHelper.getIndex(currentWidget, scopeLocator);
			if (scopeRelativeIndex != WidgetLocator.UNASSIGNED)
				newParent.setIndex(scopeRelativeIndex);

			newParent.setParentInfo(scopeLocator);	
	}

	/**
	 * Set the index for this locator that describes the given widget relative to the given parent.
	 */
	private void setIndex(WidgetLocator locator, Widget currentWidget, Widget widgetParent) {
		int index = _hierarchyHelper.getIndex(currentWidget,widgetParent);
		if (index != WidgetLocator.UNASSIGNED)
			locator.setIndex(index);
	}
	

	/**
	 * Does this macther uniquely identify a widget in this Shell?
	 */
	private boolean isUniquelyIdentifying(Matcher matcher, Shell shellSearchScope) {
		return _finder.find(shellSearchScope, matcher, 0 /* no retries */).getType() == WidgetFinder.MATCH;
	}

	
	/**
	 * Create an (unelaborated) info object for this widget. 
	 * @param w - the widget to describe.
	 * @return an info object that describes the widget.
	 */
	private WidgetLocator getLocator(Widget w) {
		
		if (w == null) {
			return null;
		}
		
		/**
		 * CCombos require special treatment as the chevron is a button and receives the click event.
		 * Instead of that button, we want to be identifying the combo itself (the button's parent).
		 */
		if (w instanceof Button) {
			Widget parent = new ButtonTester().getParent((Button) w);
			if (parent instanceof CCombo)
				w = parent;
		}

		WidgetLocator locator = checkForLabeledLocatorCase(w);
		
		//if we didn't find a label locator, create a standard locator
		if (locator == null) {

			Class cls = w.getClass();
			/**
			 * We don't want the combo text to be part of the identifying
			 * information since it is only set to the value AFTER it is
			 * selected... Text values are also too volatile to use as
			 * identifiers.
			 * 
			 */
			String text = (w instanceof Combo || w instanceof CCombo
					|| w instanceof Text || w instanceof StyledText) ? null
					: _hierarchyHelper.getWidgetText(w);
			locator = (text != null) ? new WidgetLocator(cls, text)
					: new WidgetLocator(cls);
		} 
		
		setDataValues(locator, w);
		return locator;
	}

	private WidgetLocator checkForLabeledLocatorCase(final Widget w) {
		Widget parent = _hierarchyHelper.getParent(w);
		if (!(parent instanceof Composite))
			return null;
		//labels are not themselves considered labelable; 
		if (w instanceof Label)
			return null;
		
		final Composite comp = (Composite)parent;
		final Class cls = w.getClass();//our target class
		
		final String labelText[] = new String[1];
		final boolean found[] = new boolean[1];
		
		/*
		 * Iterate over children looking for a Label widget.
		 * If we find one, if the next widget of the class of our target widget
		 * is the target widget, we have a labeled locator case.
		 */
		w.getDisplay().syncExec(new Runnable() {
			public void run() {

				Control[] children = comp.getChildren();
				Control child;
				for (int i = 0; i < children.length; i++) {
					child = children[i];
					//look for next widget of target class
					if (labelText[0] != null) {
						if (child.getClass().equals(cls)) {
							found[0] = child == w;
							/*
							 * only kick out if we've found it 
							 * (there may be mutiple label text pairs in a composite) 
							 */
							if (found[0])
								return;
						}
					}
					//set up for next iteration
					if (child instanceof Label)
						labelText[0] = ((Label)child).getText();
				}
			}
		});
		
		if (found[0])
			return new LabeledLocator(cls, labelText[0]);
		return null;		
	}

	
	
	/**
	 * Propagate values of interest from the widget to the locator
	 */
	private void setDataValues(WidgetLocator locator, Widget w) {
		String key;
		Object value;
		WidgetTester tester = new WidgetTester();
		for (int i = 0; i < INTERESTING_KEYS.length; ++i) {
			key = INTERESTING_KEYS[i];
			value = tester.getData(w, key);
			if (value != null)
				locator.setData(key, value.toString());
		}
	}

}
