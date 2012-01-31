
package abbot.tester.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Group.
 */
public class GroupTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/* Widget properties that are obtainable by member getter functions */
//	protected Rectangle clientArea;
//	protected String text;
	
//	/**
//	 * Sets the above properties to their current values for the given widget. 
//	 * NOTE: This should be called in a block of code synchronized on this
//	 * tester.
//	 */
//	protected synchronized void getProperties(final Group group){
//		super.getProperties(group);
//		Robot.syncExec(group.getDisplay(),this,new Runnable(){
//			public void run(){
//				clientArea = group.getClientArea();
//				text = group.getText();
//			}
//		});			
//	}

//	/**
//	 * These getter methods return a particular property of the given widget.
//	 * @see the corresponding member function in class Widget   
//	 */ 
	/* Begin getters */
	/**
	 * Proxy for {@link Group#getClientArea()}.
	 * <p/>
	 * @param group the group under test.
	 * @return the client area.
	 */
	public Rectangle getClientArea(final Group group){
		Rectangle result = (Rectangle) Robot.syncExec(group.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return group.getClientArea();
			}
		});
		return result;
	}
	
	/**
	 * Proxy for {@link Group#getText()}.
	 * <p/>
	 * @param group the group under test.
	 * @return the text (title)
	 */
	public String getText(final Group group){
		String result = (String) Robot.syncExec(group.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return group.getText();
			}
		});
		return result;
	}
	/* End getters */
}
