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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.internal.condition.PaletteShowingCondition;
import com.windowtester.runtime.gef.internal.finder.MultiplePartsFoundException;
import com.windowtester.runtime.gef.internal.finder.PartNotFoundException;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;

/**
 * Base class for palette item revealers.
 */
public abstract class AbstractPaletteRevealer implements IRevealer {
	
	private final IPaletteViewerProvider viewerProvider;
	private final IEditPartProvider partProvider;
		
	public AbstractPaletteRevealer(IPaletteViewerProvider viewerProvider, IEditPartProvider partProvider) {
		Invariants.notNull(viewerProvider);
		Invariants.notNull(partProvider);
		this.viewerProvider = viewerProvider;
		this.partProvider   = partProvider;
	}
	
	protected IEditPartProvider getPartProvider() {
		return partProvider;
	}
	
	protected IPaletteViewerProvider getViewerProvider() {
		return viewerProvider;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.selectors.IRevealer#reveal(com.windowtester.runtime.IUIContext)
	 */
	public void reveal(IUIContext ui) throws WidgetSearchException {
		doReveal(ui);
	}

	protected void doReveal(IUIContext ui) throws WidgetSearchException {
		fastFailIfNoActiveEditor();
		revealPalette(ui);
		revealPart(getPaletteViewer());
	}
	
	protected PaletteViewer getPaletteViewer() {
		return getViewerProvider().getViewer();
	}

	protected EditPart getEditPart() throws MultiplePartsFoundException, PartNotFoundException {
		return getPartProvider().getPart();
	}
	
	protected void revealPalette(IUIContext ui) throws WidgetSearchException {
		if (isPaletteVisible(ui))
			return; //no need to hover if it's already visible!
		doRevealPalette(ui);
	}

	protected boolean isPaletteVisible(IUIContext ui) {
		return ConditionMonitor.test(ui, paletteShowing());
	}

	protected PaletteShowingCondition paletteShowing() {
		return new PaletteShowingCondition(getViewerProvider());
	}

	public abstract void doRevealPalette(IUIContext ui) throws WidgetSearchException;

	protected void fastFailIfNoActiveEditor() throws WidgetSearchException {
		
		// notice we're not using the UI here since we don't to treat 
		// this as a failure
		if (ActiveEditorCondition.forName(".*").test())
			return;
		throw new WidgetSearchException("No active editor found, palette item search aborted.");

	}

	protected void revealPart(final PaletteViewer viewer)
			throws MultiplePartsFoundException, PartNotFoundException {
		if (viewer == null)
			throw new PartNotFoundException("palette viewer not found");
		final EditPart editPart = getEditPart();
		if (editPart == null)
			throw new PartNotFoundException("part not found");			
		try {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					viewer.reveal(editPart);
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}


	
}
