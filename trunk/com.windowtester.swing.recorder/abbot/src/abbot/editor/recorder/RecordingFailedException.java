package abbot.editor.recorder;

public class RecordingFailedException extends RuntimeException {
    private Throwable reason = null;
    public RecordingFailedException(String msg) {
        super(msg);
    }
    public RecordingFailedException(Throwable thr) {
        super(thr.getMessage());
        reason = thr;
    }
    public Throwable getReason() { return reason != null ? reason : this; }
}
