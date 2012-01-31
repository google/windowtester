package abbot.tester.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Composite.
 */
/* formerly extended ControlTester: thanks Markus Kuhn <markuskuhn@users.sourceforge.net> */
public class CompositeTester extends ScrollableTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Convenience factory method
	 */
	public static CompositeTester getCompositeTester(Composite c) {
		return (CompositeTester)(getTester(c));
	}

	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */	
	
	/**
	 * Proxy for {@link Composite#getChildren()}.
	 * <p/>
	 * @param c the control under test.
	 * @return the children of the composite.
	 */
	public Control[] getChildren(final Composite c) {
		Control[] result = (Control[]) Robot.syncExec(c.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return c.getChildren();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Composite#getLayout()}.
	 * <p/>
	 * @param c the control under test.
	 * @return the layout of the composite.
	 */
	public Layout getLayout(final Composite c) {
		Layout result = (Layout) Robot.syncExec(c.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return c.getLayout();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Composite#getTabList()}.
	 * <p/>
	 * @param c the control under test.
	 * @return the tab list of the composite.
	 */
	public Control[] getTabList(final Composite c) {
		Control[] result = (Control[]) Robot.syncExec(c.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return c.getTabList();
			}
		});
		return result;
	}

	/* End getters */
    
    /**
     * Proxy for {@link Composite.setFocus()}
     */
    public void setFocus(final Composite c) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setFocus();
            }
        });
    }
}
