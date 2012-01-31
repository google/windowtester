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
package com.windowtester.runtime.gef.internal.helpers;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Eases access to palette.
 */
public class PaletteAccessor {

	
	public static class ToolDescriptor {
		
		private final ToolEntry entry;

		public ToolDescriptor(ToolEntry entry) {
			this.entry = entry;
		}

		static ToolDescriptor forEntry(ToolEntry entry) {
			return new ToolDescriptor(entry);
		}
		
		public boolean isConnection() {
			return entry instanceof ConnectionCreationToolEntry;
		}
		
	}
	
	private static class NullDescriptor extends ToolDescriptor {

		public NullDescriptor() {
			super(null);
		}

		public boolean isConnection() {
			return false;
		}
		
	}
	
	
	private static class NullPaletteAccessor extends PaletteAccessor {
		public NullPaletteAccessor() {
			super(null);
		}
		
		public ToolDescriptor getActiveTool() {
			return new NullDescriptor();
		}
		
		public PaletteViewer getPalette() {
			return null;
		}
		
	}
	
	private final GraphicalViewer viewer;

	private PaletteAccessor(GraphicalViewer viewer) {
		this.viewer = viewer;
	}

	public static PaletteAccessor forViewer(GraphicalViewer viewer) {
		if (viewer == null)
			return new NullPaletteAccessor();
		return new PaletteAccessor(viewer);
	}

	public PaletteViewer getPalette() {
		EditDomain editDomain = viewer.getEditDomain();
		if (editDomain == null)
			return null;
		return editDomain.getPaletteViewer();
	}

	public ToolDescriptor getActiveTool() {
		PaletteViewer palette = getPalette();
		if (palette == null)
			return null;
		return ToolDescriptor.forEntry(palette.getActiveTool());
	}
	
	
	
}
