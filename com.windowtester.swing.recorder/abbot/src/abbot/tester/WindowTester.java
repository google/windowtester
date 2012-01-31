package abbot.tester;

import java.awt.*;
import java.awt.event.*;

import javax.accessibility.AccessibleContext;

import abbot.i18n.Strings;

/** Provides user actions on a Window. */
public class WindowTester extends ContainerTester {

    /** The window's size seems as good an ID as any.  If someone has a bunch
     * of frameless windows floating about, they can come up with a better
     * ID.
     */
    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        String tag = null;
        AccessibleContext context = ((Window)comp).getAccessibleContext();
        tag = deriveAccessibleTag(context);
        if (tag == null || "".equals(tag)) {
            Dimension size = comp.getSize();
            tag = String.valueOf(size.width) + "x" 
                + String.valueOf(size.height);
        }
        return tag;
    }

    /** Send a WINDOW_CLOSING event to the window, equivalent to the user
        closing the window through the window manager.  Note that this will
        not necessarily close the window.
    */
    public void actionClose(Component c) {
        close((Window)c);
        waitForIdle();
    }

    /** Move the window to the given location. */
    public void actionMove(Component w, int screenx, int screeny) {
        if (!userMovable(w))
            throw new ActionFailedException(Strings.get("tester.Window.no_move"));
        move((Window)w, screenx, screeny);
        waitForIdle();
    }

    /** Move the window to the given location. */
    public void actionMoveBy(Component w, int dx, int dy) {
        if (!userMovable(w))
            throw new ActionFailedException(Strings.get("tester.Window.no_move"));
        moveBy((Window)w, dx, dy);
        waitForIdle();
    }

    /** Resize the given window.  Note that this will fail on frames or
     * dialogs which are not resizable.
     */
    public void actionResize(Component w, int width, int height) {
        if (!userResizable(w))
            throw new ActionFailedException(Strings.get("tester.Window.no_resize"));
        resize((Window)w, width, height);
        waitForIdle();
    }

    /** Resize the given window.  Note that this will fail on frames or
     * dialogs which are not resizable.
     */
    public void actionResizeBy(Component w, int dx, int dy) {
        if (!userResizable(w))
            throw new ActionFailedException(Strings.get("tester.Window.no_resize"));
        resizeBy((Window)w, dx, dy);
        waitForIdle();
    }

    /** Activate the given Window.  */
    public void actionActivate(Window w) {
        activate(w);
        waitForIdle();
    }
}
