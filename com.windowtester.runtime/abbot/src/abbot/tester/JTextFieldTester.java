package abbot.tester;

import java.awt.Component;

import javax.swing.JTextField;

/** Provides actions and assertions {@link JTextField}-based
 * components.
 */ 
public class JTextFieldTester extends JTextComponentTester {

    /**
     * Enter and commit the given text.
     */
    public void actionCommitText(Component c, String text) {
        actionEnterText(c, text);
        actionCommit(c);
    }

    /** Cause the text field's notify action to be triggered. */
    public void actionCommit(Component c) {
        actionActionMap(c, JTextField.notifyAction);
    }
}
