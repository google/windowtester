package abbot.tester;

import java.awt.Component;

import javax.swing.JLabel;

/** Provides action methods and assertions for {@link JLabel}s. */

public class JLabelTester extends JComponentTester {

    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        String tag = stripHTML(((JLabel)comp).getText());
        if (tag == null || "".equals(tag)) { //$NON-NLS-1$
            tag = super.deriveTag(comp);
        }
        return tag;
    }
}
