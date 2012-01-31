package abbot.util;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import com.windowtester.runtime.util.StringComparator;

import abbot.Log;
import abbot.Platform;
import abbot.finder.*;
import abbot.finder.matchers.ClassMatcher;
import abbot.tester.*;
import abbot.tester.Robot;

/** Various AWT utilities. */

public class AWT {

    public static int POPUP_TIMEOUT = 5000;
        
    private static Hierarchy hierarchy = new AWTHierarchy();

    static {
        String to = System.getProperty("abbot.finder.popup_timeout");
        if (to != null) {
            try {
                POPUP_TIMEOUT = Integer.parseInt(to);
            }
            catch(Exception e) {
            }
        }
    }

    private AWT() { }

    /** Return whether the given Component has only its default name set. */
    public static boolean hasDefaultName(Component c) {
        String name = getName(c);
        if (name == null)
            return true;

        if (c instanceof JComponent) {
            return (c instanceof JLayeredPane
                    && "null.layeredPane".equals(name))
                || (c instanceof JPanel
                    && ("null.glassPane".equals(name)
                        || "null.contentPane".equals(name)));
        }
        
        return (c instanceof Button
                && Regexp.stringMatch("button[0-9]+", name))
            || (c instanceof Canvas
                && Regexp.stringMatch("canvas[0-9]+", name))
            || (c instanceof Checkbox
                && Regexp.stringMatch("checkbox[0-9]+", name))
            || (c instanceof Choice
                && Regexp.stringMatch("choice[0-9]+", name))
            || (c instanceof Dialog
                && Regexp.stringMatch("dialog[0-9]+", name))
            || (c instanceof FileDialog
                && Regexp.stringMatch("filedlg[0-9]+", name))
            || (c instanceof Frame
                && Regexp.stringMatch("frame[0-9]+", name))
            || (c instanceof java.awt.List
                && Regexp.stringMatch("list[0-9]+", name))
            || (c instanceof Label
                && Regexp.stringMatch("label[0-9]+", name))
            || (c instanceof Panel
                && Regexp.stringMatch("panel[0-9]+", name))
            || (c instanceof Scrollbar
                && Regexp.stringMatch("scrollbar[0-9]+", name))
            || (c instanceof ScrollPane
                && Regexp.stringMatch("scrollpane[0-9]+", name))
            || (c instanceof TextArea
                && Regexp.stringMatch("text[0-9]+", name))
            || (c instanceof TextField
                && Regexp.stringMatch("textfield[0-9]+", name))
            || (c instanceof Window
                && Regexp.stringMatch("win[0-9]+", name));
    }

    /** Ensure the given action happens on the event dispatch thread.  Any
     * component modifications must be invoked this way.
     */
    public static void invokeAndWait(Runnable action) {
        if (EventQueue.isDispatchThread()) {
            action.run();
        }
        else {
            try {
                EventQueue.invokeAndWait(action);
            }
            catch(InterruptedException ie) {
                Log.warn(ie);
            }
            catch(java.lang.reflect.InvocationTargetException ite) {
                Log.warn(ite);
            }
        }
    }

    /** Ensure the given action happens on the event dispatch thread.  Any
     * component modifications must be invoked this way.  Note that this is
     * <b>not</b> the same as EventQueue.invokeLater, since if the current
     * thread is the dispatch thread, the action is invoked immediately.
     */
    public static void invokeAction(Runnable action) {
        if (EventQueue.isDispatchThread()) {
            action.run();
        }
        else {
            EventQueue.invokeLater(action);
        }
    }

    /** Expects to be invoked on the dispatch thread only. */
    private static List disable(Object root, List list) {
        if (Bugs.hasMenuDisableBug() && root instanceof JMenuBar) 
            return list;

        if (root instanceof Component) {
            if (root instanceof Frame) {
                MenuBar mb = ((Frame)root).getMenuBar();
                if (mb != null) {
                    for (int i=0;i < mb.getMenuCount();i++) {
                        disable(mb.getMenu(i), list);
                    }
                }
            }
            if (root instanceof Container) {
                Component[] children = ((Container)root).getComponents();
                for (int i=0;i < children.length;i++) {
                    disable(children[i], list);
                }
            }
            if (((Component)root).isEnabled()) {
                list.add(root);
                ((Component)root).setEnabled(false);
            }
        }
        else if (root instanceof MenuItem) {
            if (((MenuItem)root).isEnabled()) {
                if (root instanceof Menu) {
                    Menu menu = (Menu)root;
                    for (int i=0;i < menu.getItemCount();i++) {
                        disable(menu.getItem(i), list);
                    }
                }
                list.add(root);
                ((MenuItem)root).setEnabled(false);
            }
        }
        return list;
    }

    /** Restore the enabled state. */
    public static void reenableHierarchy(final List enabled) {
        invokeAndWait(new Runnable() {
             public void run() {
                 Iterator iter = enabled.iterator();
                 while (iter.hasNext()) {
                     Object o = iter.next();
                     if (o instanceof Component)
                         ((Component)o).setEnabled(true);
                     else if (o instanceof MenuItem)
                         ((MenuItem)o).setEnabled(true);
                 }
             }
        });
    }

    /** Disable a component hierarchy starting at the given component.
     * Returns a list of all components which used to be enabled, for use with
     * reenableHierarchy. 
     */ 
    public static List disableHierarchy(final Component root) { 
        final List list = new ArrayList();
        invokeAndWait(new Runnable() {
            public void run() {
                disable(root, list);
            }
        });
        return list;
    }

    /** Returns whether the menu component is on a MenuBar. */
    public static boolean isOnMenuBar(MenuComponent mc) {
        if (mc instanceof MenuBar)
            return true;
        return mc.getParent() instanceof MenuComponent
            && isOnMenuBar((MenuComponent)mc.getParent());
    }

    /** Returns the invoker, if any, of the given AWT menu component.  Returns
        null if the menu component is not attached to anything, or if it is
        within a MenuBar hierarchy.
    */
    public static Component getInvoker(MenuComponent mc) {
        if (isOnMenuBar(mc))
            return null;
        MenuContainer parent = mc.getParent();
        while (parent instanceof MenuComponent) {
            parent = ((MenuComponent)parent).getParent();
        }
        return parent instanceof Component ? (Component)parent : null;
    }

    /** Returns the invoker, if any, of the given component.  Returns null if
     * the component is not on a popup of any sort.
     */
    public static Component getInvoker(Component comp) {
        if (comp instanceof JPopupMenu)
            return ((JPopupMenu)comp).getInvoker();
        comp = comp.getParent();
        return comp != null ? getInvoker(comp) : null;
    }

    /** Similar to SwingUtilities.getWindowAncestor(), but returns the
     * component itself if it is a Window, or the invoker's window if on a
     * popup.
     */
    public static Window getWindow(Component comp) {
        if (comp == null)
            return null;
        if (comp instanceof Window)
            return (Window)comp;
        if (comp instanceof MenuElement) {
            Component invoker = getInvoker(comp);
            if (invoker != null)
                return getWindow(invoker);
        }
        return getWindow(hierarchy.getParent(comp));
    }

    /** Returns whether there is an AWT popup menu currently showing. */
    public static boolean isAWTPopupMenuBlocking() {
        // For now, just do a quick check to see if a PopupMenu is active on
        // w32.  Extend it if we find other common situations that might block
        // the EDT, but for now, keep it simple and restricted to what we've
        // run into.
        return Bugs.showAWTPopupMenuBlocks() && isAWTTreeLockHeld();
    }
    
    /** Returns whether the AWT Tree Lock is currently held. */
    private static boolean isAWTTreeLockHeld() {
        return isAWTTreeLockHeld(Toolkit.getDefaultToolkit().getSystemEventQueue());
    }

    /** Returns whether the AWT Tree Lock is currently held. */
    public static boolean isAWTTreeLockHeld(EventQueue eq) {
        Frame[] frames = Frame.getFrames();
        if (frames.length == 0)
            return false;

        // hack based on 1.4.2 java.awt.PopupMenu implementation,
        // which blocks the event dispatch thread while the popup is visible,
        // while holding the AWT tree lock

        // Start another thread which attempts to get the tree lock
        // If it can't get the tree lock, then there is a popup active in the
        // current tree.
        // Any component can provide the tree lock
        ThreadStateChecker checker =
            new ThreadStateChecker(frames[0].getTreeLock());
        try {
            synchronized(checker) {
                checker.start();
                if (!checker.started) {
                    checker.wait(30000); // avoid failure under heavy load
                    if (!checker.started)
                        throw new Error("Popup checking thread never started");
                }
            }
            // Wait a little bit for the checker to finish
            if (checker.isAlive())
                checker.join(100);
            return checker.isAlive();
        }
        catch(InterruptedException e) {
            return false;
        }
    }

    public static void dismissAWTPopup() {
        java.awt.Robot robot = Robot.getRobot();
        if (robot != null) {
            Component c = getFocusOwner();
            if (c != null) {
                Window w = getWindow(c);
                if (w != null && w.isShowing()) {
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                }
            }
        }
        else {
            Log.warn("The current system configuation can not automatically dismiss an AWT popup");
        }
    }

    /** Returns whether the given MenuComponent is on a top-level AWT popup
        (that is, <i>not</i> under a MenuBar. 
    */
    public static boolean isOnPopup(MenuComponent mc) {
        MenuContainer parent = mc.getParent();
        while (parent instanceof MenuComponent) {
            if (parent instanceof MenuBar)
                return false;
            parent = ((MenuComponent)parent).getParent();
        }
        return true;
    }

    /** Returns whether the given component is on a top-level popup.  A
     * top-level popup is one generated by a popup trigger, which means popups
     * generated from a JMenu are not included.
     */
    public static boolean isOnPopup(Component comp) {
        boolean isWrapper = isTransientPopup(comp);
        Component invoker = getInvoker(comp);
        boolean isOnJMenu = invoker instanceof JMenu
            && invoker.getParent() instanceof JMenuBar;
        return isWrapper || (invoker != null && !isOnJMenu);
    }

    /** Returns whether the given component is a heavyweight popup, that is, a
        container for a JPopupMenu that is implemented with a heavyweight
        component (usually a Window).
    */
    public static boolean isHeavyweightPopup(Component c) {
        if (c instanceof Window
            && !(c instanceof Dialog)
            && !(c instanceof Frame)) {
            String name = getName(c);
            String cname = c.getClass().getName();
            return ("###overrideRedirect###".equals(name)
                    || "###focusableSwingPopup###".equals(name)
                    // These classes are known to be heavyweight popups
                    // javax.swing.DefaultPopupFactory$WindowPopup (1.3)
                    || cname.indexOf("PopupFactory$WindowPopup") != -1
                    // javax.swing.Popup.HeavyWeightWindow (1.4)
                    || cname.indexOf("HeavyWeightWindow") != -1);
        }
        return false;
    }

    // Work around some components throwing exceptions if getName is 
    // called prematurely
    private static String getName(Component c) {
        try {
            return c.getName();
        }
        catch(Throwable e) {
            Log.warn(e);
            return null;
        }
    }

    /** Returns whether the given component is a lightweight popup, that is, a
        container for a JPopupMenu that is implemented with a lightweight
        component (usually JPanel).
    */
    public static boolean isLightweightPopup(Component c) {
        if (c instanceof JPanel) {
            Window w = SwingUtilities.getWindowAncestor(c);
            if (w != null && isHeavyweightPopup(w))
                return false;
            JPanel panel = (JPanel)c;
            Container parent = panel.getParent();
            if (parent != null) {
                if (parent instanceof JLayeredPane) {
                    JLayeredPane lp = (JLayeredPane)parent;
                    int layer = JLayeredPane.POPUP_LAYER.intValue();
                    if (lp.getLayer(panel) == layer)
                        return true;
                }
            }
            return panel.getComponentCount() == 1
                && panel.getComponents()[0] instanceof JPopupMenu;
        }
        return false;
    }

    /** Returns whether the given Component is the content pane for a
        {@link RootPaneContainer}. 
        @see javax.swing.RootPaneContainer#getContentPane
    */
    public static boolean isContentPane(Component c) {
        if (c.getParent() instanceof JLayeredPane) {
            JLayeredPane p = (JLayeredPane)c.getParent();
            if (p.getParent() instanceof JRootPane) {
                return ((JRootPane)p.getParent()).getContentPane() == c;
            }
            else {
                int layer = JLayeredPane.FRAME_CONTENT_LAYER.intValue();
                return p.getLayer(c) == layer
                    && !(c instanceof JMenuBar);
            }
        }
        return false;
    }

    /** Returns whether the given Component is the Glass Pane for a
        {@link JRootPane}. 
        @see javax.swing.JRootPane#getGlassPane
    */
    public static boolean isGlassPane(Component c) {
        if (c.getParent() instanceof JRootPane) {
            JRootPane p = (JRootPane)c.getParent();
            return p.getGlassPane() == c;
        }
        return false;
    }


    /** Return whether the given component is part of the transient wrapper
     * around a popup.
     */
    public static boolean isTransientPopup(Component c) {
        return isLightweightPopup(c) 
            || isHeavyweightPopup(c);
    }

    private static boolean containsToolTip(Component c) {
        if (c instanceof JToolTip)
            return true;
        if (c instanceof Container) {
            Component[] kids = ((Container)c).getComponents();
            for (int i=0;i < kids.length;i++) {
                if (containsToolTip(kids[i]))
                    return true;
            }
        }
        return false;
    }

    /** Return whether the given component is part of the transient wrapper
        around a tooltip.
    */
    public static boolean isToolTip(Component c) {
        return isTransientPopup(c)
            && containsToolTip(c);
    }

    /** Return whether the given component is part of an internal frame's LAF
        decoration.
    */
    public static boolean isInternalFrameDecoration(Component c) {
        Component parent = c.getParent();
        return (parent instanceof JInternalFrame
                && !(c instanceof JRootPane))
            || (parent != null
                && (parent.getParent() instanceof JInternalFrame)
                && (!(parent instanceof JRootPane)));
    }

    private static final boolean POPUP_ON_BUTTON2 = Platform.isMacintosh();

    /** Returns the InputEvent mask for the popup trigger button. */
    public static int getPopupMask() {
        return POPUP_ON_BUTTON2
            ? InputEvent.BUTTON2_MASK : InputEvent.BUTTON3_MASK;
    }
    /** Returns the InputEvent mask for the tertiary button. */
    public static int getTertiaryMask() {
        return POPUP_ON_BUTTON2
            ? InputEvent.BUTTON3_MASK : InputEvent.BUTTON2_MASK;
    }
    /** Returns whether the platform registers a popup on mouse press. */
    public static boolean getPopupOnPress() {
        return Platform.isWindows();
    }

    private static final PopupMenu[] NO_POPUPS = new PopupMenu[0];
    /** Return all AWT popup menus associated with the given component. */
    public static PopupMenu[] getPopupMenus(Component c) {
        // Here's a nice little hack to get access to the popup list on the
        // given invoker...
        try {
            Field field = Component.class.getDeclaredField("popups");
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Vector popups = (Vector)field.get(c);
            field.setAccessible(accessible);
            if (popups != null)
                return (PopupMenu[])popups.toArray(new PopupMenu[popups.size()]);
            return NO_POPUPS;
        }
        catch(NoSuchFieldException e) {
            // not gonna happen
            throw new Error("No field named 'popups' in class Component");
        }
        catch(IllegalAccessException e) {
            // neither should this
            throw new Error("Can't access popup for component " + c);
        }
    }

    /** Returns all MenuItems matching the given label or path which are on
        PopupMenus on the given Component. */
    public static MenuItem[] findAWTPopupMenuItems(Component parent,
                                                   String path) {
        PopupMenu[] popups = getPopupMenus(parent);
        ArrayList list = new ArrayList();
        for (int i=0;i < popups.length;i++) {
            list.addAll(findMenuItems(popups[i], path, true));
        }
        return (MenuItem[])list.toArray(new MenuItem[list.size()]);
    }

    /** Returns all MenuItems matching the given label or path which are found
        in the given Frame's MenuBar. */
    public static MenuItem[] findAWTMenuItems(Frame frame, String path) {
        MenuBar mb = frame.getMenuBar();
        if (mb != null) {
            Collection items = findMenuItems(mb, path, true);
            return (MenuItem[])items.toArray(new MenuItem[items.size()]);
        }
        return new MenuItem[0];
    }

    /** Returns a unique path to the given MenuItem. */
    public static String getPath(MenuItem item) {
        String path = getPath(item, false);
        if (isOnPopup(item)
            && findAWTPopupMenuItems(getInvoker(item), path).length > 1) {
            path = getPath(item, true);
        }
        return path;
    }

    /** Returns a unique path to the given MenuItem.  If on a PopupMenu,
        optionally include the PopupMenu name. */
    private static String getPath(MenuItem item, boolean includePopupName) {
        MenuContainer invoker = getInvoker(item);
        MenuContainer root = invoker;
        MenuContainer top;
        if (invoker == null) {
            // Find the top-most Menu above this MenuItem
            top = item.getParent();
            while (top instanceof Menu
                   && !(((Menu)top).getParent() instanceof MenuBar)) {
                top = ((Menu)top).getParent();
            }
            if (top == null)
                throw new RuntimeException("MenuItem is not attached to the hierarchy");
            root = ((Menu)top).getParent();
        }
        else {
            // Find the containing PopupMenu
            top = item.getParent();
            while (top instanceof Menu
                   && !(((Menu)top).getParent() instanceof Component)) {
                top = ((Menu)top).getParent();
            }
        }

        // Return a path to the item, starting at the first top level Menu 
        String path = item.getLabel();
        MenuItem mi = item;
        while (mi.getParent() != top) {
            mi = (MenuItem)mi.getParent();
            path = mi.getLabel() + "|" + path;
        }
        if (top instanceof PopupMenu) {
            if (includePopupName) {
                // If the popup has the default name, use its index
                // on the invoker instead.  
                String name = ((PopupMenu)top).getName();
                if (Regexp.stringMatch("popup[0-9]+", name)) {
                    PopupMenu[] all = getPopupMenus((Component)invoker);
                    for (int i=0;i < all.length;i++) {
                        if (all[i] == top) {
                            // Make it different from the default name
                            name = "popup#" + i;
                            break;
                        }
                    }
                }
                path = name + "|" + path;
            }
        }        
        else {
            path = ((Menu)top).getLabel() + "|" + path;
        }
        Log.debug("Path for " + item + " is " + path);
        return path;
    }

    /** Returns all AWT menu items found with the given label; if matchPath is
        set then the MenuItem path is examined as well as the label.
    */
    private static Collection findMenuItems(MenuContainer mc,
                                            String path,
                                            boolean matchPath) {
        if (matchPath) 
            Log.debug("Searching for '" + path + "' on '" + mc);
        ArrayList list = new ArrayList();
        if (mc instanceof MenuBar) {
            for (int i=0;i < ((MenuBar)mc).getMenuCount();i++) {
                Menu menu = ((MenuBar)mc).getMenu(i);
                Log.debug("Scanning '" + menu + "'");
                list.addAll(findMenuItems(menu, path, matchPath));
            }
        }
        else if (mc instanceof Menu) {
            for (int i=0;i < ((Menu)mc).getItemCount();i++) {
                MenuItem mi = ((Menu)mc).getItem(i);
                if (mi instanceof MenuContainer) {
                    Log.debug("Scanning '" + mi + "'");
                    list.addAll(findMenuItems((MenuContainer)mi, path,
                                              matchPath));
                }
                else if (path.equals(mi.getLabel())) {
                    Log.debug("Found '" + mi + "'");
                    list.add(mi);
                }
                else if (matchPath) {
                    if (StringComparator.matches(getPath(mi, false), path)
                        || StringComparator.matches(getPath(mi, true), path)) {
                        Log.debug("Found (path) '" + mi + "'");
                        list.add(mi);
                    }
                    // TODO: fuzzy matching on the unique id (i.e. drop off or
                    // add the popup menu name.
                }
            }
        }
        return list;
    }

    /** Return the focus owner under the given Window. 
        As of 1.4.x, components will report that they do not have focus
        if asked from a different AppContext than their own.  Account
        for that here.
    */
    public static Component getFocusOwner() {
        try {
            Class cls = Class.forName("java.awt.KeyboardFocusManager");
            Field field = cls.getDeclaredField("focusOwner");
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Component c = (Component)field.get(null);
            field.setAccessible(accessible);
            return c;
        }
        catch(Exception e) {
            if(!(e instanceof ClassNotFoundException))
                Log.log(e);
            // FIXME this lookup doesn't seem to work on 1.3!
            Iterator iter = new AWTHierarchy().getRoots().iterator();
            Component focus = null;
            while (iter.hasNext()) {
                Window w = (Window)iter.next();
                if (w.isShowing() && (focus = getFocusOwner(w)) != null)
                    break;
            }
            return focus;
        }
    }

    private static Component getFocusOwner(Window w) {
        Component focus = w.getFocusOwner();
        if (focus == null) {
            Window[] owned = w.getOwnedWindows();
            for (int i=0;i < owned.length;i++) {
                if ((focus = owned[i].getFocusOwner()) != null)
                    return focus;
            }
        }
        return focus;
    }

//NOT Supported in Mac Java5+
//    /** For debugging purposes only. */
//    public static AppContext getAppContext(Component c) {
//        try {
//            Field field = Component.class.getDeclaredField("appContext");
//            boolean accessible = field.isAccessible();
//            field.setAccessible(true);
//            AppContext appContext = (AppContext)field.get(c);
//            field.setAccessible(accessible);
//            return appContext;
//        }
//        catch(Exception e) {
//            Log.warn(e);
//            return null;
//        }
//    }

    /** WARNING: This uses 1.3/1.4 implementation details. */
    public static boolean eventTypeEnabled(Component c, int id) {
        // certain AWT components should have events enabled, even if they
        // claim not to.
        // NOTE: Checkbox could be included here, obviating the need for
        // CheckboxTester's AWT-mode function.
        if (c instanceof Choice)
            return true;
        try {
            AWTEvent ev = new AWTEvent(c, id) { };
            Method m = Component.class.getDeclaredMethod("eventEnabled",
                                                         new Class[] {
                                                             AWTEvent.class
                                                         });
            m.setAccessible(true);
            Boolean b = (Boolean)m.invoke(c, new Object[] { ev });
            return b.booleanValue();
        }
        catch(Exception e) {
            Log.warn(e);
            return true;
        }
    }

    /** Is the given component the default Swing hidden frame? */
    public static boolean isSharedInvisibleFrame(Component c) {
        return c == JOptionPane.getRootFrame();
    }

    public static boolean isAppletViewerFrame(Component c) {
        return c.getClass().getName().equals("sun.applet.AppletViewer");
    }

    private static final Matcher POPUP_MATCHER = 
        new ClassMatcher(JPopupMenu.class, true);

    /** Returns the currently active popup menu, if any.  If no popup is
        currently showing, returns null.
    */
    public static JPopupMenu getActivePopupMenu() {
        try {
            return (JPopupMenu)BasicFinder.getDefault().find(POPUP_MATCHER);
        }
        catch(ComponentSearchException e) {
            return null;
        }
    }

    /** Find the currently active Swing popup menu, if any, waiting up to
        POPUP_TIMEOUT ms.  Returns null if no popup found.
    */
    public static JPopupMenu findActivePopupMenu() {
        JPopupMenu popup = getActivePopupMenu();
        if (popup == null
            && !SwingUtilities.isEventDispatchThread()) {
            long now = System.currentTimeMillis();
            while ((popup = getActivePopupMenu()) == null) {
                if (System.currentTimeMillis() - now > POPUP_TIMEOUT) {
                    break;
                }
                try { Thread.sleep(100); } 
                catch(Exception e) { }
            }
        }
        return popup;
    }

    /** Returns the location of the given components in screen
        coordinates. Avoids lockup if an AWT popup menu is showing, which
        means it holds the AWT tree lock, which Component.getLocationOnScreen
        requires. 
    */
    public static Point getLocationOnScreen(Component c) {
        if (isAWTTreeLockHeld()) {
            if (!c.isShowing())
                throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
            Point loc = new Point(c.getLocation());
            if (!(c instanceof Window)) {
                Container parent = c.getParent();
                if (parent == null)
                    throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
                Point ploc = getLocationOnScreen(parent);
                loc.translate(ploc.x, ploc.y);
            }
            return loc;
        }
        else {
            return new Point(c.getLocationOnScreen());
        }
    }

    /** Return whether the given component is part of a transient dialog.
     * This includes dialogs generated by JFileChooser, JOptionPane,
     * JColorChooser, and ProgressMonitor.<p>
     * Note that it is possible to use JOptionPane.createDialog to create a
     * reusable dialog, so just because it's transient doesn't mean it will be
     * disposed of when it is hidden.<p>
     * Note that this won't detect transient Dialogs after their components
     * have been reassigned to a new transient Dialog.
     */
    public static boolean isTransientDialog(Component c) {
        if (c instanceof Window) {
            if (c instanceof JDialog) {
                Container contentPane = ((JDialog)c).getContentPane();
                Component[] kids = contentPane.getComponents();
                if (kids.length == 1) {
                    return kids[0] instanceof JOptionPane
                        || kids[0] instanceof JFileChooser
                        || kids[0] instanceof JColorChooser;
                }
            }
        }
        else if (!(c instanceof JOptionPane // also covers ProgressMonitor
                   || c instanceof JFileChooser
                   || c instanceof JColorChooser)) {
            Container parent = c.getParent();
            return parent != null && isTransientDialog(parent);
        }
        return false;
    }

    /** Returns the Applet descendent of the given Container, if any. */
    public static Applet findAppletDescendent(Container c) {
        try {
            return (Applet)BasicFinder.getDefault().
                find(c, new ClassMatcher(Applet.class));
        }
        catch(ComponentSearchException e) {
            return null;
        }
    }

    /** Return whether this is the tertiary button, considering primary to be
     * button1 and secondary to be the popup trigger button.
     */
    public static boolean isTertiaryButton(int mods) {
        return ((mods & AWTConstants.BUTTON_MASK) != InputEvent.BUTTON1_MASK)
            && ((mods & AWTConstants.POPUP_MASK) == 0);
    }

    /** Convert the string representation into the actual modifier mask. */
    public static int getModifiers(String mods) {
        int value = 0;
        if (mods != null && !mods.equals("")) {
            StringTokenizer st = new StringTokenizer(mods, "| ");
            while (st.hasMoreTokens()) {
                String flag = st.nextToken();
                // Allow short-form modifiers
                if (!flag.endsWith("_MASK"))
                    flag = flag + "_MASK";
                if (AWTConstants.POPUP_MODIFIER.equals(flag))
                    value |= AWTConstants.POPUP_MASK;
                else if (AWTConstants.TERTIARY_MODIFIER.equals(flag))
                    value |= AWTConstants.TERTIARY_MASK;
                else if (!flag.equals("0"))
                    value |= Reflector.getFieldValue(InputEvent.class, flag);
            }
        }
        return value;
    }

    private static String getModifiers(int flags, boolean isMouse) {
        // On a mac, ALT+BUTTON1 means BUTTON2; META+BUTTON1 means BUTTON3
        int macModifiers = 
            InputEvent.CTRL_MASK|InputEvent.ALT_MASK|InputEvent.META_MASK;
        boolean isMacButton = isMouse
            && Platform.isMacintosh()
            && (flags & macModifiers) != 0;
        String mods = "";
        String or = "";
        if ((flags & InputEvent.ALT_GRAPH_MASK) != 0) {
            mods += or + "ALT_GRAPH_MASK"; or = "|";
            flags &= ~InputEvent.ALT_GRAPH_MASK;
        }
        if ((flags & InputEvent.BUTTON1_MASK) != 0 && !isMacButton) {
            mods += or + "BUTTON1_MASK"; or = "|";
            flags &= ~InputEvent.BUTTON1_MASK;
        }
        // Mask for ALT is the same as MB2
        if ((flags & InputEvent.ALT_MASK) != 0 && !isMacButton && !isMouse) {
            mods += or + "ALT_MASK"; or = "|";
            flags &= ~InputEvent.ALT_MASK;
        }
        // Mac uses ctrl modifier to get MB2
        if ((flags & InputEvent.CTRL_MASK) != 0 && !isMacButton) {
            mods += or + "CTRL_MASK"; or = "|";
            flags &= ~InputEvent.CTRL_MASK;
        }
        // Mask for META is the same as MB3
        if ((flags & InputEvent.META_MASK) != 0 && !isMacButton && !isMouse) {
            mods += or + "META_MASK"; or = "|";
            flags &= ~InputEvent.META_MASK;
        }
        if ((flags & AWTConstants.POPUP_MASK) != 0) {
            mods += or + "POPUP_MASK"; or = "|";
            flags &= ~AWTConstants.POPUP_MASK;
        }
        if ((flags & AWTConstants.TERTIARY_MASK) != 0) {
            mods += or + "TERTIARY_MASK"; or = "|";
            flags &= ~AWTConstants.TERTIARY_MASK;
        }
        if ((flags & InputEvent.SHIFT_MASK) != 0) {
            mods += or + "SHIFT_MASK"; or = "|";
            flags &= ~InputEvent.SHIFT_MASK;
        }
        // Empty strings are confusing and invisible; make it explicit
        if ("".equals(mods))
            mods = "0";
        return mods;
    }

    public static String getKeyModifiers(int flags) {
        return getModifiers(flags, false);
    }

    public static String getMouseModifiers(int flags) {
        return getModifiers(flags, true);
    }

    /** Convert the integer modifier flags into a string representation. */
    public static String getModifiers(InputEvent event) {
        return getModifiers(event.getModifiers(), 
                            event instanceof MouseEvent);
    }

    public static String getKeyCode(int keycode) {
        return Reflector.getFieldName(KeyEvent.class, keycode, "VK_");
    }

    public static int getKeyCode(String code) {
        return Reflector.getFieldValue(KeyEvent.class, code);
    }

    public static boolean isModifier(int keycode) {
        switch(keycode) {
        case KeyEvent.VK_META:
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_ALT_GRAPH:
        case KeyEvent.VK_CONTROL:
        case KeyEvent.VK_SHIFT:
            return true;
        default:
            return false;
        }
    }

    public static int keyCodeToMask(int code) {
        switch(code) {
        case KeyEvent.VK_META: return InputEvent.META_MASK;
        case KeyEvent.VK_ALT: return InputEvent.ALT_MASK;
        case KeyEvent.VK_ALT_GRAPH: return InputEvent.ALT_GRAPH_MASK;
        case KeyEvent.VK_CONTROL: return InputEvent.CTRL_MASK;
        case KeyEvent.VK_SHIFT: return InputEvent.SHIFT_MASK;
        default:
            throw new IllegalArgumentException("Keycode is not a modifier: "
                                               + code);
        }
    }

    /** Convert the given modifier event mask to the equivalent key code. */
    public static int maskToKeyCode(int mask) {
        switch(mask) {
        case InputEvent.META_MASK: return KeyEvent.VK_META;
        case InputEvent.ALT_MASK: return KeyEvent.VK_ALT;
        case InputEvent.ALT_GRAPH_MASK: return KeyEvent.VK_ALT_GRAPH;
        case InputEvent.CTRL_MASK: return KeyEvent.VK_CONTROL;
        case InputEvent.SHIFT_MASK: return KeyEvent.VK_SHIFT;
        default:
            throw new IllegalArgumentException("Unrecognized mask '"
                                               + mask + "'");
        }
    }

    // Try to lock the AWT tree lock; returns immediately if it can
    private static class ThreadStateChecker extends Thread {
        public boolean started;
        private Object lock;
        public ThreadStateChecker(Object lock) {
            super("thread state checker");
            setDaemon(true);
            this.lock = lock;
        }
        public void run() {
            synchronized(this) {
                started = true;
                notifyAll();
            }
            synchronized(lock) {
            }
        }
    }
}
