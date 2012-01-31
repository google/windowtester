package abbot.editor;

import javax.swing.table.AbstractTableModel;

import abbot.Log;
import abbot.script.*;

/** Formats a list of ComponentReferences for display in a table. */
class ReferencesModel extends AbstractTableModel {
    private Resolver resolver;

    public ReferencesModel(Resolver resolver) {
        this.resolver = resolver;
    }

    public synchronized int getRowCount() { 
        return resolver.getComponentReferences().size();
    }
    public synchronized int getColumnCount() { return 1; }
    /** Returns the entry object at the given row. */
    public Object getValueAt(int row, int column) {
        return resolver.getComponentReferences().toArray()[row];
    }
    public String getColumnName(int col) { return ""; }
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    public Class getColumnClass(int col) {
        if (col == 0)
            return ComponentReference.class;
        return Object.class;
    }
}


