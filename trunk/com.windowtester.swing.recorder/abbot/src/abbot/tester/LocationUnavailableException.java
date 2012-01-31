package abbot.tester;

/** Indicates that a location can't be provided. */

public class LocationUnavailableException extends ActionFailedException {
    public LocationUnavailableException(String msg) {
        super(msg);
    }
}
