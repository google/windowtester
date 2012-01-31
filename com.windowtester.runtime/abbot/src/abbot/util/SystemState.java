package abbot.util;

import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.UIManager;

import abbot.Log;

/** Preserve and restore system state.
    This includes the following:
    <ul>
    <li><code>System.out/err</code> streams
    <li><code>System</code> properties
    <li>Security manager
    </ul>
 */
public class SystemState {

    private static final int CODES[] = {
        KeyEvent.VK_CAPS_LOCK,
        KeyEvent.VK_NUM_LOCK,
        KeyEvent.VK_SCROLL_LOCK,
        KeyEvent.VK_KANA_LOCK
    };
    private Properties oldProps;
    private PrintStream oldOut;
    private PrintStream oldErr;
    private SecurityManager oldsm;
    private String oldLookAndFeel;
    private boolean lockingKeys[];
    private static Robot robot = null;

    static {
        try {
            robot = new Robot();
        }
        catch(AWTException e) {
        }
    }

    /** Take a snapshot of the current System state for later restoration. */
    public SystemState() {
        lockingKeys = new boolean[CODES.length];
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (int i=0;i < CODES.length;i++) {
            try {
                lockingKeys[i] = toolkit.getLockingKeyState(CODES[i]);
                try {
                    toolkit.setLockingKeyState(CODES[i], false);
                }
                catch(UnsupportedOperationException e) {
                    // Manually toggle the key
                    if (lockingKeys[i] && robot != null) {
                        robot.keyPress(CODES[i]);
                        robot.keyRelease(CODES[i]);
                    }
                }
            }
            catch(UnsupportedOperationException e) {
                // Nothing much we can do
            }
        }
        oldLookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        oldOut = System.out;
        oldErr = System.err;
        System.setOut(new ProtectedStream(oldOut));
        System.setErr(new ProtectedStream(oldErr));
        oldProps = (Properties)System.getProperties().clone();
        oldsm = System.getSecurityManager();
    }

    /** Restore the state captured in the ctor. */
    public void restore() {
        System.setSecurityManager(oldsm); 
        System.setProperties(oldProps); 
        System.setOut(oldOut); 
        System.setErr(oldErr); 
        try { UIManager.setLookAndFeel(oldLookAndFeel); }
        catch(Exception e) { Log.warn("Could not restore LAF: " + e); }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (int i=0;i < CODES.length;i++) {
            try {
                boolean state = toolkit.getLockingKeyState(CODES[i]);
                if (state != lockingKeys[i]) {
                    try {
                        toolkit.setLockingKeyState(CODES[i], lockingKeys[i]);
                    }
                    catch(UnsupportedOperationException e) {
                        if (robot != null) {
                            robot.keyPress(CODES[i]);
                            robot.keyRelease(CODES[i]);
                        }
                    }
                }
            }
            catch(UnsupportedOperationException e) {
                // Oh, well
            }
        }
    }

    /** Provide a wrapper that prevents the original stream from being
        closed.
    */
    private class ProtectedStream extends PrintStream {
        private boolean closed = false;
        public ProtectedStream(PrintStream original) {
            super(original);
        }
        public void flush() {
            if (!closed)
                super.flush();
        }
        public void close() {
            closed = true;
        }
        public void write(int b) {
            if (!closed)
                super.write(b);
        }
        public void write(byte[] buf, int off, int len) {
            if (!closed)
                super.write(buf, off, len);
        }
    }
}

