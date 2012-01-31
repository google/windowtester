package abbot.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import abbot.Log;

/** Handle process output. */

public class InputStreamHandler extends Thread {
    private InputStream stream = null;

    public InputStreamHandler(InputStream stream) {
        super("Process Input Stream Handler");
        setDaemon(true);
        this.stream = new BufferedInputStream(stream);
    }

    /** Override this method to do something meaningful with the output. */
    public void handleBytes(byte[] buf, int count) { }

    // WARNING: closing a process output stream prematurely may result in 
    // the Process object never detecting its termination!
    private void close() {
        try { stream.close(); } 
        catch(IOException io) { Log.debug(io); }
    }

    public void run() {
        int BUFSIZE = 256;
        byte[] buf = new byte[BUFSIZE];
        Log.debug("Stream reader started");
        while(true) {
            try {
                Log.debug("Reading from stream");
                int count = stream.read(buf, 0, buf.length);
                if (count == -1) {
                    Log.debug("end of stream");
                    break;
                }
                else if (count == 0) {
                    Log.debug("No input, sleeping");
                    try { sleep(100); } 
                    catch (InterruptedException e) { Log.debug(e); }
                }
                else if (count > 0) {
                    Log.debug("Got " + count + " bytes");
                    handleBytes(buf, count);
                }
            }
            catch(IOException io) {
                // we'll get this when the stream closes
                Log.debug(io);
                break;
            }
        }
        close();
        Log.debug("stream handler terminating");
    }
}

