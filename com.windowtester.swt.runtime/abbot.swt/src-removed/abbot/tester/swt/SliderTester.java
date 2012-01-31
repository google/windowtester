
package abbot.tester.swt;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Slider;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Slider.
 */
public class SliderTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
    
    /**
     * Proxy for {@link Slider.addSelectionListener(SelectionListener listener).
     */
    public void addSelectionListener(final Slider s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider#getEnabled()}.
     */
    public boolean getEnabled(final Slider s) {
        Boolean result = (Boolean) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Boolean(s.getEnabled());
            }
        });
        return result.booleanValue();
    }
    
    /**
     * Proxy for {@link Slider#getIncrement()}.
     */
    public int getIncrement(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getIncrement());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Slider#getMaximum()}.
     */
    public int getMaximum(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getMaximum());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Slider#getMinimum()}.
     */
    public int getMinimum(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getMinimum());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Slider#getPageIncrement()}.
     */
    public int getPageIncrement(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getPageIncrement());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Slider#getSelection()}.
     */
    public int getSelection(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getSelection());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link Slider#getThumb()}.
     */
    public int getThumb(final Slider s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getThumb());
            }
        });
        return result.intValue();
    }    
    
    /**
     * Proxy for {@link Slider.removeSelectionListener(SelectionListener listener).
     */
    public void removeSelectionListener(final Slider s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.removeSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setIncrement(int increment).
     */
    public void setIncrement(final Slider s, final int increment) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setIncrement(increment);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setMinimum(int minimum).
     */
    public void setMinimum(final Slider s, final int minimum) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setMinimum(minimum);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setMaximum(int maximum).
     */
    public void setMaximum(final Slider s, final int maximum) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setMaximum(maximum);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setPageIncrement(int increment).
     */
    public void setPageIncrement(final Slider s, final int increment) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setPageIncrement(increment);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setSelection(int selection).
     */
    public void setSelection(final Slider s, final int selection) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setSelection(selection);
            }
        });
    }
    
    /**
     * Proxy for {@link Slider.setThumb(int i).
     */
    public void setThumb(final Slider s, final int i) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.setThumb(i);
            }
        });
    }
	
}
