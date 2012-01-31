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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.internal.concurrent.SafeCallable;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.operation.SWTShowMenuOperation;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor;
import com.windowtester.runtime.swt.internal.widgets.finder.MatchCollector;

/**
 * A {@link Display} reference.
 */
public class DisplayReference
	implements IVisitable, ISearchable
{
	private static final int DEFAULT_EXEC_WAIT_TIME = 10000;

	private final Display display;

	public static DisplayReference getDefault() {
		return new DisplayReference(Display.getDefault());
	}

	public DisplayReference(Display display) {
		this.display = display;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IDisplayReference#getDisplay()
	 */
	public Display getDisplay() {
		// TODO[pq]: come up with a pithy message to warn users that any access to the returned item must be down safely on the UI thread
		return display;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IDisplayReference#getFocusControl()
	 */
	public ControlReference<?> getFocusControl() {
		return execute(new Callable<ControlReference<?>>() {
			public ControlReference<?> call() throws Exception {
				return SWTWidgetReference.forControl(display.getFocusControl());
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IDisplayReference#getActiveShell()
	 */
	public ShellReference getActiveShell() {
		return execute(new Callable<ShellReference>() {
			public ShellReference call() throws Exception {
				Shell shell = display.getActiveShell();
				return ShellReference.forShell(shell);
			}
		});
	}

	public ShellReference[] getShells() {
		return execute(new Callable<ShellReference[]>() {
			public ShellReference[] call() throws Exception {
				return ShellReference.forShells(display.getShells());
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.IDisplayReference#getSystemColor(int)
	 */
	public Color getSystemColor(final int id) {
		return execute(new Callable<Color>() {
			public Color call() throws Exception {
				return display.getSystemColor(id);
			}
		});
	}

	public ISWTWidgetReference<?>[] findWidgets(ISWTWidgetMatcher matcher) {
		//implemented in terms of a visit:
		MatchCollector collector = new MatchCollector(matcher);
		return (ISWTWidgetReference<?>[]) collector.findMatchesIn(this).toArray(SWTWidgetReference.emptyArray());
	}

	public void accept(Visitor visitor) {
		for (ShellReference shell : getShells())
			shell.accept(visitor);
	}

	/**
	 * Convenience method that calls {@link #execute(Callable, long)} with a maximum wait
	 * time of 10 seconds.
	 */
	public <T> T execute(Callable<T> callable) {
		return execute(callable, DEFAULT_EXEC_WAIT_TIME);
	}

	/**
	 * Execute the specified callable on the SWT UI thread. This method will not return
	 * until the callable finishes executing, an exception is thrown, or the specified
	 * maximum number of milliseconds have elapsed. If an exception does occur and the
	 * callable object implements {@link SafeCallable}, then
	 * {@link SafeCallable#handleException(Exception)} is called with the exception that
	 * occurred.
	 * 
	 * @param maxWaitTime the maximum number of milliseconds to wait for the UI thread to
	 *            execute the callable. If this method is called from the SWT UI thread,
	 *            then the maximum number of milliseconds is ignored.
	 * @throws SWTUIException if there is an exception when executing the callable on
	 *             the UI thread
	 * @throws IllegalStateException if the receiver is already executing
	 * @throws WaitTimedOutException if the UI thread does not execute the callable with
	 *             specified number of milliseconds
	 */
	public <T> T execute(Callable<T> callable, long maxWaitTime) {
		return new SWTUIExecutor<T>(callable).run(getDisplay(), maxWaitTime, 10);
	}

	/**
	 * Convenience method that calls {@link #execute(Callable, long)} with a maximum wait
	 * time of 10 seconds, but does not return any result
	 */
	public void execute(final VoidCallable voidCallable) {
		execute(voidCallable, DEFAULT_EXEC_WAIT_TIME);
	}

	/**
	 * Convenience method that calls {@link #execute(Callable, long)} , but does not
	 * return any result
	 */
	public void execute(final VoidCallable voidCallable, int maxWaitTime) {
		execute(new SafeCallable<Object>() {
			public Object call() throws Exception {
				voidCallable.call();
				return null;
			}

			public Object handleException(Throwable e) throws Throwable {
				voidCallable.handleException(e);
				return null;
			}
		}, maxWaitTime);
	}

	/**
	 * Cleanup by closing any open menus
	 */
	public void closeAllMenus() {
		new SWTShowMenuOperation(null).closeAllMenus().execute();
	}

	/**
	 * Check if the UI thread has finished processing all asynchronous messages.
	 * 
	 * @return <code>true</code> if the UI thread is idle or disposed
	 */
	public boolean isIdle() {
		return execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return display.isDisposed() || !display.readAndDispatch();
			}
		});
	}
}
