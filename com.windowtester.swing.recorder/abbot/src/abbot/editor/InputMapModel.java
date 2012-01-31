package abbot.editor;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import abbot.i18n.Strings;

public class InputMapModel extends AbstractTableModel {

    public static InputMapModel EMPTY = new InputMapModel() {
        public int getRowCount() { return 1; }
        public Object getValueAt(int row, int col) {
            return col == 0 ? Strings.get("inputmap.unavailable") : "";
        }
    };

    private static final String[] COLUMN_NAMES = {
        Strings.get("inputmap.key"),
        Strings.get("inputmap.value"),
    };

    private InputMap map;

    public InputMapModel() {
        this(new InputMap());
    }

    public InputMapModel(InputMap map) {
        this.map = map;
    }

    public String getColumnName(int col) { return COLUMN_NAMES[col]; }
    public int getRowCount() {
        KeyStroke[] keys = map.allKeys();
        return keys == null ? 0 : keys.length;
    }
    public int getColumnCount() { return 2; }
    public Object getValueAt(int row, int col) {
        KeyStroke key = map.allKeys()[row];
        return col == 0 ? key : map.get(key);
    }
    public boolean isCellEditable(int row, int col) { return false; }
}
