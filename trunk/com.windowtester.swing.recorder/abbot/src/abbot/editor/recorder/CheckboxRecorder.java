package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.*;

import abbot.Log;
import abbot.script.*;

/**
 * Record basic click a Checkbox component. <p>
 */
public class CheckboxRecorder extends ComponentRecorder {

    public CheckboxRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Don't need to store any position or modifier information. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        ComponentReference cr = getResolver().addComponent(target);
        return new Action(getResolver(), null, "actionClick",
                          new String[] { cr.getID() });
    }
}

