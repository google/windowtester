
package abbot.tester.swt;

import org.eclipse.swt.widgets.ProgressBar;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type ProgressBar.
 */
public class ProgressBarTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
    
    /**
     * Proxy for {@link ProgressBar#getMaximum()}.
     */
    public int getMaximum(final ProgressBar p) {
        Integer result = (Integer) Robot.syncExec(p.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(p.getMaximum());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link ProgressBar#getMinimum()}.
     */
    public int getMinimum(final ProgressBar p) {
        Integer result = (Integer) Robot.syncExec(p.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(p.getMinimum());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link ProgressBar#getSelection()}.
     */
    public int getSelection(final ProgressBar p) {
        Integer result = (Integer) Robot.syncExec(p.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(p.getSelection());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link ProgressBar.setMinimum(int minimum).
     */
    public void setMinimum(final ProgressBar p, final int minimum) {
        Robot.syncExec(p.getDisplay(), null, new Runnable() {
            public void run() {
                p.setMinimum(minimum);
            }
        });
    }
    
    /**
     * Proxy for {@link ProgressBar.setMaximum(int maximum).
     */
    public void setMaximum(final ProgressBar p, final int maximum) {
        Robot.syncExec(p.getDisplay(), null, new Runnable() {
            public void run() {
                p.setMaximum(maximum);
            }
        });
    }
    
    /**
     * Proxy for {@link ProgressBar.setSelection(int selection).
     */
    public void setSelection(final ProgressBar p, final int selection) {
        Robot.syncExec(p.getDisplay(), null, new Runnable() {
            public void run() {
                p.setSelection(selection);
            }
        });
    }
}
