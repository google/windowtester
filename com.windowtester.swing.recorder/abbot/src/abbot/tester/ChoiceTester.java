package abbot.tester;

import java.awt.*;
import java.awt.event.*;

import com.windowtester.runtime.util.StringComparator;

import abbot.Platform;
import abbot.util.*;

/** AWT Choice (ComboBox/picklist) support. */
public class ChoiceTester extends ComponentTester {

    private int CHOICE_DELAY =
        Properties.getProperty("abbot.tester.choice_delay", 30000, 0, 60000);

    private class Listener implements AWTEventListener {
        public volatile boolean gotChange;
        private int targetIndex = -1;
        public Listener(int index) {
            targetIndex = index;
        }
        public void eventDispatched(AWTEvent e) {
            if (e.getID() == ItemEvent.ITEM_STATE_CHANGED
                && e.getSource() instanceof Choice) {
                gotChange = ((Choice)e.getSource()).
                    getSelectedIndex() == targetIndex;
            }
        }
    }

    /** Select an item by index. */
    public void actionSelectIndex(Component c, final int index) {
        final Choice choice = (Choice)c;
        int current = choice.getSelectedIndex();
        if (current == index)
            return;

        // Don't add an item listener, because then we're at the mercy of any
        // other ItemListener finishing.  Don't bother clicking or otherwise
        // sending events, since the behavior is platform-specific.
        Listener listener = new Listener(index);
        new WeakAWTEventListener(listener, ItemEvent.ITEM_EVENT_MASK);

        choice.select(index);
        ItemEvent ie = new ItemEvent(choice, ItemEvent.ITEM_STATE_CHANGED,
                                     choice.getSelectedObjects()[0],
                                     ItemEvent.SELECTED);
        postEvent(choice, ie);

        long now = System.currentTimeMillis();
        while (!listener.gotChange) {
            if (System.currentTimeMillis() - now > CHOICE_DELAY)
                throw new ActionFailedException("Choice didn't fire for "
                                                + "index " + index);
            sleep();
        }
        waitForIdle();
    }

    /** Select an item by its String representation. */
    public void actionSelectItem(Component c, String item) {
        Choice choice = (Choice)c;
        for (int i=0;i < choice.getItemCount();i++) {
            if (StringComparator.matches(item, choice.getItem(i))) {
                try {
                    actionSelectIndex(c, i);
                    return;
                }
                catch(ActionFailedException e) {
                    throw new ActionFailedException("Choice didn't fire for "
                                                    + "item '" + item + "'");
                }
            }
        }
        throw new ActionFailedException("Item '" + item
                                        + "' not found in Choice");
    }
}
