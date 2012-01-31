package abbot.editor.recorder;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import abbot.Log;
import abbot.script.*;
import abbot.script.Action;
import abbot.tester.*;
import abbot.tester.Robot;
import abbot.util.AWT;

/**
 * Record basic semantic events you might find on an JComboBox. <p>
 * <ul>
 * <li>Select an item by value (toString representation)
 * <li>Enter a value (if editable) (not done)
 * </ul>
 */
public class JComboBoxRecorder extends JComponentRecorder {

    private JComboBoxTester tester = new JComboBoxTester();
    private JComboBox combo = null;
    private JList list = null;
    private int index = -1;
    private ActionListener listener = null;

    public JComboBoxRecorder(Resolver resolver) {
        super(resolver);
    }

    /** Make sure we only operate on a JComboBox.  */
    public boolean accept(AWTEvent event) {
        if (isClick(event) && getComboBox(event) == null) {
            return false;
        }
        return super.accept(event);
    }

    protected void init(int recordingType) {
        super.init(recordingType);
        combo = null;
        list = null;
        index = -1;
        listener = null;
    }

    /** Return the JComboBox for the given event, or null if none. */
    private JComboBox getComboBox(AWTEvent event) {
        Component comp = (Component)event.getSource();
        // Depends somewhat on LAF; sometimes the combo box is itself the
        // button, sometimes a panel containing the button.
        if (comp instanceof javax.swing.JButton)
            comp = comp.getParent();
        if (comp instanceof JComboBox)
            return (JComboBox)comp;
        return null;
    }

    protected boolean canMultipleClick() {
        return false;
    }

    /** Parse clicks  to cancel the recording if we get a click that's not in
        the JList (or ESC). */
    protected boolean parseClick(AWTEvent event) {

        if (isFinished()) {
            return false;
        }

        // FIXME add key-based activation/termination?
        boolean consumed = true;
        if (combo == null) {
            combo = getComboBox(event);
            listener = new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    index = combo.getSelectedIndex();
                    if (!combo.isPopupVisible()) {
                        combo.removeActionListener(listener);
                        setFinished(true);
                    }
                }
            };
            combo.addActionListener(listener);
            setStatus("Waiting for selection");
        }
        else if (event.getID() == KeyEvent.KEY_RELEASED
                 && (((KeyEvent)event).getKeyCode() == KeyEvent.VK_SPACE
                     || ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ENTER)) {
            index = combo.getSelectedIndex();
            setFinished(true);
        }
        // Cancel via click somewhere else
        else if (event.getID() == MouseEvent.MOUSE_PRESSED
                 && !AWT.isOnPopup((Component)event.getSource())
                 && combo != getComboBox(event)) {
            setFinished(true);
            consumed = false;
        }
        // Cancel via ESC key
        else if (event.getID() == KeyEvent.KEY_RELEASED
                 && ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ESCAPE) {
            setStatus("Selection canceled");
            setFinished(true);
        }
        else {
            Log.debug("Event ignored");
        }
        if (list == null && combo.isPopupVisible())
            list = tester.findComboList(combo);

        if (isFinished()) {
            combo.removeActionListener(listener);
            listener = null;
        }

        return consumed;
    }

    protected Step createStep() {
        Step step = null;
        if (getRecordingType() == SE_CLICK) {
            step = createSelection(combo, index);
        }
        else {
            step = super.createStep();
        }
        return step;
    }

    protected Step createSelection(JComboBox combo, int index) {
        Step step = null;
        if (combo != null && index != -1) {
            ComponentReference cr = getResolver().addComponent(combo);
            String value = 
                tester.getValueAsString(combo, list,
                                        combo.getItemAt(index), index);
            if (value == null) {
                step = new Action(getResolver(), 
                                  null, "actionSelectIndex",
                                  new String[] {
                                      cr.getID(), String.valueOf(index)
                                  }, javax.swing.JComboBox.class);
            }
            else {
                step = new Action(getResolver(), 
                                  null, "actionSelectItem",
                                  new String[] {
                                      cr.getID(), value
                                  }, javax.swing.JComboBox.class);
            }
        }
        return step;
    }

}

