package abbot.finder;

import java.awt.Component;
import abbot.tester.Robot;

/** Indicates more than one component was found (usually where only one was
 * desired).
 */ 

public class MultipleComponentsFoundException extends ComponentSearchException {
    Component[] components;
    public MultipleComponentsFoundException(Component[] list) {
        components = list;
    }
    public MultipleComponentsFoundException(String msg, Component[] list) {
        super(msg);
        components = list;
    }
    public Component[] getComponents() { return components; }
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append(": ");
        for (int i=0;i < components.length;i++) {
            buf.append("\n (");
            buf.append(String.valueOf(i));
            buf.append(") ");
            buf.append(Robot.toHierarchyPath(components[i]));
            buf.append(": ");
            buf.append(components[i].toString());
        }
        return buf.toString();
    }
}
