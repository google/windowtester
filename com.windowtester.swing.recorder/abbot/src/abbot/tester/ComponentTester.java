package abbot.tester;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;

import abbot.BugReport;
import abbot.Log;
import abbot.WaitTimedOutError;
import abbot.finder.AWTHierarchy;
import abbot.finder.BasicFinder;
import abbot.finder.ComponentNotFoundException;
import abbot.finder.ComponentSearchException;
import abbot.finder.Hierarchy;
import abbot.finder.Matcher;
import abbot.finder.MultipleComponentsFoundException;
import abbot.finder.matchers.JMenuItemMatcher;
import abbot.finder.matchers.WindowMatcher;
import abbot.i18n.Strings;
import abbot.script.ComponentReference;
import abbot.script.Condition;
import abbot.util.*;
import abbot.util.Bugs;
import abbot.util.WeakAWTEventListener;

/** Provides basic programmatic operation of a {@link Component} and related
    UI objects such as windows, menus and menu bars throuh action methods.
    Also provides some useful assertions about properties of a {@link
    Component}.  
    <p>
    There are two sets of event-generating methods.  The internal, protected
    methods inherited from {@link abbot.tester.Robot abbot.tester.Robot} are
    for normal programmatic use within derived Tester classes.  No event queue
    synchronization should be performed except when modifying a component for
    which results are required for the action itself.
    <p>
    The public <code>actionXXX</code> functions are meant to be invoked from a
    script or directly from a hand-written test.  These actions are
    distinguished by name, number of arguments, and by argument type.  The
    actionX methods will be synchronized with the event dispatch thread when
    invoked, so you should only do synchronization with waitForIdle when you
    depend on the results of a particular event prior to sending the next one
    (e.g. scrolling a table cell into view before selecting it).
    All public action methods should ensure that the actions they
    trigger are finished on return, or will be finished before any subsequent
    actions are requested.
    <p>
    <i>Action</i> methods generally represent user-driven actions such
    as menu selection, table selection, popup menus, etc.  All actions should
    have the following signature:
    <blockquote>
    <code>public void actionSpinMeRoundLikeARecord(Component c, ...);</code>
    <code>public void actionPinchMe(Component c, ComponentLocation loc);</code>
    </blockquote>
    <p>
    It is essential that the argument is of type {@link Component}; if you use
    a more-derived class, then the actual invocation becomes ambiguous since
    method parsing doesn't attempt to determine which identically-named method
    is the most-derived.
    <p>
    The {@link ComponentLocation} abstraction allows all derived tester
    classes to inherit click, popup menu, and drag variants without having to
    explicitly define new methods for component-specific substructures.  The
    new class need only define the {@link #parseLocation(String)} method,
    which should return a location specific to the component in question.
    <p> 
    <i>Assertions</i> are either independent of any component (and should be
    implemented in this class), or take a component as the first argument, and
    perform some check on that component.  All assertions should have one of
    the following signatures:
    <blockquote> 
    <code>public boolean assertMyself(...);</code><br>
    <code>public boolean assertBorderIsAtrociouslyUgly(Component c, ...);</code>
    </blockquote>
    Note that these assertions do not throw exceptions but rather return a
    <code>boolean</code> value indicating success or failure.  Normally these
    assertions will be wrapped by an abbot.script.Assert step if you want to
    cause a test failure, or you can manually throw the proper failure
    exception if the method returns false.
    <p>
    <i>Property checks</i> may also be implemented in cases where the component
    "property" might not be readily available or easily comparable, e.g.
    see {@link abbot.tester.JPopupMenuTester#getMenuLabels(Component)}.
    <blockquote>
    <code>public Object getHairpiece(Component c);</code><br>
    <code>public boolean isRighteouslyIndignant(Component c);</code>
    </blockquote>
    Any non-property methods with the property signature, should be added to
    the {@link #IGNORED_METHODS} set, since property-like methods are scanned
    dynamically to populate the {@link abbot.editor.ScriptEditor editor}'s
    action menus. 
    <p>
    <a name=Customization>
    <h2>Extending ComponentTester</h2>
    Following are the steps required to implement a Tester object for a custom
    class.
    <ul>
    <li><h3>Create the Tester Class</h3>
    Derive from this class to implement actions and assertions specific to
    a given component class.  Testers for any classes found in the JRE
    (i.e. in the {@link java.awt} or {@link javax.swing} packages) should be
    in the {@link abbot.tester abbot.tester} package.  Extensions (testers for
    any <code>Component</code> subclasses not found in the JRE) must be in the
    <code>abbot.tester.extensions</code> package and be
    named the name of the <code>Component</code> subclass followed by "Tester".
    For example, the {@link javax.swing.JButton javax.swing.JButton} tester
    class is {@link JButtonTester abbot.tester.JButtonTester}, and a tester
    for <code>org.me.PR0NViewer</code> would be
    <code>abbot.tester.extensions.PR0NViewerTester</code>.  
    <li><h3>Add Action Methods</h3>
    Add <code>action</code> methods which effect user actions.
    See the section on <a href=Naming>naming conventions</a> below.
    <li><h3>Add Assertion Methods</h3>
    Add <code>assert</code> methods to access attributes not readily available
    as properties<br> 
    <li><h3>Add Substructure Support</h3>
    Add a corresponding <code>ComponentLocation</code> implementation to
    handle any substructure present in your <code>Component</code>.
    See {@link ComponentLocation} for details.
    <li><h3>Add Substructure-handling Methods</h3>
      <ul>
      <li>{@link #parseLocation(String)}<br>
      Convert the given <code>String</code> into an instance of
      {@link ComponentLocation} specific to your Tester.  Here is an example
      implementation from {@link JListTester}:<br>
      <code><pre>
      public ComponentLocation parseLocation(String encoded) {
          return new JListLocation().parse(encoded);
      }
      </pre></code>
      <li>{@link #getLocation(Component,Point)}<br>
      Use the given <code>Point</code> to create a {@link ComponentLocation}
      instance appropriate for the <code>Component</code> substructure
      available at that location.  For example, on a {@link javax.swing.JList},
      you would return a {@link JListLocation} based on the
      stringified value at that location, the row/index at that
      location, or the raw <code>Point</code>,
      in order of preference.  If a stringified value is unavailable,
      fall back to an indexed position; if that is not possible (if, for
      instance, the location is outside the list contents), return a
      {@link ComponentLocation} based on the raw <code>Point</code>.
      </ul>
    <li><h3>Set Editor Properties</h3>
    Add-on tester classes should set the following system properties so that
    the actions provided by their tester can be properly displayed in the
    script editor.  For an action <code>actionWiggle</code> provided by class
    <code>abbot.tester.extensions.PR0NViewerTester</code>, the following
    properties should be defined:<br>
    <ul>
    <li><code>actionWiggle.menu</code> short name for Insert menu
    <li><code>actionWiggle.desc</code> short description (optional)
    <li><code>actionWiggle.icon</code> icon for the action (optional)
    <li><code>PR0NViewerTester.actionWiggle.args</code> javadoc-style
    description of method, displayed when asking the user for its arguments
    </ul>
    Since these properties are global, if your Tester class defines a method
    which is also defined by another Tester class, you must supply the class
    name as a prefix to the property.   
    </ul>
    <p>
    <a name=MethodNaming>
    <h2>Method Naming Conventions</h2>
    Action methods should be named according to the human-centric action that
    is being performed if at all possible.  For example, use
    <code>actionSelectRow</code> in a List rather than
    <code>actionSelectIndex</code>.  If there's no common usage, or if the
    usage is too vague or diverse, use the specific terms used in code. 
    For example, {@link JScrollBarTester} uses
    {@link JScrollBarTester#actionScrollUnitUp(Component) actionScrollUnitUp()}
    and 
    {@link JScrollBarTester#actionScrollBlockUp(Component) actionScrollBlockUp()};
    since there is no common language usage for these
    concepts (line and page exist, but are not appropriate if what is scrolled
    is not text). 
    <p>
    When naming a selection method, include the logical substructure target of
    the selection in the name (e.g.
    {@link JTableTester#actionSelectCell(Component,JTableLocation)}
    JTableTester.actionSelectCell()),
    since some components may have more than one type of selectable item
    within them.  
*/
public class ComponentTester extends Robot {

    /** Add any method names here which should <em>not</em> show up in a
        dynamically generated list of property methods.
    */
    protected static Set IGNORED_METHODS = new HashSet();

    static {
        // Omit from method lookup deprecated methods or others we want to
        // ignore 
        IGNORED_METHODS.addAll(Arrays.asList(new String[] {
            "actionSelectAWTMenuItemByLabel",
            "actionSelectAWTPopupMenuItemByLabel",
            "getTag", "getTester", "isOnPopup", "getLocation",
        }));
    }

    /** Maps class names to their corresponding Tester object. */
    private static HashMap testers = new HashMap();

    /** Establish the given ComponentTester as the one to use for the given
     * class.  This may be used to override the default tester for a given
     * core class.  Note that this will only work with components loaded by
     * the framework class loader, not those loaded by the class loader for
     * the code under test.
     */
    public static void setTester(Class forClass, ComponentTester tester) {
        testers.put(forClass.getName(), tester);
    }

    /** Return a shared instance of the appropriate
        <code>ComponentTester</code> for the given {@link Component}.<p>
        This method is primarily used by the scripting system, since the
        appropriate <code>ComponentTester</code> for a given {@link Component}
        is not known <i>a priori</i>.  Coded unit tests should generally just
        create an instance of the desired type of <code>ComponentTester</code>
        for local use. 
    */
    public static ComponentTester getTester(Component comp) {
        return comp != null ? getTester(comp.getClass())
            : getTester(Component.class);
    }

    /** Return a shared instance of the corresponding
        <code>ComponentTester</code> object for the given {@link Component}
        class, chaining up the inheritance tree if no specific tester is found
        for that class.<p> 
        The abbot tester package is searched first, followed by the tester
        extensions package.<p>
        This method is primarily used by the scripting system, since the
        appropriate <code>ComponentTester</code> for a given Component is not
        known <i>a priori</i>.  Coded unit tests should generally just create
        an instance of the desired type of <code>ComponentTester</code> for
        local use. 
    */
    public static ComponentTester getTester(Class componentClass) {
        String className = componentClass.getName();
        ComponentTester tester = (ComponentTester)testers.get(className);
        if (tester == null) {
            if (!Component.class.isAssignableFrom(componentClass)) {
                String msg = "Class " + className
                    + " is not derived from java.awt.Component";
                throw new IllegalArgumentException(msg);
            }
            Log.debug("Looking up tester for " + componentClass);
            String testerName = simpleClassName(componentClass) + "Tester";
            Package pkg = ComponentTester.class.getPackage();
            String pkgName = "";
            if (pkg == null) {
                Log.warn("ComponentTester.class has null package; "
                         + "the class loader is likely flawed: "
                         + ComponentTester.class.getClassLoader()
                         + ", "
                         + Thread.currentThread().getContextClassLoader(),
                         Log.FULL_STACK);
                pkgName = "abbot.tester";
            }
            else {
                pkgName = pkg.getName();
            }
            if (className.startsWith("javax.swing.")
                || className.startsWith("java.awt.")) {
                tester = findTester(pkgName + "." + testerName,
                                    componentClass);
            }
            if (tester == null) {
                tester = findTester(pkgName + ".extensions." + testerName,
                                    componentClass);
                if (tester == null) {
                    tester = getTester(componentClass.getSuperclass());
                }
            }
            if (tester != null && !tester.isExtension()) {
                // Only cache it if it's part of the standard framework,
                // but cache it for every level that we looked up, so we
                // don't repeat the effort.
                testers.put(componentClass.getName(), tester);
            }
        }

        return tester;
    }

    /** Return whether this tester is an extension. */
    public final boolean isExtension() {
        return getClass().getName().startsWith("abbot.tester.extensions");
    }

    /** Look up the given class, using special class loading rules to maintain
        framework consistency. */
    private static Class resolveClass(String testerName, Class componentClass)
        throws ClassNotFoundException {
        // Extension testers must be loaded in the context of the code under
        // test.
        Class cls;
        if (testerName.startsWith("abbot.tester.extensions")) {
            cls = Class.forName(testerName, true,
                                componentClass.getClassLoader());
        }
        else {
            cls = Class.forName(testerName);
        }
        Log.debug("Loaded class " + testerName + " with "
                  + cls.getClassLoader());
        return cls;
    }

    /** Look up the given class with a specific class loader. */
    private static ComponentTester findTester(String testerName,
                                              Class componentClass) {
        ComponentTester tester = null;
        Class testerClass = null;
        try {
            testerClass = resolveClass(testerName, componentClass);
            tester = (ComponentTester)testerClass.newInstance();
        }
        catch(InstantiationException ie) {
            Log.warn(ie);
        }
        catch(IllegalAccessException iae) {
            Log.warn(iae);
        }
        catch(ClassNotFoundException cnf) {
            //Log.debug("Class " + testerName + " not found");
        }
        catch(ClassCastException cce) {
            throw new BugReport("Class loader conflict: environment "
                                + ComponentTester.class.getClassLoader()
                                + " vs. " + testerClass.getClassLoader());
        }

        return tester;
    }

    /** Derive a tag from the given accessible context if possible, or return
     * null.
     */
    protected String deriveAccessibleTag(AccessibleContext context) {
        String tag = null;
        if (context != null) {
            if (context.getAccessibleName() != null) {
                tag = context.getAccessibleName();
            }
            if ((tag == null || "".equals(tag))
                && context.getAccessibleIcon() != null
                && context.getAccessibleIcon().length > 0) {
                AccessibleIcon[] icons = context.getAccessibleIcon();
                tag = icons[0].getAccessibleIconDescription();
                if (tag != null) {
                    tag = tag.substring(tag.lastIndexOf("/") + 1);
                    tag = tag.substring(tag.lastIndexOf("\\") + 1);
                }
            }
        }
        return tag;
    }

    /** Default methods to use to derive a component-specific tag.  These are
     * things that will probably be useful in a custom component if the method
     * is supported.
     */
    private static final String[] tagMethods = {
        "getLabel", "getTitle", "getText",
    };

    /** Return a reasonable identifier for the given component.
        Note that this should not duplicate information provided by existing
        attributes such as title, label, or text.
        This functionality is provided to assist in identification of
        non-standard GUI components only.
     */
    public static String getTag(Component comp) {
        return getTester(comp.getClass()).deriveTag(comp);
    }

    /** Returns whether the given class is not a core JRE class. */
    protected boolean isCustom(Class c) {
        return !(c.getName().startsWith("javax.swing.")
                 || c.getName().startsWith("java.awt."));
    }

    /** Determine a component-specific identifier not already defined in other
     * component attributes.  This method should be defined to something
     * unique for custom UI components.
     */
    public String deriveTag(Component comp) {
        // If the component class is custom, don't provide a tag
        if (isCustom(comp.getClass()))
            return null;

        Method m;
        String tag = null;
        // Try a few default methods
        for (int i=0;i < tagMethods.length;i++) {
            // Don't use getText on text components
            if (((comp instanceof javax.swing.text.JTextComponent)
                 || (comp instanceof java.awt.TextComponent))
                && "getText".equals(tagMethods[i])) {
                continue;
            }
            try {
                m = comp.getClass().getMethod(tagMethods[i], null);
                String tmp = (String)m.invoke(comp, null);
                // Don't ever use empty strings for tags
                if (tmp != null && !"".equals(tmp)) {
                    tag = tmp;
                    break;
                }
            }
            catch(Exception e) {
            }
        }
        // In the absence of any other tag, try to derive one from something
        // recognizable on one of its ancestors.
        if (tag == null || "".equals(tag)) {
            Component parent = comp.getParent();
            if (parent != null) {
                String ptag = getTag(parent);
                if (ptag != null && !"".equals(tag)) {
                    // Don't use the tag if it's simply the window title; that
                    // doesn't provide any extra information.
                    if (!ptag.endsWith(" Root Pane")) {
                        StringBuffer buf = new StringBuffer(ptag);
                        int under = ptag.indexOf(" under ");
                        if (under != -1)
                            buf = buf.delete(0, under + 7);
                        buf.insert(0, " under ");
                        buf.insert(0, simpleClassName(comp.getClass()));
                        tag = buf.toString();
                    }
                }
            }
        }

        return tag;
    }

    /**
     * Wait for an idle AWT event queue.  Will return when there are no more
     * events on the event queue.
     */
    public void actionWaitForIdle() {
        waitForIdle();
    }

    /** Delay the given number of ms. */
    public void actionDelay(int ms) {
        delay(ms);
    }

    /** @deprecated  Renamed to {@link #actionSelectAWTMenuItem(Frame,String)}.
     */
    public void actionSelectAWTMenuItemByLabel(Frame menuFrame, String path) {
        actionSelectAWTMenuItem(menuFrame, path);
    }

    /** Selects an AWT menu item ({@link java.awt.MenuItem}) and returns when
        the invocation has triggered (though not necessarily completed).  
        @param menuFrame
        @param path either a unique label or the menu path.
        @see Robot#selectAWTMenuItem(Frame,String)
    */
    public void actionSelectAWTMenuItem(Frame menuFrame, String path) {
        AWTMenuListener listener = new AWTMenuListener();
        new WeakAWTEventListener(listener, ActionEvent.ACTION_EVENT_MASK);
        selectAWTMenuItem(menuFrame, path);
        long start = System.currentTimeMillis();
        while (!listener.eventFired) {
            if (System.currentTimeMillis() - start > defaultDelay) {
                throw new ActionFailedException("Menu item '" + path
                                                + "' failed to fire");
            }
            sleep();
        }
        waitForIdle();
    }

    /** @deprecated  Renamed to
        {@link #actionSelectAWTPopupMenuItem(Component,String)}.
    */
    public void actionSelectAWTPopupMenuItemByLabel(Component invoker,
                                                    String path) {
        actionSelectAWTPopupMenuItem(invoker, path);
    }
    
    /** Selects an AWT menu item and returns when the invocation has triggered
        (though not necessarily completed).
        @see Robot#selectAWTPopupMenuItem(Component,String)
    */
    public void actionSelectAWTPopupMenuItem(Component invoker, String path) {
        AWTMenuListener listener = new AWTMenuListener();
        new WeakAWTEventListener(listener, ActionEvent.ACTION_EVENT_MASK);
        selectAWTPopupMenuItem(invoker, path);
        long start = System.currentTimeMillis();
        while (!listener.eventFired) {
            if (System.currentTimeMillis() - start > defaultDelay) {
                throw new ActionFailedException("Menu item '" + path
                                                + "' failed to fire");
            }
            sleep();
        }
        waitForIdle();
    }

    /** Select the given menu item. */
    public void actionSelectMenuItem(Component item) {
        Log.debug("Attempting to select menu item " + toString(item));
        selectMenuItem(item);
        waitForIdle();
    }

    /** Select the given menu item, given its path and a component on the same
        window.
    */
    public void actionSelectMenuItem(Component sameWindow, String path) {
        Log.debug("Attempting to select menu item '" + path + "'");
        selectMenuItem(sameWindow, path);
        waitForIdle();
    }

    /** Pop up a menu at the center of the given component and
        select the given item.
     */
    public void actionSelectPopupMenuItem(Component invoker, String path) {
        actionSelectPopupMenuItem(invoker, invoker.getWidth()/2,
                                  invoker.getHeight()/2, path);
    }

    /** Pop up a menu at the given location on the given component and
        select the given item.
    */
    public void actionSelectPopupMenuItem(Component invoker,
                                          ComponentLocation loc,
                                          String path) {
        selectPopupMenuItem(invoker, loc, path);
        waitForIdle();
    }

    /** Pop up a menu at the given coordinates on the given component and
        select the given item.
     */
    public void actionSelectPopupMenuItem(Component invoker, int x, int y,
                                          String path) {
        actionSelectPopupMenuItem(invoker,
                                  new ComponentLocation(new Point(x, y)),
                                  path);
    }

    /** Pop up a menu in the center of the given component. */
    public void actionShowPopupMenu(Component invoker) {
        actionShowPopupMenu(invoker, new ComponentLocation());
    }

    /** Pop up a menu in the center of the given component. */
    public void actionShowPopupMenu(Component invoker, ComponentLocation loc) {
        Point where = loc.getPoint(invoker);
        showPopupMenu(invoker, where.x, where.y);
    }

    /** Pop up a menu at the given location on the given component. 
     */
    public void actionShowPopupMenu(Component invoker, int x, int y) {
        showPopupMenu(invoker, x, y);
    }

    /** Click on the center of the component. */
    public void actionClick(Component comp) {
        actionClick(comp, new ComponentLocation());
    }

    /** Click on the component at the given location. */
    public void actionClick(Component c, ComponentLocation loc) {
        actionClick(c, loc, InputEvent.BUTTON1_MASK);
    }

    /** Click on the component at the given location with the given
        modifiers.
    */ 
    public void actionClick(Component c, ComponentLocation loc, int buttons) {
        actionClick(c, loc, buttons, 1);
    }

    /** Click on the component at the given location, with the given
     * modifiers and click count.
     */
    public void actionClick(Component c, ComponentLocation loc,
                            int buttons, int count) {
        Point where = loc.getPoint(c);
        click(c, where.x, where.y, buttons, count);
        waitForIdle();
    }

    /** Click on the component at the given location.
     */
    public void actionClick(Component comp, int x, int y) {
        actionClick(comp, new ComponentLocation(new Point(x, y)));
    }

    /** Click on the component at the given location.  The buttons mask
     * should be the InputEvent masks for the desired button, e.g.
     * {@link InputEvent#BUTTON1_MASK}.  ComponentTester also defines
     * {@link AWTConstants#POPUP_MASK} and {@link AWTConstants#TERTIARY_MASK} for
     * platform-independent masks for those buttons.
     */
    public void actionClick(Component comp, int x, int y, int buttons) {
        actionClick(comp, x, y, buttons, 1);
    }

    /** Click on the component at the given location, specifying buttons and
     * the number of clicks.  The buttons mask should be the InputEvent mask
     * for the desired button, e.g. 
     * {@link InputEvent#BUTTON1_MASK}.  ComponentTester also defines
     * {@link AWTConstants#POPUP_MASK} and {@link AWTConstants#TERTIARY_MASK} for
     * platform-independent masks for those buttons.
     */
    public void actionClick(Component comp, int x, int y,
                            int buttons, int count) { 
        actionClick(comp, new ComponentLocation(new Point(x, y)),
                    buttons, count);
    }

    /** Send a key press event for the given virtual key code to the given
        Component.
    */ 
    public void actionKeyPress(Component comp, int keyCode) {
        actionFocus(comp);
        actionKeyPress(keyCode);
    }

    /** Generate a key press event for the given virtual key code. */
    public void actionKeyPress(int keyCode) {
        keyPress(keyCode);
        waitForIdle();
    }

    /** Generate a key release event sent to the given component. */
    public void actionKeyRelease(Component comp, int keyCode) {
        actionFocus(comp);
        actionKeyRelease(keyCode);
    }

    /** Generate a key release event. */
    public void actionKeyRelease(int keyCode) {
        keyRelease(keyCode);
        waitForIdle();
    }

    /**
     * Send the given keystroke, which must be the KeyEvent field name of a
     * KeyEvent VK_ constant to the program.  Sends a key down/up, with no
     * modifiers.  The focus is changed to the target component.
     */
    public void actionKeyStroke(Component c, int keyCode) {
        actionFocus(c);
        actionKeyStroke(keyCode, 0);
    }

    /** 
     * Send the given keystroke, which must be the KeyEvent field name of a
     * KeyEvent VK_ constant.  Sends a key press and key release, with no 
     * modifiers.  Note that this method does not affect the current focus.
     */
    public void actionKeyStroke(int keyCode) {
        actionKeyStroke(keyCode, 0);
    }

    /**
     * Send the given keystroke, which must be the KeyEvent field name of a
     * KeyEvent VK_ constant to the program.  Sends a key down/up, with the
     * given modifiers, which should be the InputEvent field name modifier
     * masks optionally ORed together with "|".  The focus is changed to the
     * target component.<p>
     */
    public void actionKeyStroke(Component c, int keyCode, int modifiers) {
        actionFocus(c);
        actionKeyStroke(keyCode, modifiers);
    }

    /**
     * Send the given keystroke, which must be the KeyEvent field name of a
     * KeyEvent VK_ constant to the program.  Sends a key down/up, with the
     * given modifiers, which should be the InputEvent field name modifier
     * masks optionally ORed together with "|".  Note that this does not
     * affect the current focus.<p>
     */
    public void actionKeyStroke(int keyCode, int modifiers) {
        if (Bugs.hasKeyStrokeGenerationBug()) {
            int oldDelay = getAutoDelay();
            setAutoDelay(50);
            key(keyCode, modifiers);
            setAutoDelay(oldDelay);
            delay(100);
        }
        else {
            key(keyCode, modifiers);
        }
        waitForIdle();
    }

    /** Send events required to generate the given string on the given
     * component.  This version is preferred over
     * {@link #actionKeyString(String)}.
     */ 
    public void actionKeyString(Component c, String string) {
        actionFocus(c);
        actionKeyString(string);
    }

    /** Send events required to generate the given string. */
    public void actionKeyString(String string) {
        keyString(string);
        // FIXME waitForIdle isn't always sufficient on OSX with key events
        if (Bugs.hasKeyStrokeGenerationBug()) {
            delay(100);
        }
        waitForIdle();
    }

    /** Set the focus on to the given component. */
    public void actionFocus(Component comp) {
        focus(comp, true);
    }

    /** Move the mouse/pointer to the given location. */
    public void actionMouseMove(Component comp, ComponentLocation loc) {
        Point where = loc.getPoint(comp);
        mouseMove(comp, where.x, where.y);
        waitForIdle();
    }

    /** Press mouse button 1 at the given location. */
    public void actionMousePress(Component comp, ComponentLocation loc) {
        actionMousePress(comp, loc, InputEvent.BUTTON1_MASK);
    }

    /** Press mouse buttons corresponding to the given mask at the given
        location.
    */ 
    public void actionMousePress(Component comp, ComponentLocation loc,
                                 int mask) {
        Point where = loc.getPoint(comp);
        mousePress(comp, where.x, where.y, mask);
        waitForIdle();
    }

    /** Release any currently held mouse buttons. */
    public void actionMouseRelease() {
        mouseRelease();
        waitForIdle();
    }

    /** Perform a drag action.  Derived classes may provide more specific
     * identifiers for what is being dragged simply by deriving a new class
     * from ComponentLocation which identifies the appropriate substructure.
     * For instance, a {@link JTreeLocation} might encapsulate the expansion
     * control location for the path [root, node A, child B].
     */
    public void actionDrag(Component dragSource, ComponentLocation loc) {
        actionDrag(dragSource, loc, "BUTTON1_MASK");
    }
    
    /** Perform a drag action.  Grabs the center of the given component. */
    public void actionDrag(Component dragSource) {
        actionDrag(dragSource, new ComponentLocation());
    }

    /** Perform a drag action with the given modifiers.  
     * @param dragSource source of the drag
     * @param loc identifies where on the given {@link Component} to begin the
     * drag.
     * @param modifiers a <code>String</code> representation of key modifiers,
     * e.g. "ALT|SHIFT", based on the {@link InputEvent#ALT_MASK InputEvent
     * <code>_MASK</code> fields}.
     * @deprecated Use the
     * {@link #actionDrag(Component,ComponentLocation,int) integer modifier mask} 
     * version instead.
     */
    public void actionDrag(Component dragSource, ComponentLocation loc,
                           String modifiers) {
        actionDrag(dragSource, loc, AWT.getModifiers(modifiers));
    }

    /** Perform a drag action with the given modifiers.  
     * @param dragSource source of the drag
     * @param loc identifies where on the given {@link Component} to begin the
     * drag.
     * @param modifiers one or more of the 
     * {@link InputEvent#ALT_MASK InputEvent <code>_MASK</code> fields}.
     */
    public void actionDrag(Component dragSource, ComponentLocation loc,
                           int modifiers) {
        Point where = loc.getPoint(dragSource);
        drag(dragSource, where.x, where.y, modifiers);
        waitForIdle();
    }

    /** Perform a drag action.  Grabs at the given explicit location. */
    public void actionDrag(Component dragSource, int sx, int sy) {
        actionDrag(dragSource, new ComponentLocation(new Point(sx, sy)));
    }

    /** Perform a drag action.  Grabs at the given location with the given
     * modifiers.
     * @param dragSource source of the drag
     * @param sx X coordinate
     * @param sy Y coordinate
     * @param modifiers a <code>String</code> representation of key modifiers,
     * e.g. "ALT|SHIFT", based on the {@link InputEvent#ALT_MASK InputEvent
     * <code>_MASK</code> fields}.
     * @deprecated Use the
     * {@link #actionDrag(Component,ComponentLocation,int) ComponentLocation/
     * integer modifier mask} version instead.
     */
    public void actionDrag(Component dragSource, int sx, int sy,
                           String modifiers) {
        actionDrag(dragSource, new ComponentLocation(new Point(sx, sy)),
                   modifiers);
    }

    /** Drag the current dragged object over the given target/location. */
    public void actionDragOver(Component target, ComponentLocation where) {
        Point loc = where.getPoint(target);
        dragOver(target, loc.x, loc.y);
        waitForIdle();
    }

    /** Perform a basic drop action (implicitly causing preceding mouse
     * drag motion).  Drops in the center of the component.
     */ 
    public void actionDrop(Component dropTarget) {
        actionDrop(dropTarget, new ComponentLocation());
    }

    /** Perform a basic drop action (implicitly causing preceding mouse
     * drag motion).
     */ 
    public void actionDrop(Component dropTarget, ComponentLocation loc) {
        Point where = loc.getPoint(dropTarget);
        drop(dropTarget, where.x, where.y);
        waitForIdle();
    }

    /** Perform a basic drop action (implicitly causing preceding mouse
     * drag motion).  The modifiers represents the set of active modifiers
     * when the drop is made.
     */ 
    public void actionDrop(Component dropTarget, int x, int y) {
        drop(dropTarget, x, y);
        waitForIdle();
    }


    /** Return whether the component's contents matches the given image. */
    public boolean assertImage(Component comp, java.io.File fileImage,
                               boolean ignoreBorder) {
        java.awt.image.BufferedImage img = capture(comp, ignoreBorder);
        return new ImageComparator().compare(img, fileImage) == 0;
    }
    
    /** Returns whether a Window corresponding to the given String is
     * showing.  The string may be a plain String or regular expression and
     * may match either the window title (for Frames or Dialogs) or its
     * Component name. 
     * @deprecated This method does not specify the proper context for the
     * lookup. 
     * @see junit.extensions.abbot.ComponentTestFixture#isShowing(String)
     */ 
    public boolean assertFrameShowing(String id) {
        try {
            Hierarchy h = AWTHierarchy.getDefault();
            abbot.finder.ComponentFinder finder = new BasicFinder(h);
            return finder.find(new WindowMatcher(id, true)) != null;
        }
        catch(ComponentNotFoundException e) {
            return false;
        }
        catch(MultipleComponentsFoundException m) {
            // Might not be the one you want, but that's what the docs say
            return true;
        }
    }

    /** Convenience wait for a window to be displayed.  The given string may
     * be a plain String or regular expression and may match either the window
     * title (for Frames and Dialogs) or its Component name.  This method is
     * provided as a convenience for hand-coded tests, since scripts will use
     * a wait step instead.<p>
     * The property abbot.robot.component_delay affects the default timeout.
     * @deprecated This method does not provide sufficient context to reliably
     * find a component.
     * @see junit.extensions.abbot.ComponentTestFixture#isShowing(String)
     */
    public void waitForFrameShowing(final String identifier) {
        wait(new Condition() {
            public boolean test() {
                return assertFrameShowing(identifier);
            }
            public String toString() {
                return Strings.get("tester.Component.show_wait",
                                   new Object[] { identifier });
            }
        }, componentDelay);
    }

    /** Return whether the Component represented by the given
        ComponentReference is available.
    */
    public boolean assertComponentShowing(ComponentReference ref) {
        try {
            Component c = ref.getComponent();
            return isReadyForInput(c);
        }
        catch(ComponentSearchException e) {
            return false;
        }
    }

    /** Wait for the Component represented by the given ComponentReference to
        become available.  The timeout is affected by
        abbot.robot.component_delay, which defaults to 30s. 
    */
    public void waitForComponentShowing(final ComponentReference ref) {
        wait(new Condition() {
            public boolean test() {
                return assertComponentShowing(ref);
            }
            public String toString() {
                return Strings.get("tester.Component.show_wait",
                                   new Object[] { ref });
            }
        }, componentDelay);
    }

    private Method[] cachedMethods = null;

    /** Look up methods with the given prefix.  Facilitates auto-scanning by
     * scripts and the script editor.
     */
    private Method[] getMethods(String prefix, Class returnType,
                                boolean componentArgument) {
        if (cachedMethods == null) {
            cachedMethods = getClass().getMethods();
        }
        ArrayList methods = new ArrayList();
        // Only save one Method for each unique name
        HashSet names = new HashSet(IGNORED_METHODS);

        Method[] mlist = cachedMethods;
        for (int i=0;i < mlist.length;i++) {
            String name = mlist[i].getName();
            if (names.contains(name) || !name.startsWith(prefix)) 
                continue;
            Class[] params = mlist[i].getParameterTypes();
            if ((returnType == null
                 || returnType.equals(mlist[i].getReturnType()))
                && ((params.length == 0 && !componentArgument)
                    || (params.length > 0
                        && (Component.class.isAssignableFrom(params[0])
                            == componentArgument)))) {
                methods.add(mlist[i]);
                names.add(name);
            }
        }
        return (Method[])methods.toArray(new Method[methods.size()]);
    }

    private Method[] cachedActions = null;
    /** Return a list of all actions defined by this class that don't depend
     * on a component argument.
     */
    public Method[] getActions() {
        if (cachedActions == null) {
            cachedActions = getMethods("action", void.class, false);
        }
        return cachedActions;
    }

    private Method[] cachedComponentActions = null;
    /** Return a list of all actions defined by this class that require
     * a component argument.
     */
    public Method[] getComponentActions() {
        if (cachedComponentActions == null) {
            cachedComponentActions = getMethods("action", void.class, true);
        }
        return cachedComponentActions;
    }

    
    private Method[] cachedPropertyMethods = null;
    /** Return an array of all property check methods defined by this class.
     * The first argument <b>must</b> be a Component.
     */
    public Method[] getPropertyMethods() {
        if (cachedPropertyMethods == null) {
            ArrayList all = new ArrayList();
            all.addAll(Arrays.asList(getMethods("is", boolean.class, true)));
            all.addAll(Arrays.asList(getMethods("has", boolean.class, true))); 
            all.addAll(Arrays.asList(getMethods("get", null, true))); 
            cachedPropertyMethods =
                (Method[])all.toArray(new Method[all.size()]);
        }
        return cachedPropertyMethods;
    }

    private Method[] cachedAssertMethods = null;
    /** Return a list of all assertions defined by this class that don't
     * depend on a component argument.
     */
    public Method[] getAssertMethods() {
        if (cachedAssertMethods == null) {
            cachedAssertMethods = getMethods("assert", boolean.class, false);
        }
        return cachedAssertMethods;
    }

    private Method[] cachedComponentAssertMethods = null;
    /** Return a list of all assertions defined by this class that require a
     * component argument.
     */
    public Method[] getComponentAssertMethods() {
        if (cachedComponentAssertMethods == null) {
            cachedComponentAssertMethods =
                getMethods("assert", boolean.class, true);
        }
        return cachedComponentAssertMethods;
    }

    /** Quick and dirty strip raw text from html, for getting the basic text
        from html-formatted labels and buttons.  Behavior is undefined for
        badly formatted html.
    */
    public static String stripHTML(String str) {
        if (str != null
            && (str.startsWith("<html>")
                || str.startsWith("<HTML>"))) {
            while (str.startsWith("<")) {
                int right = str.indexOf(">");
                if (right == -1)
                    break;
                str = str.substring(right + 1);
            }
            while (str.endsWith(">")) {
                int right = str.lastIndexOf("<");
                if (right == -1)
                    break;
                str = str.substring(0, right);
            }
        }
        return str;
    }

    /** Wait for the given condition, throwing an ActionFailedException if it
     * times out.
     */
    protected void waitAction(String desc, Condition cond)
        throws ActionFailedException {
        try { wait(cond); }
        catch(WaitTimedOutError wto) {
            throw new ActionFailedException(desc);
        }
    }

    /** Return the Component class that corresponds to this ComponentTester
        class.  For example, JComponentTester.getTestedClass(JLabel.class)
        would return JComponent.class.
    */
    public Class getTestedClass(Class cls) {
        while (getTester(cls.getSuperclass()) == this) {
            cls = cls.getSuperclass();
        }
        return cls;
    }

    /** Parse the String representation of a ComponentLocation into the actual
        ComponentLocation object.
    */
    public ComponentLocation parseLocation(String encoded) {
        return new ComponentLocation().parse(encoded);
    }

    /** Return a ComponentLocation for the given Point.  Derived classes may
     * provide something more suitable than an (x, y) coordinate.  A JTree,
     * for example, might provide a JTreeLocation indicating a path or row in
     * the tree.
     */
    public ComponentLocation getLocation(Component c, Point where) {
        return new ComponentLocation(where);
    }

    private class AWTMenuListener implements AWTEventListener {
        public volatile boolean eventFired;
        public void eventDispatched(AWTEvent e) {
            if (e.getID() == ActionEvent.ACTION_PERFORMED) {
                eventFired = true;
            }
        }
    }
}

