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
package com.windowtester.runtime.swt.locator.forms;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkFinder;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkLocatorDelegate;
import com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkFinder;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link Hyperlink} widgets.
 * <p>
 * <strong>PROVISIONAL</strong>. This class has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API for more than
 * experimental purpose without consulting with the WindowTester team.
 * </p> 
 */
public class HyperlinkLocator extends SWTWidgetLocator implements IHyperlinkLocator {
	
	private static final long serialVersionUID = -1825794406262165893L;
	
	private IHyperlinkLocator locatorDelegate;
	
	/**
	 * Create a locator instance.
	 * <p>
	 */
	public HyperlinkLocator() {
		this(null); //agg...  null is a sentinel
	}
	
	/**	 
	 * Create a locator instance.
	 * @param hyperlinkText
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public HyperlinkLocator(String hyperlinkText) {
		super(Widget.class); //ignored
		this.locatorDelegate = new HyperlinkLocatorDelegate(getFinder()).withText(hyperlinkText);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#withHRef(java.lang.String)
	 */
	public IHyperlinkLocator withHRef(String href) {
		return locatorDelegate.withHRef(href);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#withText(java.lang.String)
	 */
	public IHyperlinkLocator withText(String text) {
		return locatorDelegate.withText(text);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#inSection(java.lang.String)
	 */
	public IHyperlinkLocator inSection(String sectionTitle) {
		return locatorDelegate.inSection(sectionTitle);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#hasHRef(java.lang.String)
	 */
	public IHyperlinkCondition hasHRef(String href) {
		return locatorDelegate.hasHRef(href);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#hasText(java.lang.String)
	 */
	public IHyperlinkCondition hasText(String text) {
		return locatorDelegate.hasText(text);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#inEditor(java.lang.String)
	 */
	public IHyperlinkLocator inEditor(String editorTitle) {
		return locatorDelegate.inEditor(editorTitle);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator#inView(java.lang.String)
	 */
	public IHyperlinkLocator inView(String viewId) {
		return locatorDelegate.inView(viewId);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return locatorDelegate.findAll(ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		return locatorDelegate.click(ui, widget, click);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, IClickDescription click, String menuItemPath)
			throws WidgetSearchException {
		return locatorDelegate.contextClick(ui, widget, click, menuItemPath);
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {
		
		return new ISWTWidgetMatcher() {
			public boolean matches(ISWTWidgetReference<?> widget) {
				return locatorDelegate.matches(widget.getWidget());
			}
		};
	}
	
	private IHyperlinkFinder getFinder() {
		return HyperlinkFinder.getUnscoped();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getToStringDetail()
	 */
	protected String getToStringDetail() {
		return locatorDelegate.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		Object adapted = locatorDelegate.getAdapter(adapter);
		if (adapted != null)
			return adapted;
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getTargetClassName()
	 */
	public String getTargetClassName() {
		return "Hyperlink";
	}
	
}
