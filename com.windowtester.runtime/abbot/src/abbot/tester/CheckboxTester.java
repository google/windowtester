package abbot.tester;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.event.ItemEvent;

/** Provides Checkbox activation support, since otherwise AWT buttons cannot be
 * activated in AWT mode.
 */
public class CheckboxTester extends ComponentTester {
    /** Programmatically clicks the Checkbox if in AWT mode. */
    public void click(final Component comp, int x, int y, int mask, int count) {
        if (getEventMode() == EM_AWT) {
            final Checkbox box = (Checkbox)comp;
            invokeLater(new Runnable() {
                public void run() {
                    box.setState(!box.getState());
                    ItemEvent e =
                        new ItemEvent(box, ItemEvent.ITEM_STATE_CHANGED, 
                                      box, box.getState() ? 1 : 0);
                    postEvent(box, e);
                }
            });
        }
        else {
            super.click(comp, x, y, mask, count);
        }
    }
}
