package abbot.finder.matchers;

import java.awt.Component;

/** Provides matching of components by class. */
public class ClassMatcher extends AbstractMatcher {
    private Class cls;
    private boolean mustBeShowing;
    public ClassMatcher(Class cls) {
        this(cls, false);
    }
    public ClassMatcher(Class cls, boolean mustBeShowing) {
        this.cls = cls;
        this.mustBeShowing = mustBeShowing;
    }
    public boolean matches(Component c) {
        return cls.isAssignableFrom(c.getClass())
            && (!mustBeShowing || c.isShowing());
    }
    public String toString() {
        return "Class matcher (" + cls.getName() + ")";
    }
}
