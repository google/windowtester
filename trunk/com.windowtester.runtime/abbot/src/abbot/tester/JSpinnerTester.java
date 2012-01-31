package abbot.tester;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.text.*;

import abbot.finder.BasicFinder;
import abbot.finder.matchers.ClassMatcher;
import abbot.i18n.Strings;

/** Provides access to all user actions on a JSpinner. */
public class JSpinnerTester extends JComponentTester {

    /** Increment the JSpinner. */
    public void actionIncrement(Component c) {
        actionKeyStroke(c, KeyEvent.VK_UP);
    }

    /** Decrement the JSpinner. */
    public void actionDecrement(Component c) {
        actionKeyStroke(c, KeyEvent.VK_DOWN);
    }

    /** Set the value of the JSpinner, assuming its editor has a
        JTextComponent under it somewhere.
    */
    public void actionSetValue(Component c, String value) {
        JComponent ed = ((JSpinner)c).getEditor();
        try {
            Component tf = BasicFinder.getDefault().
                find(ed, new ClassMatcher(JTextComponent.class));
            JTextComponentTester t = new JTextComponentTester();
            t.actionEnterText(tf, value);
            t.actionKeyStroke(tf, KeyEvent.VK_ENTER);
        }
        catch(Exception e) {
            String msg = Strings.get("tester.JSpinner.unknown_editor",
                                     new String[] { ed.toString() });
            throw new ActionFailedException(msg);
        }
    }
}
