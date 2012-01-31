package example;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

// TODO: Organize the layout with labels/tooltips/descriptions of what
// each one represents.  Illustrate all aspects of recording a Swing UI.
//
// Component types and actions
// Focus accelerators
// Menu items
// Static/dynamic popup menus
// Tooltips
// Menu/button accelerators
// Mnemonics
// Tab-based keyboard traversal
// Drag/drop

public class MyCode {

    private static class PopupAdapter extends MouseAdapter {
        private boolean dynamic;
        private JPopupMenu cachedMenu = null;
        private int invokes = 0;
        public PopupAdapter(boolean dynamic) {
            this.dynamic = dynamic;
        }
        /** Some platforms popup here... */
        public void mousePressed(MouseEvent ev) {
            maybePopup(ev);
        }
        /** And some platforms popup here... */
        public void mouseReleased(MouseEvent ev) {
            maybePopup(ev);
        }
        /** And just in case... */
        public void mouseClicked(MouseEvent ev) {
            maybePopup(ev);
        }
        private void maybePopup(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                JPopupMenu menu = getPopupMenu();
                menu.pack();
                menu.show((Component)ev.getSource(), ev.getX(), ev.getY());
            }
        }
        private JPopupMenu getPopupMenu() {
            JPopupMenu menu = cachedMenu;
            if (menu == null) {
                menu = new JPopupMenu();
                if (dynamic) {
                    menu.add(new JMenuItem("Invoked " + ++invokes + " times"));
                    menu.add(new JSeparator());
                }
                menu.add(new JMenuItem("Black"));
                menu.add(new JMenuItem("Blue"));
                menu.add(new JMenuItem("Orange"));
                JMenu submenu = new JMenu("Other");
                submenu.add(new JMenuItem("White"));
                submenu.add(new JMenuItem("Green"));
                menu.add(submenu);
                if (!dynamic)
                    cachedMenu = menu;
            }
            return menu;
        }
    }

    public static void setSystemProperty(String key, String value) {
        System.setProperty(key, value);
    }

    public static void main(String[] args) {

        final JFrame frame = new JFrame("My Code");/* {
            // This will cause a NPE in the hierarchy browser
            private String name = "My Code Frame";
            public String getName() { return name.toString(); }
            };*/
        JPanel pane = new JPanel();
        pane.setName("My Pane");
        JLabel label = new JLabel("Static");
        label.addMouseListener(new PopupAdapter(false));
        pane.add(label);
        label = new JLabel("Dynamic");
        label.addMouseListener(new PopupAdapter(true));
        pane.add(label);
        JButton button = new JButton("Button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(frame, "My Dialog Message");
            }
        });
        pane.add(button);
        JTextField tf = new CustomTextField("Text field");
        tf.setFocusAccelerator('a');
        tf.setName("My Text Field");
        pane.add(tf);
        JComboBox cb = new JComboBox();
        for (int i=0;i < 20;i++)
            cb.addItem("Combo " + i);
        pane.add(cb);
        //pane.add(new JSpinner());

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //System.out.println("Action: " + ev.getActionCommand());
            }
        };

        String[] myListData = { "zero", "one", "two", "three",
                                "four", "five",
                                "six", "seven", "eight"};
        JList myList = new JList(myListData);
        myList.setToolTipText("This is a list");
        myList.setName("My List");
        myList.setVisibleRowCount(4);
        JScrollPane myScrollPane = new JScrollPane(myList);
        myScrollPane.setName("My ScrollPane");
        pane.add(myScrollPane);

        DragLabel dl = new DragLabel("Drag me");
        dl.setToolTipText("You can drag this label onto the tree to the right");
        JPanel labeled = new JPanel(new BorderLayout());
        labeled.add(dl, BorderLayout.WEST);
        JTree myTree = new DropTree();
        myTree.addMouseListener(new PopupAdapter(true));
        myTree.setEditable(true);
        myTree.setVisibleRowCount(4);
        JScrollPane sp = new JScrollPane(myTree);
        sp.setBorder(new TitledBorder("Over here"));
        labeled.add(sp);
        pane.add(labeled);

        String[][] data = new String[][] {
            { "0 one", "0 two", "0 three", "0 four" },
            { "1 one", "1 two", "1 three", "1 four" },
            { "2 one", "2 two", "2 three", "2 four" },
            { "3 one", "3 two", "3 three", "3 four" },
            { "4 one", "4 two", "4 three", "4 four" },
            { "5 one", "5 two", "5 three", "5 four" },
        };
        String[] names = { "one", "two", "three", "four" };
        JTable table = new JTable(data, names);
        table.setPreferredScrollableViewportSize(new Dimension(200, myTree.getPreferredSize().height));
        JScrollPane scroll = new JScrollPane(table);
        pane.add(scroll);

        JTextArea ta = new JTextArea("Four score and seven hundred years ago, our forebears extended claws reaching from the innermost mind to the outer limits", 10, 20);
        ta.setFocusAccelerator('b');
        ta.setToolTipText("<html>This is some <b>HTML</b> tooltip<br>text to look at</html>");
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        pane.add(new JScrollPane(ta));

        JTabbedPane tp = new JTabbedPane();
        // 1.4 only
        //tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (int i=0;i < 10;i++) {
            tp.add("tab " + i, new JLabel("Contents " + i
                                          + "                             "));
        }
        pane.add(tp);

        frame.setContentPane(pane);
        JMenuBar menubar = new JMenuBar();
        menubar.setName("My Menu Bar");
        JMenu menu = new JMenu("File");

        JMenuItem mitem = new JMenuItem("Item 1");
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_I,
                                              KeyEvent.ALT_MASK);
        mitem.setAccelerator(ks);
        menu.add(mitem);
        mitem = new JMenuItem("Open");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.META_MASK);
        mitem.setAccelerator(ks);
        mitem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new JFileChooser().showOpenDialog(null);
            }
        });
        menu.add(mitem);

        JMenu submenu = new JMenu("File submenu"); 
        menu.add(submenu);
        mitem = new JMenuItem("Quit");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK);
        mitem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(mitem);

        mitem = new JMenuItem("Submenu item");
        mitem.addActionListener(al);
        submenu.add(mitem);

        JMenu menu2 = new JMenu("Edit");
        mitem = new JMenuItem("Copy");
        menu2.add(mitem);

        menubar.add(menu);
        menubar.add(menu2);
        frame.setJMenuBar(menubar);
        frame.pack();
        frame.setSize(400, 400);
        frame.show();
    }

}

class CustomTextField extends JTextField {
    public CustomTextField(String contents) {
        super(contents);
    }
    public String getText() { 
        return super.getText();
    }
}

class DropLabel extends JLabel {
    /** Target received drag. */
    public volatile boolean dragEntered = false;
    /** Target accepted the drop. */
    public volatile boolean dropAccepted = false;
    private DropTarget dropTarget = null;
    private DropTargetListener dtl = null;
    private boolean acceptDrops = false;
    private Color oldColor = null;
    public DropLabel(String name) { this(name, true); }
    public DropLabel(String name, boolean accept) {
        super(name);
        setName("DropLabel");
        acceptDrops = accept;
        dtl = new DropTargetListener() {
                public void dragEnter(DropTargetDragEvent e) {
                    dragEntered = true;
                    if (acceptDrops) {
                        oldColor = getForeground();
                        setForeground(Color.blue);
                        paintImmediately(getBounds());
                    }
                }
                public void dragOver(DropTargetDragEvent e) {
                    if (acceptDrops)
                        e.acceptDrag(e.getDropAction());
                }
                public void dragExit(DropTargetEvent e) {
                    if (acceptDrops) {
                        setForeground(oldColor);
                        paintImmediately(getBounds());
                    }
                }
                public void dropActionChanged(DropTargetDragEvent e) {
                    if (acceptDrops)
                        e.acceptDrag(e.getDropAction());
                }
                public void drop(DropTargetDropEvent e) {
                    if (acceptDrops) {
                        e.acceptDrop(e.getDropAction());
                        e.dropComplete(true);
                        dropAccepted = true;
                        setForeground(oldColor);
                        paintImmediately(getBounds());
                    }
                }
            };
        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
                                    dtl, true);
    }

}

class DragLabel extends DropLabel {
    private class DragData implements Transferable {
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {
                DataFlavor.stringFlavor
            };
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return true;
        }
        public Object getTransferData(DataFlavor flavor) {
            return getName();
        }
    }

    /** Drag gesture was recognized. */
    public volatile boolean dragStarted = false;
    /** Drag has left the building, er, Component. */
    public volatile boolean dragExited = false;
    /** Source registered a successful drop. */
    public volatile boolean dropSuccessful = false;
    /** Source got an indication the drag ended. */
    public volatile boolean dragEnded = false;
    public Exception exception = null;
    private DragGestureListener dgl = null;
    private DragSourceListener dsl = null;
    private DragSource dragSource = null;
    private int acceptedActions = DnDConstants.ACTION_COPY_OR_MOVE;
    private Color oldColor = null;
    public DragLabel(String name) { this(name, true); }
    public DragLabel(String name, final boolean acceptDrops) { 
        super(name, acceptDrops);
        setName("DragLabel (" + name + ")");
        dragSource = DragSource.getDefaultDragSource();
        dgl = new DragGestureListener() {
                public void dragGestureRecognized(DragGestureEvent e) {
                    if ((e.getDragAction() & acceptedActions) == 0)
                        return;
                    dragStarted = true;
                    try {
                        e.startDrag(acceptDrops
                                    ? DragSource.DefaultCopyDrop
                                    : DragSource.DefaultCopyNoDrop,
                                    new DragData(), dsl);
                        oldColor = getForeground();
                        setForeground(Color.red);
                        paintImmediately(getBounds());
                    }
                    catch(InvalidDnDOperationException idoe) {
                        exception = idoe;
                    }
                }
            };
        dsl = new DragSourceListener() {
                public void dragDropEnd(DragSourceDropEvent e) {
                    dropSuccessful = e.getDropSuccess();
                    dragEnded = true;
                    setForeground(oldColor);
                    paintImmediately(getBounds());
                }
                public void dragEnter(DragSourceDragEvent e) {
                }
                public void dragOver(DragSourceDragEvent e) {
                }
                public void dragExit(DragSourceEvent e) {
                    dragExited = true;
                }
                public void dropActionChanged(DragSourceDragEvent e) {
                }
            };
        dragSource.
            createDefaultDragGestureRecognizer(this, acceptedActions, dgl);
    }
}

class DropTree extends JTree {
    /** Target received drag. */
    public volatile boolean dragEntered = false;
    /** Target accepted the drop. */
    public volatile boolean dropAccepted = false;
    private DropTarget dropTarget = null;
    private DropTargetListener dtl = null;
    private int dropRow = -1;
    public DropTree() {
        setName("DropTree");
        setCellRenderer(new DefaultTreeCellRenderer() {
            private Font originalFont;
            private Color originalColor;
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value,
                                                          boolean sel,
                                                          boolean exp,
                                                          boolean leaf,
                                                          int row, 
                                                          boolean focus) {
                Component c = super.
                    getTreeCellRendererComponent(tree, value, sel, exp,
                                                 leaf, row, focus);
                if (c instanceof JLabel) {
                    JLabel label = (JLabel)c;
                    if (originalFont == null) {
                        originalFont = label.getFont();
                        originalColor = label.getForeground();
                    }
                    if (row == dropRow) {
                        label.setForeground(Color.blue);
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    }
                    else {
                        label.setForeground(originalColor);
                        label.setFont(originalFont);
                    }
                }
                return c;
            }
        });
        dtl = new DropTargetListener() {
            public void dragEnter(DropTargetDragEvent e) {
                dragEntered = true;
                Point where = e.getLocation();
                int row = getClosestRowForLocation(where.x, where.y);
                dropRow = row;
                if (row != -1) 
                    paintImmediately(getRowBounds(row));
            }
            public void dragOver(DropTargetDragEvent e) {
                e.acceptDrag(e.getDropAction());
                Point where = e.getLocation();
                int last = dropRow;
                dropRow = getClosestRowForLocation(where.x, where.y);
                if (last != -1)
                    paintImmediately(getRowBounds(last));
                if (dropRow != -1) 
                    paintImmediately(getRowBounds(dropRow));
            }
            public void dragExit(DropTargetEvent e) {
                if (dropRow != -1) {
                    int repaint = dropRow;
                    dropRow = -1;
                    paintImmediately(getRowBounds(repaint));
                }
            }
            public void dropActionChanged(DropTargetDragEvent e) {
                e.acceptDrag(e.getDropAction());
            }
            public void drop(DropTargetDropEvent e) {
                e.acceptDrop(e.getDropAction());
                e.dropComplete(true);
                dropAccepted = true;
                if (dropRow != -1) {
                    int repaint = dropRow;
                    dropRow = -1;
                    paintImmediately(getRowBounds(repaint));
                }
            }
        };
        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
                                    dtl, true);
    }
    
}
