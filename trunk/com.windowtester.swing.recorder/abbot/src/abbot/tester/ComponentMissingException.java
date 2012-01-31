package abbot.tester;

/** Indicates that a component required by a ComponentTester action was not
 * found.
 */ 
public class ComponentMissingException extends ActionFailedException {
    public ComponentMissingException(String msg) { super(msg); }
}
