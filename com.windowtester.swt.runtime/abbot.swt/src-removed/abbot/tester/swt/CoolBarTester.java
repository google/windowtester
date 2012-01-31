
package abbot.tester.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type CoolBar.
 */
public class CoolBarTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
    /**
     * Proxy for {@link CoolBar#getItem(int i)}.
     */
    public CoolItem getItem(final CoolBar c, final int i) {
        CoolItem result = (CoolItem) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getItem(i);
                    }
                });
        return result;
    }
		
    /**
     * Proxy for {@link CoolBar#getItemCount()}.
     */
    public int getItemCount(final CoolBar c) {
        Integer result = (Integer) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(c.getItemCount());
                    }
                });
        return result.intValue();
    }
		
    /**
     * Proxy for {@link CoolBar#getItemOrder()}.
     */
    public int [] getItemOrder(final CoolBar c) {
        List result = (List) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        int [] order = c.getItemOrder();
                        //a little autoboxing would be nice!
                        List list = new ArrayList(order.length);
                        return list;
                    }
                });
        int [] order = new int [result.size()];
        for (int i = 0; i < order.length; i++) {
            order[i] = ((Integer)result.get(i)).intValue();
        }
        return order;
    }
    
    /**
     * Proxy for {@link CoolBar#getItems()}.
     */
    public CoolItem [] getItems(final CoolBar c) {
        List result = (List) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        CoolItem [] items = c.getItems();
                        List list = new ArrayList(items.length);
                        return list;
                    }
                });
        CoolItem [] items = new CoolItem [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (CoolItem)result.get(i);
        }
        return items;
    }
    
    /**
     * Proxy for {@link CoolBar#getItemSizes()}.
     */
    public Point [] getItemSizes(final CoolBar c) {
        List result = (List) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        Point [] items = c.getItemSizes();
                        List list = new ArrayList(items.length);
                        return list;
                    }
                });
        Point [] items = new Point [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (Point)result.get(i);
        }
        return items;
    }
    
    
    /**
     * Proxy for {@link CoolBar#getLocked()}.
     */
    public boolean getLocked(final CoolBar c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Boolean(c.getLocked());
                    }
                });
        return result.booleanValue();
    }
    
    /**
     * Proxy for {@link CoolBar#getWrappedIndices()}.
     */
    public int [] getWrappedIndices(final CoolBar c) {
        List result = (List) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        int [] order = c.getWrapIndices();
                        //a little autoboxing would be nice!
                        List list = new ArrayList(order.length);
                        return list;
                    }
                });
        int [] items = new int [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = ((Integer)result.get(i)).intValue();
        }
        return items;
    }
    
    /**
     * Proxy for {@link CoolBar#indexOf()}.
     */
    public int indexOf(final CoolBar c, final CoolItem item) {
        Integer result = (Integer) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(c.indexOf(item));
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for
     * {@link CoolBar#setLocked(boolean)}.
     */
    public void setLocked(final CoolBar c, final boolean locked) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setLocked(locked);
            }
        });
    }
    
    /**
     * Proxy for
     * {@link CoolBar#setWrapIndices(int [])}.
     */
    public void setWrapIndices(final CoolBar c, final int [] indices) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setWrapIndices(indices);
            }
        });
    }
}
