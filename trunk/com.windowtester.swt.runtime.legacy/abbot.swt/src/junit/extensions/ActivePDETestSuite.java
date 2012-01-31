/*
 * Created on 31.03.2005
 * by Richard Birenheide (D035816)
 *
 * Copyright SAP AG 2005
 */
package junit.extensions;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.swt.internal.operation.SWTPushEventOperation;


/**
 * A test suite for running PDE tests in a separate thread.
 * <p/>
 * Forks the entire test run to run in a new thread.<br>
 * Intended for usage with Eclipse jUnit run/debug configurations. These
 * run normally in the UI thread which blocks on showing dialogs or popup menues.
 * Eclipse can recognize classes having a method <code>public static Test suite()</code>
 * Within that one can code:<br/><code><pre>
 * public static Test suite() {
 *   ActivePDETestSuite suite = new ActivePDETestSuite("name");
 *   //Testclasses derived from org.junit.TestCase
 *   suite.addTestSuite(FirstTestClass.class);
 *   suite.addTestSuite(SecondTestClass.class);
 *   .
 *   .
 *   .
 *   return suite;
 * }
 * </pre>
 * </code>
 * @author Richard Birenheide (D035816)
 */
public class ActivePDETestSuite extends TestSuite {
	private volatile boolean testsFinished = false;
	
	/**
	 * The display associated with this run.
	 */
	protected Display display;
	/**
	 * The shell being active when starting this run.
	 */
	protected Shell rootShell;
	/**
	 * The current implementation uses ESC key strokes to close eventually left open
	 * stuff when a tests fails. Tom make this customizable, the number of strokes
	 * in each tear down is stored in this variable. The standard is 5. If this
	 * is too few strokes (more windows could be open) or too much (not necessary
	 * under any circumstances and causing too much overhead in long running tests)
	 * the value can be set accordingly. This can be done in {@link #suiteSetUp()}. 
	 */
	protected int escapeStrokes = 5;

	/**
	 * Default constructor.
	 * <p/>
	 * The name associated with this class is given the Class name. 
	 */
	public ActivePDETestSuite() {
		super(ActivePDETestSuite.class.getName());
	}
		
	/**
	 * Constructs with a test class.
	 * <p/>
	 * The name associated with this class is given the Class name. 
	 * @param theClass a test class.
	 */
	public ActivePDETestSuite(Class theClass) {
		super(theClass, ActivePDETestSuite.class.getName());
	}
	
	/**
	 * Constructs with a name containing no test.
	 * <p/>
	 * @param name the name. This name will be given to the separate thread running.
	 */
	public ActivePDETestSuite(String name) {
		super (name);
	}
	
	/**
	 * Constructs with a name and containing the test class given.
	 * <p/>
	 * @param theClass a test class.
	 * @param name the name. This name will be given to the separate thread running.
	 */
	public ActivePDETestSuite(Class theClass, String name) {
		super(theClass, name);
	}
	
	public void run(final TestResult result) {
		this.preForkSetUp();
		this.display = Display.getCurrent();
		if (this.display == null) {
			throw new IllegalStateException("The TestSuite must be run from an SWT UI thread");
		}
		this.rootShell = display.getActiveShell();
		Thread t = new Thread(this.getName()) {
			public void run() {
				try {
					ActivePDETestSuite.this.suiteSetUp();
					ActivePDETestSuite.super.run(result);
					ActivePDETestSuite.this.suiteTearDown();
				}
				finally {
					ActivePDETestSuite.this.testsFinished = true;
					display.wake();
				}
			}
		};
		t.setDaemon(true);
		t.start();
		waitUntilFinished();
	}
	
	public void runTest(final Test test, final TestResult result) {
		try {
				// inlined due to limitation in VA/Java 
				//ActiveTestSuite.super.runTest(test, result);
				test.run(result);
			} finally {
				ActivePDETestSuite.this.runFinished();
			}
	}

	private void waitUntilFinished() {
		int ctr = 0;
		while (!this.testsFinished) {
			try {
				if(!display.readAndDispatch())
					display.sleep();
			}
			catch (SWTException ex) {
				//Do nothing: rethrowing errors blocks the display thread.
				//One must rely on the fact of proper error handling of 
				//display thread users.
			}
			if(ctr++%100000==0)
				System.err.print(".");
		}
	}
	
	/**
	 * Closes all shells when finished.
	 */
	private void runFinished() {
		Runnable closeShells = new Runnable() {
			public void run() {
				if (!rootShell.isDisposed()) {
					//Close all blocking dialogs. Necessary, otherwise the junit
					//thread does not proceed.
					Shell[] shells = rootShell.getShells();
					ActivePDETestSuite.closeShells(shells);
				}
				//TODO close all open stuff eg. menues etc.
				//Introduced as work around until a way is found to close open
				//menu windows.
				//Robot robot = new Robot();
				for (int i = 0; i < escapeStrokes; i++) {
					keyClick(SWT.ESC);
				}
			}
		};
		display.syncExec(closeShells);
	}
	/**
	 * Retrieves the display associated with this test run.
	 * <p/>
	 * @return the display associated with this test run. Only valid after the test
	 * has been started. 
	 */
	public Display getDisplay() {
		return this.display;
	}
	/**
	 * Runs a set up prior to forking into a separate thread.
	 * <p/>
	 * This method is useful for tests which initially do not run in
	 * the display thread. It is expected that in that case the calling
	 * thread is made to the display thread by calling {@link Display#getDefault()}
	 * or makes the current thread to the display thread by any other means.<br/>
	 * The default implementation does nothing.
	 */
	protected void preForkSetUp() {}
	/**
	 * Runs a set up prior to executing the entire tests within this suite.
	 * <p/>
	 * The method will run in the separate thread.<br/>
	 * The default implementation does nothing.
	 */
	protected void suiteSetUp() {}
	/**
	 * Runs a tear down after executing the entire tests within this suite.
	 * <p/>
	 * The method will run in the separate thread.<br/>
	 * The default implementation does nothing.
	 */
	protected void suiteTearDown() {}
	/**
	 * Closes all shells and child shells of the given array
	 * recursively.
	 * <p>
	 * This is called after each TestCase to guarantee that no (blocking) dialogs
	 * are still open. Does currently not work perfect and it is thus highly
	 * recommended that this is done properly in TestCase.tearDown().
	 * 
	 * @param shells
	 *          the shells to close.
	 */
	public static void closeShells(final Shell[] shells) {
		for (int i = 0; i < shells.length; i++) {
			if (!shells[i].isDisposed()) {
				closeShells(shells[i].getShells());
			}
			if (!shells[i].isDisposed()) {
				shells[i].close();
				//shells[i].dispose();
			}
		}
	}
	/**
	 * Convenience method for {@link Display#syncExec(java.lang.Runnable)} catching
	 * {@link SWTException} and rethrowing {@link AssertionFailedError} if appropriate.
	 * <p>
	 * Should be used from TestCase.testXXX() methods when asserting within the
	 * SWT thread in order to guarantee that a test failure is displayed correctly.
	 * @param runnable the Runnable to execute.
	 * @throws AssertionFailedError if an assertion failed in the display thread.
	 * @throws RuntimeException either a RuntimeException has been issued by the 
	 * Runnable or the Runnable has thrown a Throwable not being a RuntimeException.
	 * In that case the RuntimeException carries the original Exception as cause.
	 */
	public static void syncExec(Display display, final Runnable runnable) {
		try {
			display.syncExec(runnable);
		}
		catch (SWTException swtEx) {
			if (swtEx.throwable instanceof AssertionFailedError) {
				throw (AssertionFailedError) swtEx.throwable;
			}
			else {
				throw swtEx;
			}
		}
	}
	/**
	 * Convenience method for {@link Display#asyncExec(java.lang.Runnable)} catching
	 * {@link SWTException} and rethrowing {@link AssertionFailedError} if appropriate.
	 * <p>
	 * Should be used from TestCase.testXXX() methods when asserting within the
	 * SWT thread in order to guarantee that a test failure is displayed correctly.<p/>
	 * NOTE that exception handling with this method cannot be guaranteed to work since
	 * exceptions are thrown asynchronously. Currently I have no idea how to notify the
	 * caller of a test being failed. But generally I have no idea why one should like
	 * to run _tests_ asynchronously. Possibly one could introduce an ErrorListener
	 * here but I am not sure. 
	 * Ideal would be to have knowledge about the actual {@link Test} and {@link TestResult} 
	 * when this method is called. Then one could feed the result with 
	 * {@link TestResult#addError(junit.framework.Test, java.lang.Throwable)} or
	 * {@link TestResult#addFailure(junit.framework.Test, junit.framework.AssertionFailedError).
	 * Actually I do not know how to get the correct Test. Unfortunately it is not
	 * the one issued by {@link #runTest(Test, TestResult).
	 * @param runnable the Runnable to execute. 
	 */
	public static void asyncExec(Display display, Runnable runnable) {
		try {
			display.asyncExec(runnable);
		}
		catch (SWTException swtEx) {
			if (swtEx.throwable instanceof AssertionFailedError) {
				throw (AssertionFailedError) swtEx.throwable;
			}
			else {
				throw swtEx;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Primitive event posting actions 
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Dispatch a keyClick(keyUp..keyDown) event.
	 * @param keyCode - the key to click.
	 */
	public void keyClick(int keyCode) {
		keyDown(keyCode);
		keyUp(keyCode);
	}

	/**
	 * Dispatch a keyClick(keyUp..keyDown) event.
	 * @param keyCode - the key to click.
	 */
	public void keyClick(char keyCode) {
		boolean shift = needsShift(keyCode);
		
		if (shift)
			keyDown(SWT.SHIFT);
		
		keyDown(keyCode);
		keyUp(keyCode);
		
		if (shift)
			keyUp(SWT.SHIFT);
	}

	
	/**
	 * Determine if this key requires a shift to dispatch the keyStroke.
	 * @param keyCode - the key in question
	 * @return true if a shift event is required.
	 */
	boolean needsShift(char keyCode) {

		if (keyCode >= 62 && keyCode <=90)
			return true;
		if (keyCode >= 123 && keyCode <=126)
			return true;
		if (keyCode >= 33 && keyCode <=43 && keyCode != 39)
			return true;
		if (keyCode >= 94 && keyCode <=95)
			return true;
		if (keyCode == 58 || keyCode == 60 || keyCode == 62)
			return true;
		
		return false;
	}

	/**
	 * Dispatch a keyUp event.
	 * @param keyCode - the key to release.
	 */
	public void keyUp(final int keyCode) {
		//trace("post key up " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.keyCode = keyCode;
		new SWTPushEventOperation(event).execute();
	}
	
	/**
	 * Dispatch a keyDown event.
	 * @param keyCode - the key to press.
	 */
	public void keyDown(final int keyCode) {
		//trace("post key down " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.keyCode = keyCode;
		new SWTPushEventOperation(event).execute();
	}
	
	
	/**
	 * Dispatch a keyUp event.
	 * @param keyCode - the key to release.
	 */
	public void keyUp(final char keyCode) {
		//trace("post key up " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.character = keyCode;
		new SWTPushEventOperation(event).execute();
	}
	
	/**
	 * Dispatch a keyDown event.
	 * @param keyCode - the key to press.
	 */
	public void keyDown(final char keyCode) {
		//trace("post key down " + keyCode);
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = keyCode;
		new SWTPushEventOperation(event).execute();
	}

	
}