package abbot.editor;

import java.awt.*;
import java.io.*;
import java.util.*;

import abbot.script.*;
import abbot.finder.Hierarchy;

/** Provides support for writing out a GUI hierarchy as XML. */
public class HierarchyWriter {
    private Hierarchy hierarchy;
    
    public HierarchyWriter(Hierarchy h) {
        this.hierarchy = h;
    }
    /** Write to the given writer the GUI hierarchy represented by the given
     * set of root windows.
     */
    public void writeHierarchy(Writer writer)
        throws IOException {
        Resolver r = new Script(hierarchy);
        writer.write("<awtHierarchy>\r\n");
        Iterator iter = hierarchy.getRoots().iterator();
        while (iter.hasNext()) {
            writeComponent(writer, r, (Window)iter.next(), 1);
        }
        writer.write("</awtHierarchy>\r\n");
        writer.close();
    }

    // FIXME should include java.awt.MenuBar
    private void writeComponent(Writer writer, Resolver r,
                                Component c, int level) throws IOException {
        ComponentReference ref = new ComponentReference(r, c);
        final String INDENT = "  ";
        String xml = ref.toXMLString();
        for (int i=0;i < level;i++) {
            writer.write(INDENT);
        }
        Collection set = hierarchy.getComponents(c);
        if (set.size() != 0) {
            writer.write(xml.substring(0, xml.length() - 2));
            writer.write(">\r\n");
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                writeComponent(writer, r, (Component)iter.next(), level + 1);
            }
            for (int i=0;i < level;i++) {
                writer.write(INDENT);
            }
            writer.write("</component>\r\n");
        }
        else {
            writer.write(xml);
            writer.write("\r\n");
        }
    }
}
