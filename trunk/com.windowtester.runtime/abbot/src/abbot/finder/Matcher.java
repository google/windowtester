package abbot.finder;

import java.awt.Component;

/** Provides an indication whether a Component matches some desired
    criteria.  For use with implementations of {@link ComponentFinder}.
    You can conveniently inline a custom matcher like so:<br>
    <pre><code>
    ComponentFinder finder;
    ...
    // Find a label with known text
    JLabel label = (JLabel)finder.find(new Matcher() {
        public boolean matches(Component c) {
            return c instanceof JLabel
                && "expected text".equals(((JLabel)c).getText());
        }
    });
    </code></pre>
    @see ComponentFinder
*/ 
public interface Matcher {
    /** Return whether the given Component matches some lookup criteria. */
    boolean matches(Component c);
}
