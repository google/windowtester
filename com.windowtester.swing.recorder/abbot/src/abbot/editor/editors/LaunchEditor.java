package abbot.editor.editors;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import abbot.i18n.Strings;
import abbot.script.Launch;
import abbot.editor.widgets.ArrayEditor;
import abbot.util.PathClassLoader;

/** Provide convenient editing of a launch step. */
public class LaunchEditor extends CallEditor {

    private Launch launch;

    private ArrayEditor classpath;
    private JCheckBox thread;

    public static final String HELP_DESC = Strings.get("FixClassname");

    public LaunchEditor(Launch launch) {
        super(launch);
        this.launch = launch;

        String[] paths =
            PathClassLoader.convertPathToFilenames(launch.getClasspath());

        // FIXME extend ArrayEditor to use file choosing buttons instead of
        // text fields alone
        classpath = addArrayEditor(Strings.get("Classpath"), paths);
        classpath.setName(TAG_CLASSPATH);

        thread = addCheckBox(Strings.get("Thread"), launch.isThreaded());
        thread.setName(TAG_THREADED);
    }

    /** Display only the public static member functions. */
    protected String[] getMethodNames(Method[] mlist) {
        ArrayList list = new ArrayList();
        int mask = Modifier.PUBLIC | Modifier.STATIC;
        for (int i=0;i < mlist.length;i++) {
            if ((mlist[i].getModifiers() & mask) == mask) {
                list.add(mlist[i].getName());
            }
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == classpath) {
            Object[] values = classpath.getValues();
            String cp = null;
            if (values.length > 0) {
                StringBuffer buf = new StringBuffer();
                for (int i=0;i < values.length;i++) {
                    if (i > 0)
                        buf.append(System.getProperty("path.separator"));
                    String path = (String)values[i];
                    if ("".equals(path))
                        path = ".";
                    buf.append(path);
                }
                cp = buf.toString();
            }
            launch.setClasspath(cp);
            // Changing the classpath may affect whether the class/method are
            // valid. 
            validateTargetClass();
            validateMethod();
            fireStepChanged();
        }
        else if (src == thread) {
            launch.setThreaded(!launch.isThreaded());
            fireStepChanged();
        }
        else {
            super.actionPerformed(ev);
        }

        // Remove the default placeholder description
        if (HELP_DESC.equals(launch.getDescription())) {
            launch.setDescription(null);
            fireStepChanged();
        }
    }
}
