package abbot.tester;

import java.awt.Component;

import javax.swing.JMenuItem;

/** Provide action methods and assertions for {@link JMenuItem}s. */

public class JMenuItemTester extends AbstractButtonTester {

    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        return ((JMenuItem)comp).getText();
    }
}
