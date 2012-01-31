/*
 * Created on Aug 12, 2004
 */
package com.realpersist.gef.editor;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;

/**
 * PaletteViewerProvider subclass used for initialising drag and drop
 * @author Phil Zoio
 */
public class SchemaPaletteViewerProvider extends PaletteViewerProvider
{

	/**
	 * implicit constructor
	 */
	public SchemaPaletteViewerProvider(EditDomain graphicalViewerDomain)
	{
		super(graphicalViewerDomain);
	}

	protected void configurePaletteViewer(PaletteViewer viewer)
	{
		super.configurePaletteViewer(viewer);
		viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
	}

}