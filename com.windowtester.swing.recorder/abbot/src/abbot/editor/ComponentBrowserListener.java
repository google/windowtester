package abbot.editor;

import java.awt.Component;

import abbot.script.ComponentReference;

public interface ComponentBrowserListener {
    public void selectionChanged(ComponentBrowser src,
                                 Component comp, ComponentReference ref);
    public void propertyAction(ComponentBrowser src,
                               java.lang.reflect.Method m,
                               Object value, boolean sample);
}
