package junit.extensions.abbot;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import junit.framework.TestCase;
import abbot.*;
import abbot.finder.*;
import abbot.finder.ComponentFinder;
import abbot.finder.matchers.*;
import abbot.script.*;
import abbot.script.Resolver;
import abbot.tester.*;
import abbot.tester.Robot;
import abbot.util.*;

/** Fixture for testing AWT and/or JFC/Swing components under JUnit.  Ensures
 * proper setup and cleanup for a GUI environment.  Provides methods for
 * automatically placing a GUI component within a frame and properly handling
 * Window showing/hiding (including modal dialogs).  Catches exceptions thrown
 * on the event dispatch thread and rethrows them as test failures.<p> 
 * Use {@link #showFrame(Component)}
 * when testing individual components, or 
 * {@link #showWindow(Window)}
 * when testing a {@link Frame}, {@link Dialog}, or {@link Window}.<p>
 * Any member fields you define which are classes derived from any of the
 * classes in {@link #DISPOSE_CLASSES} will be automatically set to null after
 * the test is run.<p>
 * <bold>WARNING:</bold> Any tests which use significant or scarce resources
 * and reference them in member fields should explicitly null those fields in
 * the tearDown method if those classes are not included or derived from those
 * in {@link #DISPOSE_CLASSES}.  Otherwise the resources will not be subject
 * to GC until the {@link TestCase} itself and any containing
 * {@link junit.framework.TestSuite} is 
 * disposed (which, in the case of the standard JUnit test runners, is
 * <i>never</i>). 
 */
public class ComponentTestFixture extends ResolverFixture {

    public class EventDispatchException extends InvocationTargetException {
        private EventDispatchException(Throwable t) {
            super(t, "An exception was thrown on the event dispatch thread: "
                  + t.toString());
        }
        public void printStackTrace() {
            getTargetException().printStackTrace();
        }
        public void printStackTrace(PrintStream p) {
            getTargetException().printStackTrace(p);
        }
        public void printStackTrace(PrintWriter p) {
            getTargetException().printStackTrace(p);
        }
    }

    /** Typical delay to wait for a robot event to be translated into a Java
        event. */
    public static final int EVENT_GENERATION_DELAY = 5000;
    public static final int WINDOW_DELAY = 20000; // for slow systems
    public static final int POPUP_DELAY = 10000;

    /** Any member data derived from these classes will be automatically set
        to <code>null</code> after the test has run.  This enables GC of said
        classes without GC of the test itself (the default JUnit runners never
        release their references to the tests) or requiring explicit
        <code>null</code>-setting in the {@link TestCase#tearDown()} method.
    */
    protected static final Class[] DISPOSE_CLASSES = {
        Component.class,
        ComponentTester.class
    };

    private static Robot robot;
    private static WindowTracker tracker;

    private AWTFixtureHelper savedState;
    private Throwable edtException;
    private long edtExceptionTime;

    /** Return an Abbot {@link abbot.tester.Robot} for basic event generation.
     */ 
    protected Robot getRobot() { return robot; }
    /** Return a WindowTracker instance. */
    protected WindowTracker getWindowTracker() { return tracker; }

    /** This method should be invoked to display the component under test.
     * The frame's size will be its preferred size.  This method will return
     * with the enclosing {@link Frame} is showing and ready for input.
     */
    protected Frame showFrame(Component comp) {
        return showFrame(comp, null);
    }

    /** This method should be invoked to display the component under test,
     * when a specific size of frame is desired.  The method will return when
     * the enclosing {@link Frame} is showing and ready for input.  
     * @param comp
     * @param size Desired size of the enclosing frame, or <code>null</code>
     * to make no explicit adjustments to its size.
     */
    protected Frame showFrame(Component comp, Dimension size) {
        JFrame frame = new JFrame(getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel pane = (JPanel)frame.getContentPane();
        pane.setBorder(new EmptyBorder(10, 10, 10, 10));
        pane.add(comp);
        showWindow(frame, size, true);
        return frame;
    }

    /** Safely display a window with proper EDT synchronization.   This method
     * blocks until the {@link Window} is showing and ready for input.
     */
    protected void showWindow(Window w) {
        showWindow(w, null, true);
    }

    /** Safely display a window with proper EDT synchronization.   This method
     * blocks until the {@link Window} is showing and ready for input.
     */
    protected void showWindow(final Window w, final Dimension size) {
        showWindow(w, size, true);
    }

    /** Safely display a window with proper EDT synchronization.   This method
     * blocks until the window is showing.  This method will return even when
     * the window is a modal dialog, since the show method is called on the
     * event dispatch thread.  The window will be packed if the pack flag is
     * set, and set to the given size if it is non-<code>null</code>.<p>
     * Modal dialogs may be shown with this method without blocking.
     */
    protected void showWindow(final Window w, final Dimension size,
                              final boolean pack) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (pack) {
                    w.pack();
                    // Make sure the window is positioned away from
                    // any toolbars around the display borders
                    w.setLocation(100, 100);
                }
                if (size != null)
                    w.setSize(size.width, size.height);
                w.show();
            }
        });
        // Ensure the window is visible before returning
        waitForWindow(w, true);
    }

    /** Return when the window is ready for input or times out waiting.
     * @param w
     */
    private void waitForWindow(Window w, boolean visible) {
        Timer timer = new Timer();
        while (tracker.isWindowReady(w) != visible) {
            if (timer.elapsed() > WINDOW_DELAY)
                throw new RuntimeException("Timed out waiting for Window to "
                                           + (visible ? "open" : "close")
                                           + " (" + timer.elapsed() + "ms)");
            robot.sleep();
        }
    }
    
    /** Synchronous, safe hide of a window.  The window is ensured to be
     * hidden ({@link java.awt.event.ComponentEvent#COMPONENT_HIDDEN} or
     * equivalent has been posted) when this method returns.  Note that this
     * will <em>not</em> trigger a 
     * {@link java.awt.event.WindowEvent#WINDOW_CLOSING} event; use
     * {@link abbot.tester.WindowTester#actionClose(Component)}
     * if a window manager window close operation is required. 
     */
    protected void hideWindow(final Window w) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                w.hide();
            }
        });
        waitForWindow(w, false);
        // Not strictly required, but if a test is depending on a window
        // event listener's actions on window hide/close, better to wait.
        robot.waitForIdle();
    }

    /** Synchronous, safe dispose of a window.  The window is ensured to be
     * disposed ({@link java.awt.event.WindowEvent#WINDOW_CLOSED} has been
     * posted) when this method returns. 
     */
    protected void disposeWindow(Window w) {
        w.dispose();
        waitForWindow(w, false);
        robot.waitForIdle();
    }

    /** Install the given popup on the given component.  Takes care of
     * installing the appropriate mouse handler to activate the popup.
     */
    protected void installPopup(Component invoker, final JPopupMenu popup) {
        invoker.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /** Safely install and display a popup in the center of the given
     * component, returning when it is visible.  Does not install any mouse
     * handlers not generate any mouse events. 
     */
    protected void showPopup(final JPopupMenu popup, final Component invoker) {
        showPopup(popup, invoker, invoker.getWidth()/2, invoker.getHeight()/2);
    }

    /** Safely install and display a popup, returning when it is visible.
        Does not install any mouse handlers not generate any mouse events.
     */
    protected void showPopup(final JPopupMenu popup, final Component invoker,
                             final int x, final int y) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                popup.show(invoker, x, y);
            }
        });
        Timer timer = new Timer();
        while (!popup.isShowing()) {
            if (timer.elapsed() > POPUP_DELAY)
                throw new RuntimeException("Timed out waiting for popup to show");
            robot.sleep();
        }
        waitForWindow(SwingUtilities.getWindowAncestor(popup), true);
    }

    /** Display a modal dialog and wait for it to show.  Useful for things
     * like {@link JFileChooser#showOpenDialog(Component)} or
     * {@link JOptionPane#showInputDialog(Component,Object)}, or any
     * other instance where the dialog contents are not predefined and
     * displaying the dialog involves anything more than 
     * {@link Window#show()} (if {@link Window#show()} is all that is
     * required, use the {@link #showWindow(Window)} method instead).<p>
     * The given {@link Runnable} should contain the code which will show the
     * modal {@link Dialog} (and thus block); it will be run on the event
     * dispatch thread.<p>
     * This method will return when a {@link Dialog} becomes visible which
     * contains the given component (which may be any component which will
     * appear on the {@link Dialog}), or the standard timeout (10s) is
     * reached, at which point a {@link RuntimeException} will be thrown.<p>
     * For example,<br>
     <pre><code>
     Frame parent = ...;
     showModalDialog(new Runnable) {
         public void run() {
             JOptionPane.showInputDialog(parent, "Hit me");
         }
     });
     </code></pre> 
     @see #showWindow(java.awt.Window)
     @see #showWindow(java.awt.Window,java.awt.Dimension)
     @see #showWindow(java.awt.Window,java.awt.Dimension,boolean)
     */
    protected Dialog showModalDialog(Runnable showAction) throws Exception {
        EventQueue.invokeLater(showAction);
        // Wait for a modal dialog to appear
        Matcher matcher = new ClassMatcher(Dialog.class, true) {
            public boolean matches(Component c) {
                return super.matches(c) 
                    && ((Dialog)c).isModal();
            }
        };
        Timer timer = new Timer();
        while (true) {
            try {
                return (Dialog)getFinder().find(matcher);
            }
            catch(ComponentSearchException e) {
                if (timer.elapsed() > 10000)
                    throw new RuntimeException("Timed out waiting for dialog to be ready");
                robot.sleep();
            }
        }
    }

    /** Similar to {@link #showModalDialog(Runnable)},
     * but provides for the case where some of the {@link Dialog}'s contents
     * are known beforehand.<p>
     * @deprecated Use {@link #showModalDialog(Runnable)} instead.
     */
    protected Dialog showModalDialog(Runnable showAction, Component contents)
        throws Exception {
        return showModalDialog(showAction);
    }

    /** Returns whether a Component is showing.  The ID may be the component
     * name or, in the case of a Frame or Dialog, the title.  Regular
     * expressions may be used, but must be delimited by slashes, e.g. /expr/.
     * Returns if one or more matches is found.
     */
    protected boolean isShowing(String id) {
        try {
            getFinder().find(new WindowMatcher(id, true));
        }
        catch(ComponentNotFoundException e) {
            return false;
        }
        catch(MultipleComponentsFoundException m) {
            // Might not be the one you want, but that's what the docs say
        }
        return true;
    }

    /** Construct a test case with the given name.  */
    public ComponentTestFixture(String name) {
        super(name);
    }

    /** Default Constructor.  The name will be automatically set from the
        selected test method.
    */ 
    public ComponentTestFixture() { }

    /** Ensure proper test harness setup and teardown that won't
     * be inadvertently overridden by a derived class. 
     */
    protected void fixtureSetUp() throws Throwable {
        super.fixtureSetUp();

        savedState = new AWTFixtureHelper();

        robot = new Robot();
        tracker = WindowTracker.getTracker();

        robot.reset();
        if (Bugs.hasMultiClickFrameBug())
            robot.delay(500);
    }
    
    /** Handles restoration of system state.  Automatically disposes of any
        Components used in the test.
    */
    protected void fixtureTearDown() throws Throwable {
        super.fixtureTearDown();
        tracker = null;
        if (robot != null) {
            int buttons = Robot.getState().getButtons();
            if (buttons != 0) {
                robot.mouseRelease(buttons);
            }
            // TODO: release any extant pressed keys
            robot = null;
        }
        edtExceptionTime = savedState.getEventDispatchErrorTime();
        edtException = savedState.getEventDispatchError();
        savedState.restore();
        savedState = null;
        clearTestFields();
    }

    /** Clears all non-static {@link TestCase} fields which are instances of
     * any class found in {@link #DISPOSE_CLASSES}.
     */
    private void clearTestFields() {
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (int i=0;i < fields.length;i++) {
                if ((fields[i].getModifiers() & Modifier.STATIC) == 0) {
                    fields[i].setAccessible(true);
                    for (int c=0;c < DISPOSE_CLASSES.length;c++) {
                        Class cls = DISPOSE_CLASSES[c];
                        if (cls.isAssignableFrom(fields[i].getType())) {
                            fields[i].set(this, null);
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            Log.warn(e);
        }
    }

    /** If any exceptions are thrown on the event dispatch thread, they count
        as errors.  They will not, however supersede any failures/errors
        thrown by the test itself.
    */
    public void runBare() throws Throwable {
        Throwable exception = null;
        long exceptionTime = -1;
        try {
            super.runBare();
        }
        catch(Throwable e) {
            exceptionTime = System.currentTimeMillis();
            exception = e;
        }
        finally {
            // Cf. StepRunner.runStep()
            // Any EDT exception which occurred *prior* to when the
            // exception on the main thread was thrown should be used
            // instead.
            if (edtException != null
                && (exception == null
                    || edtExceptionTime < exceptionTime)) {
                exception = new EventDispatchException(edtException);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
}
