

package junit.extensions;

import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.tester.swt.Robot;


/**
 * Copied and changed from ForkedPDETestCase.
 * <p/>
 * Provides more proper Exception handling and tries to close everything cleanly
 * when finished.<br/>
 * For running PDE tests I encourage anyway to use ActivePDETestSuite in conjunction	
 * with PDETestFixture instead.
 * 
 * @see junit.extensions.ForkedPDETestCase
 * @author Richard Birenheide (D035816)
 */
/*
 * To be discussed if this one could replace ForkedPDETestCase. 
 */
public class ForkedPDETestCase2 extends TestCase{
	protected InvocationTargetException ite = null;
	protected IllegalAccessException iae = null;
	protected boolean ranTest = false;
	protected Display display;
	protected Shell rootShell;
	
	public ForkedPDETestCase2(){
		super();
	}
	public ForkedPDETestCase2(String name){
		super(name);
	}
	
	protected void runTest() throws Throwable {
		IWorkbench iw = PlatformUI.getWorkbench();
		IWorkbenchWindow iww = iw.getWorkbenchWindows()[0];
		rootShell = iww.getShell();
		display = rootShell.getDisplay();
		
//		final ForkedPDETestCase2 thisTC = this;
//		final Class testClass = getClass();
//		final String name = getName();
		
		ranTest = false;
		
		Thread runTestThread = new Thread(){
			public void run(){
				assertNotNull(getName());
//				Method runMethod= null;
//				try {
//					// use getMethod to get all public inherited
//					// methods. getDeclaredMethods returns all
//					// methods of this class but excludes the
//					// inherited ones.
//					runMethod= testClass.getMethod(name, null);
//				} catch (NoSuchMethodException e) {
//					fail("Method \""+name+"\" not found");
//				}
//				if (!Modifier.isPublic(runMethod.getModifiers())) {
//					fail("Method \""+name+"\" should be public");
//				}

				try {
					//runMethod.invoke(thisTC, new Class[0]);
					ForkedPDETestCase2.super.runTest();
					ranTest = true;
				}
				catch (InvocationTargetException e) {
					e.fillInStackTrace();
					ite = e;
				}
				catch (IllegalAccessException e) {
					e.fillInStackTrace();
					iae = e;
				}
				catch (Throwable t) {
					ite = new InvocationTargetException(t);
				}
				finally {
					Runnable closeChildren = new Runnable() {
						public void run() {
							if (!rootShell.isDisposed()) {
								//Close all blocking dialogs. Necessary, otherwise the junit
								//thread does not proceed.
								Shell[] shells = rootShell.getShells();
								for (int i = 0; i < shells.length; i++) {
									if (!shells[i].isDisposed()) {
										shells[i].close();
									}
								}
								//This is in order to close cleanly any stuff (menues) which puts
								//the UI thread in blocking mode.
								//FIXME This is a crude workaround. Search for open menu items instead
								//and close these.
								Robot robot = new Robot();
								for (int i = 0; i < 25; i++) {
									robot.keyStroke(SWT.ESC);
								}
							}
						}
					};
					display.syncExec(closeChildren);
				}
			}
		};
		runTestThread.setName(getName());
		runTestThread.start();
	
		int ctr = 0;
		
		// run the UI loop until the test has completed or an exception has been thrown
		while(!ranTest && !rootShell.isDisposed()){
			// throw all caught exceptions inside the test thread
			if(ite!=null)
				//Since one wants a failure and not an error, if an assertion failed, extract that.
				if (ite.getCause() instanceof AssertionFailedError) {
					throw ite.getCause();
				}
				else {
					throw ite;
				}
			if(iae!=null)
				throw iae;	
			
			try {
				if(!display.readAndDispatch())
					display.sleep();
			}
			//if an assertion happened inside an #syncExec(Runnable) or #asyncExec(Runnable)
			//this may lead to an SWTException thrown here. Therefore catch and rethrow
			//applicable Exception.
			catch (SWTException ex) {
				if (ex.throwable instanceof AssertionFailedError) {
					throw ex.throwable;
				}
				else {
					throw ex;
				}
			}
			if(ctr++%100000==0)
				System.err.print(".");
			
		}
	
	}
}
