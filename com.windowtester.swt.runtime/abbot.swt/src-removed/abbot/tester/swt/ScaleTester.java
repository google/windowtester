
package abbot.tester.swt;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Scale;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Scale.
 */
public class ScaleTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
    
    /**
     * Proxy for {@link Scale.addSelectionListener(SelectionListener listener).
     */
    public void addSelectionListener(final Scale s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.addSelectionListener(listener);
            }
        });
    }
	
    /**
     * Proxy for {@link Scale#getIncrement()}.
     */
    public int getIncrement(final Scale s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getIncrement());
            }
        });
        return result.intValue();
    }
	
    /**
     * Proxy for {@link Scale#getMaximum()}.
     */
    public int getMaximum(final Scale s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getMaximum());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Scale#getMinimum()}.
     */
    public int getMinimum(final Scale s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getMinimum());
            }
        });
        return result.intValue();
    }
	
    /**
     * Proxy for {@link Scale#getPageIncrement()}.
     */
    public int getPageIncrement(final Scale s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getPageIncrement());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Scale#getSelection()}.
     */
    public int getSelection(final Scale s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getSelection());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Scale.removeSelectionListener(SelectionListener listener).
     */
    public void removeSelectionListener(final Scale s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.removeSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link Scale.setIncrement(int increment).
     */
    public void setIncrement(final Scale s, final int increment) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setIncrement(increment);
            }
        });
    }
    
    /**
     * Proxy for {@link Scale.setMinimum(int minimum).
     */
    public void setMinimum(final Scale s, final int minimum) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setMinimum(minimum);
            }
        });
    }
    
    /**
     * Proxy for {@link Scale.setMaximum(int maximum).
     */
    public void setMaximum(final Scale s, final int maximum) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setMaximum(maximum);
            }
        });
    }
    
    /**
     * Proxy for {@link Scale.setPageIncrement(int increment).
     */
    public void setPageIncrement(final Scale s, final int increment) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setPageIncrement(increment);
            }
        });
    }
    
    /**
     * Proxy for {@link Scale.setSelection(int selection).
     */
    public void setSelection(final Scale s, final int selection) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setSelection(selection);
            }
        });
    }
}
