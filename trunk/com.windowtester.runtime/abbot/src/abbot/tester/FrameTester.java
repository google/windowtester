package abbot.tester;

import java.awt.*;

public class FrameTester extends WindowTester {

    /** Return a unique tag to help identify the given component. */
    public String deriveTag(java.awt.Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        String tag = ((java.awt.Frame)comp).getTitle();
        if (tag == null || "".equals(tag)) {
            tag = super.deriveTag(comp);
        }
        return tag;
    }

    /** Iconify the given Frame. */
    public void actionIconify(Component comp) {
        iconify((Frame)comp);
        waitForIdle();
    }

    /** Deiconify the given Frame. */
    public void actionDeiconify(Component comp) {
        deiconify((Frame)comp);
        waitForIdle();
    }

    /** Maximize the given Frame. */
    public void actionMaximize(Component comp) {
        maximize((Frame)comp);
        waitForIdle();
    }

    /** Normalize the given Frame.  Note that on 1.3.1 systems this may have
     * no effect after a maximize.
     */
    public void actionNormalize(Component comp) {
        normalize((Frame)comp);
        waitForIdle();
    }

}

