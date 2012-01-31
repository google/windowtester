/*
 * Created on Jul 15, 2004
 */
package com.realpersist.gef.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.realpersist.gef.dnd.DataElementFactory;
import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.Table;

/**
 * Encapsulates functionality to create the PaletteViewer
 * @author Phil Zoio
 */
public class PaletteViewerCreator
{

	/** the palette root */
	private PaletteRoot paletteRoot;


	/**
	 * Returns the <code>PaletteRoot</code> this editor's palette uses.
	 * 
	 * @return the <code>PaletteRoot</code>
	 */
	public PaletteRoot createPaletteRoot()
	{
		// create root
		paletteRoot = new PaletteRoot();

		// a group of default control tools
		PaletteGroup controls = new PaletteGroup("Controls");
		paletteRoot.add(controls);

		// the selection tool
		ToolEntry tool = new SelectionToolEntry();
		controls.add(tool);

		// use selection tool as default entry
		paletteRoot.setDefaultEntry(tool);

		// the marquee selection tool
		controls.add(new MarqueeToolEntry());

		// a separator
		PaletteSeparator separator = new PaletteSeparator(SchemaDiagramPlugin.PLUGIN_ID + ".palette.seperator");
		separator.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		controls.add(separator);

		controls.add(new ConnectionCreationToolEntry("Connections", "Create Connections", null, AbstractUIPlugin
				.imageDescriptorFromPlugin(SchemaDiagramPlugin.PLUGIN_ID, "icons/relationship.gif"),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(SchemaDiagramPlugin.PLUGIN_ID, "icons/relationship.gif")));

		PaletteDrawer drawer = new PaletteDrawer("New Component", AbstractUIPlugin.imageDescriptorFromPlugin(
				SchemaDiagramPlugin.PLUGIN_ID, "icons/connection.gif"));

		List entries = new ArrayList();

		CombinedTemplateCreationEntry tableEntry = new CombinedTemplateCreationEntry("New Table", "Create a new table",
				Table.class, new DataElementFactory(Table.class), AbstractUIPlugin.imageDescriptorFromPlugin(
						SchemaDiagramPlugin.PLUGIN_ID, "icons/table.gif"), AbstractUIPlugin
						.imageDescriptorFromPlugin(SchemaDiagramPlugin.PLUGIN_ID, "icons/table.gif"));

		CombinedTemplateCreationEntry columnEntry = new CombinedTemplateCreationEntry("New Column", "Add a new column",
				Column.class, new DataElementFactory(Column.class), AbstractUIPlugin.imageDescriptorFromPlugin(
						SchemaDiagramPlugin.PLUGIN_ID, "icons/column.gif"), AbstractUIPlugin
						.imageDescriptorFromPlugin(SchemaDiagramPlugin.PLUGIN_ID, "icons/column.gif"));

		entries.add(tableEntry);
		entries.add(columnEntry);

		drawer.addAll(entries);

		paletteRoot.add(drawer);

		// todo add your palette drawers and entries here
		return paletteRoot;

	}

	/**
	 * @return Returns the paletteRoot.
	 */
	public PaletteRoot getPaletteRoot()
	{
		return paletteRoot;
	}
}