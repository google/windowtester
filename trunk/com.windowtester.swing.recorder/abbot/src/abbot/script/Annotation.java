package abbot.script;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jdom.*;
import org.jdom.Element;

import abbot.*;
import abbot.finder.*;
import abbot.i18n.Strings;
import abbot.util.Properties;

/** Provides a method for communicating a message on the display.  May display
    for a reasonable delay or require user input to continue.<p>
    Usage:<br>
    <blockquote><code>
    &lt;annotation [userDismiss="true"] &gt;Text or HTML message&lt;/annotation&gt;<br>
    </code></blockquote>
    <p>
    Properties:<br>
    abbot.annotation.min_delay: minimum time to display an annotation<br>
    abbot.annotation.delay: per-word time to display an annotation<br>
 */

public class Annotation extends Step {

    public static final String TAG_ANNOTATION = "annotation";
    public static final String TAG_USER_DISMISS = "userDismiss";
    private static final String USAGE = 
        "<annotation [title=\"...\"] [component=\"<component ID>\"] [x=XX y=YY] [width=WWW height=HHH] [userDismiss=\"true\"]>Text or HTML message</annotation>";

    private static final int WORD_SIZE = 6;
    private static final Color BACKGROUND =
        new Color((Color.yellow.getRed() + Color.white.getRed()*3)/4,
                  (Color.yellow.getGreen() + Color.white.getGreen()*3)/4,
                  (Color.yellow.getBlue() + Color.white.getBlue()*3)/4);

    private String title;
    private String componentID;
    private boolean userDismiss;
    private static int minDelay = 5000;
    private static int delayUnit = 250;
    private String text = "";
    private int x = -1;
    private int y = -1;
    private int width = -1;
    private int height = -1;

    class WindowLock {}
    private transient Object WINDOW_LOCK = new WindowLock();
    private transient volatile Frame frame;
    private transient volatile AnnotationWindow window;
    private transient Point anchorPoint;
    private transient boolean ignoreChanges;

    static {
        minDelay = Properties.getProperty("abbot.annotation.min_delay",
                                          minDelay, 0, 10000);
        delayUnit = Properties.getProperty("abbot.annotation.delay_unit",
                                           delayUnit, 1, 5000);
    }

    public Annotation(Resolver resolver, Element el, Map attributes) {
        super(resolver, attributes);
        componentID = (String)attributes.get(TAG_COMPONENT);
        userDismiss = attributes.get(TAG_USER_DISMISS) != null;
        setTitle((String)attributes.get(TAG_TITLE));
        String xs = (String)attributes.get(TAG_X);
        String ys = (String)attributes.get(TAG_Y);
        if (xs != null && ys != null) {
            try { x = Integer.parseInt(xs); y = Integer.parseInt(ys); }
            catch(NumberFormatException nfe) { x = y = -1; }
        }
        String ws = (String)attributes.get(TAG_WIDTH);
        String hs = (String)attributes.get(TAG_HEIGHT);
        if (ws != null & hs != null) {
            try {
                width = Integer.parseInt(ws);
                height = Integer.parseInt(hs);
            }
            catch(NumberFormatException nfe) { width = height = -1; }
        }

        String text = null;
        Iterator iter = el.getContent().iterator();
        while(iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof CDATA) {
                text = ((CDATA)obj).getText();
                break;
            }
        }
        if (text == null) {
            text = el.getText();
        }
        setText(text);
    }

    public Annotation(Resolver resolver, String description) {
        super(resolver, description);
    }

    public boolean isShowing() {
        synchronized(WINDOW_LOCK) {
            return window != null;
        }
    }

    private void showAnnotationWindow() {
        Window win = getWindow();
        win.pack();
        Point where = null;
        if (anchorPoint != null) {
            where = new Point(anchorPoint);
        }
        if (x != -1 && y != -1) {
            if (where != null) {
                where.x += x; where.y += y;
            }
            else {
                where = new Point(x, y);
            }
        }
        if (where != null) {
            win.setLocation(where);
        }
        if (width != -1 && height != -1) {
            win.setSize(new Dimension(width, height));
        }
        win.show();        
    }

    public void showAnnotation() {
        dispose();
        if (SwingUtilities.isEventDispatchThread()) {
            showAnnotationWindow();
        }
        else try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    showAnnotationWindow();
                }
            });
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() { }
            });
        }
        catch(Exception e) {
            Log.warn(e);
        }
    }

    public long getDelayTime() { 
        long time = (getText().length() / WORD_SIZE) * delayUnit;
        return Math.max(time, minDelay);
    }

    /** Display a non-modal window. */
    protected void runStep() throws Throwable {
        ignoreChanges = true;
        showAnnotation();
        ignoreChanges = false;
        long start = System.currentTimeMillis();
        while ((userDismiss && window != null && window.isShowing())
               || (!userDismiss
                   && System.currentTimeMillis() - start < getDelayTime())) {
            try { Thread.sleep(200); }
            catch(InterruptedException e) { }
            Thread.yield();
        }
        if (!userDismiss) {
            dispose();
        }
    }

    public Window getWindow() {
        synchronized(WINDOW_LOCK) {
            if (window == null) {
                window = createWindow();
            }
            return window;
        }
    }

    // expects to have the WINDOW_LOCK
    private AnnotationWindow createWindow() {
        Component parent = null;
        AnnotationWindow w = null;
        Frame f = null;
        anchorPoint = null;
        if (componentID != null) {
            try {
                parent = (Component)ArgumentParser.eval(getResolver(),
                                                        componentID,
                                                        Component.class);
                Point loc = parent.getLocationOnScreen();
                anchorPoint = new Point(loc.x, loc.y);
                while (!(parent instanceof Dialog)
                       && !(parent instanceof Frame)) {
                    parent = parent.getParent();
                }
                w = (parent instanceof Dialog)
                    ? (title != null
                       ? new AnnotationWindow((Dialog)parent, title)
                       : new AnnotationWindow((Dialog)parent))
                    : (title != null
                       ? new AnnotationWindow((Frame)parent, title)
                       : new AnnotationWindow((Frame)parent));
            }
            catch(ComponentSearchException e) {
                // Ignore the exception and display it in global coords
                Log.warn(e);
            }
            catch(NoSuchReferenceException nsr) {
                // Ignore the exception and display it in global coords
                Log.warn(nsr);
            }
        }
        if (w == null) {
            f = new Frame();
            w = (title != null) 
                ? new AnnotationWindow(f, title)
                    : new AnnotationWindow(f);
        }
        JPanel pane = (JPanel)w.getContentPane();
        pane.setBackground(BACKGROUND);
        pane.setLayout(new BorderLayout());
        pane.setBorder(new EmptyBorder(4,4,4,4));
        JLabel label = new JLabel(replaceNewlines(text));
        pane.add(label, BorderLayout.CENTER);
        if (userDismiss) {
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setBackground(BACKGROUND);
            JButton close = new JButton(Strings.get("annotation.continue"));
            bottom.add(close, BorderLayout.EAST);
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    dispose();
                }
            });
            pane.add(bottom, BorderLayout.SOUTH);
        }
        // If the user closes the window, make sure we continue execution
        w.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
            public void windowClosed(WindowEvent we) {
                dispose();
            }
        });
        frame = f;
        return w;
    }

    private void dispose() {
        Window w;
        Frame f;
        synchronized(WINDOW_LOCK) {
            w = window;
            f = frame;
            window = null;
            frame = null;
        }
        if (w != null) {
            if (f != null) {
                getResolver().getHierarchy().dispose(f);
            }
            getResolver().getHierarchy().dispose(w);
        }
    }

    private String replaceNewlines(String text) {
        boolean needsHTML = false;
        String[] breaks = { "\r\n", "\n" };
        for (int i=0;i < breaks.length;i++) {
            int index = text.indexOf(breaks[i]);
            while (index != -1) {
                needsHTML = true;
                text = text.substring(0, index) + "<br>"
                    + text.substring(index + breaks[i].length());
                index = text.indexOf(breaks[i]);
            }
        }
        if (needsHTML && !text.startsWith("<html>")) {
            text = "<html>" + text + "</html>";
        }
        return text;
    }

    private void updateWindowSize(Window win) {
        if (window != win)
            return;
        Dimension size = win.getSize();
        width = size.width;
        height = size.height;
    }

    private void updateWindowPosition(Window win) {
        if (window != win) 
            return;

        Point where = win.getLocation();
        x = where.x;
        y = where.y;
        if (anchorPoint != null) {
            // Update the window location
            x -= anchorPoint.x;
            y -= anchorPoint.y;
        }
    }

    public String getDefaultDescription() {
        String desc = "Annotation";
        if (!"".equals(getText())) {
            desc += ": " + getText();
        }
        return desc;
    }

    public String getUsage() { return USAGE; }

    public String getXMLTag() { return TAG_ANNOTATION; }

    protected Element addContent(Element el) {
        return el.addContent(new CDATA(getText()));
    }

    public Map getAttributes() {
        Map map = super.getAttributes();
        if (componentID != null) {
            map.put(TAG_COMPONENT, componentID);
        }
        if (userDismiss) {
            map.put(TAG_USER_DISMISS, "true");
        }
        if (title != null) {
            map.put(TAG_TITLE, title);
        }
        if (x != -1 || y != -1) {
            map.put(TAG_X, String.valueOf(x));
            map.put(TAG_Y, String.valueOf(y));
        }
        if (width != -1 || height != -1) {
            map.put(TAG_WIDTH, String.valueOf(width));
            map.put(TAG_HEIGHT, String.valueOf(height));
        }
        return map;
    }

    public boolean getUserDismiss() { return userDismiss; }
    public void setUserDismiss(boolean state) { userDismiss = state; }
    public String getRelativeTo() { return componentID; }
    public void setRelativeTo(String id) { componentID = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public void setDisplayLocation(Point pt) {
        if (pt != null) {
            x = pt.x; y = pt.y;
        }
        else {
            x = y = -1;
        }
    }
    public Point getDisplayLocation() {
        if (x != -1 || y != -1)
            return new Point(x, y);
        return null;
    }

    class AnnotationWindow extends JDialog {
        public AnnotationWindow(Dialog parent, String title) {
            super(parent, title);
            addListener();
        }
        public AnnotationWindow(Dialog parent) {
            super(parent);
            addListener();
        }
        public AnnotationWindow(Frame parent, String title) {
            super(parent, title);
            addListener();
        }
        public AnnotationWindow(Frame parent) {
            super(parent);
            addListener();
        }
        private void addListener() {
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    if (!ignoreChanges && AnnotationWindow.this.isShowing())
                        updateWindowSize(AnnotationWindow.this);
                }
                public void componentMoved(ComponentEvent ce) {
                    if (!ignoreChanges && AnnotationWindow.this.isShowing())
                        updateWindowPosition(AnnotationWindow.this);
                }
            });
        }
    }
}

