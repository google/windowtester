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
package com.windowtester.runtime.gef.internal.locator;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.windowtester.internal.runtime.locator.LocatorIterator;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.gef.internal.WTGEFPlugin;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.IItemLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * Figure label provider service.
 */
public class FigureLabelProvider extends LabelProvider {

	//public for testing
	public static final String FIGURE       = "icons/widgets/default_figure.gif";
	public static final String PALETTE_ITEM = "icons/widgets/palette_item.gif";
	public static final String CANVAS       = "icons/widgets/canvas.gif";
	
	private static final class TextProvider {
		String getText(Object element) {
			if (!(element instanceof ILocator))
				return null;
			ILocator locator = (ILocator)element;
			String text = getLocatorText(locator);
			if (text != null)
				return text;
			LocatorIterator iter = LocatorIterator.forLocator(locator);
			for ( ; iter.hasNext(); ) {
				locator = iter.next();
				text = getLocatorText(locator);
				if (text != null)
					return text;
			}
			return null;
		}

		private String getLocatorText(ILocator locator) {
			if (locator instanceof PaletteItemLocator) {
				String str = "Palette Item: ";
				IItemLocator adapter = (IItemLocator) ((PaletteItemLocator)locator).getAdapter(IItemLocator.class);
				if (adapter == null)
					return null;
				str += "\"" + adapter.getPath() + "\"";
				return str;
			}
			if (locator instanceof FigureClassLocator) {
				FigureClassLocator fcl = (FigureClassLocator)locator;
				return "Figure (" + fcl.getClassName() +")";
			}
			if (locator instanceof FigureCanvasLocator) {
				return "Figure Canvas";
			}	
			if (locator instanceof FigureCanvasXYLocator) {
				return "Figure Canvas";
			}	
			
			return null;
		}
	}
	
	
	private final TextProvider textProvider = new TextProvider();
	
	
	public static FigureLabelProvider forDelegate(FigureLocatorDelegate delegate) {
		return new FigureLabelProvider(); //TODO: arg is ignored -- if not necessary remove
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		
		element = adaptToLocatorIfSupported(element);
		
		if (element instanceof FigureCanvasLocator)
			return getImage(CANVAS);
		if (element instanceof FigureCanvasXYLocator)
			return getImage(CANVAS);
		if (element instanceof PaletteItemLocatorDelegate)
			return getImage(PALETTE_ITEM);
		if (element instanceof PaletteItemLocator)
			return getImage(PALETTE_ITEM);
		if (element instanceof FigureLocatorDelegate)
			return getImage(FIGURE);
		if (element instanceof DelegatingLocator)
			return getImage(FIGURE);
		if (element instanceof FigureLocator)
			return getImage(FIGURE);
		if (element instanceof FigureClassLocator)
			return getImage(FIGURE);

		return super.getImage(element);
	}

	//public for testing
	public static Image getImage(String imagePath) {
		return WTGEFPlugin.getDefault().getImage(imagePath);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		element = adaptToLocatorIfSupported(element);
		String text = textProvider.getText(element);
		if (text != null)
			return text;
		return super.getText(element);
	}

	private Object adaptToLocatorIfSupported(Object element) {
		if (element instanceof IAdaptable) {
			Object adapted = ((IAdaptable)element).getAdapter(ILocator.class);
			if (adapted != null)
				return adapted;
		}
		return element;
	}
	
	
}
