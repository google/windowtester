package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import abbot.Log;
import abbot.util.*;

/** Preserves the current LAF for a given component hierarchy. */
public class LookAndFeelPreserver {
    private LookAndFeel laf;
    private Map owned = new WeakHashMap();
    /** Avoid GC of weak reference. */ 
    private AWTEventListener listener;

    private Frame frame;
    /** This panel detects any global attempts to set the UI for all
        components.  It can then restore the proper LAF for all registered
        components.  We use this pseudo-listener instead of listening to a
        "lookAndFeel" property change since the UI components may have
        updateUI called an arbitrary time (or not at all) after
        UIManager.setLookAndFeel is called (which triggers the property change
        notification). 
    */
    private JPanel trigger;

    public LookAndFeelPreserver(Component c) {
        this(UIManager.getLookAndFeel(), c);
    }
    public LookAndFeelPreserver(final LookAndFeel laf, Component c) {
        this.laf = laf;
        add(c);
        listener = new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
                if (e.getID() == ContainerEvent.COMPONENT_ADDED) {
                    componentAdded(((ContainerEvent)e).getChild());
                }
            }
        };
        new WeakAWTEventListener(listener, AWTEvent.CONTAINER_EVENT_MASK);
        String name = "updateComponentTreeUI Listener";
        frame = new Frame(name);
        frame.setName(name);
        trigger = new JPanel() {
            private boolean initialized;
            { initialized = true; }
            public void updateUI() {
                if (initialized) {
                    SwingUtilities.invokeLater(new LAFRestorer(laf, owned));
                }
            }
        };
        frame.add(trigger);
    }

    /** Add a component on which to preserve the LAF. */
    public void add(final Component c) {
        owned.put(c, Boolean.TRUE);
        if (c instanceof Window) {
            Window[] subs = ((Window)c).getOwnedWindows();
            for (int i=0;i < subs.length;i++) {
                add(subs[i]);
            }
        }
    }

    private void componentAdded(Component c) {
        Window w = AWT.getWindow(c);
        if (w != null) {
            if (owned.containsKey(w)
                || owned.containsKey(w.getParent())) {
                if (!owned.containsKey(w))
                    add(w);
                SwingUtilities.invokeLater(new LAFRestorer(laf, c));
            }
        }
    }

    private static class LAFRestorer implements Runnable {
        private LookAndFeel laf;
        private Map map;
        public LAFRestorer(LookAndFeel laf, Component c) {
            this(laf, new WeakHashMap());
            map.put(c, Boolean.TRUE);
        }
        public LAFRestorer(LookAndFeel laf, Map map) {
            this.laf = laf;
            this.map = map;
        }
        public void run() {
            LookAndFeel current = UIManager.getLookAndFeel();
            if (current != laf 
                && current != null && !current.equals(laf)) {
                try {
                    UIManager.setLookAndFeel(laf);
                    Iterator iter = map.keySet().iterator();
                    while (iter.hasNext()) {
                        Component c = (Component)iter.next();
                        SwingUtilities.updateComponentTreeUI(c);
                    }
                    UIManager.setLookAndFeel(current);
                }
                catch(UnsupportedLookAndFeelException e) {
                    // ignore
                }
            }
        }
    }
}
