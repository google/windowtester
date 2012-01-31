/*
 * Copyright 2004 Timothy Wall
 *
 */
package abbot.util;

import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import abbot.Log;
import abbot.Platform;
import abbot.i18n.Strings;
import abbot.tester.Robot;

/**
 Provides extant bug characterization of the current platform.  For any bug
 encountered, there should be a static method indicating whether the bug is
 expected on the current platform, for example
 {@link #showAWTPopupMenuBlocks()}.
 See the source for <code>
 <a href=../../../../test/abbot/util/BugsTest.java>BugsTest</a></code> for the
 test implementations. 
 @author twall
*/
public class Bugs {
    private static java.util.ArrayList bugList = null;
    private static boolean gotBug1Event = false;

    private Bugs() { }

    /** Returns whether AWT menus have enable/disable problems. */
    public static boolean hasMenuDisableBug() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION >= Platform.JAVA_1_4
            && Platform.JAVA_VERSION < 0x1424; // when it got fixed
    }

    /** Returns whether KEY_TYPED events are sent to AWT listeners. */
    public static boolean hasInputMethodInsteadOfKeyTyped() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION >= Platform.JAVA_1_4
            && Platform.JAVA_VERSION < 0x1424; // when it got fixed
    }

    /** Returns whether windows send mouse motion events to AWT listeners. */
    public static boolean hasMissingWindowMouseMotion() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION >= Platform.JAVA_1_4
            && Platform.JAVA_VERSION <= 0x1425; // most recent tested
    }

    /** Returns whether mouse buttons 2/3 are swapped when using Robot. */
    public static boolean hasRobotButtonsSwapped() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION >= Platform.JAVA_1_4
            && Platform.JAVA_VERSION <= 0x1425; // most recent tested
    }

    /** Do we get multiple clicks even when the individual clicks are on
     * different frames?
     */
    // Will we get incorrectly get multiple clicks on rapid robot clicks?
    // frame1->click,frame2->click 
    // frame1->click,frame1->hide/show,frame1->click
    public static boolean hasMultiClickFrameBug() {
        // w32 (1.3.02) will count it even if it was on a different component!
        // w32 (1.3.1_06) same
        // w32 (1.4.1_02) same (sporadic)
        // OSX (1.3.x, 1.4.1) has the same problem
        // Haven't seen it on linux
        return Platform.isWindows() || Platform.isOSX()
            || Platform.JAVA_VERSION < Platform.JAVA_1_3;
    }

    /** Returns whether there may be some scenarios in which the robot does
        not function properly.
    */
    public static boolean needsRobotVerification() {
        return Platform.isWindows() || Platform.isOSX();
    }

    /** Prior to 1.4.1, hierarchy events are only sent if listeners are added
        to a given component.
     */
    public static boolean hasHierarchyEventGenerationBug() {
        return Platform.JAVA_VERSION < Platform.JAVA_1_4;
    }

    /** OSX prior to 1.4 has really crappy key input handling. */
    public static boolean hasKeyStrokeGenerationBug() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION < Platform.JAVA_1_4;
    }

    /** Returnes whether there a longer delay required between robot
        generation and event queue posting for key events. */
    public static boolean hasKeyInputDelay() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION >= Platform.JAVA_1_4
            && Platform.JAVA_VERSION <= 0x1425;
    }

    /** Some OSX releases wouldn't restore an iconified Frame. */
    public static boolean hasFrameDeiconifyBug() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION > 0x1310
            && Platform.JAVA_VERSION < 0x1424;
    }

    /** OS X (as of 1.3.1, v10.1.5), will sometimes send a click to the wrong
        component after a mouse move.  This continues to be an issue in 1.4.1
        <p>
        Linux x86 (1.3.1) has a similar problem, although it manifests it at
        different times (need a bug test case for this one).
        <p>
        Solaris and HPUX probably share code with the linux VM implementation,
        so the bug there is probably identical.
        <p>
    */
    // FIXME add tests to determine presence of bug.
    public static boolean hasRobotMotionBug() {
        return Platform.isOSX()
            || (!Platform.isWindows()
                && Platform.JAVA_VERSION < Platform.JAVA_1_4)
            || Boolean.getBoolean("abbot.robot.need_jitter");
    }

    /** Choice popup activates on mouse press, but locks up when the Robot
     * attempts to post a mouse release.
     */
    public static boolean hasChoiceLockupBug() {
        return Platform.isOSX()
            && Platform.JAVA_VERSION < Platform.JAVA_1_4;
    }

    /** Robot.keyPress(KeyEvent.VK_ESCAPE doesn't work. */
    public static boolean hasEscapeGenerationBug() {
	return Platform.isOSX()
	    && Platform.JAVA_VERSION < Platform.JAVA_1_4;
    }

    /** Returns whether the Java event queue is suspended while an AWT popup
        is showing. */
    public static boolean showAWTPopupMenuBlocks() {
        return Platform.isWindows()
            && Platform.JAVA_VERSION <= Platform.JAVA_1_5;
    }

    /** Locking key state is reported incorrectly. */
    public static boolean reportsIncorrectLockingKeyState() {
        return Platform.isLinux()
            && Platform.JAVA_VERSION <= 0x1424;
    }

    /** Whether drag/drop requires native events. */
    // TODO: needs a test
    public static boolean dragDropRequiresNativeEvents() {
        return Platform.JAVA_VERSION <= 0x1425;
    }

    /** Returns whether a {@link java.awt.FileDialog} requires an explicit
        dismiss (ok/cancel).  Ordinarily {@link Window#dispose()} will work.
    */
    public static boolean fileDialogRequiresDismiss() {
        return Platform.isOSX() && Platform.JAVA_VERSION <= 0x1425
            || (Platform.isWindows()
                && Platform.JAVA_VERSION <= Platform.JAVA_1_5);
    }

    /** Returns whether a {@link java.awt.FileDialog} misreports its screen
        location.
    */ 
    public static boolean fileDialogMisreportsBounds() {
        return (Platform.isOSX() && Platform.JAVA_VERSION <= 0x1425)
            || (Platform.isWindows()
                && Platform.JAVA_VERSION <= Platform.JAVA_1_5);
    }

    public static boolean fileDialogRequiresVisibleFrame() {
        // Subsequent FileDialogs don't show up if the frame isn't visible,
        // and a previous non-visible frame was disposed.
        return Platform.isWindows() && Platform.JAVA_VERSION <= 0x1421;
    }

    public static boolean hasTextComponentSelectionDelay() {
        // TODO: write a test for this one
        // Select TextComponent text, wait for idle, selection end not updated
        return (Platform.isLinux() || Platform.isWindows())
            && Platform.JAVA_VERSION <= 0x1424;
    }

    /** Check for certain robot-related bugs that will affect Abbot
     * operation.  Returns a String for each bug detected on the current
     * system. 
     */ 
    public static String[] bugCheck(final Window window) {
        if (bugList == null) {
            bugList = new java.util.ArrayList();
            final int x = window.getWidth() / 2;
            final int y = window.getHeight() / 2;
            if (Platform.isWindows() && !Platform.isWindowsXP()
                && Platform.JAVA_VERSION < Platform.JAVA_1_4) {
                Log.debug("Checking for w32 bugs");
                final int mask = InputEvent.BUTTON2_MASK;
                MouseAdapter ma = new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        gotBug1Event = true;
                        // w32 acceleration settings bug
                        if (e.getX() != x || e.getY() != y) {
                            bugList.add(Strings.get("Bug1"));
                        }
                        // w32 mouse button mapping bug
                        if ((e.getModifiers() & mask) != mask) {
                            bugList.add(Strings.get("Bug2"));
                        }
                    }
                };
                window.addMouseListener(ma);
                Robot robot = new Robot();
                robot.click(window, x, y, mask);
                robot.waitForIdle();
                window.toFront();
                // Bogus acceleration may mean the event goes entirely
                // elsewhere 
                if (!gotBug1Event) {
                    bugList.add(0, Strings.get("Bug1"));
                }
                window.removeMouseListener(ma);
            }
            else if (Platform.isOSX() 
                && Platform.JAVA_VERSION < 0x1430) {
                Log.debug("Checking for OSX bugs");
                final int mask = InputEvent.BUTTON2_MASK;
                MouseAdapter ma = new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if ((e.getModifiers() & mask) != mask) {
                            bugList.add(Strings.get("robot.bug3"));
                        }
                    }
                };
                window.addMouseListener(ma);
                Robot robot = new Robot();
                robot.click(window, x, y, mask);
                robot.waitForIdle();
                window.removeMouseListener(ma);
            }
        }
        return (String[])bugList.toArray(new String[bugList.size()]);
    }
    
}
