package abbot.editor.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import abbot.Log;

/** A better text area that fires when focus leaves the component, and
    also selects all the contents when the action is fired to indicate the
    contents were accepted. */
// FIXME extend to have a "commit" on enter or focus change, where ESC will
// revert 
public class TextArea extends JTextArea {

    public static final String ACTION_FOCUS_LOST = "focus-lost";
    public static final String ACTION_TEXT_CHANGED = "text-changed";
    public static final String ACTION_TEXT_INSERTED = "text-changed";
    public static final String ACTION_TEXT_REMOVED = "text-changed";

    private boolean continuousFire = true;
    private boolean fieldChanging = false;
    private ArrayList listeners = new ArrayList();
    public TextArea(String value) {
        super(value);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent ev) {
                if (!ev.isTemporary() && !isLocalMenuActive()) {
                    Log.debug("Firing on focus loss");
                    fireActionPerformed(ACTION_FOCUS_LOST);
                }
            }
        });
        getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent ev) {
                if (!fieldChanging && continuousFire)
                    fireActionPerformed(ACTION_TEXT_CHANGED);
            }
            public void insertUpdate(DocumentEvent ev) {
                if (!fieldChanging && continuousFire)
                    fireActionPerformed(ACTION_TEXT_INSERTED);
            }
            public void removeUpdate(DocumentEvent ev) {
                if (!fieldChanging && continuousFire)
                    fireActionPerformed(ACTION_TEXT_REMOVED);
            }
        });
    }

    /** Don't fire events when text is set directly (to conform to regular
        JTextArea behavior). */
    public void setText(String text) {
        fieldChanging = true;
        super.setText(text);
        fieldChanging = false;
    }

    /** Detect temporary focus loss due to menu activation. */
    private boolean isLocalMenuActive() {
        boolean active = false;
        Window window = SwingUtilities.getWindowAncestor(TextArea.this);
        while (window != null && !active) {
            window = SwingUtilities.getWindowAncestor(window);
            if (window instanceof JFrame) {
                Component comp = window.getFocusOwner();
                Log.debug("Focus is in " + abbot.tester.Robot.toString(comp));
                active = comp != null && (comp instanceof JMenuItem);
            }
        }
        return active;
    }

    protected void fireActionPerformed(String actionCommand) {
        fireActionPerformed(actionCommand, false);
    }

    protected void fireActionPerformed() {
        fireActionPerformed(getText(), true);
    }

    /** On normal fire (enter) select all text. */
    protected void fireActionPerformed(String cmd, boolean select) {
        if (select)
            selectAll();
        ActionEvent e =
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cmd);
        Iterator iter = listeners.iterator();
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
