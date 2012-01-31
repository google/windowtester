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
package com.windowtester.eclipse.ui.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import com.windowtester.codegen.debug.DebugRecordingInfo;
import com.windowtester.internal.runtime.util.StringUtils;

public class DummyBundleBuilder {

	public static final String DUMMY_PLUGIN_NAME = "com.windowtester.swt.dummy";
	private static final String NEW_LINE = StringUtils.NEW_LINE;
	
	public IPath buildDummyBundle(ILaunchConfiguration configuration, IPath dir) throws CoreException{
		// test if directory already exists, if yes delete it and create new
		if(dir.toFile().exists()&&dir.toFile().isDirectory()){
			deleteDir(dir.toFile());
		}
		dir = dir.append(new Path(DUMMY_PLUGIN_NAME));
		if(!dir.toFile().mkdirs())
			throw new CoreException(new Status(Status.ERROR, DUMMY_PLUGIN_NAME, Status.ERROR, "Cannot build the dummy bundle", null));
		// update manifest file with new classpath information
		buildManifest(configuration, dir);
		return dir;
	}
	public void buildManifest(ILaunchConfiguration configuration, IPath path) throws CoreException{
		try {
			IPath dir = path.append(new Path("META-INF"));
			if(!dir.toFile().exists()){
				if(!dir.toFile().mkdirs())
					throw new CoreException(new Status(Status.ERROR, DUMMY_PLUGIN_NAME, Status.ERROR, "Cannot build the dummy bundle", null));
			}
			IPath manifest = dir.append(new Path("MANIFEST.MF"));
			if(manifest.toFile().exists())
				manifest.toFile().delete();
			BufferedWriter out = new BufferedWriter(new FileWriter(manifest.toFile()));
			String m = "Manifest-Version: 1.0"+ NEW_LINE +
			           "Bundle-Name: Dummy Plug-in"+ NEW_LINE +
					   "Bundle-SymbolicName: "+DUMMY_PLUGIN_NAME+NEW_LINE+
					   "Bundle-Vendor: Google Inc."+NEW_LINE+
					   "Bundle-Version: 1.0.0"+ NEW_LINE +
					   "Bundle-ClassPath: "+getClasspath(configuration)+NEW_LINE+
					   "Require-Bundle: org.eclipse.core.runtime, org.eclipse.ui";
			out.write(m);
			out.close();
		} catch (Exception e) {
			throw new CoreException(new Status(Status.ERROR, DUMMY_PLUGIN_NAME, Status.ERROR, e.getMessage(), e));
		}
	}
	public String getClasspath(ILaunchConfiguration configuration) throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
		Vector cp = new Vector();
		// Add Bootstrap, Standard (except rt.jar) and User class path entries to the dummy bundle
		
		for (int i = 0; i < entries.length; i++) {
		//	if (entries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = entries[i].getLocation();
				if ((location != null)&&!location.endsWith("swt.jar")&&!location.endsWith("rt.jar")){
					cp.add(location);
				}
		//	}
		}
		StringBuffer buffer = new StringBuffer();
		int i=1;
		for (Iterator iter=cp.iterator(); iter.hasNext();i++) {
			String location = (String) iter.next();
			/* $codepro.preprocessor.if version >= 3.2.0 $ */ 
			buffer.append("external:");
			/* $codepro.preprocessor.endif$ */
			buffer.append(location);
			if(cp.size()!=i)
				buffer.append(",");
		}
		DebugRecordingInfo.getInfo().setBundleClasspath((String[]) cp.toArray(new String[cp.size()]));
		return buffer.toString();
	}
		
	
	private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
	protected void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            String[] children = srcDir.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
            }
        } else {
            copyFile(srcDir, dstDir);
        }
    }	
	protected void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
