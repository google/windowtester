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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * Factory for creating palette item {@link IRevealer}s.
 */
public class PaletteItemRevealer {

	private static class NoOpPaletteRevealer implements IRevealer {
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.gef.internal.selectors.IRevealer#reveal(com.windowtester.runtime.IUIContext)
		 */
		public void reveal(IUIContext ui) throws WidgetSearchException {
			//no-op
		}
	}
		
	private static boolean usePinningStrategy = true;
	
	private static boolean revealEnabled      = true;
	
	/*
	 * Temporary (non-API) accessors.
	 */
	public static void usePinningStrategy() {
		usePinningStrategy = true;
	}
	public static void useHoverStrategy() {
		usePinningStrategy = false;
	}
	public static void enableRevealing() {
		revealEnabled = true;
	}
	public static void disableRevealing() {
		revealEnabled = false;
	}
	
	public static IRevealer getCurrent(IPaletteViewerProvider viewerProvider, IEditPartProvider partProvider) {
		if (!revealEnabled)
			return new NoOpPaletteRevealer();
		if (usePinningStrategy)
			return new PaletteItemPinningRevealer(viewerProvider, partProvider);	
		return new PaletteItemHoveringRevealer(viewerProvider, partProvider);		
	}
	
}
