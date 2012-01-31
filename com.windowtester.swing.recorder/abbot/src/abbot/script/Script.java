package abbot.script;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import abbot.Log;
import abbot.Platform;
import abbot.finder.AWTHierarchy;
import abbot.finder.Hierarchy;
import abbot.i18n.Strings;
import abbot.script.parsers.FileParser;
import abbot.script.parsers.Parser;
import abbot.tester.Robot;
import abbot.util.Properties;

/**
 * Provide a structure to encapsulate actions invoked on GUI components and
 * tests performed on those components.  Scripts need to be short and concise
 * (and therefore easy to read/write). Extensions don't have to be.<p> 
 * This takes a single filename as a constructor argument.<p>
 * Use {@link junit.extensions.abbot.ScriptFixture} and
 * {@link junit.extensions.abbot.ScriptTestSuite}
 * to generate a suite by auto-generating a collection of {@link Script}s.<p>
 * @see StepRunner  
 * @see Fixture
 * @see Launch
 */

public class Script extends Sequence implements Resolver {
    public static final String INTERPRETER = "bsh";
    private static final String USAGE = 
        "<AWTTestScript [desc=\"\"] [forked=\"true\"] [slow=\"true\"]"
        + " [awt=\"true\"] [vmargs=\"...\"]>...</AWTTestScript>\n";

    /** Robot delay for slow playback.  */
    private static int slowDelay = 250;
    static boolean validate = true;
    private String filename;
    private File relativeDirectory;
    private boolean fork;
    private boolean slow;
    private boolean awt;
    private int lastSaved;
    private String vmargs;
    private Map properties = new HashMap();
    private Hierarchy hierarchy;
    public static final String UNTITLED_FILE =
        Strings.get("script.untitled_filename");
    protected static final String UNTITLED =
        Strings.get("script.untitled");

    /** Read-only map of ref IDs into ComponentReferences. */
    private Map refs = Collections.unmodifiableMap(new HashMap());
    /** Maps components to references.  This cache provides a 20% speedup when
     * adding new references.
     */
    private Map components = new WeakHashMap();

    static {
        slowDelay = Properties.getProperty("abbot.script.slow_delay",
                                           slowDelay, 0, 60000);
        String defValue = Platform.JAVA_VERSION < Platform.JAVA_1_4
            ? "false" : "true";
        validate = "true".equals(System.getProperty("abbot.script.validate",
                                                    defValue));
    }

    protected static Map createDefaultMap(String filename) {
        Map map = new HashMap();
        map.put(TAG_FILENAME, filename);
        return map;
    }

    /** Create a new, empty <code>Script</code>.  Used as a temporary
     * {@link Resolver}, uses the default {@link Hierarchy}.
     * @deprecated Use an explicit {@link Hierarchy} instead.
     */
    public Script() {
        // This is roughly equivalent to what
        // DefaultComponentFinder.getFinder() used to do
        this(AWTHierarchy.getDefault());
    }

    /** Create a <code>Script</code> from the given filename.  Uses the
        default {@link Hierarchy}.  
        @deprecated Use an explicit {@link Hierarchy} instead.
    */
    public Script(String filename) {
        // This is roughly equivalent to what
        // DefaultComponentFinder.getFinder() used to do
        this(filename, AWTHierarchy.getDefault());
    }

    public Script(Hierarchy h) {
        this(null, new HashMap());
        setHierarchy(h);
    }

    /** Create a <code>Script</code> from the given file.  */
    public Script(String filename, Hierarchy h) {
        this(null, createDefaultMap(filename));
        setHierarchy(h);
    }

    public Script(Resolver parent, Map attributes) {
        super(parent, attributes);
        String filename = (String)attributes.get(TAG_FILENAME);
        File file = filename != null
            ? new File(filename)
            : getTempFile(parent != null ? parent.getDirectory() : null);
        setFile(file);
        if (parent != null) {
            setRelativeTo(parent.getDirectory());
        }
        try {
            load();
        }
        catch(IOException e) {
            setScriptError(e);
        }
    }

    /** Since we allow ComponentReference IDs to be changed, make sure our map
     * is always up to date.
     */
    private synchronized void synchReferenceIDs() {
        HashMap map = new HashMap();
        Iterator iter = refs.values().iterator();
        while (iter.hasNext()) {
            ComponentReference ref = (ComponentReference)iter.next();
            map.put(ref.getID(), ref);
        }
        if (!refs.equals(map)) {
            // atomic update of references map
            refs = Collections.unmodifiableMap(map);
        }
    }

    public void setHierarchy(Hierarchy h) {
        hierarchy = h;
        components.clear();
    }

    private File getTempFile(File dir) {
        File file;
        try {
            file = (dir != null
                    ? File.createTempFile(UNTITLED_FILE, ".xml", dir)
                    : File.createTempFile(UNTITLED_FILE, ".xml"));
            // We don't actually need the file on disk
            file.delete();
        }
        catch(IOException io) {
            file = (dir != null
                    ? new File(dir, UNTITLED_FILE + ".xml")
                    : new File(UNTITLED_FILE + ".xml"));
        }
        return file;
    }

    public String getName() {
        return filename;
    }

    public void setForked(boolean fork) {
        this.fork = fork;
    }

    public boolean isForked() { return fork; }

    public void setVMArgs(String args) {
        if (args != null && "".equals(args))
            args = null;
        vmargs = args;
    }
    public String getVMArgs() { return vmargs; }

    public boolean isSlowPlayback() { return slow; }

    public void setSlowPlayback(boolean slow) { this.slow = slow; }

    public boolean isAWTMode() { return awt; }
    public void setAWTMode(boolean awt) { this.awt = awt; }

    /** Return the file where this script is saved.  Will always be an
     * absolute path.
     */
    public File getFile() {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            String path =
                getRelativeTo().getPath() + File.separator + filename;
            file = new File(path);
        }
        return file;
    }

    /** Change the file system basis for the current script.  Does not affect
        the script contents.
        @deprecated Use {@link #setFile(File)}.
    */
    public void changeFile(File file) {
        setFile(file);
    }

    /** Set the file system basis for this script object.  Use this to set the
        file from which the existing script will be loaded.
    */
    public void setFile(File file) {
        Log.debug("Script file set to " + file);
        if (file == null)
            throw new IllegalArgumentException("File must not be null");
        if (filename == null || !file.equals(getFile())) {
            filename = file.getPath();
            Log.debug("Script filename set to " + filename);
            if (relativeDirectory != null)
                setRelativeTo(relativeDirectory);
        }
        lastSaved = getHash() + 1;
    }

    /** Typical xml header, so we can know about how much file prefix to
     * skip.
     */
    private static final String XML_INFO =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    /** Flag to indicate whether emitted XML should contain the script
        contents.  Sometimes we just want a one-liner (like when displaying in
        the script editor), and sometimes we want the full contents (when
        writing to file).
    */
    private boolean formatForSave = false;

    /** Write the current state of the script to file. */
    public void save(Writer writer) throws IOException {
        formatForSave = true;
        Element el = toXML();
        formatForSave = false;
        el.setName(TAG_AWTTESTSCRIPT);

        Document doc = new Document(el);
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, writer);
    }

    /** Only thing directly editable on a script is its file path. */
    public String toEditableString() {
        return getFilename();
    }

    /** Has this script changed since the last save. */
    public boolean isDirty() {
        return getHash() != lastSaved;
    }

    /** Write the script to file.  Note that this differs from the toXML for
        the script, which simply indicates the file on which it is based. */
    public void save() throws IOException {
        File file = getFile();
        Log.debug("Saving script to '" + file + "' " + hashCode());
        OutputStreamWriter writer =
            new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        save(new BufferedWriter(writer));
        writer.close();
        lastSaved = getHash();
    }

    /** Ensure that all referenced components are actually in the components
     * list.
     */
    private synchronized void verify() throws InvalidScriptException {
        Log.debug("verifying all referenced refs exist");
        Iterator iter = refs.values().iterator();
        while (iter.hasNext()) {
            ComponentReference ref = (ComponentReference)iter.next();
            String id = ref.getAttribute(TAG_PARENT);
            if (id != null && refs.get(id) == null) {
                String msg = Strings.get("script.parent_missing",
                                         new Object[] { id });
                throw new InvalidScriptException(msg);
            }
            id = ref.getAttribute(TAG_WINDOW);
            if (id != null && refs.get(id) == null) {
                String msg = Strings.get("script.window_missing",
                                         new Object[] { id });
                throw new InvalidScriptException(msg);
            }
        }
    }

    /** Make the path to the given child script relative to this one. */
    private void updateRelativePath(Step child) {
        // Make sure included scripts are located relative to this one
        if (child instanceof Script) {
            ((Script)child).setRelativeTo(getDirectory());
        }
    }

    protected void parseChild(Element el) throws InvalidScriptException {
        if (el.getName().equals(TAG_COMPONENT)) {
            addComponentReference(el);
        }
        else {
            synchronized(steps()) {
                super.parseChild(el);
                updateRelativePath((Step)steps().get(size()-1));
            }
        }
    }

    /** Parse XML attributes for the Script. */
    protected void parseAttributes(Map map) {
        parseStepAttributes(map);
        fork = Boolean.valueOf((String)map.get(TAG_FORKED)).booleanValue();
        slow = Boolean.valueOf((String)map.get(TAG_SLOW)).booleanValue();
        awt = Boolean.valueOf((String)map.get(TAG_AWT)).booleanValue();
        vmargs = (String)map.get(TAG_VMARGS);
    }

    /** Loads the XML test script.  Performs a check against the XML schema.
        @param reader Provides the script data
        @throws InvalidScriptException
        @throws IOException
     */
    public void load(Reader reader)
        throws InvalidScriptException, IOException {
        clear();
/*        try {
            // Set things up to optionally validate on load
            SAXBuilder builder = 
                new SAXBuilder("org.apache.xerces.parsers.SAXParser", false);
            if (validate) {
                URL url = getClass().getClassLoader().getResource("abbot/abbot.xsd");
                if (url != null) {
                    builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
                    builder.setFeature("http://apache.org/xml/features/validation/schema", true);
                    builder.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", url.toString());
                }
                else {
                    Log.warn("Could not find abbot/abbot.xsd, disabling XML validation");
                    validate = false;
                }
            }
            Document doc = builder.build(reader);
            Element root = doc.getRootElement();
            Map map = createAttributeMap(root);
            parseAttributes(map);
            parseChildren(root);
        }
        catch(JDOMException e) {
            throw new InvalidScriptException(e.getMessage());
        }
*/        // Make sure we have all referenced components
        synchronized(this) {
            synchReferenceIDs();
            verify();
        }
        lastSaved = getHash();
    }

    public void addStep(int index, Step step) {
        super.addStep(index, step);
        updateRelativePath(step);
    }

    public void addStep(Step step) {
        super.addStep(step);
        updateRelativePath(step);
    }

    /** Replaces the step at the given index. */
    public void setStep(int index, Step step) {
        super.setStep(index, step);
        updateRelativePath(step);
    }

    /** Read the script from the currently set file.  */
    public void load() throws IOException {
        File file = getFile();
        if (!file.exists()) {
            if (getFilename().indexOf(Script.UNTITLED_FILE) == -1)
                Log.warn("Script " + this + " does not exist, ignoring it");
            return;
        }
        if (!file.isFile())
            throw new InvalidScriptException("Path " + getFilename() 
                                             + " refers to a directory");
        if (file.length() != 0) {
            try {
                Reader reader =
                    new InputStreamReader(new FileInputStream(file), "UTF-8");
                try {
                    load(new BufferedReader(reader));
                }
                finally {
                    try { reader.close(); } catch(IOException e) { }
                }
            }
            catch(FileNotFoundException e) {
                // should have been detected
                Log.warn("File '" + file + "' exists but is not found");
            }
        }
        else {
            Log.warn("Script file " + this + " is empty");
        }
    }

    
    protected String getFullXMLString() {
        try {
            formatForSave = true;
            return toXMLString(this);
        }
        finally {
            formatForSave = false;
        }
    }
    
    private int getHash() {
        return getFullXMLString().hashCode();
    }

    public String getXMLTag() {
        return TAG_SCRIPT;
    }

    /** Save component references in addition to everything else. */
    public Element addContent(Element el) {
        // Only save content if writing to disk
        if (formatForSave) {
            synchReferenceIDs();
            Iterator iter = new TreeSet(refs.values()).iterator();
            while (iter.hasNext()) {
                ComponentReference cref = (ComponentReference)iter.next();
                el.addContent(cref.toXML());
            }
            // Now collect our child steps
            return super.addContent(el);
        }
        return el;
    }

    /** Return the (possibly relative) path to this script. */ 
    public String getFilename() { return filename; }

    /** Provide XML attributes for this Step.  This class adds its filename. */
    public Map getAttributes() {
        Map map;
        if (!formatForSave) {
            map = new HashMap();
            map.put(TAG_FILENAME, getFilename());
        }
        else {
            map = super.getAttributes();
            // default is no fork
            if (fork) {
                map.put(TAG_FORKED, "true");
                if (vmargs != null) {
                    map.put(TAG_VMARGS, vmargs);
                }
            }
            if (slow) {
                map.put(TAG_SLOW, "true");
            }
            if (awt) {
                map.put(TAG_AWT, "true");
            }
        }
        return map;
    }

    protected void runStep(StepRunner runner) throws Throwable {
        components.clear();
        properties.clear();
        // Make all files relative to this script
        Parser fc = new FileParser() {
            public String relativeTo() {
                Log.debug("All file references will be relative to "
                          + getDirectory().getAbsolutePath());
                return getDirectory().getAbsolutePath();
            }
        };
        Parser oldfc = ArgumentParser.setParser(File.class, fc);
        int oldDelay = Robot.getAutoDelay();
        int oldMode = Robot.getEventMode();
        if (slow) {
            Robot.setAutoDelay(slowDelay);
        }
        if (awt) {
            Robot.setEventMode(Robot.EM_AWT);
        }

        try {
            super.runStep(runner);
        }
        finally {
            Robot.setAutoDelay(oldDelay);
            Robot.setEventMode(oldMode);
            ArgumentParser.setParser(File.class, oldfc);
        }
    }

    /** Set up a blank script, discarding any current state. */
    public void clear() {
        setScriptError(null);
        refs = Collections.unmodifiableMap(new HashMap());
        components.clear();
        super.clear();
    }

    public String getUsage() { return USAGE; }

    /** Return a default description for this <code>Script</code>. */
    public String getDefaultDescription() {
        String ext = fork ? " &" : "";
        String desc = Strings.get("script.desc", 
                                  new Object[] { getFilename(), ext });
        return desc.indexOf(UNTITLED_FILE) != -1 ? UNTITLED : desc;
    }

    /** Return whether this <code>Script</code> is launchable. */ 
    public boolean hasLaunch() {
        // First step might be a Launch or a Fixture
        return size() > 0 && (((Step)steps().get(0)) instanceof UIContext);
    }

    /** @return The {@link UIContext} responsible for setting up
     * a UI context for this script, or
     * <code>null</code> if the script has no UI to speak of.
     */
    public UIContext getUIContext() {
        synchronized(steps()) {
            if (hasLaunch()) {
                return (UIContext)steps().get(0);
            }
        }
        return null;
    }

    /** Defer to the {@link UIContext} to obtain a 
     * {@link ClassLoader}, or use the current {@link Thread}'s
     * context class loader.
     * @see Thread#getContextClassLoader()
     */
    public ClassLoader getContextClassLoader() {
        UIContext context = getUIContext();
        return context != null
            ? context.getContextClassLoader()
            : Thread.currentThread().getContextClassLoader();
    }

    public boolean hasTerminate() {
        return size() > 0 
            && (((Step)steps().get(size()-1)) instanceof Terminate);
    }

    /** By default, all pathnames are relative to the current working
        directory.
    */
    public File getRelativeTo() {
        if (relativeDirectory == null)
            return new File(System.getProperty("user.dir"));
        return relativeDirectory;
    }

    /** Indicate that when invoking toXML, a path relative to the given one
     * should be shown.  Note that this is a runtime setting only and never
     * shows up in saved XML.
     */
    public void setRelativeTo(File dir) {
        Log.debug("Want relative dir " + dir);
        relativeDirectory = dir;
        if (dir != null) {
            // FIXME ideally, we'd want a more robust "make relative" here.
            // for now, simply check to see if the relpath is a prefix.
            // or if the file itself is relative
            String relPath = dir.getPath();
            if (filename.startsWith(relPath)
                && relPath.length() < filename.length()) {
                char ch = filename.charAt(relPath.length());
                if (ch == '/' || ch == '\\') {
                    filename = filename.substring(relPath.length() + 1);
                }
            }
        }
        Log.debug("Relative dir set to " + relativeDirectory + " for " + this);
    }

    /** Return whether the given file looks like a valid AWT script. */
    public static boolean isScript(File file) {
        if (file.length() == 0)
            return true;
        if (!file.exists() || !file.isFile() 
            || file.length() < TAG_AWTTESTSCRIPT.length() * 2 + 5)
            return false;
        InputStream is = null;
        try {
            int len = XML_INFO.length() + TAG_AWTTESTSCRIPT.length() + 15;
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[len];
            is.read(buf, 0, buf.length);
            String str = new String(buf, 0, buf.length);
            return str.indexOf(TAG_AWTTESTSCRIPT) != -1;
        }
        catch(Exception exc) {
            return false;
        }
        finally {
            if (is != null)
                try { is.close(); } catch(Exception exc) { }
        }
    }
    
    /** All relative files should be accessed relative to this directory,
        which is the directory where the script resides.
        It will always return an absolute path.
    */
    public File getDirectory() {
        return getFile().getParentFile();
    }

    /** Returns a sorted collection of ComponentReferences. */
    public Collection getComponentReferences() {
        return new TreeSet(refs.values());
    }

    /** Add a component reference directly, replacing any existing one with
        the same ID.
    */
    public void addComponentReference(ComponentReference ref) {
        Log.debug("adding " + ref);
        synchReferenceIDs();
        HashMap map = new HashMap(refs);
        map.put(ref.getID(), ref);
        // atomic update of references map
        refs = Collections.unmodifiableMap(map);
    }

    /** Add a new component reference for the given component. */
    // FIXME: a repaint (tree locked) which accesses the refs list
    // deadlocks with cref creation (locks refs, asks for tree lock)
    // Either get tree lock first or don't require refs lock on read
    public ComponentReference addComponent(Component comp) {
        synchReferenceIDs();
        Log.debug("look up existing for " + Robot.toString(comp));
        Map newRefs = new HashMap();
        ComponentReference ref = 
            ComponentReference.getReference(this, comp, newRefs);
        Log.debug("adding " + Robot.toString(comp));
        Map map = new HashMap(refs);
        map.putAll(newRefs);
        Iterator iter = newRefs.values().iterator();
        while (iter.hasNext()) {
            ComponentReference r = (ComponentReference)iter.next();
            Component c = r.getCachedLookup(getHierarchy());
            if (c != null)
                components.put(c, r);
        }
        // atomic update of references map
        refs = Collections.unmodifiableMap(map);
        return ref;
    }

    /** Add a new component reference to the script.  For use only when
     * parsing a script.
     */
    ComponentReference addComponentReference(Element el)
        throws InvalidScriptException {
        synchReferenceIDs();
        ComponentReference ref = new ComponentReference(this, el);
        Log.debug("adding " + el);
        Map map = new HashMap(refs);
        map.put(ref.getID(), ref);
        // atomic update of references map
        refs = Collections.unmodifiableMap(map);
        return ref;
    }

    /** Return the reference for the given component, or null if none yet
     * exists. 
     */
    public ComponentReference getComponentReference(Component comp) {
        if (!getHierarchy().contains(comp)) {
            String msg = Strings.get("script.not_in_hierarchy",
                                     new Object[] { comp.toString() });
            throw new IllegalArgumentException(msg);
        }
        synchReferenceIDs();
        // Clear the component map if any one of the mappings is invalid
        Iterator iter = refs.values().iterator();
        while (iter.hasNext()) {
            ComponentReference cr = (ComponentReference)iter.next();
            if (cr.getCachedLookup(getHierarchy()) == null) {
                components.clear();
                break;
            }
        }
        ComponentReference ref = (ComponentReference)components.get(comp);
        if (ref != null) {
            if (ref.getCachedLookup(getHierarchy()) != null)
                return ref;
            components.remove(comp);
        }
        ref = ComponentReference.matchExisting(comp, refs.values());
        if (ref != null) {
            components.put(comp, ref);
        }
        return ref;
    }

    /** Convert the given reference ID into a component reference.  If it's
     * not in the Script's list, returns null.
     */
    public ComponentReference getComponentReference(String name) {
        synchReferenceIDs();
        return (ComponentReference)refs.get(name);
    }

    public void setProperty(String name, Object value) {
        if (value == null)
            properties.remove(name);
        else
            properties.put(name, value);
    }

    public Object getProperty(String name) {
        Object value = properties.get(name);
        // Lazy-load the interpreter, so it's only instantiated when required
        if (value == null && INTERPRETER.equals(name)) {
     //       Interpreter bsh = new Interpreter(this);
      //      properties.put(name, value = bsh);
            properties.put(name, "bsh");
        }
        return value;
    }

    /** Return the currently effective {@link Hierarchy} of components. */
    public Hierarchy getHierarchy() {
        Resolver r = getResolver();
        if (r != null && r != this) 
            return r.getHierarchy();
        return hierarchy != null ? hierarchy : AWTHierarchy.getDefault();
    }

    /** Return a meaningful description of where the Step came from. */
    public String getContext(Step step) {
        return getFile().toString() + ":" + getLine(this, step);
    }

    /** Return the file which defines the given step. */
    public static File getFile(Step step) {
        String context = step.getResolver().getContext(step);
        int colon = context.indexOf(":");
        if (colon == 1 && Character.isLetter(context.charAt(0))
            && Platform.isWindows() && context.length() > 2)
            colon = context.indexOf(":", 2);
        if (colon != -1)
            context = context.substring(0, colon);
        return new File(context);
    }

    /** Return the approximate line number of the given step.  File lines are
        one-based (there is no line zero).
    */
    public static int getLine(Step step) {
        String context = step.getResolver().getContext(step);
        int colon = context.indexOf(":");
        if (colon == 1 && Character.isLetter(context.charAt(0))
            && Platform.isWindows() && context.length() > 2)
            colon = context.indexOf(":", 2);
        context = context.substring(colon + 1);
        try {
            return Integer.parseInt(context);
        }
        catch(NumberFormatException e) {
            return -1;
        }
    }

    private static int getLine(Sequence seq, Step step) {
        int line = -1;
        int index = seq.indexOf(step);
        if (index == -1) {
            List list = seq.steps();
            for (int i=0;i < list.size();i++) {
                Step sub = (Step)list.get(i);
                if (sub instanceof Sequence) {
                    int subline = getLine((Sequence)sub, step);
                    if (subline != -1) {
                        line = countLines(seq, i) + subline;
                        break;
                    }
                }
            }
        }
        else {
            line = countLines(seq, index);
        }
        return line;
    }

    /** Return the number of XML lines in the given sequence that precede the
     * given index.  If the index is -1, return the number of XML lines used
     * by the whole sequence. 
     */
    public static int countLines(Sequence seq, int index) {
        int count = 1;
        int limit = index;
        if (limit == -1) {
            limit = seq.size();
            // Empty sequences take a single line
            if (limit == 0)
                return 1;
            // Otherwise, 2 + the line count of the contents
            count = 2;
        }
        if (seq instanceof Script) {
            // Add in one line per component reference in the script
            // Plus two lines for the <xml> and <AWTTestScript> lines
            count += ((Script)seq).getComponentReferences().size() + 2;
        }
        for (int i=0;i < limit;i++) {
            Step step = seq.getStep(i);
            // Included scripts take only one line
            if (step instanceof Script) {
                ++count;
            }
            else if (step instanceof Sequence) {
                count += countLines((Sequence)step, -1);
            }
            else {
                ++count;
            }
        }
        return count;
    }
}
