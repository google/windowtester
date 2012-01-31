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

import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.forms.widgets.IHyperlinkSegment;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.drivers.MenuDriver;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.runtime.util.StringComparator;

@SuppressWarnings("restriction")
public class HyperlinkSegmentReference extends AbstractHyperlinkReference {

	private final BasicWidgetSelector selector = new BasicWidgetSelector();
	
	public static HyperlinkSegmentReference forSegmentInText(IHyperlinkSegment hyperlink, FormText text) {
		return new HyperlinkSegmentReference(hyperlink, text);
	}

	private IHyperlinkSegment link;
	// TODO this should really be a reference - FormTextReference perhaps
	private final FormText text;
	
	public HyperlinkSegmentReference(IHyperlinkSegment link, FormText text) {
		super(text.getDisplay());
		this.link = link;
		this.text = text;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public Object getWidget() {
		return link;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#getControl()
	 */
	public Control getControl() {
		return text;
	}

	public Rectangle getDisplayBounds() {
		return link.getBounds();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new IWidgetReference[]{this};
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return widget == link;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "HyperlinkSegmentReference: text=" + getText() + " href= " + getHref();
	}

	public boolean hasText(String expectedText) {
		return StringComparator.matches(getText(), expectedText);
	}

	public boolean hasHRef(String expectedHref) {
		return StringComparator.matches(getHref(), expectedHref);
	}

	public String getHref() {
		return link.getHref();
	}
	
	public String getText() {
		return link.getText();
	}

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkSelector#doClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.IClickDescription, com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkReference)
	 */
	public IWidgetLocator doClick(IUIContext ui, IClickDescription click,
			IHyperlinkReference link) throws WidgetSearchException {
		Rectangle linkBounds = link.getDisplayBounds();
		selector.click(text, linkBounds.x + linkBounds.width/2 , linkBounds.y + linkBounds.height/2, click.modifierMask(), click.clicks());
		return link;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, final IClickDescription click, String menuItemPath)
		throws WidgetSearchException
	{
		//		IWidgetReference ref = widget;
		//		if (widget == null)
		//			ref = (IWidgetReference) ui.find(this); //only do this lookup if necessary 
		//
		//		IHyperlinkReference link = (IHyperlinkReference) ref;
		//		//		Rectangle linkBounds = link.getBounds();
		//		//		selector.contextClick(text, linkBounds.x + linkBounds.width/2 , linkBounds.y + linkBounds.height/2, menuItemPath);
		//		//		return link;
		return new MenuDriver().resolveAndSelect(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				return showContextMenu(click);
			}
		}, menuItemPath);
	}

	/* (non-Javadoc)
	 * @see ISWTWidgetReferenceWithContextMenu#showContextMenu()
	 */
	public MenuReference showContextMenu(IClickDescription click) {
		// TODO this really should be a FormReference, but FormReference is not an ISWTWidgetReference... why?
		SWTWidgetReference<FormText> widgetRef = new SWTWidgetReference<FormText>(text);
		SWTLocation location = SWTWidgetLocation.withDefaultCenter(widgetRef, click);
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, false);
		op.execute();
		return op.getMenu();
	}
}
