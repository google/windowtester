package abbot.tester;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

import abbot.Log;
import abbot.finder.*;
import abbot.finder.matchers.*;
import abbot.i18n.Strings;

/**
 * Tester for the javax.swing.JFileChooser.
 * Note: many more user actions/assertions could be supported, such as the
 * directory contents, selected file, et al.
 *
 * @author twall@users.sf.net
 */
public class JFileChooserTester extends JComponentTester {

    private JTextComponentTester tester = new JTextComponentTester();
    private ComponentFinder finder = BasicFinder.getDefault();
    private Component find(Container chooser, Matcher m) {
        try {
            return finder.find(chooser, m);
        }
        catch(ComponentSearchException e) {
            return null;
        }
    }

    private JButton findButton(Container chooser, final String text) {
        JButton button = (JButton)find(chooser, new ClassMatcher(JButton.class) {
            public boolean matches(Component c) {
                return super.matches(c)
                    && text.equals(((JButton)c).getText());
            }
        });
        return button;
    }

    public void actionSetFilename(Component c, String filename) {
        JTextField tf = (JTextField)
            find((JFileChooser)c, new ClassMatcher(JTextField.class));
        if (tf == null) {
            String msg = Strings.get("tester.JFileChooser.filename_not_found");
            throw new ActionFailedException(msg);
        }
        tester.actionEnterText(tf, filename);
    }

    public void actionSetDirectory(Component c, String path) {
        JFileChooser chooser = (JFileChooser)c;
        chooser.setCurrentDirectory(new File(path));
        waitForIdle();
    }

    public void actionApprove(Component c) {
        // Could invoke chooser.approveSelection, but that doesn't actually
        // fire the approve button.
        JFileChooser chooser = (JFileChooser)c;
        String text = chooser.getApproveButtonText();
        if (text == null) {
            text = chooser.getUI().getApproveButtonText(chooser);
        }
        JButton approve = findButton(chooser, text);
        if (approve == null) {
            String msg = Strings.get("tester.JFileChooser.approve_not_found");
            throw new ActionFailedException(msg);
        }
        actionClick(approve);
    }

    public void actionCancel(Component c) {
        // We could invoke chooser.cancelSelection, but that wouldn't actually
        // fire the cancel button...
        JFileChooser chooser = (JFileChooser)c;
        // FIXME localize "Cancel"
        JButton cancel = findButton(chooser, "Cancel");
        if (cancel == null) {
            String msg = Strings.get("tester.JFileChooser.cancel_not_found");
            throw new ActionFailedException(msg);
        }
        actionClick(cancel);
    }
}
