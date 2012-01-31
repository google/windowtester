package abbot.swt.eclipse.utils;

import junit.framework.Assert;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import abbot.Log;

/**
 * Static utilities for launching wizards.
 * 
 * @author Tom Roche
 * @version $Id: WizardLaunchUtils.java,v 1.2 2006-04-28 18:55:55 alexander_smirnoff Exp $
 */
public class WizardLaunchUtils {
	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2003\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	public static final String NEW_WIZARDS_EXTENSION_POINT_ID = "org.eclipse.ui.newWizards";

	public static final String WIZARD_ID_ATTRIBUTE = "id";

	public static final Object WIZARD_CONFIG_ELEMENT_NAME = "wizard";

	/**
	 * Get the new-wizards extension having the given ID.
	 * Returns <code>null</code> on error, or
	 * <code></code> if named extension not found.
	 */
	public static IExtension[] getNewWizardsExtensions() {
//		IExtension[] ret = null;
		// get the new-wizards extension point
		IExtensionPoint extpt = Platform.getExtensionRegistry().
			getExtensionPoint(NEW_WIZARDS_EXTENSION_POINT_ID);
		Assert.assertNotNull(extpt); // it really oughta be there
		return extpt.getExtensions();
	}

	/**
	 * CONTRACT: both args are well-formed
	 */
	public static IConfigurationElement getIdentifiedWizardElement(IExtension[] exts, String id) {
		IConfigurationElement ret = null;
		Assert.assertNotNull(exts); // although theoretically it could be?
		int nExts = exts.length;
		if (nExts <= 0) {
			Log.log(": ERROR: no new-wizards extensions found");
			return null;
		} else {
			// get the one we want
			for (int i = 0; i < nExts; i++) {
				IExtension ext = exts[i];
// ASSERT: platform prevents malformed extensions
				IConfigurationElement[] ices = ext.getConfigurationElements();
				int nIces = ices.length;
				if ((ices == null) || (nIces <= 0)) {
					Log.log(
						".getNamedWizardElement: ERROR: extension " + i +
						"has no config elements");
					continue;
				} else {
					for (int j = 0; j < nIces; j++) {
						IConfigurationElement ice = ices[j];
						Assert.assertNotNull(ice);
						String iceName = ice.getName();
						Utils.assertNotEmpty(iceName);
						if (iceName.equals(WIZARD_CONFIG_ELEMENT_NAME)) {
							// ICE is for a wizard, get its ID
							String iceID = ice.getAttribute(WIZARD_ID_ATTRIBUTE);
							if (Utils.isEmpty(iceID) || (!iceID.equals(id))) {
								// it's not the ICE we want
								continue;
							} else {
								return ice;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the new-wizards extension having the given ID.
	 * Returns <code>null</code> on error, or
	 * <code></code> if named extension not found.
	 */
	public static IConfigurationElement getNamedNewWizardExtension(String id) {
//		IConfigurationElement ret = null;
		IExtension[] exts = getNewWizardsExtensions();
		Assert.assertNotNull(exts); // although theoretically it could be?
		int nExts = exts.length;
		if (nExts <= 0) {
			Log.log(".getNamedNewWizardExtension: ERROR: no new-wizards extensions found");
			return null;
		} else {
			// get the one we want
			return getIdentifiedWizardElement(exts, id);
//			for (int i = 0; i < nExts; i++) {
////				IExtension ext = exts[i];
//// ASSERT: platform prevents malformed extensions
//				IConfigurationElement[] ices = exts[i].getConfigurationElements();
//				int nIces = 0;
//				if ((ices == null) || ((nIces = ices.length) <= 0)) {
//					for (int j = 0; j < nIces; j++) {
//						IConfigurationElement ice = ices[j];
//						String wizard_id = ice.getAttribute(WIZARD_ID_ATTRIBUTE);
//						String model_id = ice.getAttribute(ID_ATT);
//						if (wizard_id.equals(getWizardId()) && model_id.equals(getModelId())) {
//							wtModel = ice;
//							wtModelIndex = model_index;
//							break;
//						}
//						if (wizard_id.equals(getWizardId())) {
//							model_index++;
//						}
//					}
//				}
//			}
		}
	}
	
}
