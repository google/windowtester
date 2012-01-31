package junit.extensions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.event.swt.UIProxy;
import com.windowtester.finder.swt.ShellFinder;
import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.monitor.IUIThreadMonitorListener;
import com.windowtester.runtime.swt.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.display.DisplayIntrospection;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.state.MenuWatcher;
import com.windowtester.runtime.util.ScreenCapture;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.swt.IUIContext;
import com.windowtester.swt.RuntimePlugin;
import com.windowtester.swt.UIContextFactory;
import com.windowtester.swt.WaitTimedOutException;
import com.windowtester.swt.WidgetSearchException;
import com.windowtester.swt.runtime.settings.TestSettings;
import com.windowtester.swt.util.ExceptionHandlingHelper;

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
public class UITestCase extends TestCase
	implements IUIThreadMonitorListener
{
	
	/**
	 * A constant used to specify how many levels of menu to dismiss in
	 * {@link #dismissUnexpectedMenus()}.
	 */
	private static final int MAX_MENU_DEPTH = 5;

	/**
	 * A flag to indicate whether a test should FAIL when menus/shells
	 * are dismissed/closed at the end of a test.
	 */
	private final boolean UNEXPECTED_SHELLS_MENUS_TREATED_AS_FAILURES = false;
	
	/**
	 * The application class to be launched by calling the static main method with the
	 * specified arguments (see {@link #_launchArgs}) in a separate thread, or
	 * <code>null</code> if no application is to be launched.
	 */
	private final Class _launchClass;

	/**
	 * The arguments to be passed to the static main method of the application class to be
	 * launched (see {@link #_launchClass}, or <code>null</code> if no application is
	 * to be launched.
	 */
	private String[] _launchArgs;

	/**
	 * The application display associated with this test or <code>null</code> if it has
	 * not been cached yet.
	 * 
	 * @see #launchApp()
	 * @see #cacheDisplay()
	 */
	private Display _display;

	/**
	 * The UI Context instance associated with the receiver.
	 */
	private IUIContext _uiContext;

	/**
	 * The root shell associated with this test or <code>null</code> if none. At the end
	 * of the test, any decendent shells of this shell will be forcefully closed.
	 */
	private Shell _rootShell;

	/**
	 * Flag indicating whether or not the test is currently executing.
	 */
	private boolean _testRunning;
	
	/**
	 * Collection of seen classes for managing oneTimeSetup
	 */
	private static Set _seenClasses = new HashSet();

	/**
	 * Counter used for managing oneTimeTeardown
	 */
	private static int _testsToRun = 0;
	
	
	// Cached exceptions for re-throwing
	private InvocationTargetException _ite;
	private IllegalAccessException _iae;

	// ///////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	// ///////////////////////////////////////////////////////////////////////////////

	/**
	 * Create an instance.
	 */
	public UITestCase() {
		this((Class)null);
	}

	/**
	 * Create an instance with the given name.
	 */
	public UITestCase(String testName) {
		super(testName);
		_launchClass = null;
		initTest();
		
	}
	
	/**
	 * Create an instance that will launch and test the specified application class.
	 * 
	 * @param launchClass - The application class to be launched by calling the static
	 *            main method with the specified arguments (see ) in a separate thread, or
	 *            <code>null</code> if no application is to be launched.
	 */
	public UITestCase(Class launchClass) {
		this(launchClass, null);
	}

	/**
	 * Create an instance that will launch and test the specified application class.
	 * 
	 * @param launchClass - The application class to be launched by calling the static
	 *            main method with the specified arguments (see ) in a separate thread, or
	 *            <code>null</code> if no application is to be launched.
	 * @param launchArgs - The arguments to be passed to the static main method of the
	 *            application class to be launched, or <code>null</code> if no
	 *            application is to be launched.
	 */
	public UITestCase(Class launchClass, String[] launchArgs) {
		_launchClass = launchClass;
		_launchArgs = launchArgs;
		initTest();
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Initialization and access
	//
	// //////////////////////////////////////////////////////////////////////////

	
	/**
	 * Increment our test count -- for onetime teardown management.
	 */
	private void initTest() {
		++_testsToRun;
	}
	
	/**
	 * The application class specified in the constructor is launched in a separate thread
	 * by calling the static main method with the arguments specified in the constructor.
	 * The current thread then waits for an active Shell to become available and
	 * initializes the display associated with the receiver to be that Shell's display.
	 */
	protected void launchApp() {
		if (_launchClass != null)
			setDisplay(launchApp(_launchClass, _launchArgs));
	}

	/**
	 * The specified application class is launched in a separate thread by calling the
	 * static main method with the specified arguments. The current thread then waits for
	 * an active Shell to become available and answers that Shell's display.
	 * 
	 * @param mainApp - The application class to be launched (not <code>null</code>)
	 * @param args - Arguments for the main method
	 * @return the {@link Display} for the launched application (not <code>null</code>)
	 */
	public static Display launchApp(final Class mainApp, final String[] args) {
		// TODO [author=Dan] Move this method to some new bootstrap utility class
		if (mainApp == null)
			throw new IllegalArgumentException("main application class cannot be null");
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Method main = mainApp.getMethod("main", new Class[]{
						String[].class
					});
					Object[] realArgs = args != null ? args : new String[]{null};
					main.invoke(main, realArgs);
				}
				catch (Exception e) {
					LogHandler.log(e);
				}
			}
		});
		t.start();
		return new DisplayIntrospection(10000).syncIntrospect();
	}

	/**
	 * Answers the display.
	 * 
	 * @return the display (not <code>null</code>)
	 * @throws IllegalStateException if the display has not been initialized
	 */
	public Display getDisplay() {
		if (_display == null)
			throw new IllegalStateException("Display has not been initialized.");
		return _display;
	}

	/**
	 * Cache the current {@link Display} if it has not already been cached by the
	 * {@link #launchApp()} method or a prior call to this method.
	 */
	protected void cacheDisplay() {
		if (_display == null)
			// TODO[pq]: is this right?
			// can we safely do this in the RCP/workbench test case?
			setDisplay(Display.getCurrent());
	}

	/**
	 * Sets the display.
	 * 
	 * @param display The display (not <code>null</code>)
	 * @throws {@link IllegalStateException} if the display has already been initialized
	 *             via the {@link #setDisplay(Display)} method.
	 */
	protected void setDisplay(Display display) {
		if (display == null)
			throw new IllegalArgumentException("Display cannot be null");
		if (_display != null)
			throw new IllegalStateException("Display has already been initialized.");
		_display = display;
	}

	/**
	 * Get the {@link IUIContext} associated with this test.
	 * 
	 * @return the user interface context instance (not <code>null</code>)
	 * @throws IllegalStateException if the context has not been initialized
	 */
	protected final IUIContext getUIContext() {
		if (_uiContext == null)
			throw new IllegalArgumentException("UI context has not been initialized");
		return _uiContext;
	}

	/**
	 * Cache the {@link IUIContext} associated with this test if it has not already been
	 * cached by prior call to this method.
	 */
	protected void cacheUIContext() {
		if (_uiContext == null){
//			PlaybackSettings settings = getPlaybackSettings();
//			if(settings.getExperimentalPlaybackOn()){
//				setUIContext(UIContextFactory.createContext(SwtPlaybackContext.class, getDisplay()));
//			}else{
				setUIContext(UIContextFactory.createContext(getDisplay()));
//			}
		}
	}
	
	protected PlaybackSettings getPlaybackSettings() {
		return Platform.isRunning() ? RuntimePlugin.getDefault().getPlaybackSettings() : PlaybackSettings.loadFromFile();
	}

	/**
	 * Set the user interface context associated with this test
	 * 
	 * @param context the user interface context (not <code>null</code>)
	 * @throws IllegalStateException if the context has already been initialized.
	 */
	protected void setUIContext(IUIContext context) {
		if (context == null)
			throw new IllegalArgumentException("UI context cannot be null");
		if (_uiContext != null)
			throw new IllegalStateException("UI context has already been initialized");
		_uiContext = context;
	}

	/**
	 * Answer the root shell associated with this test or <code>null</code> if none has
	 * been specified.
	 * 
	 * @return the root shell or <code>null</code>
	 */
	protected Shell getRootShell() {
		return _rootShell;
	}

	/**
	 * Cache the active shell so that any decendent shells can be forcefully closed at the
	 * end of the test.
	 */
	protected void cacheRootShell() {
		getDisplay().syncExec(new Runnable() {
			public void run() {
				setRootShell(getDisplay().getActiveShell());
			}
		});
	}

	/**
	 * Set the root shell associated with the receiver. At the end of the test, any
	 * decendent shells of this shell that are still open will be forcefully closed.
	 * 
	 * @param shell the shell or <code>null</code>
	 */
	protected void setRootShell(Shell shell) {
		_rootShell = shell;
	}

	/**
	 * A hook to register widgets with the ui context.
	 */
	protected void registerWidgetInfo() {
		// overriden in subclasses
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Cleanup
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Called after tearDown is called to perform common cleanup such as calling
	 * {@link #closeUnexpectedShells()} and {@link #dismissUnexpectedMenus()}.
	 */
	protected void cleanUp() {
		dismissUnexpectedMenus();
		closeUnexpectedShells();
	}

	/**
	 * Called at the end of the test to forcefully close shells that should not be open.
	 * and guarantee that no (blocking) dialogs are still open. By default, this method
	 * closes any shells decendent from the root shell but not the root shell itself. This
	 * is necessary so that the junit thread can proceed. Subclasses may override or
	 * extend to close a different set of shells.
	 * 
	 * @see #setRootShell(Shell)
	 */
	protected void closeUnexpectedShells() {
		closeDecendentShells(getRootShell());
	}

	/**
	 * Called at the end of the test to dismiss menus that were left open at the end of 
	 * the test.
	 *
	 */
	protected void dismissUnexpectedMenus() {
		//if a menu is open, take a screenshot and dismiss
		if (MenuWatcher.getInstance(_display).isMenuOpen()) {
			if (UNEXPECTED_SHELLS_MENUS_TREATED_AS_FAILURES) {
				createScreenCapture("unexpected menu");
				//register an exception if there isn't one already
				if (_ite == null)
					_ite = new InvocationTargetException(new AssertionFailedError("Menu left open at end of test"));
			}
			
			//TODO: this may be OS-specific...
			for (int i= 0; i <= MAX_MENU_DEPTH; ++i) {
				getUIContext().keyClick(SWT.ESC); //close menu by hitting ESCAPE
			}
		}
	}
	
	
	/**
	 * Forcefully close any shells decendent from the specified shell but does not close
	 * the specified shell itself.
	 * 
	 * @param shell the shell
	 */
	protected void closeDecendentShells(final Shell shell) {
		// guard against case where display is already disposed
		
		Display display = getDisplay();
		if (!display.isDisposed() && shell != null && !shell.isDisposed())
			display.syncExec(new Runnable() {
				public void run() {
					if (!shell.isDisposed()) {
						// Close all blocking dialogs. Necessary, otherwise the junit
						// thread does not proceed.
						Shell[] shells = shell.getShells();
						closeShells(shells);
					}
				}

				private void closeShells(final Shell[] shells) {
					for (int i = 0; i < shells.length; i++) {
						if (!shells[i].isDisposed()) {
							closeShells(shells[i].getShells());
						}
						if (!shells[i].isDisposed()) {
							if (UNEXPECTED_SHELLS_MENUS_TREATED_AS_FAILURES) {
								// we specifically care about modal shells...
								if (ShellFinder.isModal(shells[i])) {
									createScreenCapture("forcing shell close: "
											+ shells[i].getText());
									/*
									 * If there is no error associated, signal
									 * one
									 */
									if (_ite == null)
										_ite = new InvocationTargetException(
												new AssertionFailedError(
														"Shell left open at end of test"));
								}
							}

							closeShell(shells[i]);

						}
					}
				}

				private void closeShell(Shell shell) {
					/*
					 * Forcefully closing toolitp shells causes the workbench to get totally wedged.
					 * It's VERY difficult to reliably identify tooltip shells; as a stop-gap we are
					 * ignoring ALL non-modal shells in cleanup.  If this is unsafe, we'll have to 
					 * figure out what non-modal dialogs are a problem and close only them.
					 * 
					 */
					String shellDescription = UIProxy.getToString(shell);
					if (ShellFinder.isModal(shell)) {
						shell.close();
						// shells[i].dispose();
						TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "Test cleanup closing shell: " + shellDescription);
					} else {
						TraceHandler.trace(IRuntimePluginTraceOptions.BASIC, "Test cleanup skipping shell: " + shellDescription + " [not modal]");
					}
				}
			});
	}

	// ///////////////////////////////////////////////////////////////////////////////
	//
	// Test Execution
	//
	// ///////////////////////////////////////////////////////////////////////////////

	/**
	 * Runs the bare test sequence.
	 * 
	 * @see junit.framework.TestCase#runBare()
	 */
	public final void runBare() throws Throwable {
		
		TestSettings.getInstance().push();
		
		if (_testRunning)
			throw new IllegalStateException("test is already running");
		_testRunning = true;
		TestMonitor.getInstance().beginTestCase(this);
		--_testsToRun;
		launchApp();
		cacheDisplay();
		cacheUIContext();
		cacheRootShell();
		registerWidgetInfo();
		// TODO [author=Dan] merge the two test monitors
		startTestMonitor();
		try {
			launchTestThread();
			waitUntilFinished();
		}
		catch (Throwable e) {
			handleException(e);
		}
		stopTestMonitor();
		TestMonitor.getInstance().endTestCase();
		TestSettings.getInstance().pop();

	}

	/**
	 * Launch a seperate test thread that will call {@link TestCase#setUp()}, test and
	 * {@link TestCase#tearDown()} and interact with the UI thread through the
	 * {@link IUIContext} associated with this test.
	 */
	protected void launchTestThread() {
		Thread testThread = new Thread(getName()) {
			public void run() {
				try {
					// System.out.println("running test in " + Thread.currentThread());
					try {
						try {
							UITestCase.this.doOneTimeSetup();
						}catch (Throwable e) {
							handleException(e);
						}
						try {
							UITestCase.this.setUp();
						}
						catch (Throwable e) {
							handleException(e);
						}
						try {
							UITestCase.this.runTest();
							/*
							 * post test-run, check for any active conditions to properly
							 * cleanup (close open dialogs, etc)
							 */
							// TODO [author=Dan] make this IUIContext API ?
							((IUIContext) getUIContext()).handleConditions();
						}
						catch (Throwable e) {
							handleException(e);
						}
						finally {
							try {
								tearDown();
							}
							catch (Throwable e) {
								handleException(e);
							}
							try {
								UITestCase.this.doOneTimeTearDown();
							}catch (Throwable e) {
								handleException(e);
							}
						}
					}
					finally {
						try {
							cleanUp();
						}
						catch (Throwable e) {
							// internal error; no screen capture
							logException(e);
						}
					}
				}
				catch (InvocationTargetException e) {
					// e.printStackTrace();
					e.fillInStackTrace();
					_ite = e;
				}
				catch (IllegalAccessException e) {
					// e.printStackTrace();
					e.fillInStackTrace();
					_iae = e;
				}
				catch (Throwable e) {
					// e.printStackTrace();
					// e.fillInStackTrace();
					_ite = new InvocationTargetException(e);
				}
				finally {
					_testRunning = false;
					// guard against case where display is already disposed
					
					if (!getDisplay().isDisposed())
						getDisplay().wake();
				}
			}
		};
		testThread.setDaemon(true);
		testThread.start();
	}

	
	/**
	 * Perform one time setup (internal).
	 */
	private void doOneTimeSetup() throws Exception {
		//only exec onetime setup if class has not been seen
		if (_seenClasses.add(getClass())) {
			TestSettings.getInstance().push(); //create fresh settings scope
			oneTimeSetup();
		}
	}
	
	/**
	 * Performs one time setup of the test fixture.  Called once per test class, before 
	 * setup.
	 */
	protected void oneTimeSetup() throws Exception {
		//default: no-op
	}
	
	/**
	 * Perform one time setup (internal).
	 */
	private void doOneTimeTearDown() throws Exception {
		//only exec onetime teardown if all the tests have been run
		//System.out.println("tests to run: " + _testsToRun);
		if (_testsToRun == 0) {
			oneTimeTearDown();
			TestSettings.getInstance().pop();
		}
	}
	
	/**
	 * Performs one time teardown of the test fixture.  Called once per test class, after 
	 * teardown.
	 */
	protected void oneTimeTearDown() throws Exception {
		//default: no-op
	}
	

	/**
	 * Read and dispatch UI events until the test thread is finished. Exceptions thrown by
	 * tests are re-thrown.
	 * 
	 * @throws Throwable - a test-thrown assertion/exception
	 */
	private void waitUntilFinished() throws Throwable {
		Display display = getDisplay();

		// Loop until test completes or there is an exception

		while (_testRunning && _ite == null && _iae == null && !display.isDisposed()) {
			try {
				if (!display.isDisposed() && !display.readAndDispatch()) {
					display.sleep();
				}
			}
			catch (SWTException ex) {
				// Do nothing: rethrowing errors blocks the display thread.
				// One must rely on the fact of proper error handling of
				// display thread users.
				//!pq: this was the abbot assumption, but is it right?  why not cache and fail later?
				LogHandler.log("Exception caught in UITestCase.waitUntilFinished():");
				LogHandler.log(ex);
			}
			catch (Throwable t) {
				LogHandler.log("Exception caught in UITestCase.waitUntilFinished():");
				LogHandler.log(t);
			}
		}

		// Throw all exceptions caught inside the test thread

		if (_ite != null) {
			// Extract the wrappered exception as appropriate
			if (_ite.getCause() instanceof AssertionFailedError)
				throw _ite.getCause();
			throw _ite;
		}
		if (_iae != null)
			throw _iae;
	}

	/**
	 * Start monitoring the UI for responsiveness.
	 */
	protected void startTestMonitor() {
		getUIThreadMonitor().setListener(this);
	}

	/**
	 * Stop monitoring the UI for responsiveness.
	 */
	protected void stopTestMonitor() {
		getUIThreadMonitor().setListener(null);
	}

	/**
	 * Answer the user interface thread monitor used to determine if the user interface
	 * thread is idle or unresponsive longer than some expected time.
	 * 
	 * @return the user interface thread monitor (not <code>null</code>)
	 */
	protected IUIThreadMonitor getUIThreadMonitor() {
		return (IUIThreadMonitor) getUIContext().getAdapter(IUIThreadMonitor.class);
	}

	/**
	 * Process the exception by logging it, taking a screen capture, and rethrowing the
	 * exception.
	 * 
	 * @param e the Exception
	 * @throws Throwable rethrows the passed exception
	 */
	protected void handleException(Throwable e) throws Throwable {
		logException(e);
		/*
		 * To avoid creating multiple screenshots for the same exception (which might
		 * get handled multiple times as it pops us the call stack) we check to see if
		 * the exception has already been captured.
		 */

		//!pq: re-enabled to address:
		if (_iae == null  &&_ite == null) {
			/*
			 * WidgetSearch and Wait Exceptions are handled in UIContext, so all
			 * we have to handle here are others.
			 * 
			 * NOTE: if a user throws one of these exceptions themselves, the screenshots won't happen as they expect...
			 */
			if (!(e instanceof WidgetSearchException) && !(e instanceof WaitTimedOutException)) {
				createScreenCapture(e.getMessage());
			}
		}
		throw e;
	}

	private void createScreenCapture(String desc) {
		TraceHandler.trace(IRuntimePluginTraceOptions.CONDITIONS, "Creating screenshot ("+ desc +") for testcase: " + getId());
		ScreenCapture.createScreenCapture(getId());
	}

	/**
	 * Log the exception.
	 * 
	 * @param e the Exception
	 */
	protected void logException(Throwable e) {
		LogHandler.log(e);
	}

	/**
	 * Called by the user interface thread monitor if the UI thread is idle for longer
	 * than some expected time.
	 * 
	 * @see com.windowtester.swt.monitor.IUIThreadMonitorListener#uiTimeout(boolean)
	 */
	public void uiTimeout(boolean isIdle) {
		_ite = new InvocationTargetException(new WaitTimedOutException("UI thread idle timeout"));
		// do a screenshot before shutting down
		ScreenCapture.createScreenCapture(getId());
		// TODO [author=Dan] How can shells be closed or the test be stopped if UI thread is busy? 
		// TODO [author=Dan] Can close shells be done by the closeUnexpectedShells method?
		if (isIdle)
			new ExceptionHandlingHelper(getDisplay(), true).closeOpenShells();
	}

	/**
	 * The test identifier for screen captures
	 * 
	 * @return the test id (not <code>null</code>)
	 */
	private String getId() {
		return TestMonitor.getInstance().getCurrentTestCaseID();
	}
}
