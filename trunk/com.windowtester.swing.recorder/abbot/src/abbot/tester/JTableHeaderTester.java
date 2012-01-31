package abbot.tester;

import java.awt.Component;
import java.awt.Point;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/** Provide table header location support, mostly. */

public class JTableHeaderTester extends JComponentTester {
    
    /** Parse the String representation of a JTableHeaderLocation into the
        actual JTableHeaderLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new JTableHeaderLocation().parse(encoded);
    }

    /** Return (in order of preference) the location corresponding to column
        name (value), column index, or coordinate.
     */
    public ComponentLocation getLocation(Component c, Point p) {
        JTableHeader header = (JTableHeader)c;
        int col = header.columnAtPoint(p);
        if (col != -1) {
            String value = header.getTable().getModel().getColumnName(col);
            if (value != null)
                return new JTableHeaderLocation(value);
            else
                return new JTableHeaderLocation(col);
        }
        return new JTableHeaderLocation(p);
    }
}
