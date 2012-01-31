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
package com.windowtester.swt.bootstrap;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.swt.event.recorder.EventRecorderPlugin;

public class SwtBootstrapApplication implements IPlatformRunnable {

	public static final String WINDOWTESTER_SWT_DUMMY_BUNDLE = "com.windowtester.swt.dummy";
	public static final String LAUNCH_CLASS_NAME_SYS_PROPERTY = "com.windowtester.swt.launch.class.name";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		// first start recording session
		EventRecorderPlugin.getDefault().startSession();
		// then load dummy bundle and use it's classloader
		Bundle bundle = Platform.getBundle(WINDOWTESTER_SWT_DUMMY_BUNDLE);
		if(bundle==null){
			throw new Exception("Cannot find dummy bundle : "+WINDOWTESTER_SWT_DUMMY_BUNDLE);
		}
		String className = System.getProperty(LAUNCH_CLASS_NAME_SYS_PROPERTY);
		Class swtClass = bundle.loadClass(className);
		ClassLoader bundleCl = swtClass.getClassLoader();
		ClassLoader bootstrapCl = Thread.currentThread().getContextClassLoader();
		// replace context classloader for main invocation
		if(bundleCl!=null&&bootstrapCl!=bundleCl){
			Thread.currentThread().setContextClassLoader(bundleCl);
		}else{
			LogHandler.log("Same context classloader in SWT bootstrap class! This should not have happened...");
		}
		Method main = swtClass.getMethod("main", new Class[] {String[].class});
		main.invoke(main, new Object[] {(String[])args});
		// fix for running Swing applications
		if(EventRecorderPlugin.isSwingRecordingSession()){
			// kp : 5/3/07
			//int count = 0;
			// wait before analyzing the frames for main application to start a new Frame
			//while(Frame.getFrames().length==0){
				//Thread.sleep(100);
				//if(count++>100)
				//	break;
			//}
			//if(count>100)
				//throw new Exception("Main Swing application did not opened any frame");
			//Frame[] f = Frame.getFrames();
			//while (f.length>0) {
				//Thread.sleep(100);
			//}
			while(true){
				Thread.sleep(100);
			}
		}
		// return back original context classloader
		Thread.currentThread().setContextClassLoader(bootstrapCl);
		return new Integer(0);
	}
}
