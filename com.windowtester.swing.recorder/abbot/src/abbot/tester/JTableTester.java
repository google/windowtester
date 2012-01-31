package abbot.tester;

import java.awt.*;

import javax.swing.JTable;
import abbot.script.ArgumentParser;

import abbot.script.ArgumentParser;

/** Provide user actions on a JTable.
    The JTable substructure is a "Cell", and JTableLocation provides different
    identifiers for a cell.
    <ul>
    <li>Select a cell by row, column index
    <li>Select a cell by value (its string representation)
    </ul>

    @see abbot.tester.JTableLocation
 */
// TODO: multi-select
public class JTableTester extends JComponentTester {

    /** Convert the value in the list at the given index into a reasonable
        string representation, or null if one can not be obtained.
    */
    public static String valueToString(JTable table, int row, int col) {
        Object value = table.getValueAt(row, col);
        Component cr = table.getCellRenderer(row, col).
            getTableCellRendererComponent(table, value, false, false,
                                          row, col);
        if (cr instanceof javax.swing.JLabel) {
            String label = ((javax.swing.JLabel)cr).getText();
            if (label == null)
                label = "";
            label = label.trim();
            if (!"".equals(label)
                && !ArgumentParser.isDefaultToString(label))
                return label;
        }
        String toString = ArgumentParser.toString(value);
        return toString == ArgumentParser.DEFAULT_TOSTRING
            ? null : toString;
    }

    /** Select the given cell, if not already. */
    public void actionSelectCell(Component c, JTableLocation loc) {
        JTable table = (JTable)c; 
        JTableLocation.Cell cell = loc.getCell(table);
        if (table.isRowSelected(cell.row)
            && table.isColumnSelected(cell.col)
            && table.getSelectedRowCount() == 1) {
            return;
        }
        actionClick(c, loc);
    }

    /** Select the given cell, if not already.
        Equivalent to actionSelectCell(c, new JTableLocation(row, col)).
     */
    public void actionSelectCell(Component c, int row, int col) {
        actionSelectCell(c, new JTableLocation(row, col));
    }

    /** Parse the String representation of a JTableLocation into the actual
        JTableLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new JTableLocation().parse(encoded);
    }

    /** Return (in order of preference) the location corresponding to value,
     * cell, or coordinate.
     */
    public ComponentLocation getLocation(Component c, Point p) {
        JTable table = (JTable)c;
        int row = table.rowAtPoint(p);
        int col = table.columnAtPoint(p);
        if (row != -1 && col != -1) {
            String value = valueToString(table, row, col);
            if (value != null)
                return new JTableLocation(value);
            else
                return new JTableLocation(row, col);
        }
        return new JTableLocation(p);
    }
}
