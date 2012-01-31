package abbot.util;

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.*;
import java.util.*;

import abbot.Log;

/** Provides add-and-forget listening to the AWT event queue.
 * Provides an AWTEventListener which will automatically disconnect the
 * target listener when the target gets garbage-collected.   Once the target
 * is GC'd, this listener will remove itself from the AWT event listener list.
 */
public class WeakAWTEventListener implements AWTEventListener {

    private WeakReference listener;

    public WeakAWTEventListener(AWTEventListener l, long mask) {
        listener = new WeakReference(l);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, mask);
    }
    public void dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }
    public void eventDispatched(AWTEvent e) {
        AWTEventListener l = (AWTEventListener)listener.get();
        if (l != null) {
        	try {
            l.eventDispatched(e);
        	} catch(RuntimeException ex) {
        		throw ex;
        	}
        }
        else {
            dispose();
        }
    }
}
