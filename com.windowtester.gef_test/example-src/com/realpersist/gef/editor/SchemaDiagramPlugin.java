package com.realpersist.gef.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The plugin implementation class
 */
public class SchemaDiagramPlugin extends AbstractUIPlugin
{

	/** the plugin id */
	public static final String PLUGIN_ID = "com.realpersist.gef.schemaeditor";

	//The shared instance.
	private static SchemaDiagramPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;


	/**
	 * The constructor.
	 */
	public SchemaDiagramPlugin()
	{
		super();
		plugin = this;
		try
		{
			resourceBundle = ResourceBundle.getBundle("com.realpersist.gef.schemaeditor.SchemaDiagramPluginResources");
		}
		catch (MissingResourceException x)
		{
			resourceBundle = null;
		}
	}

	/**
	 * The constructor.
	 */
	public SchemaDiagramPlugin(IPluginDescriptor descriptor)
	{
		super(descriptor);
		plugin = this;
		try
		{
			resourceBundle = ResourceBundle.getBundle("com.realpersist.gef.schemaeditor.SchemaDiagramPluginResources");
		}
		catch (MissingResourceException x)
		{
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static SchemaDiagramPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle = SchemaDiagramPlugin.getDefault().getResourceBundle();
		try
		{
			return bundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}
}