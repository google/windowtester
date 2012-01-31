package abbot;

import java.io.File;

import abbot.i18n.Strings;
import abbot.script.Step;
import abbot.script.Script;

/** Indirect usage to avoid too much direct linkage to JUnit. */
public class AssertionFailedError
    extends junit.framework.AssertionFailedError {
    private File file;
    private int line;
    public AssertionFailedError() { }
    public AssertionFailedError(String msg) { super(msg); }
    public AssertionFailedError(String msg, Step step) {
        super(getMessage(msg, step));
        this.file = Script.getFile(step);
        this.line = Script.getLine(step);
    }
    public File getFile() { return file; }
    public int getLine() { return line; }
    private static String getMessage(String msg, Step step) {
        File file = Script.getFile(step);
        int line = Script.getLine(step);
        if (file == null || line <= 0)
            return msg;
        return Strings.get("step.failure", new Object[] {
            msg, file, new Integer(line)
        });
    }
}
