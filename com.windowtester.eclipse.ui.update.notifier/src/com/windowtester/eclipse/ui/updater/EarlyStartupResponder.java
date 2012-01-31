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
package com.windowtester.eclipse.ui.updater;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;


/**
 * Handles {@link IStartup#earlyStartup()}.
  * 
 * @author Phil Quitslund
 *
 */
public class EarlyStartupResponder implements IStartup {

	
	private static final boolean DEBUGGING = false;  //always show dialog (for testing)
	
	protected WTV5UpdateDialog dialog;

	private static final String[] IDE_PRODUCT_IDS  = new String[] {"org.eclipse.sdk.ide", "org.eclipse.platform.ide"};


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		//DISABLED: pending further investigation
//		if (DEBUGGING || (!alreadySeen() && isDevWorkbench() && !pdeLaunch()))
//			showDialog();
	}


	private boolean isDevWorkbench() {
		IProduct product = Platform.getProduct();
		String id = product.getId();
		for (String productId : IDE_PRODUCT_IDS) {
			if (productId.equals(id))
				return true;
		}
		return false;
	}


	private boolean alreadySeen() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final String key = WTUpdateConstants.P_SHOW_UPDATE_MSG;
		String value = store.getString(key);
		if (!"".equals(value)) {
//			System.out.println("already seen");
			return true; 
		}
		return false;
	}


	private boolean pdeLaunch() {
		String pdeLaunchEnv = System.getProperty("eclipse.pde.launch");
		if (pdeLaunchEnv == null)
			return false;
		if (pdeLaunchEnv.equals("true")) {
//			System.out.println("pde launch");
			return true;
		}
		return false;
	}


	private void showDialog() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				dialog = new WTV5UpdateDialog();
				dialog.open();
			}
		});
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final String key = WTUpdateConstants.P_SHOW_UPDATE_MSG;
		store.setValue(key, "seen");
	}

}
