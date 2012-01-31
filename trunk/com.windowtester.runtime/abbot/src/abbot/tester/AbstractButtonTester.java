package abbot.tester;

import java.awt.Component;

import javax.swing.*;


public class AbstractButtonTester extends JComponentTester {

    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        AbstractButton absButton = ((AbstractButton)comp);
        String tag = stripHTML(absButton.getText());
        if ("".equals(tag) || tag == null) {
            tag = super.deriveTag(comp);  
        }
        return tag;
    }

    /** AbstractButton click action. */
    public void actionClick(final Component c) {
        /*
        if (getEventMode() == EM_PROG) {
            invokeAndWait(new Runnable() {
                public void run() {
                    ((JButton)c).doClick();
                }
            });
        }
        */
        click(c);
        waitForIdle();
    }
}
