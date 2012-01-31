package abbot.editor.editors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import abbot.i18n.Strings;
import abbot.script.Appletviewer;
import abbot.editor.widgets.ArrayEditor;

/** Provide convenient editing of an applet launch step. */
public class AppletviewerEditor extends StepEditor {

    public static final String HELP_DESC = Strings.get("editor.applet.desc");

    private Appletviewer applet;

    private JTextField code;
    private ArrayEditor params;
    private JTextField codebase;
    private JTextField archive;
    private JTextField width;
    private JTextField height;

    public AppletviewerEditor(Appletviewer applet) {
        super(applet);
        this.applet = applet;

        code = addTextField(Strings.get("editor.applet.code"),
                            applet.getCode());
        width = addTextField(Strings.get("editor.applet.width"),
                             applet.getWidth());
        height = addTextField(Strings.get("editor.applet.height"),
                              applet.getHeight());
        // For some reason, if we *don't* futz with the layout of
        // width/height, the pane never reconfigures properly for the array
        // editor. 
        Component c;
        ArrayList list = new ArrayList();
        while ((c = getComponent(getComponentCount()-1)) != code) {
            remove(c);
            list.add(c);
        }
        JPanel p = new JPanel();
        //p.setBorder(new TitledBorder(Strings.get("editor.applet.size")));
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        for (int i=list.size()-1;i >= 0;i--) {
            p.add((Component)list.get(i));
            if (i != 0)
                p.add(Box.createHorizontalStrut(MARGIN));
        }
        add(p);

        params = addArrayEditor(Strings.get("editor.applet.params"),
                                applet.getParamsAsArray());

        codebase = addTextField(Strings.get("editor.applet.codebase"),
                                applet.getCodebase());
        archive = addTextField(Strings.get("editor.applet.archive"),
                               applet.getArchive());
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == code) {
            applet.setCode(code.getText());
            fireStepChanged();
        }
        else if (src == params) {
            Object[] values = params.getValues();
            Map map = new HashMap();
            for (int i=0;i < values.length;i++) {
                String v = (String)values[i];
                int eq = v.indexOf("=");
                if (eq != -1) {
                    String key = v.substring(0, eq);
                    String value = v.substring(eq+1);
                    map.put(key, value);
                }
            }
            applet.setParams(map);
            fireStepChanged();
        }
        else if (src == codebase) {
            String value = codebase.getText();
            if ("".equals(value))
                value = null;
            applet.setCodebase(value);
            fireStepChanged();
        }
        else if (src == archive) {
            String value = archive.getText();
            if ("".equals(value))
                value = null;
            applet.setArchive(value);
            fireStepChanged();
        }
        else if (src == width) {
            String value = width.getText();
            if ("".equals(value))
                value = null;
            try {
                Integer.parseInt(value);
                applet.setWidth(value);
                width.setForeground(DEFAULT_FOREGROUND);
                fireStepChanged();
            }
            catch(NumberFormatException e) {
                width.setForeground(ERROR_FOREGROUND);
            }
        }
        else if (src == height) {
            String value = height.getText();
            if ("".equals(value))
                value = null;
            try {
                Integer.parseInt(value);
                applet.setHeight(value);
                width.setForeground(DEFAULT_FOREGROUND);
                fireStepChanged();
            }
            catch(NumberFormatException e) {
                width.setForeground(ERROR_FOREGROUND);
            }
        }
        else {
            super.actionPerformed(ev);
        }

        // Remove the default placeholder description
        if (HELP_DESC.equals(applet.getDescription())) {
            applet.setDescription(null);
            fireStepChanged();
        }
    }
}
