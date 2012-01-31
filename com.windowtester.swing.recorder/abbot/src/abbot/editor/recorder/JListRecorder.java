package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import javax.swing.JList;

import abbot.script.*;

/**
 * Record basic semantic events you might find on an JList. <p>
 * <ul>
 * <li>Select a cell
 * </ul>
 */
public class JListRecorder extends JComponentRecorder {

    public JListRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Create a click referencing the String value that was clicked. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        JList list = (JList)target;
        ComponentReference cr = getResolver().addComponent(target);
        String methodName = "actionSelectRow";
        ArrayList args = new ArrayList();
        args.add(cr.getID());
        args.add(getLocationArgument(list, x, y));
        if (list.locationToIndex(new Point(x, y)) == -1) {
            methodName = "actionClick";
        }
        if ((mods != 0 && mods != InputEvent.BUTTON1_MASK)
            || count > 1) {
            methodName = "actionClick";
            args.add(abbot.util.AWT.getMouseModifiers(mods));
            if (count > 1) {
                args.add(String.valueOf(count));
            }
        }
        return new Action(getResolver(), null, methodName,
                          (String[])args.toArray(new String[args.size()]),
                          javax.swing.JList.class);
    }
}

