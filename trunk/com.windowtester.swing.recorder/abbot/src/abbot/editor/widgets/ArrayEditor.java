package abbot.editor.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.net.*;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.editor.widgets.TextField;

/** Provides editing of a variable-length array of strings.  Actions fired
    will have the index of the changed item in the ID (or -1 if the list added
    or removed an element), and the action command be one of
    ACTION_LIST_CHANGED, ACTION_ITEM_CHANGED, ACTION_ROW_INSERTED or
    ACTION_ROW_REMOVED.  
    Allows for a single type of editor, as provided by the createEditor
    method. 
*/
// TODO: use a model (cf TableModel)
public class ArrayEditor extends Box {

    public static final String ACTION_LIST_CHANGED = "list.changed";
    public static final String ACTION_ITEM_CHANGED = "item.changed";
    public static final String ACTION_ITEM_INSERTED = "item.inserted";
    public static final String ACTION_ITEM_DELETED = "item.deleted";

    private static final int DEFAULT_COLUMNS = 10;
    private ArrayList listeners = new ArrayList();
    private ArrayList data;
    private boolean adjusting = false;
    private ArrayList rows = new ArrayList();

    protected interface ElementEditor {
        void setValue(Object value);
        Object getValue();
        Component getEditorComponent();
        void addActionListener(ActionListener listener);
        void removeActionListener(ActionListener listener);
        void setEnabled(boolean enabled);
    }

    /** The default editor for array elements. */
    protected class TextEditor extends TextField implements ElementEditor {
        public TextEditor(Object value) {
            super(value.toString(), DEFAULT_COLUMNS);
        }
        public Object getValue() { return getText(); }
        public void setValue(Object o) {
            setText(o == null ? "" : o.toString());
        }
        public Component getEditorComponent() {
            return this;
        }
    }

    /** Encapsulates one row of the entire array, an editor with add/remove
        buttons.
    */
    protected class Row extends JPanel {
        public ElementEditor elementEditor;
        public Component editor;
        public JButton addButton;
        public JButton removeButton;

        private class SizedButton extends JButton { 
            public SizedButton(String label) {
                super(label);
            }
            /** Ensure all insets are equal. */
            public Insets getInsets() {
                Insets insets = super.getInsets();
                int min = Math.min(insets.top,
                                   Math.min(insets.bottom,
                                            Math.min(insets.right,
                                                     insets.left)));
                insets.right = insets.left = insets.top = insets.bottom = min;
                return insets;
            }
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
            public Dimension getPreferredSize() {
                Dimension size = editor.getPreferredSize();
                size.width = size.height;
                return size;
            }
        }

        public Row(Object value) {
            BorderLayout layout = new BorderLayout();
            layout.setVgap(0);
            setLayout(layout);
            elementEditor = createEditor(value);
            add(editor = elementEditor.getEditorComponent());
            editor.setName("editor");
            Box buttons = new Box(BoxLayout.X_AXIS);
            add(buttons, BorderLayout.EAST);
            buttons.add(removeButton = new SizedButton("-"));
            removeButton.setName("remove");
            removeButton.setToolTipText(Strings.get("editor.array.remove"));
            buttons.add(addButton = new SizedButton("+"));
            addButton.setName("add");
            addButton.setToolTipText(Strings.get("editor.array.insert"));

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    insertRow(getRowCount() == 0
                              ? 0 : getRowIndex() + 1);
                }
            });
            removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeRow(getRowIndex());
                }
            });
            elementEditor.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = getRowIndex();
                    if (!adjusting && index != -1) {
                        adjusting = true;
                        setValueAt(index, elementEditor.getValue(), true);
                        adjusting = false;
                    }
                }
            });
        }

        public Dimension getMaximumSize() {
            Dimension size = super.getMaximumSize();
            size.height = super.getPreferredSize().height;
            return size;
        }

        protected int getRowIndex() {
            Container parent = getParent();
            if (parent != null) {
                Component kids[] = parent.getComponents();
                for (int i=0;i < kids.length;i++) {
                    if (kids[i] == this) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public String toString() {
            return super.toString()
                + "[" + getRowIndex() + ": " + elementEditor.getValue() + "]";
        }
    }

    /** Creates a default, empty editor. */
    public ArrayEditor() {
        this(new String[0]);
    }

    /** Creates an editor with the given contents. */
    public ArrayEditor(Object[] contents) {
        super(BoxLayout.Y_AXIS);
        setValues(contents, false);
    }

    protected ElementEditor createEditor(Object initialValue) {
        return new TextEditor(initialValue);
    }

    private Row getRow(int row) {
        return (Row)rows.get(row);
    }

    private Row addRow(int index, Object value) {
        Row row = new Row(value);
        rows.add(index, row);
        add(row, index);
        return row;
    }

    private void init() {
        removeAll();
        rows.clear();
        for (int i=0;i < data.size();i++) {
            addRow(i, data.get(i));
        }
        // Always keep one editing component visible
        if (data.size() == 0) {
            Row row = addRow(0, "");
            row.removeButton.setEnabled(false);
            row.elementEditor.setEnabled(false);
        }
        validate();
        repaint();
    }

    private void setValues(Object[] contents, boolean fire) {
        if (contents == null) {
            if (data != null) {
                data = null;
                init();
                if (fire)
                    fireActionPerformed(ACTION_LIST_CHANGED, -1);
            }
        }
        else if (data == null || contents.length != data.size()) {
            data = new ArrayList(Arrays.asList(contents));
            init();
            if (fire)
                fireActionPerformed(ACTION_LIST_CHANGED, -1);
        }
        else {
            for (int i=0;i < contents.length;i++) {
                setValueAt(i, contents[i], fire);
            }
        }
    }

    public int getRowCount() {
        return data.size();
    }

    public void insertRow(int row) {
        insertRow(row, "");
    }

    public void insertRow(int row, Object value) {
        if (row < 0 || row > data.size())
            row = data.size();
        data.add(row, value);
        if (data.size() == 1) {
            row = 0;
            Row r = getRow(row);
            r.elementEditor.setEnabled(true);
            r.removeButton.setEnabled(true);
            r.elementEditor.setValue(value);
        }
        else {
            addRow(row, value);
        }
        validate();
        repaint();
        fireActionPerformed(ACTION_ITEM_INSERTED, row);
    }

    public void removeRow(int index) {
        if (index < 0 || index >= getRowCount()) 
            throw new IllegalArgumentException("Row " + index
                                               + " out of bounds ("
                                               + getRowCount() + ")");
        Row r = getRow(index);
        data.remove(index);
        // Always keep one editing component visible
        if (data.size() == 0) {
            // ignore messaging
            adjusting = true;
            r.elementEditor.setValue(null);
            adjusting = false;
            r.elementEditor.setEnabled(false);
            r.removeButton.setEnabled(false);
        }
        else {
            rows.remove(r);
            remove(r);
        }
        validate();
        repaint();
        fireActionPerformed(ACTION_ITEM_DELETED, index);
    }

    public Object[] getValues() {
        return data.toArray(new Object[data.size()]);
    }

    public void setValues(Object[] contents) {
        setValues(contents, true);
    }

    public void setValueAt(int index, Object value) {
        setValueAt(index, value, true);
    }

    private void setValueAt(int index, Object value, boolean fire) {
        if (index < 0 || index >= data.size())
            throw new IndexOutOfBoundsException("Index " + index
                                                + " out of range ("
                                                + data.size() + ")");
        if (value == data.get(index)
            || (value != null && value.equals(data.get(index))))
            return;

        data.set(index, value);
        if (!adjusting)
            getRow(index).elementEditor.setValue(value);
        if (fire) 
            fireActionPerformed(ACTION_ITEM_CHANGED, index);
    }

    public String getValueAt(int index) {
        return (String)data.get(index);
    }

    protected void fireActionPerformed(String action, int index) {
        Iterator iter = listeners.iterator();
        ActionEvent e = new ActionEvent(this, index, action);
        while (iter.hasNext()) {
            ((ActionListener)iter.next()).actionPerformed(e);
        }
    }

    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }
}
