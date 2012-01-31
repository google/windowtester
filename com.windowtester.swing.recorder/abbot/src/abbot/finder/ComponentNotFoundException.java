package abbot.finder;

/** Indicates no component could be found, where one was required. */
public class ComponentNotFoundException extends ComponentSearchException {
    public ComponentNotFoundException() { }
    public ComponentNotFoundException(String msg) { super(msg); }
}
