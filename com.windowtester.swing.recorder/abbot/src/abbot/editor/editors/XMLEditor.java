package abbot.editor.editors;

import java.awt.Component;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import abbot.script.XMLifiable;

/**
 * An editor for an XMLifiable object. 
 * It'd be nice to provide a real XML editor here...
 */

public class XMLEditor extends AbstractCellEditor 
    implements TableCellEditor, TreeCellEditor { 

    private JTextField textField = new JTextField();
    protected JComponent editorComponent = textField;
    protected EditorDelegate delegate;
    protected int clickCountToStart = 1;

    /**
     * Constructs an XMLEditor that uses a text field.
     */
    public XMLEditor() {
	this.clickCountToStart = 2;
        delegate = new EditorDelegate() {
            /** Set the contents of the editor based on the original
                object. */ 
            public void setValue(Object value) {
                if (value instanceof XMLifiable) {
                    value = ((XMLifiable)value).toEditableString();
                }
		textField.setText((value != null) ? value.toString() : "");
            }

	    public Object getCellEditorValue() {
		return textField.getText();
	    }
        };
	textField.addActionListener(delegate);
    }

    /**
     * Returns the component used to edit this editor's value.
     *
     * @return the editor Component
     */
    public Component getComponent() {
	return editorComponent;
    }

    /**
     * Specifies the number of clicks needed to start editing.
     *
     * @param count  an int specifying the number of clicks needed to start
     *               editing 
     * @see #getClickCountToStart
     */
    public void setClickCountToStart(int count) {
	clickCountToStart = count;
    }

    /**
     *  ClickCountToStart controls the number of clicks required to start
     *  editing.
     */
    public int getClickCountToStart() {
	return clickCountToStart;
    }

    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) { 
	return delegate.isCellEditable(anEvent); 
    }
    
    public boolean shouldSelectCell(EventObject anEvent) { 
	return delegate.shouldSelectCell(anEvent); 
    }

    public boolean stopCellEditing() {
	return delegate.stopCellEditing();
    }

    public void cancelCellEditing() {
	delegate.cancelCellEditing();
    }

    //
    //  Implementing the TreeCellEditor Interface
    //

    public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row) {
	String         stringValue = tree.convertValueToText(value, isSelected,
					    expanded, leaf, row, false);

	delegate.setValue(stringValue);
	return editorComponent;
    }

    //
    //  Implementing the CellEditor Interface
    //

    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {
        delegate.setValue(value);
	return editorComponent;
    }


    //
    //  Protected EditorDelegate class
    //

    protected class EditorDelegate 
        implements ActionListener, ItemListener {

        protected Object value;

        public Object getCellEditorValue() {
            return value;
        }

    	public void setValue(Object value) { 
	    this.value = value; 
	}

        public boolean isCellEditable(EventObject anEvent) {
	    if (anEvent instanceof MouseEvent) { 
		return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
	    }
	    return true;
	}
    	
        public boolean shouldSelectCell(EventObject anEvent) { 
            return true; 
        }

        public boolean startCellEditing(EventObject anEvent) {
	    return true;
	}

        public boolean stopCellEditing() { 
	    fireEditingStopped(); 
	    return true;
	}

       public void cancelCellEditing() { 
	   fireEditingCanceled(); 
       }

        public void actionPerformed(ActionEvent e) {
            XMLEditor.this.stopCellEditing();
	}

        public void itemStateChanged(ItemEvent e) {
	    XMLEditor.this.stopCellEditing();
	}
    }
}
