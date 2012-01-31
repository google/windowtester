package abbot.editor;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.PropertyCall;
import abbot.tester.ComponentTester;

class ComponentPropertyModel extends DefaultTableModel {
        
    public static final int PROPERTY_NAME = 0;
    public static final int PROPERTY_VALUE = 1;
    public static final int ACCESSIBLE = 2;
    public static final int METHOD_OBJECT = 3;

    private static Set filteredMethods = new HashSet();

    static {
        // Indicate things that aren't particularly interesting
        filteredMethods.addAll(Arrays.asList(new String[] {
            // Component
            "getAccessibleContext",
            "getAlignmentX",
            "getAlignmentY",
            "getColorModel",
            "getComponentListeners",
            "getComponentOrientation",
            "getDropTarget",
            "getFocusListeners",
            "getGraphics",
            "getGraphicsConfiguration",
            "getHierarchyBoundsListeners",
            "getHierarchyListeners",
            "getInputContext",
            "getInputMethodListeners",
            "getInputMethodRequests",
            "getKeyListeners",
            "getMouseListeners",
            "getMouseMotionListeners",
            "getMouseWheelListeners",
            "getParent",
            "getPeer",
            "getToolkit",
            "getTreeLock",
            // Container
            "getComponents",
            "getContainerListeners",
            // JComponent
            "getActionMap",
            "getAncestorListeners",
            "getAutoscrolls",
            "getBufferStrategy",
            "getDebugGraphicsOptions",
            "getInputMap",
            "getInputVerifier",
            "getPropertyChangeListeners",
            "getRegisteredKeyStrokes",
            "getRootPane",
            "getTopLevelAncestor",
            "getUIClassID",
            "getVerifyInputWhenFocusTarget",
            "getVetoableChangeListeners",
            "getVisibleRect",
            "isFocusCycleRoot",
            "isOpaque",
            "isOptimizedDrawingEnabled",
            "isPaintingTile",
            "isPreferredSizeSet",
            "isRequestFocusEnabled",
            "isValidateRoot",
            // Window
            "getOwnedWindows",
            "getWindowFocusListeners",
            "getWindowListeners",
            "getWindowStateListeners",
            // Frame
            "getFrames",
        }));
    }
    
    private Map noAccess = new WeakHashMap();

    /** Install the given filtered property method properties.  Add-on
        ComponentTester classes should invoke this for the list of property
        methods they want to appear in the filtered property list. */
    public static void setFilteredPropertyMethods(String[] methods) {
        filteredMethods.addAll(Arrays.asList(methods));
    }

    /** Create a model with two columns, the property name and the property
     * value.
     */
    public ComponentPropertyModel() {
        super(new Object[]{ Strings.get("Name"),
                            Strings.get("Value") }, 0);
    }

    public void clear() {
        // The setNumRows() method is required to be compatible with JDK
        // 1.2.2 and should be replaced with setRowCount() for 1.3 and above.
        //propTableModel.setNumRows(0); 
        setRowCount(0);
    }

    public void setComponent(Component comp) {
        setComponent(comp, true);
    }
    
    /** The current list of property methods and values corresponds to this
     * component.
     */
    private Component currentComponent = null;
    /** Whether the current list is filtered. */
    private boolean filtered = false;

    /** Update the list of property methods based on the newly selected
        component.
    */
    public void setComponent(Component comp, boolean filter) {
        Class cls = comp != null ? comp.getClass() : null;
        if (currentComponent == comp
            && filter == filtered)
            return;

        clear();
        currentComponent = comp;
        filtered = filter;
        Method[] all = getPropertyMethods(cls, filter);
        Arrays.sort(all, new Comparator() {
            public int compare(Object o1, Object o2) {
                String n1 = getPropertyName(((Method)o1).getName());
                String n2 = getPropertyName(((Method)o2).getName());
                return n1.compareTo(n2);
            }
        });
        Object[] noArgs = new Object[0];
        Object[] oneArg = new Object[] { comp };
        for (int i=0;i < all.length;i++) {
            Method method = all[i];
            Object value = "";
            try {
                Object target = comp;
                Object[] args = noArgs;
                if (ComponentTester.class.
                    isAssignableFrom(method.getDeclaringClass())) {
                    target = ComponentTester.getTester(comp);
                    args = oneArg;
                }
                if ((method.getModifiers() & Modifier.PUBLIC) == 0
                    || (method.getDeclaringClass().getModifiers()
                        & Modifier.PUBLIC) == 0) {
                    noAccess.put(method, Boolean.TRUE);
                    method.setAccessible(true);
                }
                value = method.invoke(target, args);
            }
            catch(IllegalArgumentException e) {
                value = "<illegal argument>";
                Log.debug(e);
            }
            catch(InvocationTargetException e) {
                value = "<target exception>";
                Log.debug(e);
            }
            catch(IllegalAccessException e) {
                // method was somehow protected?
                value = "<not accessible>";
                Log.debug(e);
            }
            addRow(new Object[] { method, value });
        }
        fireTableDataChanged();
    }
    
    Method[] getPropertyMethods(Class cls, boolean filter) {
        if (cls == null) 
            return new Method[0];

        // Make sure we only get one of each named method 
        HashMap processed = new HashMap();
        Method[] methods = cls.getMethods();
        for(int i = 0; i < methods.length; i++) {
            if (isGetterMethod(methods[i], false)
                && !processed.containsKey(methods[i].getName())) {
                if (filter
                    && filteredMethods.contains(methods[i].getName())) {
                    continue;
                }
                processed.put(methods[i].getName(), methods[i]);
            }
        }
        // Now look up propert accessors provided by the corresponding
        // ComponentTester class.
        ComponentTester tester = ComponentTester.getTester(cls);
        methods = tester.getPropertyMethods();
        for (int i=0;i < methods.length;i++) {
            if (!processed.containsKey(methods[i].getName())) {
                // Properties provided by the ComponentTester are never
                // filtered. 
                processed.put(methods[i].getName(), methods[i]);
            }
        }
        
        return (Method[])processed.values().toArray(new Method[processed.size()]);
    }
    
    /**
     * Method to check if the method specified on the class
     * is a "getter" for an attribute of that class
     *
     * @param method The method to be tested
     * @return true if the method is a "getter"
     */
    private boolean isGetterMethod(Method method, boolean isTester) {
        Class[] types = method.getParameterTypes();
        int argc = types.length;
        return ((isTester && argc == 1
                 && Component.class.isAssignableFrom(types[0]))
                || (!isTester && argc == 0))
            && PropertyCall.isPropertyMethod(method);
    }

    /**
     * Method to "extract" the property name from the method name
     * following the convention specified for a bean
     *
     * @param methodName The name of the method
     * @return The name of the attribute
     */
    private String getPropertyName(String methodName) {
        String propName = methodName;
        if (methodName.startsWith("get")
            || methodName.startsWith("has")) {
            propName = methodName.substring(3);
        }
        else if(methodName.startsWith("is")) {
            propName = methodName.substring(2);
        }
        return propName.substring(0, 1).toLowerCase() + propName.substring(1);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /** Display the property name column apropriately. */
    public Object getValueAt(int row, int col) {
        // The Method object is in column zero, we want only the property part
        // to appear. 
        if (col == PROPERTY_NAME) {
            Method m = (Method)super.getValueAt(row, col);
            return getPropertyName(m.getName());
        }
        if (col == METHOD_OBJECT) {
            return (Method)super.getValueAt(row, 0);
        }
        if (col == ACCESSIBLE) {
            Method m = ((Method)getValueAt(row, METHOD_OBJECT));
            return noAccess.get(m) != null ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.getValueAt(row, col);
    }
}
