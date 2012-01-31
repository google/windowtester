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
package com.windowtester.runtime.swt.locator;

import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;

import abbot.Platform;

import com.windowtester.runtime.ClickDescription;
import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.condition.LocatorClosingHandler;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.internal.operation.BasicSWTWidgetClickOperation;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.state.MouseConfig;
import com.windowtester.runtime.swt.internal.widgets.CTabFolderReference;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates {@link CTabItem} widgets.
 */
public class CTabItemLocator extends SWTWidgetLocator {
	
	private static final long serialVersionUID = 2464452061853145190L;
	
	
	private static class Closer implements ICloseableLocator, Serializable {
	
		private static final long serialVersionUID = 5713321416399466153L;
	
		private static final int BETWEEN_CLICKS_DELAY = 700;
		private final transient CTabItemLocator tabItemLocator;

		public Closer(CTabItemLocator tabItemLocator) {
			this.tabItemLocator = tabItemLocator;
		}

		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.locator.ICloseableLocator#doClose(com.windowtester.runtime.IUIContext)
		 */
		public void doClose(IUIContext ui) throws WidgetSearchException {
			//conditions handled in find
			IWidgetReference widget = (IWidgetReference) ui.find(tabItemLocator);
			IClickDescription click = ClickDescription.create(1, tabItemLocator, MouseConfig.BUTTONS_REMAPPED ? WT.BUTTON3 : WT.BUTTON1);
			doClose(ui, widget, click);
		}
		
		
		public void doClose(IUIContext ui, IWidgetReference widget,
				IClickDescription click) throws WidgetSearchException {
			selectTabItem(ui, widget, click);
			closeTabItem(widget, click);		
		}

		private void closeTabItem(IWidgetReference widget,
				IClickDescription click) throws WidgetSearchException {
			final CTabItem item = (CTabItem) widget.getWidget();
			final CTabFolder folder[] = new CTabFolder[1]; 
			final Rectangle rect[] = new Rectangle[1]; 
			final Exception exception[] = new Exception[1]; 
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() {
					folder[0] = item.getParent();
					try {
						Field closeRect = CTabItem.class.getDeclaredField("closeRect");
						closeRect.setAccessible(true);
						rect[0] = (Rectangle) closeRect.get(item);
					} catch (Exception e) {
						exception[0] = e;
					}
				}
			});
			if (exception[0] != null)
				throw new WidgetSearchException(exception[0].getMessage());
//			tabItemLocator.doClick(1, folder[0], new Point(rect[0].x + rect[0].width/2, rect[0].y + rect[0].height/2), click.modifierMask());
			
			CTabFolderReference folderReference = new CTabFolderReference(folder[0]);
			
			new BasicSWTWidgetClickOperation<CTabFolderReference>(folderReference).atOffset(rect[0].x + rect[0].width/2, rect[0].y + rect[0].height/2).withModifiers(click.modifierMask()).execute();
			
		}

		private void selectTabItem(IUIContext ui, IWidgetReference widget,
				IClickDescription click) throws WidgetSearchException {
			//click first to ensure the X is visible..
			tabItemLocator.click(ui, widget, click);
			UIDriver.pause(BETWEEN_CLICKS_DELAY); //to ensure a double click is not registered
		}
		
	}
	
	
	/** 
	 * Create a locator instance for the common case where no information is needed
	 * to disambiguate the parent control.
	 * <p>
	 * This convenience constructor is equivalent to the following:
	 * <pre>
	 * new CTabItemLocator(itemText, new SWTWidgetLocator(CTabFolder.class));
	 * </pre>
	 * 
	 * @param text the text of the CTab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public CTabItemLocator(String itemText) {
		super(CTabItem.class, itemText);
	}


	//child
	/**
	 * Create a locator instance.
	 * @param text the text of the CTab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the parent locator	 
	 */
	public CTabItemLocator(String text, SWTWidgetLocator parent) {
		super(CTabItem.class, text, parent);
	}

	//indexed child
	/**
	 * Create a locator instance.
	 * @param text the text of the CTab to select (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index this locators index with respect to its parent
	 * @param parent the parent locator
	 */
	public CTabItemLocator(String text, int index, SWTWidgetLocator parent) {
		super(CTabItem.class, text, index, parent);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == ICloseableLocator.class)
			return new Closer(this);
		return super.getAdapter(adapter);
	}
	

	protected Point getUnspecifiedXYOffset(Rectangle rect) {
		// account for the closing X on the right in Linux
		if (Platform.isLinux())
			return new Point((rect.width - 20)/2, rect.height/2);
		return super.getUnspecifiedXYOffset(rect);
	}
	
	
	/**
	 * Create a condition handler that ensures that this {@link CTabItem} is closed.
	 * 
	 * @since 5.0.0
	 */
	public IConditionHandler isClosed() {
		return new LocatorClosingHandler(this);
	}
}
