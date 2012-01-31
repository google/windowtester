/*
 * Created on Aug 12, 2004
 */
package com.realpersist.gef.editor;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.windowtester.test.gef.GEFTestPlugin;


/**
 * Contains the preferences for the palette flyout
 * 
 * [!pq:] hacked to get prefs from our activator
 * 
 * @author Phil Zoio
 * @author Phil Quitslund
 */
public class PaletteFlyoutPreferences implements FlyoutPreferences
{

	public static final int DEFAULT_PALETTE_WIDTH = 150;

	protected static final String PALETTE_DOCK_LOCATION = "Dock location";
	protected static final String PALETTE_SIZE = "Palette Size";
	protected static final String PALETTE_STATE = "Palette state";

	public int getDockLocation()
	{
		int location = getIntPreference(PALETTE_DOCK_LOCATION);
		if (location == 0)
		{
			return PositionConstants.WEST;
		}
		return location;
	}

	private int getIntPreference(String prefId) {
		return getPreferenceStore().getInt(prefId);
	}

	public int getPaletteState()
	{
		int state = getIntPreference(PALETTE_STATE);
		return state;
	}

	public int getPaletteWidth()
	{
		int width = getIntPreference(PALETTE_SIZE);
		if (width == 0)
			return DEFAULT_PALETTE_WIDTH;
		return width;
	}

	public void setDockLocation(int location)
	{
		setIntPreference(PALETTE_DOCK_LOCATION, location);
	}

	private void setIntPreference(String key, int value) {
		getPreferenceStore().setValue(key, value);
	}

	private IPreferenceStore getPreferenceStore() {
		return GEFTestPlugin.getDefault().getPreferenceStore();
//		return SchemaDiagramPlugin.getDefault().getPreferenceStore();
	}

	public void setPaletteState(int state)
	{
		setIntPreference(PALETTE_STATE, state);
	}

	public void setPaletteWidth(int width)
	{
		setIntPreference(PALETTE_SIZE, width);
	}

}