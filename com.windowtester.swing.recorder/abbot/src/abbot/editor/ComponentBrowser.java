package abbot.editor;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import abbot.*;
import abbot.editor.editors.XMLEditor;
import abbot.editor.widgets.*;
import abbot.finder.*;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.script.Resolver;
import abbot.tester.Robot;
import abbot.util.*;

/** Browse an existing component hierarchy.  Thanks to the JFCUnit guys for
 * the basis for this code.
 */
// FIXME put the component reference ID into a label, not in the status
public class ComponentBrowser
    extends JPanel implements ActionListener {
    private int TAB_HIERARCHY = 0;

    private JButton refreshButton;
    private JCheckBox filterButton;
    private JButton addAssertButton;
    private JButton addSampleButton;
    private JCheckBox filterPropertiesCheckBox;
    private Resolver resolver;
    private ComponentTree componentTree;
    private JTable propTable;
    private ReferencesModel refModel;
    private JTable refTable;
    private ComponentPropertyModel propertyModel;
    private JTable attributeTable;
    private ReferenceAttributeModel attributeModel;
    private JTable inputMapTable;
    private JTable actionMapTable;

    private boolean filter = true;

    private JTabbedPane tabs;
    private JTabbedPane tabs2;

    /** Currently selected component. */
    private Component selectedComponent = null;
    /** Is the currently selected component "fake"? */
    private boolean fakeComponent = false;
    /** Currently selected reference. */
    private ComponentReference selectedReference = null;
    private LocalHierarchy hierarchy;

    /**
     * Default constructor
     */
    public ComponentBrowser(Resolver r, Hierarchy h) {
        this.resolver = r;
        this.hierarchy = new LocalHierarchy(h);
        setName("browser");
        equip(this);
        setSelectedComponent(null);
        setSelectedReference(null);
    }

    /**
     * Method to create required widgets/components and populate
     * the content pane with them.
     *
     * @param pane   The content pane to which the created objects have to
     *               be added into.
     */
    private void equip(Container pane) {
        setLayout(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          createLeftPanel(),
                                          createRightPanel());
        // prefer tree over property table
        split.setResizeWeight(1.0);
        split.setDividerSize(4);
        split.setBorder(null);
        pane.add(split, BorderLayout.CENTER);
    }

    private JPanel createHierarchyView() {
        JPanel pane = new JPanel(new BorderLayout());
        componentTree = new ComponentTree(hierarchy);
        componentTree.setName("browser.hierarchy");
        componentTree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent e) {
                if (!ignoreHierarchyChange) {
                    setSelectedComponent(getSelectedComponentFromTree());
                }
            }
        });
        JScrollPane scroll = new JScrollPane(componentTree);
        scroll.getViewport().setBackground(componentTree.getBackground());
        pane.add(scroll, BorderLayout.CENTER);

        refreshButton = new JButton(Strings.get("browser.hierarchy.reload"));
        refreshButton.addActionListener(this);
        filterButton = new JCheckBox(Strings.get("browser.hierarchy.concise"));
        filterButton.addActionListener(this);
        filterButton.setSelected(filter);
        filterButton.setToolTipText(Strings.get("browser.hierarchy.filter.tip"));
        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(0, 0, 0, 0));
        buttons.add(refreshButton);
        buttons.add(filterButton);
        JPanel leftJustify = new JPanel(new BorderLayout());
        leftJustify.setBorder(new EmptyBorder(0, 0, 0, 0));
        leftJustify.add(buttons, BorderLayout.WEST);
        pane.add(leftJustify, BorderLayout.SOUTH);
        return pane;
    }

    /** Set the resolver on which the references list is based. */
    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
        refModel = new ReferencesModel(resolver);
        refTable.setModel(refModel);
    }

    private Component createReferenceView() {
        // FIXME need buttons for new/delete (delete only enabled if the
        // reference is entirely unused
        refModel = new ReferencesModel(resolver);
        refTable = new JTable(refModel) {
            public void setRowSelectionInterval(int start, int end) {
                super.setRowSelectionInterval(start, end);
                // Make sure the selection is always visible. 
                Rectangle cellRect = getCellRect(start, 0, true);
                if (cellRect != null)
                    super.scrollRectToVisible(cellRect);
            }
        };
        refTable.setName("browser.references");
        refTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refTable.setDefaultEditor(ComponentReference.class, new XMLEditor());
        refTable.clearSelection();
        ListSelectionListener lsl = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    referenceListSelectionChanged(lse);
                }
            }
        };
        refTable.getSelectionModel().addListSelectionListener(lsl);
        JScrollPane scroll = new JScrollPane(refTable);
        scroll.getViewport().setBackground(refTable.getBackground());
        return scroll;
    }

    /**
     * Create a tabbed pane for browsing either existing components or
     * component references.
     *
     * @return A JPanel for the left side of the main frame
     */
    private Component createLeftPanel() {
        tabs = new JTabbedPane() {
            public Dimension getPreferredSize() {
                return new Dimension(250, 200);
            }
        };
        tabs.add(Strings.get("Hierarchy"), createHierarchyView());
        tabs.setToolTipTextAt(0, Strings.get("browser.hierarchy.tip"));
        tabs.add(Strings.get("References"), createReferenceView());
        tabs.setToolTipTextAt(1, Strings.get("browser.references.tip"));
        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                tabChanged(e);
            }
        });
        return tabs;
    }

    /**
     * Create the property browser/selection table.
     *
     * @return A JPanel for the right side of the main frame
     */
    private Component createRightPanel() {
        JPanel pane = new JPanel(new BorderLayout());
        propertyModel = new ComponentPropertyModel();
        propTable = new JTable(propertyModel);
        propTable.setName("browser.properties");
        propTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        propTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propTable.setDefaultRenderer(Object.class, new PropertyRenderer());
        ListSelectionListener lsl = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    enableAssertSampleButtons();
                }
            }
        };
        propTable.getSelectionModel().addListSelectionListener(lsl);

        addAssertButton = new JButton("");
        addAssertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                firePropertyCheck(false);
            }
        });
        addAssertButton.setEnabled(false);
        updateAssertText(false, false);

        addSampleButton = new JButton(Strings.get("SampleProperty"));
        addSampleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                firePropertyCheck(true);
            }
        });
        addSampleButton.setEnabled(false);

        String waitKeyName = KeyEvent.getKeyText(ScriptEditor.KC_WAIT);
        String invertKeyName = KeyEvent.getKeyText(ScriptEditor.KC_INVERT);
        String tip = Strings.get("AssertPropertyTip",
                                 new Object[] {
                                     invertKeyName, waitKeyName,
                                 });
        addAssertButton.setToolTipText(TextFormat.tooltip(tip));
        tip = Strings.get("SamplePropertyTip");
        addSampleButton.setToolTipText(TextFormat.tooltip(tip));

        filterPropertiesCheckBox = new JCheckBox(Strings.get("Filter"));
        filterPropertiesCheckBox.addActionListener(this);
        filterPropertiesCheckBox.setEnabled(true);
        filterPropertiesCheckBox.setSelected(true); 
        filterPropertiesCheckBox.setToolTipText(Strings.get("browser.properties.filter.tip"));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addAssertButton);
        buttonsPanel.add(addSampleButton);
        buttonsPanel.add(filterPropertiesCheckBox);
        JPanel leftJustify = new JPanel(new BorderLayout());
        leftJustify.setBorder(new EmptyBorder(0, 0, 0, 0));
        leftJustify.add(buttonsPanel, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane(propTable);
        scroll.getViewport().setBackground(propTable.getBackground());
        scroll.setColumnHeaderView(propTable.getTableHeader());
        pane.add(scroll, BorderLayout.CENTER);
        pane.add(leftJustify, BorderLayout.SOUTH);

        attributeModel = new ReferenceAttributeModel();
        attributeModel.addTableModelListener(new AttributeListener());
        attributeTable = new JTable(attributeModel);
        JScrollPane scroll1 = new JScrollPane(attributeTable);
        scroll1.getViewport().setBackground(attributeTable.getBackground());

        inputMapTable = new JTable(InputMapModel.EMPTY);
        JScrollPane scroll2 = new JScrollPane(inputMapTable);
        scroll2.getViewport().setBackground(inputMapTable.getBackground());

        actionMapTable = new JTable(ActionMapModel.EMPTY);
        JScrollPane scroll3 = new JScrollPane(actionMapTable);
        scroll3.getViewport().setBackground(actionMapTable.getBackground());

        tabs2 = new JTabbedPane() {
            public Dimension getPreferredSize() {
                return new Dimension(300, 150);
            }
        };
        tabs2.add(Strings.get("browser.properties"), pane);
        tabs2.setToolTipTextAt(0, Strings.get("browser.properties.tip"));
        tabs2.add(Strings.get("browser.attributes"), scroll1);
        tabs2.setToolTipTextAt(1, Strings.get("browser.attributes.tip"));
        tabs2.add(Strings.get("browser.inputmap"), scroll2);
        tabs2.setToolTipTextAt(2, Strings.get("browser.inputmap.tip"));
        tabs2.add(Strings.get("browser.actionmap"), scroll3);
        tabs2.setToolTipTextAt(3, Strings.get("browser.actionmap.tip"));

        return tabs2;
    }

    public void updateAssertText(boolean isWait, boolean invert) {
        addAssertButton.setText(Strings.get(isWait ? "WaitProperty"
                                            : "AssertProperty",
                                            new Object[]{ 
                                                invert
                                                ? Strings.get("assert.not_equals")
                                                : Strings.get("assert.equals")
                                            }));
    }

    /** Select the given reference in the current view. */
    public void setSelectedReference(ComponentReference ref) {
        if (ref != selectedReference) {
            selectedReference = ref;
            updateReferenceSelection(ref);
            Component c = ref != null
                ? getComponentForReference(ref) : null;
            if (c != selectedComponent) {
                selectedComponent = c;
                updateComponentSelection(c);
            }
            fireSelectionChanged();
        }
    }

    /** Select the given component (and make it visible) in the current
        view.  Update the auxiliary view components appropriately.
        If showing component references and the given component doesn't have
        one, switch the view to the hierarchy.
    */ 
    public void setSelectedComponent(Component comp) {
        selectedComponent = comp;
        ComponentReference ref = null;
        if (comp != null && resolver.getHierarchy().contains(comp)) {
            ref = resolver.getComponentReference(comp);
        }
        if (ref != selectedReference) {
            selectedReference = ref;
            updateReferenceSelection(ref);
        }
        if (ref == null && !showingHierarchy()) {
            tabs.setSelectedIndex(TAB_HIERARCHY);
        }
        updateComponentSelection(comp);
        fireSelectionChanged();
    }

    /** Return the row index of the given component reference. */
    private int getRow(ComponentReference ref) {
        if (ref != null) {
            for (int i=0;i < refTable.getRowCount();i++) {
                ComponentReference value = (ComponentReference)
                    refTable.getValueAt(i, 0);
                if (ref == value) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** Flag to avoid responding to list/tree selection changes when they're
     * made programmatically instead of by the user.
     */
    private boolean ignoreHierarchyChange = false;
    private boolean ignoreReferenceChange = false;

    /** Set the appropriate selection in the reference list. */
    private void updateReferenceSelection(ComponentReference ref) {
        if (!showingHierarchy()) {
            int row = getRow(ref);
            ignoreReferenceChange = true;
            if (row == -1) {
                refTable.clearSelection();
            }
            else {
                refTable.setRowSelectionInterval(row, row);
            }
            ignoreReferenceChange = false;
        }
        updateAttributesList();
        updatePropertyList();
    }

    /** Set the appropriate selection in the component hierarchy tree. */
    private void updateComponentSelection(Component comp) {
        if (showingHierarchy()) {
            ignoreHierarchyChange = true;
            if (comp == null) {
                componentTree.clearSelection();
            }
            else {
                TreePath path = componentTree.getPath(comp);
                ComponentNode node =
                    (ComponentNode)path.getLastPathComponent();
                if (node.getUserObject() != comp) {
                    setCompactDisplay(false);
                    path = componentTree.getPath(comp);
                }
                componentTree.setSelectionPath(path);
            }
            ignoreHierarchyChange = false;
        }
        updateAttributesList();
        updatePropertyList();
    }

    /**
     * Utility method showing whether a component node has been selected or
     * not. 
     */
    public boolean isComponentSelected() {
        if (showingHierarchy()) {
            return (ComponentNode) 
                componentTree.getLastSelectedPathComponent() != null;
        }
        return refTable.getSelectedRow() != -1;
    }

    /** When re-enabled, perform a reload of the tree. */
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if (state)
            refresh();
    }

    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                componentTree.reload(null);
            }
        });
    }

    /** Convert the component reference into an actual component, creating a
     * dummy one if the real one is not available.
     */
    private Component getComponentForReference(ComponentReference ref) {
        Component comp = null;
        fakeComponent = false;
        try {
            comp = ref.getComponent();
        }
        catch(ComponentSearchException e) {
            if (e instanceof MultipleComponentsFoundException) {
                // FIXME query the user to select the right one?
                // the right one may not exist at this point in time.
                MultipleComponentsFoundException mc = 
                    (MultipleComponentsFoundException)e;
                Component[] list = mc.getComponents();
                String warning = "Multiple components found for "
                    + ref.toXMLString() + ": ";
                for (int i=0;i < list.length;i++) {
                    warning +=
                        "\n" + Robot.toHierarchyPath(mc.getComponents()[i]);
                }
                Log.warn(warning);
            }
            try {
                fakeComponent = true;
                comp = (Component)
                    (Class.forName(ref.getRefClassName())).newInstance();
                comp.setName(Strings.get("browser.hierarchy.proxy",
                                         new Object[] { ref.getID() }));
                if (comp instanceof Window) {
                    // make sure it never appears in the hierarchy
                    hierarchy.filter(comp);
                    componentTree.reload();
                }
            }
            catch(Exception exc) {
                // Not much we can do here; we require a no-args constructor
                // FIXME show a warning dialog
            }
        }
        return comp;
    }

    public boolean showingHierarchy() {
        return tabs.getSelectedIndex() == TAB_HIERARCHY;
    }

    /** Returns the currently selected reference. */
    public ComponentReference getSelectedReference() {
        return selectedReference;
    }

    /** Returns which component is currently selected. */
    public Component getSelectedComponent() {
        return selectedComponent;
    }

    /** Sets whether uninteresting components are elided from the display. */
    public void setCompactDisplay(boolean compact) {
        filter = compact;
        filterButton.setSelected(filter);
        hierarchy.setCompact(filter);
        componentTree.setHierarchy(hierarchy);
    }

    /** Returns whether the current display mode is compact. */
    public boolean isCompactDisplay() {
        return filter;
    }

    /**
     * Generic action handler for buttons.  
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == refreshButton) {
            refresh();
        }
        else if(e.getSource() == filterButton) {
            setCompactDisplay(!filter);
        }
        else if(e.getSource() == filterPropertiesCheckBox) {
            updatePropertyList();
        }
    }

    private void updateAttributesList() {
        attributeModel.setReference(selectedReference);
    }

    private void updatePropertyList() {
        int row = propTable.getSelectedRow();
        String savedProperty = row == -1 ? "" : (String)propTable.
            getValueAt(row, ComponentPropertyModel.PROPERTY_NAME);

        addAssertButton.setEnabled(false);
        addSampleButton.setEnabled(false);
        Component comp = showingHierarchy() || selectedReference != null
            ? selectedComponent : null;
        propertyModel.setComponent(comp, filterPropertiesCheckBox.isSelected());
        for (row=0;row < propTable.getRowCount();row++) {
            String prop = (String)propTable.
                getValueAt(row, ComponentPropertyModel.PROPERTY_NAME);
            if (prop.equals(savedProperty)) {
                propTable.setRowSelectionInterval(row, row);
                Rectangle rect = propTable.getCellRect(row, 0, true);
                propTable.scrollRectToVisible(rect);
                enableAssertSampleButtons();
                break;
            }
        }

        if (comp instanceof JComponent) {
            InputMap im = ((JComponent)comp).getInputMap();
            ActionMap am = ((JComponent)comp).getActionMap();
            inputMapTable.setModel(new InputMapModel(im));
            actionMapTable.setModel(new ActionMapModel(am));
        }
        else {
            inputMapTable.setModel(InputMapModel.EMPTY);
            actionMapTable.setModel(ActionMapModel.EMPTY);
        }
        inputMapTable.repaint();
        actionMapTable.repaint();
    }

    /** Returns the Component represented by the current tree selection. */
    private Component getSelectedComponentFromTree() {
        ComponentNode node = (ComponentNode)
            componentTree.getLastSelectedPathComponent();
        Component comp = node != null ? node.getComponent() : null;
        return comp;
    }

    private ComponentReference getReferenceAt(int row) {
        return (ComponentReference)refTable.getValueAt(row, 0);
    }

    /** Returns the component reference represented by the current selection
     * in the reference list.
     */
    private ComponentReference getSelectedReferenceFromList() {
        int refrow = refTable.getSelectedRow();
        return refrow == -1 ? null : getReferenceAt(refrow);
    }

    public void enableAssertSampleButtons() {
        int row = propTable.getSelectedRow();
        addAssertButton.setEnabled(row != -1 && isComponentSelected());
        addSampleButton.setEnabled(row != -1 && isComponentSelected());
    }

    /** Called when a the reference list selection changes, and when the
        property list changes. */
    public void referenceListSelectionChanged(ListSelectionEvent e) {
        if (!ignoreReferenceChange) {
            setSelectedReference(getSelectedReferenceFromList());
        }
    }

    /** Invoked when the hierarchy/reference tab changes. */
    public void tabChanged(ChangeEvent e) {
        if (showingHierarchy()) {
            // If we were viewing a fake component in the reference view,
            // switch to no component selection in the hierarchy view
            if (fakeComponent) {
                fakeComponent = false;
                setSelectedComponent(null);
            }
            else {
                updateComponentSelection(selectedComponent);
            }
        }
        else {
            // Bug on OSX always leaves a selection in the reference list
            // Avoid it by explicitly setting the selection if we can
            if (selectedReference == null && refTable.getRowCount() > 0) {
                setSelectedReference(getReferenceAt(0));
            }
            else {
                updateReferenceSelection(selectedReference);
            }
        }
    }

    private ArrayList listeners = new ArrayList();
    public void addSelectionListener(ComponentBrowserListener cbl) {
        ArrayList list = new ArrayList(listeners);
        list.add(cbl);
        listeners = list;
    }

    public void removeSelectionListener(ComponentBrowserListener cbl) {
        ArrayList list = new ArrayList(listeners);
        list.remove(cbl);
        listeners = list;
    }

    protected void fireSelectionChanged() {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ((ComponentBrowserListener)iter.next()).
                selectionChanged(this, selectedComponent, selectedReference);
        }
    }

    protected void firePropertyCheck(boolean sample) {
        int row = propTable.getSelectedRow();
        if (row == -1 || selectedComponent == null)
            return;
        Method m = (Method)
            propertyModel.getValueAt(row, ComponentPropertyModel.METHOD_OBJECT);
        Object value = 
            propertyModel.getValueAt(row, ComponentPropertyModel.PROPERTY_VALUE);
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            ((ComponentBrowserListener)iter.next()).
                propertyAction(this, m, value, sample);
        }
    }

    /** Provides filtering of another hierarchy to remove locally-spawned
     * throwaway components.  
     */
    private class LocalHierarchy extends CompactHierarchy {
        private Map filtered = new WeakHashMap();
        private Hierarchy raw = new AWTHierarchy();
        public LocalHierarchy(Hierarchy h) {
            super(h);
        }
        public Collection getRoots() {
            Collection roots = isCompact()
                ? super.getRoots() : raw.getRoots();
            roots.removeAll(filtered.keySet());
            return roots;
        }
        public Collection getComponents(Component c) {
            Collection kids = isCompact()
                ? super.getComponents(c) : raw.getComponents(c);
            kids.removeAll(filtered.keySet());
            return kids;
        }
        public boolean contains(Component c) {
            return (isCompact() 
                    ? super.contains(c) 
                    : raw.contains(c)) 
                && !filtered.containsKey(c);
        }
        public void filter(Component c) {
            filtered.put(c, Boolean.TRUE);
        }
    }

    private class AttributeListener implements TableModelListener {
        private boolean messaging = false;
        public void tableChanged(TableModelEvent e) {
            // Preserve the reference table selection
            // NOTE: only really need to message on ID changes, since that's
            // the only thing displayed in the reference table.
            // NOTE: does anything other than the Script use the cref id?
            // first arg of most actions, what else? 
            if (!messaging) {
                messaging = true; 
                ComponentReference ref = selectedReference;
                refModel.fireTableDataChanged();
                setSelectedReference(ref);
                messaging = false;
            }
        }
    }

    /** To be invoked when an underlying component reference has changed. */
    public void referencesChanged() {
        if (SwingUtilities.isEventDispatchThread())
            refModel.fireTableDataChanged();
        else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() { referencesChanged(); }
            });
        }
    }

    private class PropertyRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value,
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table,
                                                              value,
                                                              isSelected,
                                                              hasFocus,
                                                              row, column);
            value = propertyModel.getValueAt(row, ComponentPropertyModel.ACCESSIBLE);
            if (column == 1 && Boolean.TRUE.equals(value)) {
                setToolTipText(Strings.get("Inaccessible"));
                c.setBackground(Color.gray);
            }
            else {
                c.setBackground(UIManager.getColor("Table.background"));
            }
            return c;
        }
        protected void setValue(Object value) {
            String str = ArgumentParser.toString(value);
            setToolTipText(str);
            super.setValue(str == ArgumentParser.DEFAULT_TOSTRING
                           ? value.toString() : str);
        }
    }
}
