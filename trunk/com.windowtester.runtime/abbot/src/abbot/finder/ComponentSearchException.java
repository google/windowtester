package abbot.finder;

/** General exception class which encapsulates all failures generated
 * attempting to find a component in the currently available GUI.
 */
public class ComponentSearchException extends Exception {
	private static final long serialVersionUID = 1L;

    public ComponentSearchException() { }
    public ComponentSearchException(String msg) { super(msg); }
}
