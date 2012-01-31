package abbot.editor.editors;

import javax.swing.*;
import java.util.*;

import abbot.Log;
import abbot.script.*;
import abbot.tester.ComponentTester;

/** Edit a raw AWTEvent. */

public class EventEditor extends StepEditor {

    private Event event;
    JComboBox type;
    JComboBox kind;
    JComboBox cref;
    private boolean ignoreCombo = false;

    JTextField xValue;
    JTextField yValue;

    JTextField keyCode;

    public EventEditor(Event event) {
        super(event);
        this.event = event;
        String idtype = event.getType();
        String idkind = event.getKind();
        String[] typeValues = { "Mouse Event", "Key Event" };
        ignoreCombo = true;
        type = addComboBox("Type", typeValues[0], typeValues);
        type.setEnabled(false);
        type.setEditable(false);
        Resolver resolver = event.getResolver();
        String refid = event.getComponentID();
        if ("MouseEvent".equals(idtype)) {
            String[] kindValues = { "MOUSE_PRESSED", "MOUSE_RELEASED",
                                    "MOUSE_MOVED", "MOUSE_DRAGGED" };
            kind = addComboBox("Kind", idkind, kindValues);
            kind.setEditable(false);
            kind.setEnabled(false);
            cref = addComponentSelector("On Component", refid,
                                        resolver, false);
            xValue = addTextField("X", event.getAttribute(XMLConstants.TAG_X));
            yValue = addTextField("Y", event.getAttribute(XMLConstants.TAG_Y));
        }
        else if ("KeyEvent".equals(idtype)) {
            type.setSelectedItem(typeValues[1]);
            String[] kindValues = { "KEY_PRESSED", "KEY_RELEASED" };
            kind = addComboBox("Kind", idkind, kindValues);
            kind.setEditable(false);
            cref = addComponentSelector("On Component", refid,
                                        resolver, false);
            // FIXME make a popup w/all keycodes
            keyCode = addTextField("Key Code", event.getAttribute(XMLConstants.TAG_KEYCODE));
        }
        else {
            Log.warn("Unhandled ID type: " + idtype);
        }
        ignoreCombo = false;
    }

    public void actionPerformed(java.awt.event.ActionEvent ev) {
        Object src = ev.getSource();
        if (src == cref) {
            event.setComponentID((String)cref.getSelectedItem());
            fireStepChanged();
        }
        else if (src == kind) {
            if (!ignoreCombo) {
                event.setAttribute(XMLConstants.TAG_KIND, (String)kind.getSelectedItem());
                fireStepChanged();
            }
        }
        else if (src == xValue) {
            try {
                int value = Integer.parseInt(xValue.getText());
                event.setAttribute(XMLConstants.TAG_X, String.valueOf(value));
                xValue.setForeground(DEFAULT_FOREGROUND);
                fireStepChanged();
            }
            catch(NumberFormatException nfe) {
                xValue.setForeground(ERROR_FOREGROUND);
            }
        }
        else if (src == yValue) {
            try {
                int value = Integer.parseInt(yValue.getText());
                event.setAttribute(XMLConstants.TAG_Y, String.valueOf(value));
                yValue.setForeground(DEFAULT_FOREGROUND);
                fireStepChanged();
            }
            catch(NumberFormatException nfe) {
                yValue.setForeground(ERROR_FOREGROUND);
            }
        }
        else if (src == keyCode) {
            try {
                String codestr = keyCode.getText().trim();
                event.setAttribute(XMLConstants.TAG_KEYCODE, codestr);
                keyCode.setForeground(DEFAULT_FOREGROUND);
                fireStepChanged();
            }
            catch(IllegalArgumentException iae) {
                keyCode.setForeground(ERROR_FOREGROUND);
            }
        }
        else {
            super.actionPerformed(ev);
        }
    }

}
