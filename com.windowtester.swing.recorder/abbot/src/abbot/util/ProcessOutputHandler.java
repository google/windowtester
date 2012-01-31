package abbot.util;

import java.io.*;
import java.util.*;
import abbot.Log;
import abbot.Platform;

/** Provides handling of process output/error streams. */

public class ProcessOutputHandler {

    public static class ProcessAbnormalExitException extends IOException {
        private int code;
        private ProcessAbnormalExitException(String msg, int code) {
            super(msg);
            this.code = code;
        }
        public int getExitValue() { return code; }
    }

    private InputStreamHandler stderr;
    private InputStreamHandler stdout;
    private StringBuffer err = new StringBuffer();

    public ProcessOutputHandler() { }

    public ProcessOutputHandler(Process p) {
        setProcess(p);
    }

    public String getError() {
        return err.toString();
    }

    public synchronized void setProcess(Process p) {
        stdout = new InputStreamHandler(p.getInputStream()) {
            public void handleBytes(byte[] buf, int count) {
                handleOutput(buf, count);
            }
        };
        stdout.start();
        stderr = new InputStreamHandler(p.getErrorStream()) {
            public void handleBytes(byte[] buf, int count) {
                err.append(new String(buf, 0, count));
                handleError(buf, count);
            }
        };
        stderr.start();
    }

    /** Override this method to handle stdout output.  The default
        implementation does nothing. */
    protected void handleOutput(byte[] buf, int count) { }

    /** Override this method to handle stderr output.  The default
        implementation does nothing. */
    protected void handleError(byte[] buf, int count) { }

    public synchronized void waitFor() throws InterruptedException {
        if (stderr != null)
            stderr.join();
        if (stdout != null)
            stdout.join();
    }

    /** Returns the output of the given command as a String. */
    public static String exec(String[] command)
        throws IOException {
        return exec(command, null);
    }

    /** Returns the output of the given command as a String. */
    public static String exec(String[] command, String[] environment)
        throws IOException {
        return exec(command, environment, null);
    }

    /** Returns the output of the given command as a String. */
    public static String exec(String[] command, String[] environment, File dir)
        throws IOException {
        final StringBuffer output = new StringBuffer();
        Log.debug("Running " + Arrays.asList(command));
        Process p = Runtime.getRuntime().exec(command, environment, dir);
        ProcessOutputHandler handler = new ProcessOutputHandler(p) {
            public void handleOutput(byte[] buf, int count) {
                output.append(new String(buf, 0, count));
            }
        };
        try { p.waitFor(); } catch(InterruptedException e) {Log.debug(e);}
        try { handler.waitFor(); } catch(InterruptedException e) { }
        int code = p.exitValue();
        if (code != 0) {
            String msg = "Process " + Arrays.asList(command)
                + " exited with " + code;
            String err = handler.getError();
            if (!"".equals(err))
                msg += ":\n" + err;
            Log.debug(msg);
            throw new ProcessAbnormalExitException(msg, code);
        }
        Log.debug("output=" + output.toString());
        return output.toString();
    }
}
