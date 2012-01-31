
package abbot.tester.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type TabFolder.
 */
public class TabFolderTester extends CompositeTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
    /**
     * Proxy for {@link TabFolder.addSelectionListener(SelectionListener listener).
     */
    public void addSelectionListener(final TabFolder t, final SelectionListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link TabFolder#getClientArea()}.
     */
    public Rectangle getClientArea(final TabFolder t) {
        Rectangle result = (Rectangle) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return t.getClientArea();
                    }
                });
        return result;
    }
    
    /**
     * Proxy for {@link TabFolder#getItem(int)}.
     */
    public TabItem getItem(final TabFolder t, final int index) {
        TabItem result = (TabItem) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return t.getItem(index);
                    }
                });
        return result;
    }
    
    /**
     * Proxy for {@link TabFolder#getItemCount()}.
     */
    public int getItemCount(final TabFolder t) {
        Integer result = (Integer) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(t.getItemCount());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link TabFolder#getItems()}.
     */
    public TabItem [] getItems(final TabFolder t) {
        List result = (List) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        TabItem [] items = t.getItems();
                        List list = new ArrayList(items.length);
                        //!pq: fix to actually *add* the items...
                        list.addAll(Arrays.asList(items));
                        return list;
                    }
                });
        TabItem [] items = new TabItem [result.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = (TabItem)result.get(i);
        }
        return items;
    }
    
    /**
     * Proxy for {@link TabFolder#getSelection()}.
     */
    public TabItem [] getSelection(final TabFolder t) {
        TabItem [] result = (TabItem []) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return t.getSelection();
                    }
                });
        return result;
    }
    
    /**
     * Returns the first selected TabItem, or null, if no items are selected.
     * 
     * @param t
     * @return
     */
    public TabItem getSelectionItem(final TabFolder t) {
        TabItem [] array = getSelection(t);
        if (array.length > 0) return array[0];
        return null;
    }
    
    /**
     * Proxy for {@link TabFolder#getSelectionIndex()}.
     */
    public int getSelectionIndex(final TabFolder t) {
        Integer result = (Integer) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(t.getSelectionIndex());
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link TabFolder#indexOf(TabItem)}.
     */
    public int indexOf(final TabFolder t, final TabItem item) {
        Integer result = (Integer) Robot.syncExec(t.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(t.indexOf(item));
                    }
                });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link TabFolder.setSelection(int).
     */
    public void setSelection(final TabFolder t, final int index) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.setSelection(index);
            }
        });
    }
	
    /**
     * Proxy for {@link TabFolder.removeSelectionListener(SelectionListener listener).
     */
    public void removeSelectionListener(final TabFolder t, final SelectionListener listener) {
        Robot.syncExec(t.getDisplay(), null, new Runnable() {
            public void run() {
                t.removeSelectionListener(listener);
            }
        });
    }
}
