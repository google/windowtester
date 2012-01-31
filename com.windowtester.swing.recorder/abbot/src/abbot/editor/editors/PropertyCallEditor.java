package abbot.editor.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.tester.ComponentTester;

/** Provide convenient editing of a PropertyCall step. */
public abstract class PropertyCallEditor extends CallEditor {

    private JComboBox component;
    private PropertyCall call;

    public PropertyCallEditor(PropertyCall call) {
        super(call);
        this.call = call;
        component = addComponentSelector(Strings.get("ComponentID"),
                                         call.getComponentID(),
                                         call.getResolver(), true);
        component.setName(TAG_COMPONENT);
    }

    /** Get the class of the current component target, if any. */
    protected Class getComponentTargetClass(Class cls) {
        String[] args = getCall().getArguments();
        if (args.length == 1) {
            String id = args[0];
            ComponentReference ref =
                getCall().getResolver().getComponentReference(id);
            if (ref != null) {
                try {
                    return getCall().resolveClass(ref.getRefClassName());
                }
                catch(ClassNotFoundException e) {
                }
            }
        }
        return null;
    }

    /** If the target is Component or ComponentTester, provide a merge of
        property-like methods from both.
    */
    protected Map getMethods(Class cls, int mask) {
        boolean isTester = ComponentTester.class.isAssignableFrom(cls);
        boolean isComponent = Component.class.isAssignableFrom(cls);
        Class componentClass = isTester
            ? getComponentTargetClass(cls)
            : (isComponent ? cls : null);
        Class testerClass = isTester
            ? cls : (isComponent
                     ? ComponentTester.getTester(componentClass).getClass()
                     : null);
        if (!isTester && !isComponent)
            return super.getMethods(cls, mask);

        Map map = new HashMap();
        if (componentClass != null) {
            Iterator iter =
                super.getMethods(componentClass, mask).values().iterator();
            while (iter.hasNext()) {
                Method m = (Method)iter.next();
                if (PropertyCall.isPropertyMethod(m)) {
                    map.put(m.getName(), m);
                }
            }
        }
        // Scan the corresponding component tester for additional property
        // methods 
        try {
            ComponentTester tester = componentClass != null
                ? ComponentTester.getTester(componentClass)
                : (ComponentTester)testerClass.newInstance();
            Iterator iter = getComponentTesterMethods(tester).iterator();
            while (iter.hasNext()) {
                Method m = (Method)iter.next();
                map.put(m.getName(), m);
            }
        }
        catch(Exception e) {
        }
        return map;
    }

    protected boolean includeMethod(Class cls, Method m) {
        return true;
    }

    protected Collection getComponentTesterMethods(ComponentTester tester) {
        return Arrays.asList(tester.getPropertyMethods());
    }

    /** Synchronize the component selector with the PropertyCall data. */
    protected void componentChanged() {
        component.setSelectedItem(call.getComponentID());
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == component) {
            String id = (String)component.getSelectedItem();
            if (id != null)
                id = id.trim();
            if ("".equals(id)) {
                id = null;
            }
            ComponentReference ref =
                call.getResolver().getComponentReference(id);
            String tcn = ref != null
                ? ref.getRefClassName()
                : Component.class.getClass().getName();
            call.setComponentID(id);

            call.setTargetClassName(tcn);
            call.setArguments(new String[0]);
            targetClassChanged();
            argumentsChanged();

            fireStepChanged();
        }
        else if (src == method) {
            super.actionPerformed(ev);
            // When the method changes to or from a ComponentTester
            // pseudo-property method, we need to change the target class.
            try {
                Class cls = call.getTargetClass();
                String methodName = (String)method.getSelectedItem();
                Map methods = getMethods(cls, Modifier.PUBLIC);
                Method m = (Method)methods.get(methodName);
                if (m != null) {
                    Class newClass = m.getDeclaringClass();
                    if (ComponentTester.class.isAssignableFrom(newClass)
                        && Component.class.isAssignableFrom(cls)) {
                        String id = call.getComponentID();
                        if (id != null) {
                            call.setArguments(new String[] { id });
                            argumentsChanged();
                        }
                        call.setComponentID(null);
                        call.setTargetClassName(newClass.getName());
                        componentChanged();
                        targetClassChanged();

                        fireStepChanged();
                    }
                    else if (Component.class.isAssignableFrom(newClass)
                             && ComponentTester.class.isAssignableFrom(cls)
                             && call.getArguments().length == 1) {
                        newClass = getComponentTargetClass(cls);
                        String id = call.getArguments()[0];

                        call.setArguments(new String[0]);
                        call.setComponentID(id);
                        call.setTargetClassName(newClass.getName());
                        argumentsChanged();
                        componentChanged();
                        targetClassChanged();

                        fireStepChanged();
                    }
                }
            }
            catch(ClassNotFoundException e) {
                // don't care
            }
        }
        else {
            super.actionPerformed(ev);
        }
    }
}
