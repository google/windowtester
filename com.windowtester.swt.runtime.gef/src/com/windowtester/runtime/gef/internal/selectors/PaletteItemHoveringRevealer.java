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
package com.windowtester.runtime.gef.internal.selectors;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.matchers.ByClassMatcher;
import com.windowtester.runtime.swt.internal.matchers.IsVisibleMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * A revealer for palette items.
 */
public class PaletteItemHoveringRevealer extends AbstractPaletteRevealer {

	private class SashLocator extends SWTWidgetLocator {
		private static final long serialVersionUID = 1L;
		public SashLocator() {
			super(Widget.class);
		}
		protected ISWTWidgetMatcher buildMatcher() {
			return buildPaletteSashLocator();
		}
	}
	
			
	public PaletteItemHoveringRevealer(IPaletteViewerProvider viewerProvider, IEditPartProvider partProvider) {
		super(viewerProvider, partProvider);
	}

	public void hoverToReveal(IUIContext ui) throws WidgetSearchException {
		
		//wiggleMouse(ui, sash); //<--- probably not necessary
		hoverOver(ui, new SashLocator());
		
//		//TODO: this search needs to be scoped -- (not just any visible palette sash)
//		ui.mouseMove(new SWTWidgetLocator(Widget.class) {
//			private static final long serialVersionUID = 1L;
//			protected IWidgetMatcher buildMatcher() {
//				return buildPaletteSashLocator();
//			}
//		});
//		ui.wait(paletteShowing());
	}



	private void hoverOver(IUIContext ui, IWidgetLocator sash) throws WidgetSearchException {
		ui.mouseMove(sash);
		ui.wait(paletteShowing());
	}

//	private void wiggleMouse(IUIContext ui, IWidgetLocator sash) throws WidgetSearchException {
//		ui.mouseMove(new XYLocator(sash, -10, -10));
//		ui.pause(500);
//		ui.mouseMove(new XYLocator(sash, -5, -5));
//		ui.pause(500);
//	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.AbstractPaletteRevealer#doRevealPalette(com.windowtester.runtime.IUIContext)
	 */
	public void doRevealPalette(IUIContext ui) throws WidgetSearchException {
		hoverToReveal(ui);
	}
	

//	//find the title canvas in the palette viewer's control
//	protected IWidgetMatcher buildPaletteSashLocator() {
//		IWidgetMatcher titleCanvasMatcher = new ByNameClassMatcher(
//				"org.eclipse.gef.ui.palette.FlyoutPaletteComposite$TitleCanvas");
//		titleCanvasMatcher = new CompoundMatcher(titleCanvasMatcher,
//				VisibilityMatcher.create(true));
//		
//		//TODO: construct a matcher that can be used to scope the search for the hover target				
//		//TODO: this scoping should really be delegated to a search scope provider...
//		//TODO: this is a place-holder...
//		IWidgetMatcher scopingMatcher = new IWidgetMatcher() {
//			public boolean matches(Object widget) {
//				return true;
//			}
//		};		
//		return new CompoundMatcher(titleCanvasMatcher, scopingMatcher);
//	}

	//find the title canvas in the palette viewer's control
	protected ISWTWidgetMatcher buildPaletteSashLocator() {
		ISWTWidgetMatcher titleCanvasMatcher = new ByClassMatcher(
				"org.eclipse.gef.ui.palette.FlyoutPaletteComposite$TitleCanvas").and(IsVisibleMatcher.forValue(true));
//		
//		//TODO: construct a matcher that can be used to scope the search for the hover target				
//		//TODO: this scoping should really be delegated to a search scope provider...
//		//TODO: this is a place-holder...
//		IWidgetMatcher scopingMatcher = new IWidgetMatcher() {
//			public boolean matches(Object widget) {
//				return true;
//			}
//		};		
//		return new CompoundMatcher(titleCanvasMatcher, scopingMatcher);
		return titleCanvasMatcher;
	}


	
}
