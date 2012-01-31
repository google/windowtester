package abbot.script;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.SwingUtilities;

import abbot.Log;
import abbot.NoExitSecurityManager;
import abbot.util.AWT;
import abbot.finder.*;
import abbot.finder.matchers.*;
import abbot.i18n.Strings;
import abbot.tester.Robot;
import abbot.tester.ComponentTester;
import abbot.tester.WindowTracker;

/** 
 * Provides applet launch capability.  Usage:<br>
 * <blockquote><code>
 *  &lt;applet code="..." [codebase="..."] [params="..."]
 * [archive="..."]&gt;<br>
 * </code></blockquote><p> 
 * The attributes are equivalent to those provided in the HTML
 * <code>applet</code> tag.  The <code>params</code> attribute is a
 * comma-separated list of <code>name=value</code> pairs, which will be passed
 * to the applet within the <code>applet</code> tag as <code>param</code>
 * elements. 
 * <p>
 * <em>WARNING: Closing the appletviewer window from the window manager
 * close button will result applet-spawned event dispatch threads being left
 * running.   To avoid this situation, always use the appletviewer <b>Quit</b>
 * menu item or use the {@link #terminate()} method of this class.</em> 
 */
public class Appletviewer extends Launch {

    private String code;
    private Map params;
    private String codebase;
    private String archive;
    private String width;
    private String height;
    private ClassLoader appletClassLoader;
    private Frame appletViewerFrame;
    private transient SecurityManager oldSM;
    private transient boolean terminating;

    private static final String DEFAULT_WIDTH = "100";
    private static final String DEFAULT_HEIGHT = "100";
    private static final String CLASS_NAME = "sun.applet.Main";
    private static final String METHOD_NAME = "main";
    private static final int LAUNCH_TIMEOUT = 30000;

    private static final String USAGE = 
        "<appletviewer code=\"...\" [params=\"name1=value1,...\"] "
        + "[codebase=\"...\"] [archive=\"...\"]>";

    private static void quitApplet(final Frame frame) {
        new ComponentTester().selectAWTMenuItem(frame, "Applet|Quit");
    }

    private static Map patchAttributes(Map map) {
        map.put(TAG_CLASS, CLASS_NAME);
        map.put(TAG_METHOD, METHOD_NAME);
        return map;
    }

    /** Create an applet-launching step. */
    public Appletviewer(Resolver resolver, Map attributes) {
        super(resolver, patchAttributes(attributes));
        code = (String)attributes.get(TAG_CODE);
        params = parseParams((String)attributes.get(TAG_PARAMS));
        codebase = (String)attributes.get(TAG_CODEBASE);
        archive = (String)attributes.get(TAG_ARCHIVE);
        width = (String)attributes.get(TAG_WIDTH);
        height = (String)attributes.get(TAG_HEIGHT);
    }

    /** Create an applet-launching step. */
    public Appletviewer(Resolver resolver, String description,
                        String code, Map params,
                        String codebase, String archive, String classpath) {
        super(resolver, description,
              CLASS_NAME, METHOD_NAME, null, 
              classpath, false);
        this.code = code;
        this.params = params;
        this.codebase = codebase;
        this.archive = archive;
    }

    /** Run this step.  Causes the appropriate HTML file to be written for use
        by appletviewer and its path used as the sole argument.
    */
    public void runStep() throws Throwable {
        File dir = new File(System.getProperty("user.dir"));
        File htmlFile = File.createTempFile("abbot-applet", ".html", dir);
        htmlFile.deleteOnExit();
        try {
            FileOutputStream os = new FileOutputStream(htmlFile);
            os.write(generateHTML().getBytes());
            os.close();
            setArguments(new String[] { "[" + htmlFile.getName() + "]" });
            super.runStep();
            // Wait for the applet to become visible
            long start = System.currentTimeMillis();
            ComponentFinder finder =
                new BasicFinder(getResolver().getHierarchy());
            Matcher matcher = new ClassMatcher(Applet.class, true);
            while (true) {
                try {
                    Component c = finder.find(matcher);
                    appletViewerFrame = (Frame)
                        SwingUtilities.getWindowAncestor(c);
                    addCloseListener(appletViewerFrame);
                    appletClassLoader = c.getClass().getClassLoader();
                    break;
                }
                catch(ComponentSearchException e) {
                }
                if (System.currentTimeMillis() - start > LAUNCH_TIMEOUT) {
                    throw new RuntimeException(Strings.get("step.appletviewer.launch_timed_out"));
                }
                Thread.sleep(200);
            }
        }
        finally {
            htmlFile.delete();
        }
    }

    private void addCloseListener(final Frame frame) {
        // Workaround for lockup when applet is closed via WM close button and
        // then relaunched.  Can't figure out how to kill those extant threads.
        // This avoids the lockup, but leaves threads around.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Invoke the frame's quit menu item
                quitApplet(frame);
            }
        });
    }

    /** Generate HTML suitable for launching this applet. */
    protected String generateHTML() {
        StringBuffer html = new StringBuffer();
        html.append("<html><applet code=\"" + getCode() + "\"");
        html.append(" width=\"" + getWidth() + "\""
                    + " height=\"" + getHeight() + "\"");
        if (getCodebase() != null) {
            html.append(" codebase=\"" + getCodebase() + "\"");
        }
        if (getArchive() != null) {
            html.append(" archive=\"" + getArchive() + "\"");
        }
        html.append(">");
        Iterator iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)params.get(key);
            html.append("<param name=\""
                        + key + "\" value=\"" + value + "\">");
        }
        html.append("</applet></html>");
        return html.toString();
    }

    public void setTargetClassName(String name) {
        if (CLASS_NAME.equals(name))
            super.setTargetClassName(name);
        else
            throw new IllegalArgumentException(Strings.get("step.call.immutable_class"));
    }

    public void setMethodName(String name) {
        if (METHOD_NAME.equals(name))
            super.setMethodName(name);
        else 
            throw new IllegalArgumentException(Strings.get("step.call.immutable_method"));
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() { return code; }

    public void setCodebase(String codebase) {
        this.codebase = codebase;
    }

    public String getCodebase() { return codebase; }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getArchive() { return archive; }

    public String getWidth() {
        return width != null ? width : DEFAULT_WIDTH;
    }
    public void setWidth(String width) {
        this.width = width;
        try { Integer.parseInt(width); }
        catch(NumberFormatException e) { this.width = null; }
    }
    public String getHeight() {
        return height != null ? height : DEFAULT_HEIGHT;
    }
    public void setHeight(String height) {
        this.height = height;
        try { Integer.parseInt(height); }
        catch(NumberFormatException e) { this.height = null; }
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    protected Map parseParams(String attribute) {
        Map map = new HashMap();
        if (attribute != null) {
            String[] list = ArgumentParser.parseArgumentList(attribute);
            for (int i=0;i < list.length;i++) {
                String p = list[i];
                int eq = p.indexOf("=");
                map.put(p.substring(0, eq), p.substring(eq + 1));
            }
        }
        return map;
    }

    public String[] getParamsAsArray() {
        ArrayList list = new ArrayList();
        // Ensure we always get a consistent order
        Iterator iter = new TreeMap(params).keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = (String)params.get(key);
            list.add(key + "=" + value);
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    public String getParamsAttribute() {
        return ArgumentParser.encodeArguments(getParamsAsArray());
    }

    public Map getAttributes() {
        Map map = super.getAttributes();
        map.put(TAG_CODE, getCode());
        if (params.size() > 0) 
            map.put(TAG_PARAMS, getParamsAttribute());
        if (getCodebase() != null)
            map.put(TAG_CODEBASE, getCodebase());
        if (getArchive() != null)
            map.put(TAG_ARCHIVE, getArchive());
        if (!DEFAULT_WIDTH.equals(getWidth()))
            map.put(TAG_WIDTH, getWidth());
        if (!DEFAULT_HEIGHT.equals(getHeight()))
            map.put(TAG_HEIGHT, getHeight());

        // don't need to store these
        map.remove(TAG_CLASS);
        map.remove(TAG_METHOD);
        map.remove(TAG_THREADED);
        map.remove(TAG_ARGS);

        return map;
    }

    public String getDefaultDescription() {
        String desc = Strings.get("step.appletviewer",
                                  new Object[] { getCode() });
        return desc;
    }

    public String getUsage() { return USAGE; }

    public String getXMLTag() { return TAG_APPLETVIEWER; }

    /** Returns the applet class loader. */
    public ClassLoader getContextClassLoader() {
        // Maybe use codebase/archive to have an alternative classpath?
        ClassLoader cl = super.getContextClassLoader();
        return appletClassLoader != null
            ? appletClassLoader : cl;
    }

    protected void install() {
        super.install();

        // Appletviewer expects the security manager to be an instance of
        // AppletSecurity.  Use the custom class loader, *not* the one for the
        // applet. 
        installAppletSecurityManager(super.getContextClassLoader());
    }

    /** Install a security manager if there is none; this is a workaround
     * to prevent sun's applet viewer's security manager from preventing
     * any classes from being loaded.
     */
    private void installAppletSecurityManager(ClassLoader cl) {
        oldSM = System.getSecurityManager();
        if (oldSM == null || (oldSM instanceof NoExitSecurityManager)) {
            Log.debug("install security manager");
            // NOTE: the security manager *must* be loaded with the same class
            // loader as the appletviewer.
            try {
                Class cls = Class.forName("abbot.script.AppletSecurityManager",
                                          true, cl);
                Constructor ctor = cls.getConstructor(new Class[] {
                    SecurityManager.class
                });
                SecurityManager sm = (SecurityManager)
                    ctor.newInstance(new Object[] { oldSM });
                System.setSecurityManager(sm);
            }
            catch(Exception exc) {
                Log.warn(exc);
            }
        }
        else {
            Log.debug("old sm=" + oldSM);
        }
    }

    /** To properly terminate, we need to invoke AppletViewer's appletQuit()
     * method (protected, but accessible).
     */
    public void terminate() {
        synchronized(this) {
            // Avoid recursion, since we'll return here when the applet
            // invokes System.exit.
            if (terminating)
                return;
            
            terminating = true;
        }
        Frame frame = appletViewerFrame;
        appletViewerFrame = null;
        try {
            // FIXME: figure out why closing the appletviewer window causes an
            // EDT hangup.  (maybe need to post this to the applet's event
            // queue?) 
            // Also figure out who's creating all the extra EDTs and dispose of
            // them properly, but it's probably not worth the effort.
            if (frame != null) {
                quitApplet(frame);
            }
            // Now clean up normally
            super.terminate();
            if (oldSM != null) {
                Log.debug("restore sm=" + oldSM);
                System.setSecurityManager(oldSM);
            }
            appletClassLoader = null;
        }
        finally {
            synchronized(this) {
                terminating = false;
            }
        }
    }
}
