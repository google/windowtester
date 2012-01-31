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
package com.windowtester.runtime.swt.internal.locator.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.internal.runtime.ICodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.internal.runtime.PropertySet.IPropertyProvider;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.condition.IsVisible;
import com.windowtester.runtime.condition.IsVisibleCondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkMatcher.HyperlinkTextMatcher;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public class HyperlinkLocatorDelegate extends WidgetLocator implements IHyperlinkLocator, IUISelector, IsVisible, IHyperlinkFinder {
	
	private class Describer implements ICodegenParticipant {

		/* (non-Javadoc)
		 * @see com.windowtester.internal.runtime.ICodegenParticipant#describeTo(com.windowtester.internal.runtime.ICodeGenerator)
		 */
		public void describeTo(ICodeGenerator generator) {
			generator.addImport(HyperlinkLocator.class.getName());
			generator.append("new HyperlinkLocator(").append(getText()).append(")").append(getScope());
				
		}
		
		private String getScope() {
			StringBuffer sb = new StringBuffer();
			for (Iterator<IHyperlinkMatcher> iter = criteria.iterator(); iter.hasNext(); ) {
				IHyperlinkMatcher matcher = iter.next();
				if (matcher instanceof HyperlinkTextMatcher) //handled in constructor
					continue;
				sb.append(HyperlinkMatcher.toCriteriaString(matcher));
			}
			return sb.toString();
		}
		
		private String getText() {
			for (Iterator<IHyperlinkMatcher> iter = criteria.iterator(); iter.hasNext(); ) {
				IHyperlinkMatcher matcher = iter.next();
				if (matcher instanceof HyperlinkTextMatcher)
					return '"' + ((HyperlinkTextMatcher)matcher).getText() + '"';
			}
			return "";
		}
		
	}
	
	
	private static class HintProvider implements IIdentifierHintProvider {
		/* (non-Javadoc)
		 * @see com.windowtester.internal.runtime.finder.IIdentifierHintProvider#requiresXY()
		 */
		public boolean requiresXY() {
			return false;
		}
	}
	
	private class PropertyProvider implements IPropertyProvider {

		private final PropertyMapping[] EMPTY_MAP = new PropertyMapping[0];
		
		/* (non-Javadoc)
		 * @see com.windowtester.internal.runtime.PropertySet.IPropertyProvider#getProperties(com.windowtester.runtime.IUIContext)
		 */
		public PropertyMapping[] getProperties(IUIContext ui) {
			IWidgetLocator[] locators = findAll(ui);
			if (locators.length != 1)
				return EMPTY_MAP;
			IHyperlinkReference link = (IHyperlinkReference)locators[0];
			String href = link.getHref();
			if (href == null)
				return EMPTY_MAP;
			return new PropertyMapping[]{HyperlinkMatcher.HAS_HREF.withValue(href)};
		}
		
	}
	
	
	private static final long serialVersionUID = 5619480286768921788L;
	
	private final transient IHyperlinkFinder hyperlinkFinder;
	
	//concrete type to ensure serializable contract
	private final ArrayList<IHyperlinkMatcher> criteria = new ArrayList<IHyperlinkMatcher>();
	
	public HyperlinkLocatorDelegate(IHyperlinkFinder hyperlinkFinder) {
		super(Widget.class); //ignored 
		this.hyperlinkFinder = hyperlinkFinder;
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		throw new RuntimeException("unsupported method - should call testUI(IUIContext) instead");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		IHyperlinkReference[] hyperlinks = hyperlinkFinder.findAllHyperlinks(ui);
		for (int i = 0; i < hyperlinks.length; i++) {
			if (matches(hyperlinks[i]))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		if (!(widget instanceof IHyperlinkReference))
			return widgetMatches(widget);
		return refMatches((IHyperlinkReference)widget);
	}

	private boolean refMatches(IHyperlinkReference link) {
		for (Iterator<IHyperlinkMatcher> iter = criteria.iterator(); iter.hasNext(); ) {
			IHyperlinkMatcher matcher = iter.next();
			if (!matcher.matches(link))
				return false;
		}
		return true;
	}
	
	//this gets called during recording
	private boolean widgetMatches(Object widget) {
		if (!(widget instanceof Widget))
			return false;
		IHyperlinkReference[] links = HyperlinkFinder.resolveHyperlinks((Widget) widget);
		for (int i = 0; i < links.length; i++) {
			if (refMatches(links[i]))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		IHyperlinkReference[] hyperlinks = hyperlinkFinder.findAllHyperlinks(ui);
		List<IHyperlinkReference> matches = new ArrayList<IHyperlinkReference>();
		for (int i = 0; i < hyperlinks.length; i++) {
			IHyperlinkReference link = hyperlinks[i];
			if (matches(link))
				matches.add(link);
		}
		IWidgetLocator[] locators = new IWidgetLocator[matches.size()];
		for (int i = 0; i < locators.length; i++) {
			locators[i] = (IWidgetLocator) matches.get(i);
		}
		return locators;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#withHRef(java.lang.String)
	 */
	public IHyperlinkLocator withHRef(String href) {
		criteria.add(HyperlinkMatcher.forHref(href));
		return this;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#withText(java.lang.String)
	 */
	public IHyperlinkLocator withText(String text) {
		//nulls are ignored. See com.windowtester.runtime.swt.locator.forms.HyperlinkLocator.HyperlinkLocator()
		if (text != null)
			criteria.add(HyperlinkMatcher.forText(text));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#inEditor(java.lang.String)
	 */
	public IHyperlinkLocator inEditor(String editorTitle) {
		criteria.add(HyperlinkMatcher.forEditor(editorTitle));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#inSection(java.lang.String)
	 */
	public IHyperlinkLocator inSection(String sectionTitle) {
		criteria.add(HyperlinkMatcher.forSection(sectionTitle));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#inView(java.lang.String)
	 */
	public IHyperlinkLocator inView(String viewId) {
		criteria.add(HyperlinkMatcher.forView(viewId));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#isVisible()
	 */
	public IUICondition isVisible() {
		return new IsVisibleCondition(this);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IsVisible#isVisible(com.windowtester.runtime.IUIContext)
	 */
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		return testUI(ui);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#hasHRef(java.lang.String)
	 */
	public IHyperlinkCondition hasHRef(String href) {
		return new HyperlinkCondition(this).withHRef(href);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocator#hasText(java.lang.String)
	 */
	public IHyperlinkCondition hasText(String text) {
		return new HyperlinkCondition(this).withText(text);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkFinder#findAllHyperlinks(com.windowtester.runtime.IUIContext)
	 */
	public IHyperlinkReference[] findAllHyperlinks(IUIContext ui) {
		IWidgetLocator[] locators = ui.findAll(this);
		return HyperlinkFinder.adaptLocatorsToReferences(locators);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hyperlink [");
		for (Iterator<IHyperlinkMatcher> iter = criteria.iterator(); iter.hasNext();) {
			IHyperlinkMatcher matcher = iter.next();
			sb.append(matcher);
			if (iter.hasNext())
				sb.append(", ");	
		}
		sb.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference ref,
			IClickDescription click) throws WidgetSearchException {
		IHyperlinkReference linkRef = adaptToHyperlinkRef(ref);
		return linkRef.click(ui, linkRef, click);
	}


	private IHyperlinkReference adaptToHyperlinkRef(IWidgetReference ref) {
		
		if (ref instanceof IHyperlinkReference)
			return ((IHyperlinkReference)ref);
		return HyperlinkControlReference.forControl((Hyperlink) ref.getWidget());
	}


	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui,
			IWidgetReference widget, IClickDescription click,
			String menuItemPath) throws WidgetSearchException {
		return ((IHyperlinkReference)widget).contextClick(ui, widget, click, menuItemPath);
		
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == ICodegenParticipant.class)
			return new Describer();
		if (adapter == IIdentifierHintProvider.class)
			return new HintProvider();
		if (adapter == IPropertyProvider.class)
			return new PropertyProvider();
		return null;
	}
	


	
}