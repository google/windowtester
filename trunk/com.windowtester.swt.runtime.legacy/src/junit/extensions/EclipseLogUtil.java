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
package junit.extensions;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.windowtester.internal.runtime.util.StringUtils;

/**
 * A utility class that provides access to events recorded to the Eclipse log.
 * 
 * @author Phil Quitslund
 *
 */
public class EclipseLogUtil extends com.windowtester.runtime.swt.internal.util.EclipseLogUtil {

	//Null-instance
	public static final EclipseLogUtil NULL = new EclipseLogUtil() {};
	

	/** A listener for tracking logged exceptions */
	private LogListener _logListener;
	
		
	/**
	 * Setup a log listener if platform is running.
	 */
	public void setUp() {
		if (Platform.isRunning()) {
			_logListener = new LogListener();
			Platform.addLogListener(_logListener);
		}
	}

	/**
	 * Remove log listener (if platform is running).
	 */
	public void tearDown() {
		if (Platform.isRunning()) {
			if (_logListener != null)
				Platform.removeLogListener(_logListener);
		}
	}
	
	
	/**
	 * Get all exceptions logged to the Platform logging service (up to this point) 
	 * in the execution of this test.  Note that it is only legal to invoke this method when
	 * the Platform is running (e.g., in workbench tests).
	 * @return an array of the logged exceptions
	 * @throws IllegalStateException if invoked when the Platform is not running
	 */
	public Throwable[] getLoggedExceptions() {
		if (!Platform.isRunning())
			throw new IllegalStateException("Logged exceptions can only be requested when the Platform is running");
		return _logListener.getLoggedExceptions();
	}
	
	
	/**
	 * Asserts that there are no exceptions logged to the platform log. Note that it is only legal to invoke this method when
	 * the Platform is running (e.g., in workbench tests).
	 * @throws AssertionFailedError if there are logged exceptions 
	 * @throws IllegalStateException if invoked when the Platform is not running
	 */
	public void assertNoLoggedExceptions() {
		int numEx = getLoggedExceptions().length;
		String msg = "Logged exceptions: ";
		if (numEx > 0)
			msg += StringUtils.toString(getLoggedExceptions());
		Assert.assertEquals(msg, 0, numEx);
	}
	
	
	/**
	 * A listener that caches logged errors.
	 * 
	 * @author Phil Quitslund
	 */
	final private class LogListener implements ILogListener {

		List _loggedExceptions = new ArrayList();
		
		/**
		 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus, java.lang.String)
		 */
		public void logging(IStatus status, String plugin) {
			//for now just tracking thrown exceptions... consider broadening this?
			Throwable t = status.getException();
			if (t!=null)
				_loggedExceptions.add(t);
		}
		
		Throwable[] getLoggedExceptions() {
			return (Throwable[]) _loggedExceptions.toArray(new Throwable[]{});
		}
		
	}
	
	
	

	
}
