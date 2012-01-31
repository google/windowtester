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
package com.windowtester.eclipse.ui.target;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.LogHandler;


public class BundleHelper {

	
	public static class BundleSet {

		private final Bundle[] bundles;

		public BundleSet(Bundle[] bundles) {
			this.bundles = bundles;
		}
		
		public File[] asFiles() throws IOException {
			File[] files = new File[bundles.length];
			for (int i = 0; i < files.length; i++) {
				files[i] = getBundleFile(bundles[i]);
			}
			return files;
		}
		
		public void copyTo(File destination) throws IOException {
			FileHelper.copy(this.asFiles(), destination);
		}
		
	}
	
	
	/**
	 * Back-port of 3.4 {@link FileLocator#getBundleFile(Bundle)}.
	 */
	public static File getBundleFile(Bundle bundle) throws IOException {
		URL rootEntry = bundle.getEntry("/"); //$NON-NLS-1$
		rootEntry = FileLocator.resolve(rootEntry);
		if ("file".equals(rootEntry.getProtocol())) //$NON-NLS-1$
			return new File(rootEntry.getPath());
		if ("jar".equals(rootEntry.getProtocol())) { //$NON-NLS-1$
			String path = rootEntry.getPath();
			if (path.startsWith("file:")) {
				// strip off the file: and the !/
				path = path.substring(5, path.length() - 2);
				return new File(path);
			}
		}
		throw new IOException("Unknown protocol"); //$NON-NLS-1$
	}
	
	
	public static BundleSet getBundlesWithDependencies(String[] ids) {
		List<Bundle> bundles = new ArrayList<Bundle>();
		for (String id : ids) {			
			Bundle bundle = getBundle(id);
			if (bundle == null)
				LogHandler.log("bundle for id: " + id + " not resolved (skipped in provisioner creation)");
			else {
				add(bundles, bundle);						
				parseAndAddRequiredBundles(bundles, bundle);		
			}
		}
		return new BundleSet(bundles.toArray(new Bundle[]{}));
	}


	private static void parseAndAddRequiredBundles(List<Bundle> bundles, Bundle bundle) {
		String requiredBundles = getRequiredBundles(bundle);
		if (requiredBundles == null)
			return;
		
		String[] reqs = requiredBundles.split(",");
		for (int j = 0; j < reqs.length; j++) {
			String req = reqs[j];
			if (isOurBundleId(req)) {
				add(bundles, getBundle(trim(req)));
			}
		}
	}


	@SuppressWarnings("unchecked")
	private static String getRequiredBundles(Bundle bundle) {
		Dictionary<String,String> headers = bundle.getHeaders();
		String requiredBundles = (String) headers.get("Require-Bundle");
		return requiredBundles;
	}


	private static Bundle getBundle(String id) {
		return Platform.getBundle(id);
	}
	
	private static void add(List<Bundle> bundles, Bundle bundle) {
		if (!bundles.contains(bundle))
			bundles.add(bundle);
	}
	
	private static String trim(String bundleId) {
		return bundleId.split(";")[0];
	}

	private static boolean isOurBundleId(String bundleId) {
		return bundleId.startsWith("com.windowtester");
	}
	
	
}
