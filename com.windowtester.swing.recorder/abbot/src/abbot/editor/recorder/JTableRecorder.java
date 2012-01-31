package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTable;

import abbot.script.*;

/**
 * Record basic semantic events you might find on an JTable. <p>
 * <ul>
 * <li>Click one or more times in a cell
 * </ul>
 */
public class JTableRecorder extends JComponentRecorder {

    public JTableRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Normally, a click in a table results in selection of a given cell. */
    protected Step createClick(Component target, int x, int y,
                               int mods, int count) {
        JTable table = (JTable)target;
        Point where = new Point(x, y);
        int row = table.rowAtPoint(where);
        int col = table.columnAtPoint(where);
        ComponentReference cr = getResolver().addComponent(target);
        String methodName = "actionSelectCell";
        ArrayList args = new ArrayList();
        args.add(cr.getID());
        args.add(getLocationArgument(table, x, y));
        if (row == -1 || col == -1) {
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
                          javax.swing.JTable.class);
    }
}

