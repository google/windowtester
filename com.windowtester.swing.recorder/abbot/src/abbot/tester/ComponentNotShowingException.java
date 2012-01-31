package abbot.tester;

/** Indicates that a ComponentTester action failed due to the component not
 * being visible on screen.
 */
public class ComponentNotShowingException extends ActionFailedException {
    public ComponentNotShowingException(String msg) { super(msg); }
}
