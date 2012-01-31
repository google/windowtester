package abbot.editor.editors;

import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.editor.widgets.*;

/**
   Provide an editor for call steps.
   @author Blake Christensen <bchristen@users.sourceforge.net>
   @author twall@users.sourceforge.net
 */
// TODO: add a help button for the selected method
public class CallEditor extends StepEditor {

    private Call call;
    protected JTextField target;
    protected JComboBox method;
    protected ArrayEditor arguments;
    private String[] names = new String[0];
    private boolean fieldChanging;

    public CallEditor(Call call) {
        super(call);
        this.call = call;

        target = addTextField(Strings.get("TargetClass"),
                              call.getTargetClassName());
        target.setName(TAG_CLASS);

        method = addComboBox(Strings.get("Method"),
                             call.getMethodName(),
                             getMethodNames());
        method.setName(TAG_METHOD);

        arguments = addArrayEditor(Strings.get("Arguments"),
                                   call.getArguments());
        arguments.setName(TAG_ARGS);
    }

    protected Call getCall() { return call; }

    /** Provide a list of method names corresponding to the current target
        class.  Be careful overloading this method, since it is called from
        the Constructor.
    */
    protected String[] getMethodNames() {
        try {
            Class cls = call.getTargetClass();
            String[] names = getMethodNames(getMethods(cls, Modifier.PUBLIC));
            Arrays.sort(names);
            return names;
        }
        catch(ClassNotFoundException e) {
            return new String[0];
        }
    }
    
    protected Class getTargetClass() throws ClassNotFoundException {
        try {
            return call.getTargetClass();
        }
        catch(NoClassDefFoundError e) {
            throw new ClassNotFoundException(e.getMessage());
        }
    }

    /** Return all public member and static methods. */
    protected Map getMethods(Class cls, int mask) {
        HashMap processed = new HashMap();
        while (cls != null) {
            Method[] methods = cls.getDeclaredMethods();
            for (int i=0;i < methods.length;i++) {
                if ((methods[i].getModifiers() & mask) == mask) {
                    processed.put(methods[i].getName(), methods[i]);
                }
            }
            cls = cls.getSuperclass();
        }
        return processed;
    }

    /** Convert the given array of methods into an array of strings.
        Subclasses can do additional filtering here.
    */
    protected String[] getMethodNames(Map methods) {
        return (String[])methods.keySet().toArray(new String[methods.size()]);
    }

    protected void validateTargetClass() {
        try {
            call.getTargetClass();
            target.setForeground(DEFAULT_FOREGROUND);
        }
        catch(ClassNotFoundException e) {
            target.setForeground(ERROR_FOREGROUND);
        }
        catch(NoClassDefFoundError e) {
            target.setForeground(ERROR_FOREGROUND);
        }
    }

    protected void validateMethod() {
        try {
            call.getMethod();
            method.setForeground(DEFAULT_FOREGROUND);
        }
        catch(IllegalArgumentException e) {
            method.setForeground(ERROR_FOREGROUND);
        }
        catch(NoSuchMethodException e) {
            method.setForeground(ERROR_FOREGROUND);
        }
        catch(ClassNotFoundException e) {
            method.setForeground(ERROR_FOREGROUND);
        }
        catch(NoClassDefFoundError e) {
            target.setForeground(ERROR_FOREGROUND);
        }
    }

    /** Sychronize the UI with the Call data. */
    private void availableMethodsChanged() {
        fieldChanging = true;
        String[] newNames = getMethodNames();
        boolean changed = newNames.length != names.length;
        for (int i=0;i < newNames.length && !changed;i++) {
            changed = !newNames[i].equals(names[i]);
        }
        if (changed) {
            method.setModel(new DefaultComboBoxModel(newNames));
            String name = call.getMethodName();
            if (!name.equals(method.getSelectedItem()))
                method.setSelectedItem(name);
            names = newNames;
        }
        fieldChanging = false;
    }

    /** Sychronize the UI with the Call data. */
    protected void targetClassChanged() {
        fieldChanging = true;
        target.setText(call.getTargetClassName());
        fieldChanging = false;
        availableMethodsChanged();
        validateTargetClass();
        validateMethod();
    }

    /** Sychronize the UI with the Call data. */
    protected void methodChanged() {
        if (!call.getMethodName().equals(method.getSelectedItem()))
            method.setSelectedItem(call.getMethodName());
        validateMethod();
    }

    /** Sychronize the UI with the Call data. */
    protected void argumentsChanged() {
        arguments.setValues(call.getArguments());
        validateMethod();
    }

    public void actionPerformed(ActionEvent ev) {
        if (fieldChanging)
            return;

        Object src = ev.getSource();
        if (src == target) {
            String cname = target.getText().trim();
            if (!cname.equals(call.getTargetClassName())) {
                call.setTargetClassName(cname);
                availableMethodsChanged();
                validateTargetClass();
                validateMethod();
                fireStepChanged();
            }
        }
        else if (src == method) {
            String name = (String)method.getSelectedItem();
            if (!name.equals(call.getMethodName())) {
                call.setMethodName(name);
                validateMethod();
                fireStepChanged();
            }
        }
        else if (src == arguments) {
            // FIXME check method signature and do component field if the
            // first arg is a component, do popup from available refs
            // FIXME check arguments against method signature
            Object[] values = arguments.getValues();
            String[] svalues = new String[values.length];
            System.arraycopy(values, 0, svalues, 0, values.length);
            call.setArguments(svalues);
            validateMethod();
            fireStepChanged();
        }
        else {
            super.actionPerformed(ev);
        }
    }
}

