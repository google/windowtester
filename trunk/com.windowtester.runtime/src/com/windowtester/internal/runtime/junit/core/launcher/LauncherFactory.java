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
package com.windowtester.internal.runtime.junit.core.launcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.internal.runtime.junit.core.ISequenceRunner.IRunnable;

/**
 * A factory for Application launchers.
 */
public class LauncherFactory {

	
	/**
	 * Abstract base launcher implementation.
	 */
	static abstract class AbstractLauncher implements IApplicationLauncher {
		
		List _listeners = new ArrayList();
		
		/* (non-Javadoc)
		 * @see junit.extensions.core.launcher.IApplicationLauncher#addListener(junit.extensions.core.launcher.ILaunchListener)
		 */
		public void addListener(ILaunchListener listener) {
			_listeners.add(listener);
		}
		
		/**
		 * Notify listeners post launch.
		 */
		protected void notifyPostLaunch() {
			for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
				ILaunchListener listener = (ILaunchListener) iter.next();
				listener.postFlight();
			}
		}
		
		/**
		 * Notify listeners pre launch.
		 */
		protected void notifyPreLaunch() {
			for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
				ILaunchListener listener = (ILaunchListener) iter.next();
				listener.preFlight();
			}
		}		
	}
	
	
	/**
	 * A special launcher that does nothing (presumably because the application
	 * is bootstrapped elsewhere).
	 *
	 */
	static class NoOpLauncher extends AbstractLauncher {
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.test.launcher.IApplicationLauncher#launch()
		 */
		public void launch() {
			//do nothing
		}
	}
	
	/**
	 * A launcher that spawns another thread in which to execute.
	 * Note that pre and post launch notifications occur before and after
	 * the thread is started respectively.
	 */
	static class SeparateThreadLauncher extends AbstractLauncher {
		
		private final IRunnable _runnable;

		public SeparateThreadLauncher(IRunnable runnable) {
			_runnable = runnable;
		}
		

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.test.launcher.LauncherFactory.AbstractLauncher#doLaunch()
		 */
		public void launch() {
			notifyPreLaunch();
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						_runnable.run();
					} catch (Throwable e) {
						e.printStackTrace();
						LogHandler.log(e);
					}
				}
				});
			t.start();
			notifyPostLaunch();
		}
	}
	
	/**
	 * A launcher that invokes the main method of a class in a separate
	 * thread.
	 */
	static class MainRunner extends SeparateThreadLauncher {

		public MainRunner(final Class launchClass, final String[] launchArgs) {
			super(new IRunnable() {
				public void run() throws Throwable {
					Object[] realArgs;
					String[] nullArgs = new String[0];
					Method main = launchClass.getMethod("main",
							new Class[] { String[].class });
					
					// instead pass null string
					if (launchArgs == null)
						realArgs = new Object[] {nullArgs};
					else  realArgs = new Object[]{launchArgs};
				//	main.setAccessible(true);
					main.invoke(null, realArgs);
				}
			});
		}
	}
	
	
	/**
	 * Launcher factory method; creates a launcher appropriate for the given 
	 * arguments.
	 * @param launchClass the class to launch (may be <code>null</code>)
	 * @param launchArgs the program arguments to pass to the launched class (may be <code>null</code>)
	 * @return a launcher appropriate to the given arguments
	 */
	public static IApplicationLauncher create(Class launchClass, String[] launchArgs) {
		if (launchClass == null)
			return new NoOpLauncher();
		return new MainRunner(launchClass, launchArgs);
	}
	
}
