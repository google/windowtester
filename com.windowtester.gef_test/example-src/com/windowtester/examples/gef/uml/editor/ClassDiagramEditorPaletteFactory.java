/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package com.windowtester.examples.gef.uml.editor;

//import org.eclipse.gef.examples.shapes.ShapesPlugin;
//import org.eclipse.gef.examples.shapes.model.Connection;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.windowtester.examples.gef.uml.model.ClassElement;
import com.windowtester.test.gef.GEFTestPlugin;

/**
 * Utility class that can create a GEF Palette.
 * 
 * @see #createPalette()
 * @author Elias Volanakis
 * @author Phil Quitslund
 */
final class ClassDiagramEditorPaletteFactory {

	/** Preference ID used to persist the palette location. */
	private static final String PALETTE_DOCK_LOCATION = "ClassDiagramEditorPaletteFactory.Location";
	/** Preference ID used to persist the palette size. */
	private static final String PALETTE_SIZE = "ClassDiagramEditorPaletteFactory.Size";
	/** Preference ID used to persist the flyout palette's state. */
	private static final String PALETTE_STATE = "ClassDiagramEditorPaletteFactory.State";

	
	/** Create the "Shapes" drawer. */
	private static PaletteContainer createElementsDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Elements");

		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				"Class", "Create a class element", ClassElement.class,
				new ClassCreationFactory(), AbstractUIPlugin.imageDescriptorFromPlugin(GEFTestPlugin.PLUGIN_ID,
							"icons/class.gif"),AbstractUIPlugin.imageDescriptorFromPlugin(GEFTestPlugin.PLUGIN_ID,
								"icons/class.gif"));
		
		
		
		componentsDrawer.add(component);

//		component = new CombinedTemplateCreationEntry("Rectangle",
//				"Create a rectangular shape", RectangularShape.class,
//				new SimpleFactory(RectangularShape.class), ImageDescriptor
//						.createFromFile(ShapesPlugin.class,
//								"icons/rectangle16.gif"), ImageDescriptor
//						.createFromFile(ShapesPlugin.class,
//								"icons/rectangle24.gif"));
//		componentsDrawer.add(component);

		return componentsDrawer;
	}

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @return a new PaletteRoot
	 */
	static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createElementsDrawer());
		return palette;
	}

	/** Create the "Tools" group. */
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteGroup toolGroup = new PaletteGroup("Tools");

		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolGroup.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		toolGroup.add(new MarqueeToolEntry());

		// Add a (unnamed) separator to the group
		toolGroup.add(new PaletteSeparator());

		// Add (solid-line) connection tool
		/*
		tool = new ConnectionCreationToolEntry("Inherits",
				"Create an inheritance relationship", new CreationFactory() {
					public Object getNewObject() {
						return null;
					}

					// see ShapeEditPart#createEditPolicies()
					// this is abused to transmit the desired line style
					public Object getObjectType() {
						return Connection.SOLID_CONNECTION;
					}
				}, ImageDescriptor.createFromFile(ShapesPlugin.class,
						"icons/connection_s16.gif"), ImageDescriptor
						.createFromFile(ShapesPlugin.class,
								"icons/connection_s24.gif"));
		*/
		toolGroup.add(tool);

		// Add (dashed-line) connection tool
		/*
		tool = new ConnectionCreationToolEntry("Implements",
				"Create an implements relationship", new CreationFactory() {
					public Object getNewObject() {
						return null;
					}

					// see ShapeEditPart#createEditPolicies()
					// this is abused to transmit the desired line style
					public Object getObjectType() {
						return Connection.DASHED_CONNECTION;
					}
				}, ImageDescriptor.createFromFile(ShapesPlugin.class,
						"icons/connection_d16.gif"), ImageDescriptor
						.createFromFile(ShapesPlugin.class,
								"icons/connection_d24.gif"));
		*/
		toolGroup.add(tool);

		return toolGroup;
	}

	/** Utility class. */
	private ClassDiagramEditorPaletteFactory() {
		// Utility class
	}

}