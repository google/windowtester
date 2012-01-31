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
package com.windowtester.runtime.gef.internal.condition;

import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.widgets.Control;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.internal.selectors.IPaletteViewerProvider;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;

/**
 * Tests to see if a palette is showing by checking the visibility of its
 * Control as provided by {@link PaletteViewer#getControl()}.
 */
public class PaletteShowingCondition implements ICondition {

	private class PaletteControlVisibleCondition implements ICondition {
		public boolean test() {
			PaletteViewer paletteViewer = getPaletteViewer();
			if (paletteViewer == null) {
				LogHandler.log("palette viewer is null in palette showing condition test");
				return false;
			}
			Control control = paletteViewer.getControl();
			if (control == null) {
				LogHandler.log("palette control is null in palette showing condition test");
				return false;	
			}
				
			boolean visible = SWTHierarchyHelper.isVisible(control);
			//System.out.println("control: " + UIProxy.getToString(control) + " visible: " + visible);
			return visible;
		}
	};

	
	private final IPaletteViewerProvider paletteViewerProvider;

	public PaletteShowingCondition(IPaletteViewerProvider paletteViewerProvider) {
		this.paletteViewerProvider = paletteViewerProvider;
	}

	public PaletteViewer getPaletteViewer() {
		return paletteViewerProvider.getViewer();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {	
		return new PaletteControlVisibleCondition().test();
	}
}