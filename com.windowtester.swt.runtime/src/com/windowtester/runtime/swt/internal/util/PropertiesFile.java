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
package com.windowtester.runtime.swt.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;


/**
 * Wrapper for file (specifically {@link IFile}) access to a {@link Properties} object.
 */
public class PropertiesFile {

	
	private final File _file;

	//note: file is expected to exist
	public PropertiesFile(IFile file) {
		this(toFile(file));
		Assert.isTrue(file.exists(), "file must exist");
	}
	
	public PropertiesFile(File file) {
		Assert.isNotNull(file, "file must not be null");
		_file = file;
	}
	
			
	protected final File getFile() {
		return _file;
	}
	
	protected static final File toFile(IFile file) {
		return absoluteFileFrom(file);
	}
	
	protected final Properties getProperties() throws IOException {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(getFile());
			Properties props = new Properties();
			props.load(stream);
			return props;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e); //shouldn't happen
		} finally {
			stream.close();
		}
	}
	
	public String getProperty(String key) throws IOException {
		return getProperties().getProperty(key);
	}
	
	public void setProperty(String key, String value) throws IOException {
		Properties properties = getProperties();
		properties.setProperty(key, value);
		FileOutputStream out = new FileOutputStream(getFile());
		try {
			properties.store(out, null);
			//refreshLocal();
		} finally {
			out.close();
		}
	}

	//annoyingly these are coming back Object when they should be Strings...
	public Set entrySet() throws IOException {
		return Collections.unmodifiableSet(getProperties().entrySet());
	}
	
//	private void refreshLocal()  {
//		try {
//			getFile().refreshLocal(IResource.DEPTH_ZERO, null);
//		} catch (CoreException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
    /**
     * Utility for getting an absolute file from a resurce.
     * 
     * @param resource
     *            the resource from which to derive the File, or null
     * @return the File that corresponds to the input resource, or null if no
     *         File can be determined.
     */
    public static File absoluteFileFrom(IResource resource) {
        if (resource == null) {
            return null;
        }
        IPath location = resource.getLocation();
        if (location != null) {
            return location.toFile();
        }
        return null;
    }
	
}
