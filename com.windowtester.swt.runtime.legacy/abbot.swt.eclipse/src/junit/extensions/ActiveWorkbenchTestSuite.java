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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



/**
 * A test suite for running PDE tests in a separate workbench job.
 * <p/>
 * Forks the entire test run to run in a workbench job.<br>
 * Intended for usage with Eclipse jUnit run/debug configurations. These
 * run normally in the UI thread which blocks on showing dialogs or popup menues.
 * Eclipse can recognize classes having a method <code>public static Test suite()</code>
 * Within that one can code:<br/><code><pre>
 * public static Test suite() {
 *   ActiveWorkbenchTestSuite suite = new ActiveWorkbenchTestSuite("name");
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
public class ActiveWorkbenchTestSuite extends TestSuite {
	
	/**
	 * The family of this job.
	 */
	public static final String JOB_FAMILY = "WorkbenchTestSuite";
	
	private volatile boolean testsFinished = false;
	/**
	 * The job which runs this TestSuite.
	 */
	protected Job runner = null;
	/**
	 * The display associated with this run.
	 */
	protected Display display;
	/**
	 * The shell being active when starting this run.
	 */
	protected Shell rootShell;

	/**
	 * Default constructor.
	 * <p/>
	 * The name associated with this class is given the Class name. 
	 */
	public ActiveWorkbenchTestSuite() {
		super(ActiveWorkbenchTestSuite.class.getName());
	}
		
	/**
	 * Constructs with a test class.
	 * <p/>
	 * The name associated with this class is given the Class name. 
	 * @param theClass a test class.
	 */
	public ActiveWorkbenchTestSuite(Class theClass) {
		super(theClass, ActiveWorkbenchTestSuite.class.getName());
	}
	
	/**
	 * Constructs with a name containing no test.
	 * <p/>
	 * @param name the name. This name will be given to the separate thread running.
	 */
	public ActiveWorkbenchTestSuite(String name) {
		super (name);
	}
	
	/**
	 * Constructs with a name and containing the test class given.
	 * <p/>
	 * @param theClass a test class.
	 * @param name the name. This name will be given to the separate thread running.
	 */
	public ActiveWorkbenchTestSuite(Class theClass, String name) {
		super(theClass, name);
	}
	
	public void run(final TestResult result) {
		this.display = Display.getCurrent();
		if (this.display == null) {
			throw new IllegalStateException("The TestSuite must be run from an SWT UI thread");
		}
		this.rootShell = this.display.getActiveShell();
		this.runner = new Job(this.getName()) {
			public IStatus run(IProgressMonitor monitor) {
				try {
					ActiveWorkbenchTestSuite.this.suiteSetUp();
					ActiveWorkbenchTestSuite.super.run(result);
					ActiveWorkbenchTestSuite.this.suiteTearDown();
					return MultiStatus.OK_STATUS;
				}
				finally {
					ActiveWorkbenchTestSuite.this.testsFinished = true;
					display.wake();
				}
			}
			public boolean belongsTo(Object family) {
				return (JOB_FAMILY.equals(family));
			}
		};
		this.runner.setSystem(true);
		this.configureJob(this.runner);
		this.runner.schedule();
		waitUntilFinished();
	}
	
	public void runTest(final Test test, final TestResult result) {
		try {
				// inlined due to limitation in VA/Java 
				//ActiveTestSuite.super.runTest(test, result);
				test.run(result);
			} finally {
				ActiveWorkbenchTestSuite.this.runFinished();
			}
	}

	private void waitUntilFinished() {
		int ctr = 0;
		while (!this.testsFinished) {
			try {
				if(!display.readAndDispatch())
					display.sleep();
			}
			//if an assertion happened inside an #syncExec(Runnable) or #asyncExec(Runnable)
			//this may lead to an SWTException thrown here. Therefore catch and rethrow
			//applicable Exception.
			catch (SWTException ex) {
				if (ex.throwable instanceof AssertionFailedError) {
					throw (AssertionFailedError) ex.throwable;
				}
				else {
					throw ex;
				}
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
					ActiveWorkbenchTestSuite.closeShells(shells);
				}
				//TODO close all open stuff eg. menues etc.
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
	 * Possibility to configure the Job the tests will be run in.
	 * <p/>
	 * Note that this will run in the initiating thread prior to the job being
	 * scheduled.<br/>
	 * One should not schedule the job from here since the tests would then
	 * be run twice.<br/>
	 * The default implementation does nothing.
	 * @param runner the Job running the tests.
	 */
	protected void configureJob(Job runner) {}
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
	 */
	public static void syncExec(Display display, Runnable runnable) {
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
	 * SWT thread in order to guarantee that a test failure is displayed correctly.
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
}