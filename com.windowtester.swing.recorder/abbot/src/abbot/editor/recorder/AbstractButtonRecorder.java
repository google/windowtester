package abbot.editor.recorder;

import java.awt.Component;
import java.awt.event.InputEvent;

import abbot.script.*;

/**
 * Record basic semantic events you might find on an AbstractButton.  This
 * class handles a click on the button.
 */
public class AbstractButtonRecorder extends JComponentRecorder {

    public AbstractButtonRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Usually don't bother tracking drags/drops on buttons. */
    protected boolean canDrag() {
        return false;
    }

    /** Usually aren't interested in multiple clicks on a button. */
    protected boolean canMultipleClick() {
        return false;
    }

    /** Create a button-specific click action. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        // No need to store the coordinates, the center of the button is just
        // fine.   Only care about button 1, though.
        ComponentReference cr = getResolver().addComponent(target);
        if (mods == 0 || mods == InputEvent.BUTTON1_MASK)
            return new Action(getResolver(), 
                              null, "actionClick",
                              new String[] { cr.getID() },
                              javax.swing.AbstractButton.class);
        return null;
    }
}

