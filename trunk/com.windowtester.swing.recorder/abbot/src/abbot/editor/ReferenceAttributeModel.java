package abbot.editor;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import abbot.i18n.Strings;
import abbot.script.*;

/** Provides a table model for ComponentReference attributes.  */
public class ReferenceAttributeModel extends AbstractTableModel {
    private ComponentReference reference;

    private static String[] COLUMN_NAMES = {
        Strings.get("attribute.name"),
        Strings.get("attribute.value")
    };

    public void setReference(ComponentReference ref) {
        reference = ref;
        fireTableDataChanged();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }
    public int getRowCount() {
        return reference == null
            ? 0 : reference.getAttributes().values().size();
    }
    public int getColumnCount() {
        return 2;
    }
    public Object getValueAt(int row, int col) {
        Map.Entry entry = (Map.Entry)
            reference.getAttributes().entrySet().toArray()[row];
        return col == 0 ? entry.getKey() : entry.getValue();
    }
    public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            String key = (String)getValueAt(row, 0);
            reference.setAttribute(key, (String)value);
            fireTableCellUpdated(row, col);
        }
    }
    public boolean isCellEditable(int row, int col) {
        return col == 1;
    }
}
