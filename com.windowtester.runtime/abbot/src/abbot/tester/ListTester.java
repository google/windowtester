package abbot.tester;

import java.awt.*;
import java.awt.event.*;
import abbot.util.*;
import abbot.i18n.Strings;

/** Provides actions for <code>java.awt.List</code>. */
// TODO: double-click (actionPerformed)
// TODO: multi-select
public class ListTester extends ComponentTester {

    private int LIST_DELAY =
        Properties.getProperty("abbot.tester.list_delay", 30000, 0, 60000);

    private class Listener implements AWTEventListener {
        public volatile boolean selected;
        private int targetIndex = -1;
        public Listener(int index, boolean state) {
            targetIndex = index;
            selected = !state;
        }
        public void eventDispatched(AWTEvent e) {
            if (e.getID() == ItemEvent.ITEM_STATE_CHANGED
                && e.getSource() instanceof List) {
                if (((List)e.getSource()).
                    getSelectedIndex() == targetIndex) {
                    selected = ((ItemEvent)e).
                        getStateChange() == ItemEvent.SELECTED;
                }
            }
        }
    }

    /** @deprecated Use actionSelectRow instead. */
    public void actionSelectIndex(Component c, int index) {
        actionSelectRow(c, new ListLocation(index));
    }

    /** Select the row corresponding to the given ListLocation. */
    public void actionSelectRow(Component c, ListLocation location) {
        List list = (List)c;
        try {
            int index = location.getIndex(list);
            if (index < 0 || index >= list.getItemCount()) {
                String msg = Strings.get("tester.JList.invalid_index",
                                         new Object[] { new Integer(index) });
                throw new ActionFailedException(msg);
            }
            if (list.getSelectedIndex() != index) {
                setSelected(list, index, true);
            }
        }
        catch(LocationUnavailableException e) {
            actionClick(c, location);
        }
    }

    protected void setSelected(List list, int index, boolean selected) {
        Listener listener = new Listener(index, selected);
        new WeakAWTEventListener(listener, ItemEvent.ITEM_EVENT_MASK);
        list.select(index);
        ItemEvent ie = new ItemEvent(list, ItemEvent.ITEM_STATE_CHANGED,
                                     list.getSelectedItem(),
                                     selected ? ItemEvent.SELECTED
                                     : ItemEvent.DESELECTED);
        postEvent(list, ie);
        long now = System.currentTimeMillis();
        while (listener.selected != selected) {
            if (System.currentTimeMillis() - now > LIST_DELAY)
                throw new ActionFailedException("List didn't fire for "
                                                    + "index " + index
                                                    + " selection");
            sleep();
        }
        waitForIdle();
    }

    /** Parse the String representation of a ListLocation into the actual
        ListLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new ListLocation().parse(encoded);
    }

    /** Return the value, row, or coordinate location. */
    public ComponentLocation getLocation(Component c, Point p) {
        throw new RuntimeException("List locations must be manually generated");
    }
}
