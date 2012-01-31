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
import org.eclipse.ui.forms.widgets.Hyperlink;

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
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.MenuReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.runtime.util.StringComparator;


public class HyperlinkControlReference extends AbstractHyperlinkReference implements ISWTWidgetReference {

	public static IHyperlinkReference forControl(Hyperlink link) {
		return new HyperlinkControlReference(link);
	}	

	private final Hyperlink link;
	private final BasicWidgetSelector selector = new BasicWidgetSelector();
	
	private final SWTWidgetReference<Hyperlink> linkRef;
	
	
	public HyperlinkControlReference(Hyperlink link) {
		super(link.getDisplay());
		this.link = link;
		this.linkRef = new SWTWidgetReference<Hyperlink>(link);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#getBounds()
	 */
	public Rectangle getDisplayBounds() {
		return linkRef.getDisplayBounds();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#getHref()
	 */
	public String getHref() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return (String) link.getHref();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#getText()
	 */
	public String getText() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return (String) link.getText();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#hasHRef(java.lang.String)
	 */
	public boolean hasHRef(String href) {
		return StringComparator.matches(href, getHref());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#hasText(java.lang.String)
	 */
	public boolean hasText(String text) {
		return StringComparator.matches(text, getText());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public Hyperlink getWidget() {
		return link;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.forms.IHyperlinkReference#getControl()
	 */
	public Control getControl() {
		return link;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return new IHyperlinkReference[]{this};
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return widget == link;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkSelector#doClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.IClickDescription, com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkReference)
	 */
	public IWidgetLocator doClick(IUIContext ui, IClickDescription click,
			IHyperlinkReference linkRef) throws WidgetSearchException {
		Rectangle linkBounds = getDisplayBounds();
		selector.click(link, linkBounds.width/2, linkBounds.height/2, click.modifierMask(), click.clicks());
		return linkRef;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference widget, final IClickDescription click, String menuItemPath)
		throws WidgetSearchException
	{
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
		//		Rectangle linkBounds = getBounds();
		//		selector.contextClick(link, linkBounds.x +linkBounds.width/2 , linkBounds.y +linkBounds.height/2, menuItemPath);
		//		return linkRef;
		SWTLocation location = SWTWidgetLocation.withDefaultCenter(this, click);
		SWTMenuOperation op = new SWTShowMenuOperation(null).waitForIdle().click(WT.BUTTON3, location, false);
		op.execute();
		return op.getMenu();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "HyperlinkControlReference: text=" + getText() + " href= " + getHref();
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getName()
	 */
	public String getName() {
		return linkRef.getName();
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getData()
	 */
	public Object getData() {	
		return linkRef.getData();
	}

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getData(java.lang.String)
	 */
	public Object getData(final String key) {	
		return linkRef.getData(key);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getParent()
	 */
	public ISWTWidgetReference getParent() {
		return linkRef.getParent();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getChildren()
	 */
	public ISWTWidgetReference[] getChildren() {
		return linkRef.getChildren();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getStyle()
	 */
	public int getStyle() {
		return linkRef.getStyle();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#hasStyle(int)
	 */
	public boolean hasStyle(int style) {
		return linkRef.hasStyle(style);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#getTextForMatching()
	 */
	public String getTextForMatching() {
		return getText();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#hasText()
	 */
	public boolean hasText() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#isEnabled()
	 */
	public boolean isEnabled() {
		return linkRef.isEnabled();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#isDisposed()
	 */
	public boolean isDisposed() {
		return linkRef.isDisposed();
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetReference#isVisible()
	 */
	public boolean isVisible() {
		return linkRef.isVisible();
	}

	/* (non-javadoc)
	 * @see ISWTWidgetReference#showPulldownMenu(IClickDescription)
	 */
	public MenuReference showPulldownMenu(IClickDescription click) {
		throw new RuntimeException(toString() + " does not have a pulldown menu");
	}
}
