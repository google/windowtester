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
package com.windowtester.runtime.swt.internal.selector;

import java.awt.Point;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.internal.runtime.provisional.WTInternal;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTWidgetLocation;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgets;
import com.windowtester.runtime.swt.widgets.ISWTWidgetReference;

/**
 * A default selector for SWT widgets. 
 *
 * @author Phil Quitslund
 *
 */
public class DefaultSWTWidgetSelector implements IUISelector {

	private BasicWidgetSelector _selector = new BasicWidgetSelector();
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference ref, IClickDescription click) throws WidgetSearchException {
		//WidgetReference ref = (WidgetReference)ui.find(this);
		Widget w = (Widget)ref.getWidget();
		Point offset = getXYOffset(w, click);
		Widget clicked = doClick(click.clicks(), w, offset, click.modifierMask());		
		return WidgetReference.create(clicked, this);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IUISelector#contextClick(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.WidgetReference, com.windowtester.runtime.IClickDescription, java.lang.String)
	 */
	public IWidgetLocator contextClick(IUIContext ui, IWidgetReference ref, IClickDescription click, String menuItemPath) throws WidgetSearchException {
//		Widget w = (Widget)ref.getWidget();
//		Point offset = getXYOffset(w, click);
//		Widget clicked = doContextClick(w, offset, menuItemPath);		
//		return WidgetReference.create(clicked, this);
		SWTLocation location = unspecifiedXY(click)
			? new SWTWidgetLocation((ISWTWidgetReference<?>) ref, WTInternal.CENTER)
			: new SWTWidgetLocation((ISWTWidgetReference<?>) ref, WTInternal.TOPLEFT).offset(click.x(), click.y());
		return new SWTMenuSelector().contextClick(location, false, menuItemPath);
	}
	
	/**
	 * Test this click to see if an offset is specified.
	 */
	private boolean unspecifiedXY(IClickDescription click) {
		//dummy sentinel for now
		return click.isDefaultCenterClick();
	}

	/**
	 * Get the x,y offset for the click.
	 * @param click 
	 */
	public Point getXYOffset(Widget w, IClickDescription click) {
		if (unspecifiedXY(click)) {
			Rectangle rect = SWTWidgets.asReference(w).getDisplayBounds();
			return new Point(rect.width/2, rect.height/2);
		}
		return new Point(click.x(), click.y());
	}
	
	/**
	 * Perform the click.  This is intended to be overridden in subclasses
	 * @param clicks - the number of clicks
	 * @param w - the widget to click
	 * @param offset - the x,y offset (from top left corner)
	 * @param modifierMask - the mouse modifier mask
	 * @return the clicked widget
	 */
	protected Widget doClick(int clicks, Widget w, Point offset, int modifierMask) {
		return _selector.click(w, offset.x, offset.y, modifierMask, clicks);
	}
	
//	/* (non-Javadoc)
//	 * @see com.windowtester.runtime2.locator.IUISelector#contextClick(com.windowtester.runtime2.IUIContext2, com.windowtester.runtime2.locator.WidgetReference, java.lang.String)
//	 */
//	public IWidgetLocator contextClick(IUIContext ui, WidgetReference ref,
//			String menuItemPath) throws WidgetSearchException {
//		Widget w = (Widget)ref.getWidget();
//		//TODO: XYs ignored on context clicks!!!!!
//		//Rectangle rect = UIProxy.getBounds(w);
//		//Point offset = new Point(rect.width/2, rect.height/2);
//		Widget clicked = doContextClick(w, /*offset, click.modifierMask()*/ menuItemPath);		
//		return WidgetReference.create(clicked, this);
//	}

	
	
	
//	private Widget doContextClick(Widget w, Point offset, String menuItemPath) throws MultipleWidgetsFoundException, WidgetNotFoundException {
//		return _selector.contextClick(w, offset.x, offset.y, menuItemPath);
//	}

}
