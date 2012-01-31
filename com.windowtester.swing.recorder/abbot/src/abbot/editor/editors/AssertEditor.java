package abbot.editor.editors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.*;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.tester.ComponentTester;

/** Provide convenient editing of an Assert step. */
public class AssertEditor extends PropertyCallEditor {

    private Assert step;
    private JTextField value;
    private JCheckBox invert;
    private JCheckBox wait;
    private JTextField timeout = null;
    private JTextField interval = null;
    private int optionsIndex;

    public AssertEditor(Assert step) {
        super(step);
        this.step = step;

        value = addTextField(Strings.get("ExpectedResult"),
                             step.getExpectedResult());
        value.setName(TAG_VALUE);
        
        invert = addCheckBox(Strings.get("Invert"),
                             step.isInverted());
        invert.setName(TAG_INVERT);

        wait = addCheckBox(Strings.get("WaitToggle"),
                           step.isWait());
        wait.setName(TAG_WAIT);

        // Put wait/invert side by side
        Component c;
        while ((c = getComponent(getComponentCount()-1)) != value) {
            remove(c);
        }
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 2));
        p.add(invert);
        p.add(wait);
        add(p);
        optionsIndex = getComponentCount();

        configureWaitOptionFields();
    }

    /** Add in assertXXX methods to the list already generated. */
    protected Collection getComponentTesterMethods(ComponentTester tester) {
        
        ArrayList list =
            new ArrayList(Arrays.asList(tester.getPropertyMethods()));
        list.addAll(Arrays.asList(tester.getAssertMethods()));
        list.addAll(Arrays.asList(tester.getComponentAssertMethods()));
        return list;
    }

    private void configureWaitOptionFields() {

        if (step.isWait()) {
            if (timeout == null) {
                timeout = addTextField(Strings.get("Timeout"),
                                       String.valueOf(step.getTimeout()),
                                       String.valueOf(Assert.DEFAULT_TIMEOUT));
                timeout.setName(TAG_TIMEOUT);
                interval = addTextField(Strings.get("PollInterval"),
                                        String.valueOf(step.getPollInterval()),
                                        String.valueOf(Assert.DEFAULT_INTERVAL));
                interval.setName(TAG_POLL_INTERVAL);
            }
        }
        else if (timeout != null) {
            // remove them
            while (getComponentCount() > optionsIndex) {
                remove(getComponentCount()-1);
            }
            timeout = interval = null;
        }
        revalidate();
        repaint();
    }

    protected boolean validateTimeout(String value) {
        try {
            step.setTimeout(Long.parseLong(value));
            timeout.setForeground(DEFAULT_FOREGROUND);
            return true;
        }
        catch(NumberFormatException nfe) {
            timeout.setForeground(ERROR_FOREGROUND);
        }
        return false;
    }

    protected boolean validateInterval(String value) {
        try {
            step.setPollInterval(Long.parseLong(value));
            interval.setForeground(DEFAULT_FOREGROUND);
            return true;
        }
        catch(NumberFormatException nfe) {
            interval.setForeground(ERROR_FOREGROUND);
        }
        return false;
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == value) {
            step.setExpectedResult(value.getText());
            fireStepChanged();
        }
        else if (src == invert) {
            step.setInverted(!step.isInverted());
            fireStepChanged();
        }
        else if (src == wait) {
            step.setWait(!step.isWait());
            configureWaitOptionFields();
            fireStepChanged();
        }
        else if (src == timeout) {
            String value = timeout.getText();
            if (validateTimeout(value))
                fireStepChanged();
        }
        else if (src == interval) {
            String value = interval.getText();
            if (validateInterval(value))
                fireStepChanged();
        }
        else {
            super.actionPerformed(ev);
        }
    }
}
