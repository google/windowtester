package abbot.tester;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.StringTokenizer;

import javax.swing.JTable;

import abbot.i18n.Strings;

import com.windowtester.runtime.util.StringComparator;

/** Provides encapsulation of a location on a JTable (notably a row).
    Use the JTableLocation#JTableLocation(Point) ctor to indicate a specific
    coordinate. 
*/

public class JTableLocation extends ComponentLocation {
    public static class Cell {
        public int row;
        public int col;
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
        public boolean equals(Object o) {
            return (o instanceof Cell)
                && ((Cell)o).row == row && ((Cell)o).col == col;
        }
        public String toString() {
            return "[" + row + "," + col + "]";
        }
    }

    protected String value = null;
    protected Cell cell = null;

    public JTableLocation() {
    }

    public JTableLocation(String value) {
        this.value = value;
    }

    public JTableLocation(int row, int col) {
        if (row < 0 || col < 0) {
            String msg = Strings.get("tester.JTable.invalid_cell", 
                                     new Object[] { new Integer(row),
                                                    new Integer(col) });
            throw new LocationUnavailableException(msg);
        }
        cell = new Cell(row, col);
    }

    public JTableLocation(Point p) {
        super(p);
    }

    protected String badFormat(String encoded) {
        return Strings.get("location.table.bad_format",
                           new Object[] { encoded });
    }

    /** Convert the given row, col into a coordinate pair. */
    protected Point cellToPoint(JTable table, int row, int col) {
        if (row < 0 || row >= table.getRowCount()
            || col < 0 || col >= table.getColumnCount()) {
            String msg = Strings.get("tester.JTable.invalid_cell", 
                                     new Object[] { new Integer(row),
                                                    new Integer(col) });
            throw new LocationUnavailableException(msg);
        }
        Rectangle rect = getCellBounds(table, row, col);
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }
    
    protected Rectangle getCellBounds(JTable table, int row, int col) {
        return table.getCellRect(row, col, false);
    }

    /** Return the row, col of the first object matching the given String. */
    private Cell valueToCell(JTable table, String value) {
        for (int row=0;row < table.getRowCount();row++) {
            for (int col=0;col < table.getColumnCount();col++) {
                String str = JTableTester.valueToString(table, row, col);
                if (StringComparator.matches(str, value)) {
                    return new JTableLocation.Cell(row, col);
                }
            }
        }
        String msg = Strings.get("tester.JTable.invalid_value",
                                 new Object[] { value });
        throw new LocationUnavailableException(msg);
    }

    public Point getPoint(Component c) {
        JTable table = (JTable)c;
        if (value != null) {
            Cell tmp = valueToCell(table, value);
            return cellToPoint(table, tmp.row, tmp.col);
        }
        if (cell != null) {
            return cellToPoint(table, cell.row, cell.col);
        }
        return super.getPoint(table);
    }

    public Cell getCell(JTable table) {
        if (value != null)
            return valueToCell(table, value);
        if (cell != null) 
            return new Cell(cell.row, cell.col);
        Point where = super.getPoint(table);
        return new Cell(table.rowAtPoint(where), table.columnAtPoint(where));
    }

    public Rectangle getBounds(Component c) {
        JTable table = (JTable)c;
        Cell cell = getCell(table);
        if (cell == null) {
            String msg = Strings.get("tester.JTable.invalid_cell",
                                     new Object[] {
                                         new Integer(cell.row),
                                         new Integer(cell.col),
                                     });
            throw new LocationUnavailableException(msg);
        }
        return getCellBounds(table, cell.row, cell.col);
    }

    public boolean equals(Object o) {
        if (o instanceof JTableLocation) {
            JTableLocation loc = (JTableLocation)o;
            if (value != null)
                return value.equals(loc.value);
            if (cell != null)
                return cell.equals(loc.cell);
        }
        return super.equals(o);
    }

    public String toString() {
        if (value != null)
            return encodeValue(value);
        if (cell != null)
            return cell.toString();
        return super.toString();
    }

    public ComponentLocation parse(String encoded) {
        encoded = encoded.trim();
        if (isValue(encoded)) {
            value = parseValue(encoded);
            return this;
        }
        else if (isIndex(encoded)) {
            String num = encoded.substring(1, encoded.length()-1).trim();
            StringTokenizer st = new StringTokenizer(num, ",");
            try {
                int r = Integer.parseInt(st.nextToken().trim());
                int c = Integer.parseInt(st.nextToken().trim());
                cell = new Cell(r, c);
                return this;
            }
            catch(NumberFormatException e) {
                throw new IllegalArgumentException(badFormat(encoded));
            }
        }
        return super.parse(encoded);
    }
}
