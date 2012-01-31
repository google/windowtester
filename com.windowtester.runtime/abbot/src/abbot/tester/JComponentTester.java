package abbot.tester;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.accessibility.AccessibleContext;
import javax.swing.*;

import abbot.*;
import abbot.i18n.Strings;
import abbot.util.AWT;

/** Provides auto-scrolling prior to events for JComponent-derived classes. */
// NOTE may eventually need to push ComponentLocation up to Robot, so that
// only mousePress and actionDrop need to be overridden.  This would be mostly
// aesthetic, since making the target point visible is sufficient; having the
// entire substructure visible is just more pleasing to the eye.
// FIXME may need to override key/focus actions to scroll prior to sending the
// events, similar to how click is overridden here
public class JComponentTester extends ContainerTester {

    /** This property is a duplicate of the one in JLabel, which we can't
     * access.
     */
    private static final String LABELED_BY_PROPERTY = "labeledBy";

    /** Derive a tag for identifying this component.  */
    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        JComponent jComp = ((JComponent)comp);
        String tag = null;
        // If label.setLabelFor has been used, then this component has
        // a label; use its text
        JLabel label = (JLabel)
            ((JComponent)comp).getClientProperty(LABELED_BY_PROPERTY);
        if (label != null
            && label.getText() != null
            && label.getText().length() > 0) {
            tag = label.getText();
        }
        if (tag == null || "".equals(tag)) {
            AccessibleContext context = jComp.getAccessibleContext();
            tag = deriveAccessibleTag(context);
        }
        if (tag == null || "".equals(tag)) {
            tag = super.deriveTag(comp);
        }
        return tag;
    }

    /** Scrolls to ensure the substructure is in view before clicking.
        @deprecated Use
        {@link #actionClick(Component, ComponentLocation, int, int)}
        instead.
     */
    public void actionClick(Component c, ComponentLocation loc,
                            String buttons, int count) {
        actionClick(c, loc, AWT.getModifiers(buttons), count);
    }

    /** Scrolls to ensure the substructure is in view before clicking. */
    public void actionClick(Component c, ComponentLocation loc,
                            int buttons, int count) {
        if (c instanceof JComponent) {
            scrollToVisible(c, loc.getBounds(c));
        }
        super.actionClick(c, loc, buttons, count);
    }

    /** @deprecated Use
        {@link #actionDrag(Component, ComponentLocation, int)} instead.
    */
    public void actionDrag(Component c, ComponentLocation loc, String mods) {
        actionDrag(c, loc, AWT.getModifiers(mods));
    }

    /** Scrolls to ensure the substructure is in view before starting the
     * drag.
     */ 
    public void actionDrag(Component c, ComponentLocation loc, int modifiers) {
        if (c instanceof JComponent) {
            scrollToVisible(c, loc.getBounds(c));
        }
        super.actionDrag(c, loc, modifiers);
    }

    /** Scrolls to ensure the drop target substructure is in view before
        dropping (normally handled by autoscroll).
    */
    public void actionDrop(Component c, ComponentLocation loc) {
        if (c instanceof JComponent) {
            scrollToVisible(c, loc.getBounds(c));
        }
        super.actionDrop(c, loc);
    }

    /** Click in the given part of the component, scrolling the component if
     * necessary to make the point visible.  Performing the scroll here
     * obviates the need for all derived classes to remember to do it for
     * actions involving clicks.
     */
    public void mousePress(Component comp, int x, int y, int buttons) {
        if (comp instanceof JComponent) {
            scrollToVisible(comp, x, y);
        }
        super.mousePress(comp, x, y, buttons);
    }

    /**
     * Scrolls the component so that the coordinate x and y are visible.  Has
     * no effect if the component has no JViewport ancestor.
     *
     * @param comp the Component to scroll
     * @param x    the x coordinate to be visible
     * @param y    the y coordinate to be visible
     */
    protected void scrollToVisible(Component comp, int x, int y) {
        Rectangle rect = new Rectangle(x, y, 1, 1);
        scrollToVisible(comp, rect);
    }

    /** Invoke {@link JComponent#scrollRectToVisible(Rectangle)} on the given
        {@link JComponent} on the event dispatch thread.
    */
    protected void scrollRectToVisible(final JComponent jc,
                                       final Rectangle rect) {
        // Ideally, we'd use scrollbar commands to effect the scrolling,
        // but that gets really complicated for no real gain in function.
        // Fortunately, Swing's Scrollable makes for a simple solution.
        // NOTE: absolutely MUST wait for idle in order for the scroll to
        // finish, and the UI to update so that the next action goes
        // to the proper location within the scrolled component.
        invokeAndWait(new Runnable() {
            public void run() {
                jc.scrollRectToVisible(rect);
            }
        });
    }

    protected boolean isVisible(JComponent c, Rectangle rect) {
        Rectangle visible = c.getVisibleRect();
        return visible.contains(rect);
    }

    protected boolean isVisible(JComponent c, int x, int y) {
        Rectangle visible = c.getVisibleRect();
        return visible.contains(x, y);
    }

    /**
     * Scrolls the component so that the given rectangle is visible.  Has no
     * effect if the component has no JViewport ancestor.  When this method
     * returns, the requested rectangle's upper left corner will be visible
     * (i.e. no {@link #waitForIdle} is required.
     *
     * @param comp the Component to scroll
     * @param rect the Rectangle to make visible.
     */
    protected void scrollToVisible(Component comp, 
                                   final Rectangle rect) {
        final JComponent jc = (JComponent)comp;
        if (!isVisible(jc, rect)) {
            scrollRectToVisible(jc, rect);
            // Need to make at least the upper left corner of the requested
            // rectangle visible.
            if (!isVisible(jc, rect.x, rect.y)) {
                String msg = Strings.get("tester.JComponent.not_visible",
                                         new Object[] { 
                                             new Integer(rect.x),
                                             new Integer(rect.y), 
                                             jc,
                                         });
                throw new ActionFailedException(msg);
            }
        }
    }

    /** Make sure the given point is visible.  Note that this may have no
     * effect if the component is not actually in a scroll pane.
     */
    public void actionScrollToVisible(Component comp, ComponentLocation loc) {
        scrollToVisible(comp, loc.getBounds(comp));
        waitForIdle();
    }

    /** Make sure the given point is visible.  Note that this may have no
     * effect if the component is not actually in a scroll pane.
     */
    public void actionScrollToVisible(Component comp, int x, int y) {
        actionScrollToVisible(comp, new ComponentLocation(new Point(x, y)));
    }

    /** Make sure the given rectangle is visible.  Note that this may have no
     * effect if the component is not actually in a scroll pane.
     */
    public void actionScrollToVisible(Component comp, int x, int y, 
                                      int width, int height) {
        scrollToVisible(comp, new Rectangle(x, y, width, height));
        waitForIdle();
    }

    /** Invoke an action from the component's action map. */
    public void actionActionMap(Component comp, String name) {
        focus(comp, true);
        JComponent jc = (JComponent)comp;

        ActionMap am = jc.getActionMap();
        // On OSX/1.3.1, some action map keys are actions instead of strings.
        // On XP/1.4.1, all action map keys are strings.
        // If we can't look it up with the string key we saved, check all the
        // actions for a corresponding name.
        Object action = am.get(name);
        if (action == null) {
            Object[] keys = am.allKeys();
            for (int i=0;keys != null && i < keys.length;i++) {
                Object value = am.get(keys[i]);
                if ((value instanceof Action)) {
                    String aname = (String)
                        ((Action)value).getValue(Action.NAME);
                    if (aname != null && aname.equals(name)) {
                        action = value;
                        break;
                    }
                }
            }
        }
        if (action == null) {
            String available = "Available actions are the following:";
            Object[] names = am.allKeys();
            if (names != null) {
                Arrays.sort(names, new java.util.Comparator() {
                    public int compare(Object o1, Object o2) {
                        String n1 = o1.toString();
                        String n2 = o2.toString();
                        return n1.compareTo(n2);
                    }
                });
                for (int i=0;i < names.length;i++) {
                    available += "\n" + names[i];
                    if (!(names[i] instanceof String))
                        available += " (" + names[i].getClass() + ")";
                }
            }
            throw new AssertionFailedError("No such action '"
                                           + name + "'. " + available);
        }
        InputMap im = jc.getInputMap();
        KeyStroke[] events = im.allKeys();
        for (int i=0;events != null && i < events.length;i++) {
            KeyStroke ks = events[i];
            Object key = im.get(ks);
            // If the key is an action (OSX/1.3.1), grab the action name
            // instead 
            Log.debug("ks=" + ks + " key=" + key);
            if (key instanceof Action) {
                Object nm = ((Action)key).getValue(Action.NAME);
                if (nm != null)
                    key = nm;
            }
            if (name.equals(key)) {
                Log.debug("Generating keystroke " + ks
                          + " for action " + name);
                if (ks.getKeyCode() == KeyEvent.VK_UNDEFINED)
                    keyStroke(ks.getKeyChar());
                else 
                    key(ks.getKeyCode(), ks.getModifiers());
                waitForIdle();
                return;
            }
        }
        throw new ActionFailedException("No input event found for action key '"
                                        + name + "'");
    }

    /** Return a shared instance of JComponentTester. */
    public static JComponentTester getTester(JComponent c) {
        return (JComponentTester)ComponentTester.getTester(JComponent.class);
    }
}
