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
package com.windowtester.runtime.gef.locator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.finder.IIdentifierHintProvider;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.locator.SWTWidgetByClassNameLocator;

/**
 * Locates the {@link Button} widget used to open and close a 
 * Flyout Palette ({@link FlyoutPaletteComposite}).
 */
public class PaletteButtonLocator extends SWTWidgetByClassNameLocator {

	private static final String BUTTON_CLASS_NAME = "org.eclipse.gef.ui.palette.FlyoutPaletteComposite$ButtonCanvas";

	private static final long serialVersionUID = 2173131452914706312L;

	
	private static final class Identifier implements IFigureIdentifier, IIdentifierHintProvider {
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
		 */
		public ILocator identify(IFigure figure, Event event) {
			//NOTICE: the figure is ignored...
			Widget w = event.widget;
			//NOTICE: we are doing an exact match...
			if (w == null)
				return null;
			if (w.getClass().getName().equals(BUTTON_CLASS_NAME))
				return new PaletteButtonLocator();
			return null;
		}
		/* (non-Javadoc)
		 * @see com.windowtester.internal.runtime.finder.IIdentifierHintProvider#requiresXY()
		 */
		public boolean requiresXY() {
			return false;
		}
	}
	
	private static final Identifier IDENTIFIER_HELPER = new Identifier();
	
	public PaletteButtonLocator() {
		super(BUTTON_CLASS_NAME);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class<?> adapter) {
		if (adapter == IFigureIdentifier.class)
			return IDENTIFIER_HELPER;
		if (adapter == IIdentifierHintProvider.class)
			return IDENTIFIER_HELPER;
		return super.getAdapter(adapter);
	}
	
}
