package abbot.tester.swt.eclipse;

import org.eclipse.swt.widgets.Display;

/** An abstract class to facilitate the testing of dialogs in eclipse.
 * 
 * 	Currently, this class only supports dialogs which have a title.
 *   
 *  protected abstract void invokeDialog() throws Throwable;
 *  protected abstract void doTestDialog() throws Throwable;
 *  protected abstract void doCloseDialog( boolean ok ) throws Throwable;
 *  
 *  Nesting of AbstractDialogTesters IS supported. All dialogs should
 *  be launched from their respective invokeDialog methods.
 *  e.g. 
 *  
 *  AbstractDialogTester one = AbstractDialogTester(firstTitle,_display) {
 *  	invokeDialog() throws Throwable {
 *  		//invoke first dialog
 *  	}
 *  
 *  	doTestDialog() throws Throwable {
 *  		AbstractDialogTester two = new AbstractDialogTester(secondTitle,_display) {
 *  			protected void invokeDialog) throws Throwable {
 *  				//invoke second dialog
 *  			}
 *  			protected void doTestDialog() throws Throwable { ... }
 *  			protected void doCloseDialog(boolean ok) throws Throwable { //close second dialog }
 *  		};
 *  		//maybe run some tests on first dialog before launching second.
 *			two.runDialog();
 *			//maybe run some tests on first dialog after closing second.
 *  	}
 *  
 *  	doCloseDialog(boolean ok) throws Throwable { //close first dialog }
 *  }
 *  one.runDialog(); //run all of the above code.
 *  
 *  
 */
public abstract class AbstractDialogTester extends abbot.tester.swt.AbstractDialogTester
{
    public AbstractDialogTester(String title, Display display)
    {
        super(title, display);
    }
    
    public AbstractDialogTester(String title, Display display, int timeoutMinutes)
    {
        super(title, display,timeoutMinutes);
    }
    
}
