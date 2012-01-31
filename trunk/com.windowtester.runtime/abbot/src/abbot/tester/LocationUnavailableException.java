package abbot.tester;

/** Indicates that a location can't be provided. */

public class LocationUnavailableException extends ActionFailedException {
	private static final long serialVersionUID = 1L;

    public LocationUnavailableException(String msg) {
        super(msg);
    }
}
