package abbot.finder;

/** Indicates no component could be found, where one was required. */
public class ComponentNotFoundException extends ComponentSearchException {
	private static final long serialVersionUID = 1L;

    public ComponentNotFoundException() { }
    public ComponentNotFoundException(String msg) { super(msg); }
}
