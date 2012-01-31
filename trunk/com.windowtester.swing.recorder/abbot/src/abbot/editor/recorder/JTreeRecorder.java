package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTree;

import abbot.script.*;
import abbot.tester.*;

/**
 * Record basic semantic events you might find on an JTree. <p>
 * <ul>
 * <li>Click one or more times in a cell
 * </ul>
 */
public class JTreeRecorder extends JComponentRecorder {

    public JTreeRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Normally, a click in a tree results in selection of a given row. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        JTree tree = (JTree)target;
        ComponentReference cr = getResolver().addComponent(target);
        String methodName = "actionSelectRow";
        ArrayList args = new ArrayList();
        args.add(cr.getID());
        args.add(getLocationArgument(target, x, y));
        if (tree.getRowForLocation(x, y) == -1) {
            if (JTreeTester.isLocationInExpandControl(tree, x, y)
                && count == 1)
                methodName = "actionToggleRow";
            else
                methodName = "actionClick";
        }
        if ((mods != 0 && mods != MouseEvent.BUTTON1_MASK)
            || count > 1) {
            methodName = "actionClick";
            args.add(abbot.util.AWT.getMouseModifiers(mods));
            if (count > 1) {
                args.add(String.valueOf(count));
            }
        }
        return new Action(getResolver(), null, methodName,
                          (String[])args.toArray(new String[args.size()]),
                          javax.swing.JTree.class);
    }
}

