package abbot.script;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Window;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import abbot.Log;
import abbot.finder.ComponentNotFoundException;
import abbot.finder.ComponentSearchException;
import abbot.finder.Hierarchy;
import abbot.finder.MultipleComponentsFoundException;
import abbot.i18n.Strings;
import abbot.tester.ComponentTester;
import abbot.tester.Robot;
import abbot.util.AWT;

import com.windowtester.runtime.util.StringComparator;

/** Encapsulate as much information as is available to identify a GUI
 * component. Usage:<br>
 * <blockquote><code>
 * &lt;component id="..." class="..." [...]&gt;<br>
 * </code></blockquote>
 * The component reference ID may be used in scripts in place of the actual
 * component to which this reference refers.  The conversion will be made when
 * the actual component is needed.  The ID is arbitrary, and may be changed in
 * scripts to any unique string (just remember to change all references to the
 * ID in other places in the script as well).<p>
 * A number of optional tags are supported to provide an increasingly precise
 * specification of the desired component:<br>
 * <ul>
 * <li><b><code>weighted</code></b> refers to the name of an available
 * attribute which should be weighted more heavily in comparisons, e.g. the
 * label on a JButton.<br>
 * <li><b><code>parent</code></b> the reference id of this component's
 * parent.<br> 
 * </ul>
 * <p>
 * ComponentReferences may be created in one of three ways, each of which has
 * slightly different implications.
 * <ul>
 * <li>Resolver.addComponent(Component) - creates a reference if one doesn't
 * already exist and modifiers the Resolver to include it.
 * <li>getReference(Resolver, Component, Map) - create a reference only if a
 * matching one does not exist, but does not modify the Resolver.
 * <li>ComponentReference<init> - create a new reference.
 * </ul>
 */
// TODO: lose exact hierarchy match, cf Xt resource specifier, e.g.
// TODO: add window appearance order
//   JRootPane.<name>|<class>.*.JPanel
// Other attributes that might be useful: getAccessibleRole,
// getAccessibleDescription, tooltip, accessibleRelation, selection start/end 

/*
  To extend, probably want to make a static function here that stores
  attributes and a lookup interface to read that attribute.  Do this only if
  it is directly needed.

  Should the JRE class be used instead of a custom class?  
    pros: doesn't save custom classes, which might change
    cons: ?
  Should class mismatches be allowed?
    cons: can't change classes 
    pros: exact (or derived) class matching eliminates a lot of comparisons
*/

/*
 Optimization note:  All lookups are cached, so that at most we have to
 traverse the hierarchy once.
 Successful lookups are cached until the referenced ocmponent is GCd,
 removed from the hierarchy, or otherwise marked invalid.
 Unsuccessful lookups are cached for the duration of a particular lookup.
 These happen in several places.
 1) when resolving a cref into a component (getComponent())
 2) when a script is checking for existing references prior to creating a
 new one (getReference()).
 3) when creating a new reference (ComponentReference().
 4) when looking for a matching, existing reference (matchExisting()).
 In these cases, the failed lookup cache is cleared only after the
 entire operation is complete.
 see also NOTES
*/

public class ComponentReference
    implements XMLConstants, XMLifiable, Comparable {

    public static final String SHARED_FRAME_ID = "shared frame";

    // Matching weights for various attributes
    private static final int MW_NAME = 100; 
    private static final int MW_ROOT = 25;
    //private static final int MW_WEIGHTED = 50;
    private static final int MW_TAG = 50;
    private static final int MW_PARENT = 25; //
    private static final int MW_WINDOW = 25; 
    private static final int MW_INVOKER = 25;
    private static final int MW_TITLE = 25;
    private static final int MW_BORDER_TITLE = 25;
    private static final int MW_LABEL = 25; 
    private static final int MW_TEXT = 25; //
    private static final int MW_ICON = 25;
    private static final int MW_INDEX = 10; //
    private static final int MW_CLASS = 1;
    // Pretty much for applets only, or other embedded frames
    private static final int MW_PARAMS = 1;
    private static final int MW_DOCBASE = 1;
    // Mostly for distinguishing between multiple components that would
    // otherwise all match 
    private static final int MW_HORDER = 1;
    private static final int MW_VORDER = 1;
    //private static final int MW_ENABLED = 1;
    //private static final int MW_FOCUSED = 1;
    private static final int MW_SHOWING = 1;
    /** Match weight corresponding to no possible match. */
    public static final int MW_FAILURE = 0;
    static final String ANON_INNER_CLASS = "/^.*\\$[0-9]+$/";

    private Resolver resolver;
    private Map attributes = new HashMap();
    // This helps component reference creation by an order of magnitude,
    // especially when dealing with ordered attributes.
    private WeakReference cachedLookup;
    /** This ThreadLocal allows us to keep track of unresolved components on a
     * per-thread (basically per-lookup) basis.
     */
    private static ThreadLocal lookupFailures = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };
    /** This ThreadLocal allows us to keep track of non-showing, resolved
     * components on a per-thread (basically per-lookup) basis.
     */
    private static ThreadLocal nonShowingMatches = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new HashMap();
        }
    };
    /** Keep track of which ComponentReference ctor is the first one. */
    private static ThreadLocal ownsFailureCache = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return Boolean.TRUE;
        }
    };
    /** Cached XML representation. */
    private String xml;

    /** Disable immediate cacheing of components when a reference is created
        based on a Component.  Cacheing will first be done when the reference
        is resolved for the first time after creation.  For testing purposes
        only. 
    */
    static boolean cacheOnCreation = true;

    /** For creation from XML.
     */
    public ComponentReference(Resolver resolver, Element el) 
        throws InvalidScriptException {
        this.resolver = resolver;
        fromXML(el, true);
    }

    /** Create a reference to an instance of the given class, given an array
        of name/value pairs of attributes.
     */
    public ComponentReference(Resolver r, Class cls, String[][] attributes) {
        this(r, cls, createAttributeMap(attributes));
    }

    /** Create a reference to an instance of the given class, given a Map of
        attributes.   
     */
    public ComponentReference(Resolver resolver, Class cls, Map attributes) {
        // sort of a hack to provide a 'default' resolver
        this.resolver = resolver;
        this.attributes.putAll(attributes);
        this.attributes.put(TAG_CLASS, Robot.getCanonicalClass(cls).getName());
        if (resolver != null) {
            if (this.attributes.get(TAG_ID) == null) {
                this.attributes.put(TAG_ID, getUniqueID(new HashMap()));
            }
            resolver.addComponentReference(this);
        }
    }

    /** Create a reference based on the given component.  Will not use or
        create any ancestor components/references.
     */
    public ComponentReference(Resolver resolver, Component comp) {
        this(resolver, comp, false, new HashMap());
    }

    /** Create a reference based on the given component.  May recursively
        create other components required to identify this one.
     */
    public ComponentReference(Resolver resolver, Component comp,
                              Map newReferences) {
        this(resolver, comp, true, newReferences);
    }

    /** Create a reference based on the given component.  May recursively
        create other components required to identify this one if
        <code>includeHierarchy</code> is true.  If <code>newReferences</code>
        is non-null, new ancestor references will be added to it; if null,
        they will be added to the resolver instead.
     */
    private ComponentReference(Resolver resolver, Component comp,
                               boolean includeHierarchyAttributes,
                               Map newReferences) {
        // This method may be called recursively (indirectly through
        // Resolver.addComponent) in order to add references for parent
        // components.  Make note of whether this instantiation needs 
        // to clear the failure cache when it's done. 
        boolean cleanup = ((Boolean)ownsFailureCache.get()).booleanValue();
        ownsFailureCache.set(Boolean.FALSE);

        Log.debug("ctor: " + comp);
        this.resolver = resolver;

        if (AWT.isSharedInvisibleFrame(comp)) {
            setAttribute(TAG_ID, SHARED_FRAME_ID);
            setAttribute(TAG_CLASS, comp.getClass().getName());
        }
        else {
            Class refClass = Robot.getCanonicalClass(comp.getClass());
            setAttribute(TAG_CLASS, refClass.getName());
        }
        String name = getName(comp);
        if (name != null)
            setAttribute(TAG_NAME, name);

        // Only generate a tag attribute for custom components; using a tag
        // attribute for standard components is deprecated.
        String cname = comp.getClass().getName();
        if (!(cname.startsWith("java.awt.")
              || cname.startsWith("javax.swing."))) {
            String tag = ComponentTester.getTag(comp);
            if (tag != null)
                setAttribute(TAG_TAG, tag);        
        }
        
        // only take the title on a Frame/Dialog
        // using the window title for other components is obsolete 
        String title = getTitle(comp);
        if (title != null)
            setAttribute(TAG_TITLE, title);

        String borderTitle = getBorderTitle(comp);
        if (borderTitle != null)
            setAttribute(TAG_BORDER_TITLE, borderTitle);

        String label = getLabel(comp);
        if (label != null)
            setAttribute(TAG_LABEL, label);
        String text = getText(comp);
        if (text != null)
            setAttribute(TAG_TEXT, text);
        String icon = getIconName(comp);
        if (icon != null)
            setAttribute(TAG_ICON, icon);

        if (comp instanceof Applet) {
            Applet applet = (Applet)comp;
            setAttribute(TAG_PARAMS, encodeParams(applet));
            // 10/3/07 : kp
            // recording on applet - get npe
            //java.net.URL url = applet.getDocumentBase();
            java.net.URL url = null;
            setAttribute(TAG_DOCBASE, url != null
                         ? url.toString() : "null");
        }

        Container parent = resolver.getHierarchy().getParent(comp);
        if (null != parent) {
            // Don't save window indices, they're not sufficiently reliable
            if (!(comp instanceof Window)) {
                int index = getIndex(parent, comp);
                if (index != -1) 
                    setAttribute(TAG_INDEX, String.valueOf(index));
            }
        }
        else if (comp instanceof Window) {
            setAttribute(TAG_ROOT, "true");
        }

        try {
            if (includeHierarchyAttributes) {
                // Provide either the invoker or the window
                boolean needWindow = !(comp instanceof Window);
                Component invoker = null;
                if (comp instanceof JPopupMenu) {
                    invoker = ((JPopupMenu)comp).getInvoker();
                    ComponentReference ref =
                        getReference(resolver, invoker, newReferences);
                    setAttribute(TAG_INVOKER, ref.getID());
                    needWindow = false;
                }
                else if (parent != null) {
                    needWindow = !(parent instanceof Window);
                    addParent(parent, newReferences);
                }
                
                if (needWindow && !(comp instanceof Window)) {
                    Window win = AWT.getWindow(comp);
                    if (win != null) {
                        ComponentReference wref =
                            getReference(resolver, win, newReferences);
                        setAttribute(TAG_WINDOW, wref.getID());
                    }
                }
                
                validate(comp, newReferences);
            }
        }
        finally {
            if (cleanup) {
                getLookupFailures().clear();
                getNonShowingMatches().clear();
                ownsFailureCache.set(Boolean.TRUE);
            }
        }

        // Set the cache immediately
        if (cacheOnCreation || AWT.isSharedInvisibleFrame(comp)) {
            Log.debug("Cacheing initial match");
            cachedLookup = new WeakReference(comp);
        }
        else {
            cachedLookup = null;
        }

        // Finally, get a unique ID for this reference
        String id = getUniqueID(newReferences);
        setAttribute(TAG_ID, id);
        Log.debug("Unique ID is " + id);

        newReferences.put(id, this);
    }

    public static int getIndex(Container parent, Component comp) {
        if (comp instanceof Window) {
            Window[] owned = ((Window)parent).getOwnedWindows();
            for (int i=0;i < owned.length;i++) {
                if (owned[i] == comp) {
                    return i;
                }
            }
        }
        else {
            Component[] children = parent.getComponents();
            for (int i = 0; i < children.length; ++i) {
                if (children[i] == comp) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** Return the component in the current Hierarchy that best matches this
        reference.
    */
    public Component getComponent()
        throws ComponentNotFoundException, MultipleComponentsFoundException {

        if (resolver == null)
            throw new ComponentNotFoundException("No default hierarchy has been provided");
        return getComponent(resolver.getHierarchy());
    }

    /** Return the component in the given Hierarchy that best matches this
        reference.
    */
    public Component getComponent(Hierarchy hierarchy)
        throws ComponentNotFoundException, MultipleComponentsFoundException {

        try {
            return findInHierarchy(null, hierarchy, 1, new HashMap());
        }
        finally {
            // never called recursively, so we can clear the cache here
            getLookupFailures().clear();
            getNonShowingMatches().clear();
        }
    }

    private void addParent(Container parent, Map newReferences) {
        ComponentReference ref = getReference(resolver, parent, newReferences);
        setAttribute(TAG_PARENT, ref.getID());
    }

    /** Returns whether the given component is reachable from the root of the
     * current hierarchy.
     * Popups' transient elements may already have gone away, and will be
     * unreachable. 
     */
    private boolean reachableInHierarchy(Component c) {
        Window w = AWT.getWindow(c);
        if (w == null)
            return false;
        Window parent = (Window)resolver.getHierarchy().getParent(w);
        return (parent == null)
            ? resolver.getHierarchy().getRoots().contains(w)
            : reachableInHierarchy(parent);
    }

    /** Ensure the reference can be used to actually look up the given
     * component.  This can be a compute-intensive search, and thus is omitted
     * from the basic constructor.
     */
    private void validate(Component comp, Map newReferences) {
        // Under certain situations where we know the component is
        // unreachable from the root of the hierarchy, or if a lookup will
        // fail for other reasons, simply check for a match.
        // WARNING: this leaves a hole if the component actually needs an
        // ORDER attribute, but the ORDER attribute is intended for applets
        // only.
        if (!reachableInHierarchy(comp)) {
            int wt = getMatchWeight(comp, newReferences);
            int exact = getExactMatchWeight();
            if (wt < exact) {
                String msg = Strings.get("component.creation_mismatch",
                                         new Object[] {
                                             toXMLString(), comp.toString(),
                                             new Integer(wt),
                                             new Integer(exact),
                                         });
                throw new Error(msg);
            }
        }
        else {
            try {
                Log.debug("Finding in hierarchy ("
                          + resolver.getHierarchy() + ")");
                findInHierarchy(null, resolver.getHierarchy(),
                                getExactMatchWeight(), newReferences);
            }
            catch (MultipleComponentsFoundException multiples) {
                try {
                    // More than one match found, so add more information
                    Log.debug("Disambiguating");
                    disambiguate(comp, multiples.getComponents(),
                                 newReferences);
                }
                catch(ComponentSearchException e) {
                    if (!(e instanceof MultipleComponentsFoundException))
                        Log.warn(e);
                    throw new Error("Reverse lookup failed to uniquely match " 
                                    + Robot.toString(comp) + ": " + e);
                }
            }
            catch (ComponentNotFoundException e) {
                // This indicates a failure in the reference recording
                // mechanism, and requires a fix.
                throw new Error("Reverse lookup failed looking for "
                                + Robot.toString(comp) + " using "
                                + toXMLString() + ": " + e);
            }
        }
    }

    /** Return a descriptive name for the given component for use in UI
     * text (may be localized if appropriate and need not be re-usable
     * across locales.
     */ 
    public static String getDescriptiveName(Component c) {
        if (AWT.isSharedInvisibleFrame(c))
            return Strings.get("component.default_frame");

        String name = getName(c);
        if (name == null) {
            if ((name = getTitle(c)) == null) {
                if ((name = getText(c)) == null) {
                    if ((name = getLabel(c)) == null) {
                        if ((name = getIconName(c)) == null) {
                        }
                    }
                }
            }
        }
        return name;
    }

    /** Return a suitably descriptive name for this reference, for use as an
        ID (returns the ID itself if already set).  Will never return an empty
        String.
    */
    public String getDescriptiveName() {
        String id = getAttribute(TAG_ID);
        if (id == null) {
            String[] attributes = {
                TAG_NAME, TAG_TITLE, TAG_TEXT, TAG_LABEL, TAG_ICON,
            };
            for (int i=0;i < attributes.length;i++) {
                String att = getAttribute(attributes[i]);
                if (att != null && !"".equals(att)) {
                    id = att;
                    break;
                }
            }
            // Fall back to "<classname> Instance" if all else fails
            if (id == null) {
                String cname = getAttribute(TAG_CLASS);
                cname = cname.substring(cname.lastIndexOf(".") + 1);
                id = cname + " Instance";
            }
        }
        return id;
    }

    public String getID() {
        return getAttribute(TAG_ID);
    }

    public String getRefClassName() { return getAttribute(TAG_CLASS); }

    public String getAttribute(String key) {
        return (String)attributes.get(key);
    }

    public Map getAttributes() {
        return new TreeMap(attributes);
    }

    public void setAttribute(String key, String value) {
        xml = null;
        attributes.put(key, value);
    }

    /** Return whether a cast to the given class name from the given class
        would work.
    */
    private boolean isAssignableFrom(String refClassName, Class cls) {
        return refClassName.equals(cls.getName())
            || (!Component.class.equals(cls)
                && isAssignableFrom(refClassName, cls.getSuperclass()));
    }

    /** Return whether this reference has the same class or is a superclass of
     * the given component's class.  Simply compare class names to avoid class
     * loader conflicts.  Note that this does not take into account interfaces
     * (which is okay, since with GUI components we're only concerned with
     * class inheritance). 
     */
    public boolean isAssignableFrom(Class cls) {
        return cls != null 
            && Component.class.isAssignableFrom(cls)
            && isAssignableFrom(getAttribute(TAG_CLASS), cls);
    }

    public ComponentReference getParentReference(Map newRefs) {
        String parentID = getAttribute(TAG_PARENT);
        ComponentReference pref = null;
        if (parentID != null) {
            pref = resolver.getComponentReference(parentID);
            if (pref == null)
                pref = (ComponentReference)newRefs.get(parentID);
        }
        return pref;
    }

    /** Reference ID of this component's parent window (optional). */
    public ComponentReference getWindowReference(Map newReferences) {
        String windowID = getAttribute(TAG_WINDOW);
        ComponentReference wref = null;
        if (windowID != null) {
            wref = resolver.getComponentReference(windowID);
            if (wref == null)
                wref = (ComponentReference)newReferences.get(windowID);
        }
        return wref;
    }

    public ComponentReference getInvokerReference(Map newReferences) {
        String invokerID = getAttribute(TAG_INVOKER);
        ComponentReference iref = null;
        if (invokerID != null) {
            iref = resolver.getComponentReference(invokerID);
            if (iref == null)
                iref = (ComponentReference)newReferences.get(invokerID);
        }
        return iref;
    }

    /** Set all options based on the given XML.
        @deprecated
     */
    // This is only used when editing scripts, since we don't want to have to
    // hunt down existing references
    public void fromXML(String input)
        throws InvalidScriptException, IOException {
        StringReader reader = new StringReader(input);
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(reader);
            Element el = doc.getRootElement();
            if (el == null)
                throw new InvalidScriptException("Invalid ComponentReference"
                                                 + " XML '" + input + "'");
            fromXML(el, false);
        }
        catch(JDOMException e) {
            throw new InvalidScriptException(e.getMessage()
                                             + " (when parsing "
                                             + input + ")");
        }
    }

    /** Parse settings from the given XML.   Only overwrite the ID if
        useGivenID is set.
        @throws InvalidScriptException if the given Element is not valid XML
        for a ComponentReference.
    */
    private void fromXML(Element el, boolean useIDFromXML)
        throws InvalidScriptException {

        Iterator iter = el.getAttributes().iterator();
        while (iter.hasNext()) {
            Attribute att = (Attribute)iter.next();
            String nodeName = att.getName();
            String value = att.getValue();
            if (nodeName.equals(TAG_ID) && !useIDFromXML)
                continue;

            setAttribute(nodeName, value);
        }
        if (getAttribute(TAG_CLASS) == null) {
            throw new InvalidScriptException("Class must be specified", el);
        }
        String id = getID();
        if (useIDFromXML) {
            // Make sure the ID we read in is not already in use by the manager
            if (id != null) {
                if (resolver.getComponentReference(id) != null) {
                    String msg = "Persistent ID '" 
                        + id + "' is already in use";
                    throw new InvalidScriptException(msg, el);
                }
            }
        }
        if (id == null) {
            Log.warn("null ID");
            setAttribute(TAG_ID, getUniqueID(new HashMap()));
        }
    }

    /** Generate an XML representation of this object. */
    public Element toXML() {
        Element el = new Element(TAG_COMPONENT);
        Iterator iter = new TreeMap(attributes).keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = getAttribute(key);
            if (value != null)
                el.setAttribute(key, value);
        }
        return el;
    }

    /** @deprecated Used to be used to edit XML in a text editor. */
    public String toEditableString() {
        return toXMLString();
    }

    /** Two ComponentReferences with identical XML representations should 
        be equal. */
    public boolean equals(Object obj) {
        return this == obj
            || (obj instanceof ComponentReference)
                && toXMLString().
            equals(((ComponentReference)obj).toXMLString());
    }

    /** Return a human-readable representation. */
    public String toString() {
        String id = getID();
        String cname = getAttribute(TAG_CLASS);
        if (cname.startsWith("javax.swing."))
            cname = cname.substring(12);
        else if (cname.startsWith("java.awt."))
            cname = cname.substring(9);
        StringBuffer buf =
            new StringBuffer(id != null ? id : (cname + " (no id yet)"));
        if (id != null && id.indexOf("Instance") == -1) {
            buf.append(" (");
            buf.append(cname);
            buf.append(")");
        }
        return buf.toString();
    }

    public String toXMLString() {
        if (xml == null)
            xml = Step.toXMLString(this);
        return xml;
    }

    /** Return which of the otherwise indistinguishable components provides
     * the best match, or throw a MultipleComponentsFoundException if no
     * distinction is possible.  Assumes that all given components return an
     * equivalent match weight.
     */
    private Component bestMatch(Set set)
        throws MultipleComponentsFoundException {
        Component[] matches = (Component[])
            set.toArray(new Component[set.size()]);
        int weights[] = new int[matches.length];
        for (int i=0;i < weights.length;i++) {
            // Prefer showing to non-showing
            Window w = AWT.getWindow(matches[i]);
            if (w != null && w.isShowing()) {
                weights[i] = MW_SHOWING;
            }
            else {
                weights[i] = 0;
            }
            // Preferring one enabled/focused state is dangerous to do:
            // An enabled component might be preferred over a disabled one,
            // but it will fail if you're trying to examine state on the
            // disabled component.  Ditto for focused.
        }
        String horder = getAttribute(TAG_HORDER);
        if (horder != null) {
            for (int i=0;i < matches.length;i++) {
                String order = getOrder(matches[i], matches, true);
                if (horder.equals(order)) {
                    weights[i] += MW_HORDER;
                }
            }
        }
        String vorder = getAttribute(TAG_VORDER);
        if (vorder != null) {
            for (int i=0;i < matches.length;i++) {
                String order = getOrder(matches[i], matches, false);
                if (vorder.equals(order)) {
                    weights[i] += MW_VORDER;
                }
            }
        }
        // Figure out the best match, if any
        ArrayList best = new ArrayList();
        best.add(matches[0]);
        int max = 0;
        for (int i=1;i < weights.length;i++) {
            if (weights[i] > weights[max]) {
                max = i;
                best.clear();
                best.add(matches[i]);
            }
            else if (weights[i] == weights[max]) {
                best.add(matches[i]);
            }
        }
        if (best.size() == 1) {
            return (Component)best.get(0);
        }
        // Finally, see if any match the old cached value
        Component cache = getCachedLookup(resolver.getHierarchy());
        if (cache != null) {
            Iterator iter = best.iterator();
            while (iter.hasNext()) {
                Component c = (Component)iter.next();
                if (cache == c) {
                    return cache;
                }
            }
        }
        String msg = "Could not distinguish between " + best.size()
            + " components using " + toXMLString();
        matches = (Component[])best.toArray(new Component[best.size()]);
        throw new MultipleComponentsFoundException(msg, matches);
    }

    /** Return the order of the given component among the array given, sorted
     * by horizontal or vertical screen position.  All components with the
     * same effective value will have the same order.
     */
    static String getOrder(Component original, Component[] matchList,
                           boolean horizontal) {
        Comparator c = horizontal
            ? HORDER_COMPARATOR : VORDER_COMPARATOR;
        Component[] matches = (Component[])matchList.clone();
        Arrays.sort(matches, c);
        int order = 0;
        for (int i=0;i < matches.length;i++) {
            // Only change the order magnitude if there is a difference
            // between consecutive objects. 
            if (i > 0 && c.compare(matches[i-1], matches[i]) != 0)
                ++order;
            if (matches[i] == original) {
                return String.valueOf(order);
            }
        }
        return null;
    }

    /** Add sufficient information to the reference to distinguish it among
        the given components.
        Note that the ordering attributes can only be evaluated when looking
        at several otherwise identical components.
    */
    private void disambiguate(Component original, Component[] matches,
                              Map newReferences)
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Log.debug("Attempting to disambiguate multiple matches");
        Container parent = resolver.getHierarchy().getParent(original);
        boolean retryOnFailure = false;
        String order = null;
        try {
            String cname = original.getClass().getName();
            // Use the inner class name unless it's numeric (numeric values
            // can easily change).
            if (!cname.equals(getAttribute(TAG_CLASS))
                && !expressionMatch(ANON_INNER_CLASS, cname)) {
                setAttribute(TAG_CLASS, original.getClass().getName());
                retryOnFailure = true;
            }
            else if (parent != null && getAttribute(TAG_PARENT) == null
                && !(original instanceof JPopupMenu)) {
                Log.debug("Adding parent");
                addParent(parent, newReferences);
                retryOnFailure = true;
            }
            else if (getAttribute(TAG_HORDER) == null
                     && (order = getOrder(original, matches, true)) != null) {
                Log.debug("Adding horder");
                setAttribute(TAG_HORDER, order);
                retryOnFailure = true;
            }
            else if (getAttribute(TAG_VORDER) == null
                     && (order = getOrder(original, matches, false)) != null) {
                Log.debug("Adding vorder");
                setAttribute(TAG_VORDER, order);
                retryOnFailure = true;
            }
            // Try the lookup again to make sure it works this time
            Log.debug("Retrying lookup with new values");
            // Remove this cref and its ancestors from the failure
            // cache so we don't automatically fail
            getLookupFailures().remove(this);
            findInHierarchy(null, resolver.getHierarchy(),
                            getExactMatchWeight(), newReferences);
            Log.debug("Success!");
        }
        catch(MultipleComponentsFoundException multiples) {
            if (retryOnFailure) {
                disambiguate(original, multiples.getComponents(),
                             newReferences);
            }
            else
                throw multiples;
        }
    }

    /** Return a measure of how well the given component matches the given
     * component reference.  The weight performs two functions; one is to
     * loosely match so that we can find a component even if some of its
     * attributes have changed.  The other is to distinguish between similar
     * components. <p>
     * In general, we want to match if we get any weight at all, and there's
     * only one component that matches.
     */
    int getMatchWeight(Component comp) {
        return getMatchWeight(comp, new HashMap());
    }

    /** Return a measure of how well the given component matches the given
     * component reference.  The weight performs two functions; one is to
     * loosely match so that we can find a component even if some of its
     * attributes have changed.  The other is to distinguish between similar
     * components. <p>
     * In general, we want to match if we get any weight at all, and there's
     * only one component that matches.
     */
    private int getMatchWeight(Component comp, Map newReferences) {
        // Match weights may be positive or negative.  They should only be
        // negative if the attribute is highly unlikely to change.

        int weight = MW_FAILURE;

        if (null == comp) {
            return MW_FAILURE;
        }

        // FIXME might want to allow changing the class?  or should we just
        // ask the user to fix the script by hand?
        if (!isAssignableFrom(comp.getClass())) {
            return MW_FAILURE;        
        } 

        weight += MW_CLASS;
        // Exact class matches are better than non-exact matches
        if (getAttribute(TAG_CLASS).equals(comp.getClass().getName()))
            weight += MW_CLASS;
       
        String refTag = getAttribute(TAG_TAG);
        String compTag = null;
        if (null != refTag) {
            compTag = ComponentTester.getTag(comp);
            if (compTag != null && expressionMatch(refTag, compTag)) {
                weight += MW_TAG;
            }
        }

        String refName = getAttribute(TAG_NAME);
        if (null != refName) {
            String compName = getName(comp);
            if (compName != null && expressionMatch(refName, compName)) {
                weight += MW_NAME;
            }
            else {
                weight -= MW_NAME;
            }
        }

        if (null != getAttribute(TAG_INVOKER)) {
            ComponentReference iref = getInvokerReference(newReferences);
            Component invoker = (comp instanceof JPopupMenu)
                ? ((JPopupMenu)comp).getInvoker() : null;
            if (invoker == iref.resolveComponent(invoker, newReferences)) {
                weight += MW_INVOKER;
            }
            else {
                // Invoking components aren't likely to change
                weight -= MW_INVOKER;
            }
        }

        if (null != getAttribute(TAG_PARENT)) {
            ComponentReference pref = getParentReference(newReferences);
            Component parent = resolver.getHierarchy().getParent(comp);
            if (parent == pref.resolveComponent(parent, newReferences)) {
                weight += MW_PARENT;
            }
            // Don't detract on parent mismatch, since changing a parent is
            // not that big a change (e.g. adding a scroll pane)
        }
        // ROOT and PARENT are mutually exclusive
        else if (null != getAttribute(TAG_ROOT)) {
            weight += MW_ROOT;
        }

        if (null != getAttribute(TAG_WINDOW)) {
            ComponentReference wref = getWindowReference(newReferences);
            Window w = AWT.getWindow(comp);
            if (w == wref.resolveComponent(w, newReferences)) {
                weight += MW_WINDOW;
            } 
            else if (w != null) {
                // Changing windows is a big change and not very likely
                weight -= MW_WINDOW;
            }
        }

        // TITLE is no longer used except by Frames, Dialogs, and
        // JInternalFrames, being superseded by the ancestor window 
        // reference.  For other components, it represents an available
        // ancestor window title (deprecated usage only).   
        String title = getAttribute(TAG_TITLE);
        if (null != title) {
            String title2 = (comp instanceof Frame
                             || comp instanceof Dialog
                             || comp instanceof JInternalFrame)
                ? getTitle(comp)
                : getComponentWindowTitle(comp);
            if (title2 != null && expressionMatch(title, title2)) {
                weight += MW_TITLE;
            }
            // Don't subtract on mismatch, since title changes are common
        }

        String borderTitle = getAttribute(TAG_BORDER_TITLE);
        if (null != borderTitle) {
            String bt2 = getBorderTitle(comp);
            if (bt2 != null && expressionMatch(borderTitle, bt2)) {
                weight += MW_BORDER_TITLE;
            }
        }

        String label = getAttribute(TAG_LABEL);
        if (null != label) {
            String label2 = getLabel(comp);
            if (label2 != null && expressionMatch(label, label2)) {
                weight += MW_LABEL;
            }
        }

        String text = getAttribute(TAG_TEXT);
        if (null != text) {
            String text2 = getText(comp);
            if (text2 != null && expressionMatch(text, text2)) {
                weight += MW_TEXT;
            }
        }

        String icon = getAttribute(TAG_ICON);
        if (null != icon) {
            String icon2 = getIconName(comp);
            if (icon2 != null && expressionMatch(icon, icon2)) {
                weight += MW_ICON;
            }
        }

        String idx = getAttribute(TAG_INDEX);
        if (null != idx) {
            Container parent = resolver.getHierarchy().getParent(comp);
            if (null != parent) {
                int i = getIndex(parent, comp);
                if (expressionMatch(idx, String.valueOf(i))) {
                    weight += MW_INDEX;
                }
            }
            // Don't subtract for index mismatch, since ordering changes are
            // common. 
        }

        if (comp instanceof Applet) {
            Applet applet = (Applet)comp;
            String params = getAttribute(TAG_PARAMS);
            if (null != params) {
                String params2 = encodeParams(applet);
                if (expressionMatch(params, params2))
                    weight += MW_PARAMS;
            }
            String docBase = getAttribute(TAG_DOCBASE);
            if (null != docBase) {
                // 10/3/07: kp causes an NPE 
            	
            	//java.net.URL url = applet.getDocumentBase();
            	java.net.URL url = null;	
            	if (url != null && expressionMatch(docBase, url.toString()))
                    weight += MW_DOCBASE;
            }
            // No negative weighting here
        }

        if (Log.isClassDebugEnabled(ComponentReference.class))
            Log.debug("Compared " + Robot.toString(comp)
                      + " to " + toXMLString() 
                      + " weight is " + weight);

        return weight;
    }

    /** Return the total weight required for an exact match. */
    private int getExactMatchWeight() {
        int weight = MW_CLASS;
        if (getAttribute(TAG_NAME) != null) 
            weight += MW_NAME;
        if (getAttribute(TAG_TAG) != null) 
            weight += MW_TAG;
        if (getAttribute(TAG_INVOKER) != null) 
            weight += MW_INVOKER;
        if (getAttribute(TAG_ROOT) != null)
            weight += MW_ROOT;
        if (getAttribute(TAG_PARENT) != null) 
            weight += MW_PARENT;
        if (getAttribute(TAG_WINDOW) != null) 
            weight += MW_WINDOW;
        if (getAttribute(TAG_TITLE) != null) 
            weight += MW_TITLE;
        if (getAttribute(TAG_BORDER_TITLE) != null) 
            weight += MW_BORDER_TITLE;
        if (getAttribute(TAG_INDEX) != null) 
            weight += MW_INDEX;
        if (getAttribute(TAG_LABEL) != null) 
            weight += MW_LABEL;
        if (getAttribute(TAG_TEXT) != null) 
            weight += MW_TEXT;
        if (getAttribute(TAG_ICON) != null) 
            weight += MW_ICON;
        if (getAttribute(TAG_PARAMS) != null)
            weight += MW_PARAMS;
        if (getAttribute(TAG_DOCBASE) != null)
            weight += MW_DOCBASE;

        if (Log.isClassDebugEnabled(ComponentReference.class))
            Log.debug("Exact match weight for " + toXMLString()
                      + " is " + weight);
        return weight;
    }

    /** Returns an existing component which matches this reference; the given
        Component is the one that is expected to match.  Returns null if no
        match or multiple matches are found and the preferred Component is not
        among them.<p> 
        This method is used in two instances:
        <ul>
        <li>Resolving a component's ancestors (window, parent, or invoker),
        the ancestor reference is checked against the ancestor of the
        Component currently being compared.
        <li>When referring to a component, determining if a reference to it
        already exists, all references are resolved to see if any resolves to
        the preferred Component.
        </ul>
        While there is a subtle difference between the two cases (when running
        a test it is expected that there will be some match, whereas when
        creating a new reference there may or may not be a match, based on the
        current script contents), it is not a useful distinction.
     */
    private Component resolveComponent(Component preferred,
                                       Map newReferences) {
        // This call should be equivalent to getComponent(), but without
        // clearing the lookup failure cache on completion
        if (Log.isClassDebugEnabled(ComponentReference.class))
            Log.debug("Looking up " + toXMLString() + " in hierarchy");
        Component found = null;
        try {
            found = findInHierarchy(null, resolver.getHierarchy(),
                                    1, newReferences);
        }
        catch(MultipleComponentsFoundException e) {
            Component[] list = e.getComponents();
            for (int i=0;i < list.length;i++) {
                if (list[i] == preferred)
                    return preferred;
            }
            //Log.warn("Preferred not found among many");
        }
        catch(ComponentNotFoundException e) {
            // If the preferred component is not reachable in the hierarchy
            // (if it has just been removed from the hierarchy, or an ancestor
            // pane was replaced), require an exact match to avoid
            // spurious matches. 
            int minWeight = getExactMatchWeight();
            if (getAttribute(TAG_WINDOW) != null)
                minWeight -= MW_WINDOW;
            if (getAttribute(TAG_PARENT) != null)
                minWeight -= MW_PARENT;
            if (AWT.getWindow(preferred) == null
                  && getMatchWeight(preferred) >= minWeight) {
                Log.debug("Using preferred component: "
                          + Robot.toString(preferred));
                found = preferred;
            }
        }
        return found;
    }

    /** Returns a reference to the given component, preferring an existing
     * reference if a matching one is available or creating a new one if not. 
     * The new references are <i>not</i> added to the resolver.
     */
    // FIXME: keep newly-created ancestors in a collection and let the
    // resolver add them.  (maybe create everything, then let the resolver
    // sort out duplicates when adding).
    // TODO: require exact matches, otherwise create a new ref; this means
    // that we need to provide a method to repair refs.
    public static ComponentReference getReference(Resolver r, Component comp,
                                                  Map newReferences) {
        Log.debug("Looking for a reference for " + Robot.toString(comp));
        // Preserve the failure cache across both lookup and creation
        boolean cleanup = ((Boolean)ownsFailureCache.get()).booleanValue();
        ownsFailureCache.set(Boolean.FALSE);

        // Allow the resolver to do cacheing if it needs to; otherwise we'd
        // call matchExisting directly.
        ComponentReference ref = r.getComponentReference(comp);
        try {
            if (ref == null) {
                Log.debug("No existing reference found, creating a new one");
                ref = new ComponentReference(r, comp, newReferences);
            }
        }
        finally {
            if (cleanup) {
                getLookupFailures().clear();
                getNonShowingMatches().clear();
                ownsFailureCache.set(Boolean.TRUE);
            }
        }
        return ref;
    }

    /** Match the given component against an existing set of references. */
    public static ComponentReference matchExisting(final Component comp,
                                                   Collection existing) {

        Log.debug("Matching " + Robot.toString(comp)
                  + " against existing refs");

        // This method might be called recursively (indirectly through
        // Resolver.addComponent) in order to add references for parent
        // components.  Make note of whether this level of invocation needs 
        // to clear the failure cache when it's done. 
        boolean cleanup = ((Boolean)ownsFailureCache.get()).booleanValue();
        ownsFailureCache.set(Boolean.FALSE);

        ComponentReference match = null;
        Iterator iter = existing.iterator();
        // Sort such that the best match comes first
        Map matches = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((ComponentReference)o2).getMatchWeight(comp)
                    - ((ComponentReference)o1).getMatchWeight(comp);
            }
        });
        Map empty = new HashMap();
        while (iter.hasNext()) {
            ComponentReference ref = (ComponentReference)iter.next();
            if (comp == ref.getCachedLookup(ref.resolver.getHierarchy())
                || comp == ref.resolveComponent(comp, empty)) {
                matches.put(ref, Boolean.TRUE);
            }
        }
        if (matches.size() > 0) {
            match = (ComponentReference)matches.keySet().iterator().next();
        }

        if (cleanup) {
            // Clear failures only after we've attempted a match for *all* refs
            getLookupFailures().clear();
            getNonShowingMatches().clear();
            ownsFailureCache.set(Boolean.TRUE);
        }

        Log.debug(match != null ? "Found" : "Not found");
        return match;
    }

    /** Return whether the given pattern matches the given string.  Performs
     * variable substitution on the pattern.
     */
    boolean expressionMatch(String pattern, String actual) {
        pattern = ArgumentParser.substitute(resolver, pattern);
        return StringComparator.matches(actual, pattern);
    }

    private static String getText(Component c) {
        if (c instanceof AbstractButton) {
            return ComponentTester.stripHTML(((AbstractButton)c).getText());
        }
        else if (c instanceof JLabel) {
            return ComponentTester.stripHTML(((JLabel)c).getText());
        }
        else if (c instanceof Label) {
            return ((Label)c).getText();
        }
        return null;
    }

    private static final String LABELED_BY_PROPERTY = "labeledBy";
    private static String getLabel(Component c) {
        String label = null;
        if (c instanceof JComponent) {
            Object obj =
                ((JComponent)c).getClientProperty(LABELED_BY_PROPERTY);
            // While the default is a JLabel, users may use something else as
            // the property, so be careful.
            if (obj != null) {
                if (obj instanceof JLabel) {
                    label = ((JLabel)obj).getText();
                }
                else if (obj instanceof String) {
                    label = (String)obj;
                }
            }
        }
        return ComponentTester.stripHTML(label);
    }

    private static String getIconName(Component c) {
        String icon = null;
        AccessibleContext context = c.getAccessibleContext();
        if (context != null) {
            AccessibleIcon[] icons = context.getAccessibleIcon();
            if (icons != null && icons.length > 0) {
                icon = icons[0].getAccessibleIconDescription();
                if (icon != null) {
                    icon = icon.substring(icon.lastIndexOf("/") + 1);
                    icon = icon.substring(icon.lastIndexOf("\\") + 1);
                }
            }
        }
        return icon;
    }

    private static String getName(Component c) {
        String name = AWT.hasDefaultName(c)
            ? null : c.getName();
        // Accessibility behaves like what we used to do with getTag.
        // Not too helpful for our purposes, especially when the
        // data on which the name is based might be dynamic.
        /*
        if (name == null) {
            AccessibleContext context = c.getAccessibleContext();
            if (context != null) 
                name = context.getAccessibleName();
        }
        */
        return name;
    }

    /** Convert the given applet's parameters into a simple String. */
    private String encodeParams(Applet applet) {
        // TODO: is there some other way of digging out the full set of
        // parameters that were passed the applet? b/c here we rely on the
        // applet having been properly written to tell us about supported
        // parameters.
        StringBuffer sb = new StringBuffer();
        String[][] info = applet.getParameterInfo();
        if (info == null) {
            // Default implementation of applet returns null
            return "null";
        }
        for (int i=0;i < info.length;i++) {
            sb.append(info[i][0]);
            sb.append("=");
            String param = applet.getParameter(info[i][0]);
            sb.append(param != null ? param : "null");
            sb.append(";");
        }
        return sb.toString();
    }

    /** Return the cached component match, if any. */
    public Component getCachedLookup(Hierarchy hierarchy) {
        if (cachedLookup != null) {
            Component c = (Component)cachedLookup.get();
            // Discard if the component has been gc'd, is no longer in the
            // hierarchy, or is no longer reachable from a Window.
            if (c != null && hierarchy.contains(c)
                && AWT.getWindow(c) != null) {
                return c;
            }
            Log.debug("Discarding cached value: " + Robot.toString(c));
            cachedLookup = null;
        }
        return null;
    }

    /** Compare this ComponentReference against each component below the given
     * root in the given hierarchy whose match weight exceeds the given
     * minimum.  If a valid cached lookup exists, that is returned
     * immediately. 
     */
    // TODO: refactor this to extract the finder/lookup logic into a separate
    // class.  the ref should only store attributes.
    private Component findInHierarchy(Container root, Hierarchy hierarchy,
                                      int weight, Map newReferences) 
        throws ComponentNotFoundException, MultipleComponentsFoundException {
        Component match = null;

        ComponentSearchException cse = (ComponentSearchException)
            getLookupFailures().get(this);
        if (cse instanceof ComponentNotFoundException) {
            Log.debug("lookup already failed: " + cse);
            throw (ComponentNotFoundException)cse;
        }
        if (cse instanceof MultipleComponentsFoundException) {
            Log.debug("lookup already failed: " + cse);
            throw (MultipleComponentsFoundException)cse;
        }

        Set set = new HashSet();
        match = getCachedLookup(hierarchy);
        if (match != null) {
            // This is always valid
            if (AWT.isSharedInvisibleFrame(match))
                return match;
            // TODO: always use the cached lookup; since TestHierarchy
            // auto-disposes, only improperly disposed components will still
            // match.  Codify this behavior with an explicit test.



            // Normally, we'd always want to use the cached lookup, but there
            // are instances where a component hierarchy may be used in a
            // transient way, so a given reference may need to match more than
            // one object without the first having been properly disposed.
            // Consider a createDialog() method, which creates an identical
            // dialog on each invocation, with an OK button.  Every call of
            // the method is semantically providing the same component,
            // although the implementation may create a new one each time.  If
            // previous instances have not been properly disposed, we need a
            // way to prefer a brand new instance over an old one.  We do that
            // by checking the cache window's showing state.
            // A showing match will trump a non-showing one,
            // but if there are multiple, non-showing matches, the cached
            // lookup will win.  
            // We check the window, not the component itself, because some
            // components hide their children.
            Window w = AWT.getWindow(match);
            if (w != null
                && (w.isShowing()
                    || getNonShowingMatches().get(this) == match)) {
                Log.debug("Using cached lookup for " + getID()
                          + " (hierarchy=" + hierarchy + ")");
                return match;
            }
            else {
                Log.debug("Skipping non-showing match (once) " + hashCode());
            }
        }

        weight = findMatchesInHierarchy(root, hierarchy, weight, set,
                                        newReferences);

        Log.debug("Found " + set.size() + " matches for " + toXMLString());
        if (set.size() == 1) {
            match = (Component)set.iterator().next();
        }
        else if (set.size() > 0) {
            // Distinguish between more than one match with the exact same
            // weight 
            try {
                match = bestMatch(set);
            }
            catch(MultipleComponentsFoundException e) {
                getLookupFailures().put(this, e);
                throw e;
            }
        }
        if (match == null) {
            String msg = "No component found which matches " + toXMLString();
            ComponentNotFoundException e =
                new ComponentNotFoundException(msg);
            getLookupFailures().put(this, e);
            throw e;
        }
        // This provides significant speedup when many similar components are
        // in play.
        Log.debug("Cacheing match: " + Integer.toHexString(match.hashCode()));
        cachedLookup = new WeakReference(match);
        if (!match.isShowing()) {
            getNonShowingMatches().put(this, match);
        }
        return match;
    }

    /** Return the the set of all components under the given component's
     * hierarchy (inclusive) which match the given reference.
     */
    private int findMatchesInHierarchy(Component root,
                                       Hierarchy hierarchy, 
                                       int currentMaxWeight,
                                       Set currentSet,
                                       Map newReferences) {
        if (root == null) {
            // Examine all top-level components and their owned windows.
            Iterator iter = hierarchy.getRoots().iterator();
            while (iter.hasNext()) {
                currentMaxWeight =
                    findMatchesInHierarchy((Window)iter.next(), hierarchy,
                                           currentMaxWeight, currentSet,
                                           newReferences);
            }
            return currentMaxWeight;
        }

        if (!hierarchy.contains(root)) {
            Log.debug("Component not in hierarchy");
            return currentMaxWeight;
        }

        int weight = getMatchWeight(root, newReferences);
        if (weight > currentMaxWeight) {
            currentSet.clear();
            currentMaxWeight = weight;
            currentSet.add(root);
        }
        else if (weight == currentMaxWeight) {
            currentSet.add(root);
        }

        // TODO: don't check window contents in the hierarchy if the cref is a
        // Window.  oops, how do you tell the cref is a Window?
        // (no window tag, parent tag or root tag, no index tag)
        // no guarantee, though
        Collection kids = hierarchy.getComponents(root);
        Iterator iter = kids.iterator();
        while (iter.hasNext()) {
            Component child = (Component)iter.next();
            currentMaxWeight =
                findMatchesInHierarchy(child, hierarchy,
                                       currentMaxWeight, currentSet,
                                       newReferences);
        }

        return currentMaxWeight;
    }

    /** Given an array of name, value pairs, generate a map suitable for
        creating a ComponentReference.
    */
    private static Map createAttributeMap(String[][] values) {
        Map map = new HashMap();
        for (int i=0;i < values.length;i++) {
            map.put(values[i][0], values[i][1]);
        }
        return map;
    }

    private static final Comparator HORDER_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            Component c1 = (Component)o1;
            Component c2 = (Component)o2;
            int x1 = -100000;
            int x2 = -100000;
            try { x1 = c1.getLocationOnScreen().x; }
            catch(Exception e) { }
            try { x2 = c2.getLocationOnScreen().x; }
            catch(Exception e) { }
            return x1 - x2;
        }
    };

    private static final Comparator VORDER_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            Component c1 = (Component)o1;
            Component c2 = (Component)o2;
            int y1 = -100000;
            int y2 = -100000;
            try { y1 = c1.getLocationOnScreen().y; }
            catch(Exception e) { }
            try { y2 = c2.getLocationOnScreen().y; }
            catch(Exception e) { }
            return y1 - y2;
        }
    };

    /** @deprecated use getAttribute(TAG_NAME) instead. */
    public String getName() { return getAttribute(TAG_NAME); }
    /** @deprecated use getAttribute(TAG_TAG) instead. */
    public String getTag() { return getAttribute(TAG_TAG); }
    /** @deprecated use getAttribute(TAG_INVOKER) instead. */
    public String getInvokerID() { return getAttribute(TAG_INVOKER); }
    /** @deprecated use getAttribute(TAG_WINDOW) instead. */
    public String getWindowID() { return getAttribute(TAG_WINDOW); }
    /** @deprecated use getAttribute(TAG_TITLE) instead. */
    public String getTitle() { return getAttribute(TAG_TITLE); }
    /** @deprecated use getAttribute(TAG_INDEX) instead. */
    public int getIndex() {
        try { 
            return Integer.parseInt(getAttribute(TAG_INDEX));
        }
        catch(Exception e) {
            return -1;
        }
    }
    public int compareTo(Object o) {
        return getID().compareTo(((ComponentReference)o).getID());
    }

    /** See javax.swing.JComponent.getBorderTitle. */
    private static String getBorderTitle(Border b) {
        String title = null;
        if (b instanceof TitledBorder)
            title = ((TitledBorder)b).getTitle();
        else if (b instanceof CompoundBorder) {
            title = getBorderTitle(((CompoundBorder)b).getInsideBorder());
            if (title == null) {
                title = getBorderTitle(((CompoundBorder)b).getOutsideBorder());
            }
        }
        return title;
    }

    private static String getBorderTitle(Component c) {
        String title = null;
        if (c instanceof JComponent) {
            title = getBorderTitle(((JComponent)c).getBorder());
        }
        return title;
    }

    private static String getTitle(Component c) {
        if (c instanceof Dialog)
            return ((Dialog)c).getTitle();
        else if (c instanceof Frame) 
            return ((Frame)c).getTitle();
        else if (c instanceof JInternalFrame)
            return ((JInternalFrame)c).getTitle();
        return null;
    }

    private String getComponentWindowTitle(Component c) {
        Component parent = c;
        while (!(c instanceof Frame || c instanceof Dialog)
               && (c = resolver.getHierarchy().getParent(parent)) != null) {
            parent = c;
        }
        String title = null;
        if (parent instanceof Frame) {
            title = ((Frame)parent).getTitle();
        }
        else if (parent instanceof Dialog) {
            title = ((Dialog)parent).getTitle();
        }
        return title;
    }

    private static Map getLookupFailures() {
        return (Map)lookupFailures.get();
    }

    private static Map getNonShowingMatches() {
        return (Map)nonShowingMatches.get();
    }

    public String getUniqueID(Map refs) {
        String id = getDescriptiveName();
        String ext = "";
        int count = 2;
        while (refs.get(id + ext) != null 
               || resolver.getComponentReference(id + ext) != null) {
            ext = " " + count++;
        }
        return id + ext;
    }
}
