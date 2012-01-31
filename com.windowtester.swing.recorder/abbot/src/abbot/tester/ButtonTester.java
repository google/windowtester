package abbot.tester;

import java.awt.Component;
import java.awt.Button;
import java.awt.event.ActionEvent;

/** Provides Button activation support, since otherwise AWT buttons cannot be
 * activated in AWT mode.
 */
public class ButtonTester extends ComponentTester {
    /** Programmatically clicks the Button if in AWT mode. */
    public void click(Component comp, int x, int y, int mask, int count) {
        if (getEventMode() == EM_AWT) {
            postEvent(comp, new ActionEvent(comp, ActionEvent.ACTION_PERFORMED,
                                            ((Button)comp).getLabel()));
        }
        else {
            super.click(comp, x, y, mask, count);
        }
    }
}
