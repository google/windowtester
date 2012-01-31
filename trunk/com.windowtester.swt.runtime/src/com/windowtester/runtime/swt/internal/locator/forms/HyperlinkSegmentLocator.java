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



import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.forms.widgets.FormText;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.locator.forms.FormTextLocator.IHyperlinkLocatorSpecifier;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public class HyperlinkSegmentLocator extends AbstractHyperlinkSegmentAccessor implements IHyperlinkLocatorSpecifier, IHyperlinkHandler {

	private final BasicWidgetSelector selector = new BasicWidgetSelector();
	
	public static IHyperlinkLocatorSpecifier forFormText(FormTextLocator formTextLocator) {
		return new HyperlinkSegmentLocator(formTextLocator);
	}
	
	private HyperlinkSegmentLocator(FormTextLocator textLocator) {
		super(textLocator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocatorSpecifier#withHRef(java.lang.String)
	 */
	public IHyperlinkLocator withHRef(String href) {
		return new HyperlinkLocatorDelegate(this).withHRef(href);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.FormTextLocator.IHyperlinkLocatorSpecifier#withText(java.lang.String)
	 */
	public IHyperlinkLocator withText(String text) {
		return new HyperlinkLocatorDelegate(this).withText(text);
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkSelector#doClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.IClickDescription, com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkReference)
	 */
	public IWidgetLocator doClick(IUIContext ui, IClickDescription click,
			IHyperlinkReference link) throws WidgetSearchException {
		Rectangle linkBounds = link.getDisplayBounds();
		FormTextReference textRef = (FormTextReference) ui.find(textLocator);
		FormText text = (FormText) textRef.getWidget();
	
		selector.click(text, linkBounds.x +linkBounds.width/2 , linkBounds.y +linkBounds.height/2, click.modifierMask(), click.clicks());
		
		return link;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkSelector#doContextClick(com.windowtester.runtime.IUIContext, java.lang.String, com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkReference)
	 */
//	public IWidgetLocator doContextClick(IUIContext ui, String menuItemPath,
//			IHyperlinkReference link) throws WidgetSearchException {
////		Rectangle linkBounds = link.getBounds();
////		
////		FormTextReference textRef = (FormTextReference) ui.find(textLocator);
////		FormText text = (FormText) textRef.getWidget();
////	
////		selector.contextClick(text, linkBounds.x +linkBounds.width/2 , linkBounds.y +linkBounds.height/2, menuItemPath);
////		return link;
//		SWTWidgetLocation location = new SWTWidgetLocation((ISWTWidgetReference<?>) link, WTInternal.CENTER);
//		SWTShowMenuOperation op = new SWTShowMenuOperation().openMenuClick(WT.BUTTON3, location, false);
//		op.execute();
//		MenuReference menu = op.getMenu();
//		return new MenuDriver().resolveAndSelect(menu, menuItemPath);
//	}
	

}
