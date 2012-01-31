package abbot.finder.matchers;

import java.awt.Component;
import abbot.finder.Matcher;
import abbot.util.AWT;
import abbot.util.ExtendedComparator;

/** Provides matching of Components by component name. */
public class NameMatcher extends AbstractMatcher {
    private String name;

    /** Construct a matcher that will match any component that has
        explicitly been assigned the given <code>name</code>.  Auto-generated
        names (e.g. <code>win0</code>, <code>frame3</code>, etc. for AWT
        (native) based components will not match.
    */
    public NameMatcher(String name) {
        this.name = name;
    }

    /** @return whether the given component has been explicitly assigned the
        name given in the constructor.
    */
    public boolean matches(Component c) {
        String cname = c.getName();
        if (name == null)
            return cname == null || AWT.hasDefaultName(c);
        return stringsMatch(name, cname);
    }
    public String toString() {
        return "Name matcher (" + name + ")";
    }
}
