package abbot.tester;

import java.awt.Component;

/** Hierarchy placeholder for JRootPane.  There are no specific user
 * actions.
 */ 
public class JRootPaneTester extends JComponentTester {

    /**
     * Return a unique identifier for the given Component.  There is only
     * ever one JRootPane for a given Window, so derive from the parent's
     * tag.
     */
    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        String tag = null;
        if (comp.getParent() != null) {
            String ptag = getTag(comp.getParent());
            if (ptag != null && !"".equals(ptag))
                tag = ptag + " Root Pane";
        }
        if (tag == null || "".equals(tag))
            tag = super.deriveTag(comp);
        return tag;
    }
}
