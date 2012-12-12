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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.Bundle;

import com.windowtester.internal.debug.LogHandler;

@SuppressWarnings({ "unchecked" })
public final class ProgramArgumentBuilder {


	
	public String[] getProgramArguments(ILaunchConfiguration configuration,
			File configDir, String[] requiredPluginsIds) throws CoreException {
		HashSet notFound=new HashSet();
		try{
		
		/* $if eclipse.version >= 3.6 $ */
		
		return new ProgramArgumentBuilder36(notFound).getProgramArguments(configuration, requiredPluginsIds);
		
		/* $elseif eclipse.version == 3.5 $ */
		
//		return new ProgramArgumentBuilder35(notFound).getProgramArguments(configuration, requiredPluginsIds);
	
		/* $elseif eclipse.version == 3.4 $
		
		return new ProgramArgumentBuilder34(notFound).getProgramArguments(configuration, configDir, requiredPluginsIds);
		
		$elseif eclipse.version == 3.3 $

		return new ProgramArgumentBuilder33(notFound).getProgramArguments(configuration, configDir, requiredPluginsIds);
		
		$elseif eclipse.version == 3.2 $
		
		return new ProgramArgumentBuilder32(notFound).getProgramArguments(configuration, configDir, requiredPluginsIds);
		
		$endif$ */
		}finally{
			checkConfig(configDir, notFound);
		}
	}
	
	private void checkConfig(File configDir,HashSet ids) {
		HashSet locations=new HashSet();
		Iterator it=ids.iterator();
		while (it.hasNext()){
			String id=(String) it.next();
			Bundle bundle = Platform.getBundle(id);
			if (bundle!=null){
				locations.add(bundle.getLocation());
			}
			else{
				LogHandler.log("Error: No plugin models for plugin with id: '"
						+ id + "' were found in runtime");
			}
		}
		if (!locations.isEmpty()){
			File file = new File(configDir,"config.ini");
			Properties pr=new Properties();
			try {
				pr.load(new FileInputStream(file));
				String property = pr.getProperty("osgi.bundles");
				StringBuffer bld=new StringBuffer();
				bld.append(property);
				String[] split = property.split(",");
				locations.removeAll(new HashSet(Arrays.asList(split)));
				Iterator i=locations.iterator();
				while (i.hasNext()){
					String location=(String) i.next();
					bld.append(',');
					bld.append(location);
				}
				pr.setProperty("osgi.bundles", bld.toString());
				pr.store(new FileOutputStream(file),"Configuration after bundle injection");
			} catch (FileNotFoundException e) {
				LogHandler.log("Configuration file not found");
			} catch (IOException e) {
				LogHandler.log("IO Error during reading configuration file");				
			}
		}
	}
}
