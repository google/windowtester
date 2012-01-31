

package junit.extensions;

import org.eclipse.ui.*;
import org.eclipse.swt.widgets.*;
import junit.framework.TestCase;
import java.lang.reflect.*;

/**
 * For use with PDE-Junit in Eclipse.  Runs each test in a separate thread while 
 * running the UI loop.  NOTE: I never got around to completely testing this code, 
 * but I figured it was worth keeping around
 * 
 * @author ktdale
 */
public class ForkedPDETestCase extends TestCase{
	protected InvocationTargetException ite = null;
	protected IllegalAccessException iae = null;
	protected boolean ranTest = false;
	protected Display display;
	protected Shell rootShell;
	
	public ForkedPDETestCase(){
		super();
	}
	public ForkedPDETestCase(String name){
		super(name);
	}
	
	protected void runTest() throws Throwable {
		IWorkbench iw = PlatformUI.getWorkbench();
		IWorkbenchWindow iww = iw.getWorkbenchWindows()[0];
		rootShell = iww.getShell();
		display = rootShell.getDisplay();
		
		final ForkedPDETestCase thisTC = this;
		final Class testClass = getClass();
		final String name = getName();
		
		ranTest = false;
		
		Thread runTestThread = new Thread(){
			public void run(){
				assertNotNull(getName());
				Method runMethod= null;
				try {
					// use getMethod to get all public inherited
					// methods. getDeclaredMethods returns all
					// methods of this class but excludes the
					// inherited ones.
					runMethod= testClass.getMethod(name, (Class[]) null);
				} catch (NoSuchMethodException e) {
					fail("Method \""+name+"\" not found");
				}
				if (!Modifier.isPublic(runMethod.getModifiers())) {
					fail("Method \""+name+"\" should be public");
				}

				try {
					runMethod.invoke(thisTC, new Object[0]);
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
			}

		};
		runTestThread.setName(getName());
		runTestThread.start();
	
		int ctr = 0;
		
		// run the UI loop until the test has completed or an exception has been thrown
		while(!ranTest && !rootShell.isDisposed()){
			// throw all caught exceptions inside the test thread
			if(ite!=null)
				throw ite;
			if(iae!=null)
				throw iae;	
			
			if(!display.readAndDispatch())
				display.sleep();
			if(ctr++%100000==0)
				System.err.print(".");
			
		}
	
	}
}
