package abbot.editor;

import javax.swing.ActionMap;
import javax.swing.table.AbstractTableModel;
import abbot.i18n.Strings;

public class ActionMapModel extends AbstractTableModel {

    public static ActionMapModel EMPTY = new ActionMapModel() {
        public int getRowCount() { return 1; }
        public Object getValueAt(int row, int col) {
            return col == 0 ? Strings.get("actionmap.unavailable") : "";
        }
    };

    private static final String[] COLUMN_NAMES = {
        Strings.get("actionmap.key"),
        Strings.get("actionmap.value"),
    };

    private ActionMap map;

    public ActionMapModel() {
        this(new ActionMap());
    }

    public ActionMapModel(ActionMap map) {
        this.map = map;
    }

    public String getColumnName(int col) { return COLUMN_NAMES[col]; }
    public int getRowCount() {
        Object[] keys = map.allKeys();
        return keys == null ? 0 : keys.length;
    }
    public int getColumnCount() { return 2; }
    public Object getValueAt(int row, int col) {
        Object key = map.allKeys()[row];
        return col == 0 ? key : map.get(key);
    }
    public boolean isCellEditable(int row, int col) { return false; }
}
