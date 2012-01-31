package abbot.tester;

import java.awt.*;

public class DialogTester extends WindowTester {

    /** Return a unique tag to help identify the given component. */
    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        String tag = ((Dialog)comp).getTitle();
        if (tag == null || "".equals(tag)) {
            tag = super.deriveTag(comp);
        }
        return tag;
    }
}
