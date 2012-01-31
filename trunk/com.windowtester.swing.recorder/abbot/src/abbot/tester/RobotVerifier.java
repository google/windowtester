package abbot.tester;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import abbot.Platform;
import abbot.util.Bugs;

/** Provides methods to verify that the robot on the current platform works
 * properly.
 */
public class RobotVerifier {
    // No instantiations
    private RobotVerifier() { }

    private static final String WINDOW_NAME = "Abbot Robot Verification";

    /** Auto-detect whether the robot actually works. 
        Use this to tell whether we're in w32 pseudo-headless mode, such as
        when in a remote shell (ssh) or running as a service.  This is also
        used as a delay mechanism to ensure the AWT subsystem is running
        before continuing (particularly a problem on early OSX 1.4 VM
        versions). 
        @return false if the robot fails.
    */
    public static boolean verify(java.awt.Robot robot) {
        if (!Bugs.needsRobotVerification())
            return true;

        class Flag { volatile boolean flag = false; }
        final Flag flag = new Flag();
        final int SIZE = 4;
        Window w;
        Frame f = new Frame(WINDOW_NAME);
        f.setName(WINDOW_NAME);
        if (Bugs.hasMissingWindowMouseMotion()) {
            // so use an undecorated dialog instead.
            w = new Dialog(f);
            try {
                // setUndecorated is 1.4+ only
                w.getClass().
                    getDeclaredMethod("setUndecorated",
                                      new Class[] { boolean.class }).
                    invoke(w, new Object[] { Boolean.TRUE });
            }
            catch(Exception e) { }
        }
        else {
            w = new Window(f);
        }
        w.setName(WINDOW_NAME);
        w.pack();
        w.setSize(SIZE, SIZE);
        w.setLocation(100, 100);
        w.addMouseMotionListener(new MouseInputAdapter() {
            public void mouseMoved(MouseEvent e) {
                synchronized(flag) {
                    flag.flag = true;
                    flag.notifyAll();
                }
            }
        });
        w.show();
        robot.waitForIdle();
        WindowTracker tracker = WindowTracker.getTracker();
        while (!tracker.isWindowReady(w)) {
            robot.delay(20);
        }
        // Bail if we get no response after 30s
        final int AWT_WAIT_TIMEOUT = 30000;
        long start = System.currentTimeMillis();
        try {
            do {
                w.toFront();
                synchronized(flag) {
                    robot.mouseMove(w.getX(),
                                    w.getY() + w.getHeight() - 1);
                    robot.mouseMove(w.getX() + 1,
                                    w.getY() + w.getHeight() - 2);
                    try {
                        flag.wait(500);
                    }
                    catch(InterruptedException e) {
                    }
                }
            } while (!flag.flag 
                     && System.currentTimeMillis() - start < AWT_WAIT_TIMEOUT);
        }
        finally {
            w.dispose();
        }
        return flag.flag;
    }
}
