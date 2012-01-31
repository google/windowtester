package abbot.editor.widgets;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import abbot.Log;

/** A better text field with some useful features.
    <ul>
    <li>Fires when focus leaves the component.
    <li>Selects all the contents when the action is fired to indicate the
    contents were accepted.
    <li>Until the user causes a notify-field-accept (usually with the 
    Enter key), the contents may be reverted to the original value by invoking 
    the field-revert action (bound to ESC by default).
    <li>Actions are fired on all edits.
    <li>The field has a fixed height.
    <li>Pressing Enter when the field is blank will insert a default value, if
    one has been provided.
    </ul>
    <p>
    This functionality may be applied to any JTextField with the
    {@link #decorate(JTextField)} method. 
*/
public class TextField extends JTextField {

    /** Action command when the field loses focus. */
    public static final String ACTION_FOCUS_LOST = "focus-lost";
    /** Action command when the text changes. */
    public static final String ACTION_TEXT_CHANGED = "text-changed";
    /** Action command when text is inserted. */
    public static final String ACTION_TEXT_INSERTED = "text-inserted";
    /** Action command when text is removed. */
    public static final String ACTION_TEXT_REMOVED = "text-removed";
    /** Action command when the field reverts to its original value.  The
     * action is equivalent to typing the original text and hitting "enter".
     */
    public static final String ACTION_TEXT_REVERTED = "text-reverted";

    private static final String REVERT_ACTION_NAME = "field-revert";

    public static boolean isDocumentAction(String action) {
        return action == ACTION_TEXT_CHANGED
            || action == ACTION_TEXT_INSERTED
            || action == ACTION_TEXT_REMOVED;
    }

    public static void decorate(JTextField tf) {
        new Decorator(tf);
    }

    public static void decorate(JTextField tf, String defaultValue) {
        new Decorator(tf, defaultValue);
    }

    /** Avoid recursive changes to the field's text. */
    private boolean notifying;
    private Decorator decorator;

    public TextField(String value, int columns) {
        super(value, columns);
        decorator = new Decorator(this);
    }

    public TextField(String value, String defaultValue, int columns) {
        super(value, columns);
        decorator = new Decorator(this, defaultValue);
    }

    public TextField(String value) {
        super(value);
        decorator = new Decorator(this);
    }

    public TextField(String value, String defaultValue) {
        super(value);
        decorator = new Decorator(this, defaultValue);
    }

    /** Don't allow text field to resize height. */
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.height = super.getPreferredSize().height;
        return size;
    }

    /** Don't allow text field to resize height. */
    public Dimension getMinimumSize() {
        Dimension size = super.getMinimumSize();
        size.height = super.getPreferredSize().height;
        return size;
    }

    /** The default value will be inserted when the field is blank and ENTER
        is pressed.  This behavior is disabled if the value is null.
    */
    public void setDefaultValue(String value) {
        decorator.setDefaultValue(value);
    }

    public void setText(String text) {
        if (!getText().equals(text) && !notifying)
            super.setText(text != null ? text : "");
    }

    protected void fireActionPerformed() {
        notifying = true;
        try {
            super.fireActionPerformed();
        }
        finally {
            notifying = false;
        }
    }

    public static class Decorator {
        private JTextField textField;
        /** Text used when field is reverted.  Updated on any
            notify-field-accept action or when setText() is invoked directly.
        */
        private String revertText;
        // whether to notify action listeners on every text change
        private boolean continuousFire = true;
        // Value to place in field when it is empty and Enter is hit
        private String defaultValue;

        public Decorator(JTextField textField) {
            this(textField, null);
        }

        public Decorator(final JTextField textField, String defValue) {
            this.textField = textField;
            this.defaultValue = defValue;
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent ev) {
                    if (!ev.isTemporary() && !isLocalMenuActive(textField)) {
                        fireActionPerformed(ACTION_FOCUS_LOST);
                    }
                }
            });
            DocumentListener listener = new DocumentListener() {
                public void changedUpdate(DocumentEvent ev) {
                    if (continuousFire) {
                        fireActionPerformed(ACTION_TEXT_CHANGED);
                    }
                }
                public void insertUpdate(DocumentEvent ev) {
                    // If setText is called, update the revert text
                    String stack = Log.getStack(Log.FULL_STACK);
                    if (stack.indexOf("JTextComponent.setText") != -1) {
                        revertText = textField.getText();
                    }
                    if (continuousFire) {
                        fireActionPerformed(ACTION_TEXT_INSERTED);
                    }
                }
                public void removeUpdate(DocumentEvent ev) {
                    if (continuousFire) {
                        fireActionPerformed(ACTION_TEXT_REMOVED);
                    }
                }
            };
            textField.getDocument().addDocumentListener(listener);
            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Select all text when there is an effective commit,
                    // and make note of the new committed text.  
                    // If the field is blank, set the default value if there
                    // is one. 
                    String text = textField.getText();
                    if (!isDocumentAction(e.getActionCommand())) {
                        if (defaultValue != null && "".equals(text)) {
                            text = defaultValue;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    textField.setText(defaultValue);
                                    textField.selectAll();
                                }
                            });
                        }
                        revertText = text;
                        textField.selectAll();
                    }
                }
            });

            // Changing the input map doesn't work on the JComboBox editor,
            // so use a key listener instead.
            textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        revertText();
                    }
                }
            });

            /*
            // This would appear to be a better method for handling revert,
            // but the following code doesn't work, and I can't figure out why
            ActionMap am = textField.getActionMap();
            am.put(REVERT_ACTION_NAME, new RevertFieldAction());
            
            InputMap im = textField.getInputMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                   REVERT_ACTION_NAME);
            */
            // Initialize
            revertText = textField.getText();
        }

        private void setDefaultValue(String value) {
            this.defaultValue = value;
        }

        private void revertText() {
            if (!textField.getText().equals(revertText)) {
                textField.setText(revertText);
                fireActionPerformed(ACTION_TEXT_REVERTED);
            }
        }

        private void fireActionPerformed(String command) {
            textField.setActionCommand(command);
            textField.postActionEvent();
            textField.setActionCommand(null);
        }

        /** Detect temporary focus loss due to menu activation (pre-1.4). */
        private boolean isLocalMenuActive(JTextField field) {
            Window window = SwingUtilities.getWindowAncestor(field);
            if (window != null) {
                Component comp = window.getFocusOwner();
                return comp != null && (comp instanceof JMenuItem);
            }
            return false;
        }

        protected class RevertFieldAction extends AbstractAction {
            public RevertFieldAction() {
                super(REVERT_ACTION_NAME);
            }
            public void actionPerformed(ActionEvent e) {
                revertText();
            }
        }
    }
}
