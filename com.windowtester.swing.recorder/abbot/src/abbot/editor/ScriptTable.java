package abbot.editor;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.*;

import abbot.Log;
import abbot.script.*;

/** Provides a component to edit a test script.  A cursor indicates where
    insertions will be positioned.  Supports drag & drop within the component
    itself.<p>
    Actions supported:<br>
    move-rows-up<br>
    move-rows-down<br>
    toggle<br>
 */

public class ScriptTable extends JTable implements Autoscroll {
    private int cursorRow = 0;
    private Sequence cursorParent = null;
    private int cursorParentIndex = 0;
    private int cursorDepth = 0;
    private boolean isDragging = false;

    private DragSource dragSource;
    private DragSourceListener dragSourceListener;
    private static Icon openIcon;
    private static Icon closedIcon;
    private static int baseIndent;
    private static final int MARGIN = 4;

    static {
        URL url1 = ScriptTable.class.getResource("icons/triangle-dn.gif");
        URL url2 = ScriptTable.class.getResource("icons/triangle-rt.gif");
        if (url1 != null && url2 != null) {
            openIcon = new ImageIcon(url1);
            closedIcon = new ImageIcon(url2);
        }
        else {
            BasicTreeUI ui = (BasicTreeUI)(new JTree().getUI());
            openIcon = ui.getExpandedIcon();
            closedIcon = ui.getCollapsedIcon();
        }
        baseIndent = openIcon.getIconWidth();
    }

    private ScriptModel model;

    public ScriptTable() {
        this(new ScriptModel());
    }

    public ScriptTable(ScriptModel scriptModel) {
        super(scriptModel);
        setSelectionModel(new SelectionModel());
        model = scriptModel;
        TableCellRenderer cr = new ScriptTableCellRenderer();
        setDefaultRenderer(Object.class, cr);
        Dimension spacing = getIntercellSpacing();
        spacing.height = 2;
        setIntercellSpacing(spacing);

        initDragDrop();

        // Detect clicks on the table in order to position the cursor
        // and expand entries.
        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getModifiers() != InputEvent.BUTTON1_MASK)
                    return;
                if (me.getClickCount() == 2) {
                    int row = rowAtPoint(me.getPoint());
                    Log.debug("Toggling row at " + row);
                    toggle(row);
                }
                else {
                    click(me.getPoint());
                }
            }
        };
        addMouseListener(ml);
        // Set up our custom actions; note that there are no default input
        // bindings 
        ActionMap map = getActionMap();
        map.put("move-rows-up", new AbstractAction() {
            public void actionPerformed(ActionEvent ev) {
                moveUp();
            }
        });
        map.put("move-rows-down", new AbstractAction() {
            public void actionPerformed(ActionEvent ev) {
                moveDown();
            }
        });
        map.put("toggle", new AbstractAction() {
            public void actionPerformed(ActionEvent ev) {
                int selRow = getSelectedRow();
                if (selRow != -1) {
                    toggle(selRow);
                }
            }
        });
    }

    /** Toggle the open/closed state of a sequence. */
    public void toggle(int row) {
        int[] rows = getSelectedRows();
        if (rows.length > 0) {
            int anchor = getSelectionModel().getAnchorSelectionIndex();
            Step first = model.getStepAt(rows[0]);
            int lastRow = rows[rows.length-1];
            Step last = model.getStepAt(lastRow);
            Step afterLast = lastRow < getRowCount() - 1
                ? model.getStepAt(lastRow + 1) : null;
            clearSelection();
            model.toggle(row);
            int index0 = model.getRowOf(first);
            int index1 = model.getRowOf(last);
            // If the last step in the selection becomes hidden, change the
            // selection and the cursor position. 
            if (index1 == -1) {
                index1 = afterLast == null
                    ? row : model.getRowOf(afterLast) - 1;
            }
            if (anchor == index0) {
                Log.debug("Updating selection to " + index0 + " to " + index1);
                setRowSelectionInterval(index0, index1);
            }
            else {
                Log.debug("Updating selection to " + index1 + " to " + index0);
                setRowSelectionInterval(index1, index0);
            }
        }
        else {
            model.toggle(row);
        }
    }

    /** Return the bounding for the given cell.  If the step at the given row
     * is contained within a sequence, the rect will be offset to the right.
     */
    public Rectangle getCellRect(int row, int col, boolean includeBorder) {
        Rectangle rect = super.getCellRect(row, col, includeBorder);
        int indent = getIndentation(row);
        rect.x += indent;
        rect.width -= indent;
        return rect;
    }

    /** Return the number of pixels offset from the left edge of the table for
        the given row.
    */
    public int getIndentation(int row) {
        return getDepthIndentation(model.getNestingDepthAt(row));
    }

    /** Return the number of pixels offset from the left edge of the table for
        the given level of indentation.
    */
    public int getDepthIndentation(int depth) {
        return baseIndent * depth;
    }

    private void click(Point pt) {
        int row = rowAtPoint(pt);
        if (row == -1) {
            clearSelection();
            setCursorLocation(getRowCount());
        }
        else {
            Rectangle rect = getCellRect(row, 0, true);
            // If the click is on the open/close icon, then toggle
            if ((model.getStepAt(row) instanceof Sequence)
                && pt.x >= rect.x
                && pt.x < rect.x + baseIndent
                && pt.y > rect.y + rect.height / MARGIN
                && pt.y < rect.y + rect.height * (MARGIN-1)/MARGIN) {
                toggle(row);
            }
            else {
                setCursorLocation(pt);
            }
        }
    }

    private void initDragDrop() {
        int action = DnDConstants.ACTION_MOVE;
        dragSource = DragSource.getDefaultDragSource();
        DragGestureListener dgl = new DGListener();
        dragSource.createDefaultDragGestureRecognizer(this, action, dgl);
        dragSourceListener = new DSListener();

        DropTarget dt = new DropTarget(this, new DTListener());
        dt.setDefaultActions(DnDConstants.ACTION_MOVE);
    }

    /** Determine what the background color for the given step should be. */
    protected Color getStepColor(Step step, boolean selected) {
        return selected ? getSelectionBackground() : getBackground();
    }

    /** Returns the script context of the currently selected row. */
    public Script getScriptContext() {
        int row = getSelectedRow();
        if (row == -1)
            return model.getScript();
        return model.getScriptOf(row);
    }

    /** Returns the row number of the cursor.  The number of cursor locations
     * is one greater than the number of table 
     * entries.
     */
    public int getCursorRow() {
        return cursorRow;
    }

    /** Returns the target parent of the current cursor location. */
    public Sequence getCursorParent() {
        return cursorParent;
    }

    /** Returns the target index within the parent of the current cursor
        location. */
    public int getCursorParentIndex() {
        return cursorParentIndex;
    }

    protected Rectangle getCursorBounds() {
        Insets insets = getInsets();
        Dimension d = getSize();
        Dimension m = getIntercellSpacing();
        if (m.height == 0)
            m.height = 1;
        int width = d.width - insets.left - insets.right;
        int row = Math.min(cursorRow, getRowCount()-1);
        Rectangle cellRect = super.getCellRect(row, 0, false);
        int y = cellRect.y;
        if (cursorRow == getRowCount())
            y += cellRect.height + m.height;
        int indent = getDepthIndentation(cursorDepth);
        return new Rectangle(indent, y - m.height, width - indent, m.height);
    }

    /** Given an arbitrary point within the table, return the nearest valid
        row for the cursor to be placed.
    */
    private int getCursorRowAtPoint(Point where) {
        int row = rowAtPoint(where);
        if (row == -1) {
            row = where.y < 0 ? 0 : getRowCount();
        }
        else {
            Rectangle rect = super.getCellRect(row, 0, true);
            if (where.getY() > rect.y + rect.height / 2)
                ++row;
        }
        // When dragging, don't put the cursor somewhere which would produce
        // no effect, or which would be illegal.
        if (isDragging) {
            int selStart = getSelectedRow();
            int count = getSelectedRowCount();
            if (row > selStart && row <= selStart + count) {
                if (row > count / 2
                    && selStart + count + 1 < getRowCount()) {
                    row = selStart + count + 1;
                }
                else {
                    row = selStart;
                }
            }
        }

        return row;
    }

    // FIXME sometimes the cursor row leads the selected row by two
    public void setCursorLocation(Point where) {
        int row = getCursorRowAtPoint(where);
        setCursorLocation(row, where.x);
    }

    /** Set the cursor location, using the given indentation to determine the
     * appropriate target parent sequence.
     */
    private void setCursorLocation(int row, int indentation) {
        Rectangle oldRect = getCursorBounds();
        Script script = model.getScript();
        if (script == null)
            return;
        // Can't position the cursor after a terminate step
        if (script.hasTerminate() && row == getRowCount()) 
            --row;
        else if (script.hasLaunch() && row == 0)
            ++row;
        cursorRow = row;
        Sequence parent = script;
        int index = row == getRowCount()
            ? parent.size() : parent.indexOf(model.getStepAt(row));
        int depth = 0;
        if (row > 0) {
            // Place the cursor based on the previous step
            Step prev = model.getStepAt(row - 1);
            if (model.isOpen(prev)) {
                parent = (Sequence)prev;
                index = 0;
                // Depth is one greater than the depth of the parent
                depth = model.getNestingDepthAt(row - 1) + 1;
            }
            else {
                parent = model.getParent(prev);
                index = parent.indexOf(prev) + 1;
                depth = model.getNestingDepthAt(row - 1);
            }
            int indent = getDepthIndentation(depth);
            // Shift up the hierarchy until we reach the appropriate
            // indentation level.
            while (indent > indentation && parent != script) {
                Sequence nextUp = model.getParent(parent);
                index = nextUp.indexOf(parent) + 1;
                parent = nextUp;
                indent = getDepthIndentation(--depth);
            }
        }
        cursorParent = parent;
        cursorParentIndex = index;
        cursorDepth = depth;
        if (oldRect != null)
            repaint(oldRect);
        repaint(getCursorBounds());
    }

    /** Set the cursor location to a reasonable target for the given row. */
    public void setCursorLocation(int row) {
        setCursorLocation(row, 0);
    }

    protected void drawCursor(Graphics g, int row) {
        g.setColor(Color.green);
        ((Graphics2D)g).fill(getCursorBounds());
    }

    /** We paint a cursor where insertions will take effect. */
    public void paint(Graphics g) {
        super.paint(g);
        drawCursor(g, cursorRow == getRowCount()
                   ? cursorRow-1 : cursorRow);
    }

    public void autoscroll(Point pt) {
        Rectangle bounds = getBounds();
        Log.debug("autoscroll at " + pt + " bounds " + bounds);

        // Figure out which row we're on.
        int row = rowAtPoint(pt);
        if (row < 0)
            return;

        if (pt.y + bounds.y <= AUTOSCROLL_MARGIN) {
            if (row > 0) --row;
        }
        else {
            if (row < getRowCount() - 1) ++row;
        }
        scrollRectToVisible(getCellRect(row, 0, true));
    }

    private static final int AUTOSCROLL_MARGIN = 12;
    public Insets getAutoscrollInsets() {
        // Calculate the insets for the JTree, not the viewport the tree is
        // in. 
        Rectangle tree = getBounds();
        Rectangle view = getParent().getBounds();
        return new Insets(view.y - tree.y + AUTOSCROLL_MARGIN,
                          view.x - tree.x + AUTOSCROLL_MARGIN,
                          tree.height - view.height - view.y
                          + tree.y + AUTOSCROLL_MARGIN,
                          tree.width - view.width - view.x
                          + tree.x + AUTOSCROLL_MARGIN);
    }

    private class ScriptTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value,
                                                       boolean sel, 
                                                       boolean focus,
                                                       int row, int col) {
            // We know that the default renderer for a JTable
            // is a subclass of JLabel
            JLabel renderer = (JLabel)
                super.getTableCellRendererComponent(table,
                                                    value, sel,
                                                    focus, 
                                                    row, col);
            Step step = model.getStepAt(row);
            Icon icon = null;
            if (step instanceof Sequence) {
                icon = model.isOpen(row) ? openIcon : closedIcon;
            }
            renderer.setIcon(icon);
            
            super.setBackground(getStepColor(step, sel));
            setOpaque(true);

            return renderer;
        }
    }

    /** Return the first selected step. */
    public Step getSelectedStep() {
        return getSelectedRowCount() > 0
            ? model.getStepAt(getSelectedRow()) : null;
    }

    /** Return the set of selected steps, restricted to siblings of the first
        selected row.
    */
    public List getSelectedSteps() {
        ArrayList list = new ArrayList();
        int[] rows = getSelectedRows();
        if (rows.length > 0) {
            Step step = model.getStepAt(rows[0]);
            Sequence parent = model.getParent(step);
            for (int i=0;i < rows.length;i++) {
                step = model.getStepAt(rows[i]);
                if (model.getParent(step) == parent) {
                    list.add(step);
                }
            }
        }
        return list;
    }
    
    public boolean canMoveDown() {
        int[] rows = getSelectedRows();
        int max = getRowCount() - (model.getScript().hasTerminate()
                                   ? 2 : 1);
        return rows[rows.length-1] < max;
    }

    public boolean canMoveUp() {
        int row = getSelectedRow();
        int min = model.getScript().hasLaunch() ? 1 : 0;
        return row > min && !(model.getStepAt(row) instanceof Terminate);
    }

    /** Move the selected step(s) up.  If the previous row is part of an open
     * sequence, move to the end of the sequence.  Otherwise, switch places
     * with the step at the previous row.
     */
    public void moveUp() {
        if (!canMoveUp())
            return;
        List list = getSelectedSteps();
        int leadRow = getSelectedRow();
        Step lead = (Step)list.get(0);
        Sequence parent = model.getParent(lead);
        Step prev = model.getStepAt(leadRow - 1);
        int targetIndex = 0;
        // If the previous row to the selection is its parent, move previous
        // to the parent.
        if (parent.indexOf(lead) == 0) {
            Log.debug("Move out of sequence");
            Sequence newParent = model.getParent(parent);
            targetIndex = newParent.indexOf(parent);
            parent = newParent;
        }
        // Is the previous step part of an open sequence?
        else if (model.isOpen(prev)) {
            Log.debug("Move to previous empty open sequence");
            parent = (Sequence)prev;
            targetIndex = 0;
        }
        else if (model.getParent(prev) != parent) {
            Log.debug("Move to previous open sequence");
            parent = model.getParent(prev);
            targetIndex = parent.indexOf(prev) + 1;
        }
        else {
            Log.debug("Move previous");
            targetIndex = parent.indexOf(prev);
        }
        moveSelectedRows(parent, targetIndex);
    }

    /** Move the currently selected rows down one row. */
    public void moveDown() {
        if (!canMoveDown()) {
            Log.warn("Unexpected move down state");
            return;
        }
        List list = getSelectedSteps();
        Step lead = (Step)list.get(0);
        Sequence leadParent = model.getParent(lead);
        int[] rows = getSelectedRows();
        Step next = model.getStepAt(rows[rows.length-1] + 1);
        Sequence parent = model.getParent(next);
        // Default behavior moves after the next step
        int targetIndex = parent.indexOf(next) + 1;
        // If the next step after the selection is not a sibling,
        // make the group a sibling to its old parent
        if (leadParent != parent) {
            Sequence nextParent = model.getParent(leadParent);
            targetIndex = nextParent.indexOf(leadParent) + 1;
            parent = nextParent;
        }
        // If the next step is an open sequence, move into it
        else if (model.isOpen(next)) {
            parent = (Sequence)next;
            targetIndex = 0;
        }
        moveSelectedRows(parent, targetIndex);
    }

    /** Move the currently selected rows into the given parent at the given
        index.
    */
    public void moveSelectedRows(Sequence parent, int index) {
        List steps = getSelectedSteps();
        Step first = (Step)steps.get(0);
        if (parent.indexOf(first) == index) 
            return;

        ListSelectionModel lsm = getSelectionModel();
        boolean firstIsAnchor =
            getSelectedRow() == lsm.getAnchorSelectionIndex();
        model.moveSteps(parent, steps, index);
        int firstRow = model.getRowOf(first);
        if (firstIsAnchor)
            lsm.setSelectionInterval(firstRow, firstRow + steps.size() - 1);
        else
            lsm.setSelectionInterval(firstRow + steps.size() - 1, firstRow);
    }

    private class DGListener implements DragGestureListener {
        public void dragGestureRecognized(DragGestureEvent e) {
            Point where = e.getDragOrigin();
            int firstRow = getSelectedRow();
            if (firstRow == -1) 
                return;
            if ((e.getDragAction() & DnDConstants.ACTION_MOVE) != 0) {
                Transferable tf = getSelectedRowCount() > 1
                    ? new StepTransferable(getSelectedSteps())
                    : new StepTransferable(getSelectedStep());
                try {
                    Rectangle rect = getCellRect(firstRow, 0, true);
                    int count = getSelectedRowCount();
                    rect.height *= count;
                    Point offset = new Point(rect.x - where.x,
                                             rect.y - where.y);
                    rect.x = rect.y = 0;
                    BufferedImage image =
                        new BufferedImage(rect.width, rect.height, 
                                          BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics2D graphics = image.createGraphics();

                    graphics.setColor(Color.gray);
                    --rect.width;--rect.height;
                    graphics.draw(rect);
                    graphics.dispose();

                    e.startDrag(DragSource.DefaultMoveDrop,
                                image, offset,
                                tf, dragSourceListener); 
                    isDragging = true;
                }
                catch(InvalidDnDOperationException exc) {
                    Log.warn(exc);
                }
            }
        }
    }

    /** Listens to events coming from the source of the drag action. */
    private class DSListener implements DragSourceListener {
        public void dragDropEnd(DragSourceDropEvent e) {
            Log.debug("drag drop end " + e.getDropAction());
            // OSX bug makes this fail, so do it in the target listener instead
            if (!e.getDropSuccess()) {
                Log.debug("drop failed");
            }
        }
        public void dragEnter(DragSourceDragEvent e) {
            Log.debug( "drag enter " + e.getDropAction());
            DragSourceContext context = e.getDragSourceContext();
            // intersection of the users selected action, and the source and
            // target actions 
            int action = e.getDropAction();
            if ((action & DnDConstants.ACTION_MOVE) != 0) {
                context.setCursor(DragSource.DefaultMoveDrop);	  
            }
            else {
                context.setCursor(DragSource.DefaultMoveNoDrop);     
            }
        }
        public void dragOver(DragSourceDragEvent e) { }
        public void dragExit(DragSourceEvent e) { }
        public void dropActionChanged(DragSourceDragEvent e) { 
            Log.debug("action changed " + e.getDropAction());
            DragSourceContext context = e.getDragSourceContext();      
            context.setCursor(DragSource.DefaultMoveNoDrop);	  	
        }
    }

    /** Listens to events coming from the target of the drag action. */
    private class DTListener implements DropTargetListener {
        public void dragEnter(DropTargetDragEvent e) {
            if (!isDragAcceptable(e)) {
                e.rejectDrag();
            }
            else {
                e.acceptDrag(DnDConstants.ACTION_MOVE);
            }
        }
        public void dragExit(DropTargetEvent e) { }
        public void dropActionChanged(DropTargetDragEvent e) {
            if (!isDragAcceptable(e)) {
                e.rejectDrag();
            }
            else {
                e.acceptDrag(DnDConstants.ACTION_MOVE);
            }
        }
        public void dragOver(DropTargetDragEvent e) {
            Log.debug("drag over target " + e.getDropAction());
            if (isDragAcceptable(e)) {
                e.acceptDrag(DnDConstants.ACTION_MOVE);
                Rectangle last = getCursorBounds();
                setCursorLocation(e.getLocation());
                Rectangle current = getCursorBounds();
                paintImmediately(last);
                paintImmediately(current);
            }
            else {
                e.rejectDrag();
            }
        }
        public void drop(DropTargetDropEvent e) {
            Log.debug("drop successful " + e.getDropAction());
            if (!isDropAcceptable(e)) {
                e.rejectDrop();
            }
            else {
                e.acceptDrop(DnDConstants.ACTION_MOVE);
                moveSelectedRows(cursorParent, cursorParentIndex);
            }   
            e.dropComplete(true);
        }
        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Log.debug("drag action is " + e.getDropAction());
            return e.isDataFlavorSupported(StepTransferable.STEP_FLAVOR);
        }
        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Log.debug("drop action is " + e.getDropAction());
            return e.isDataFlavorSupported(StepTransferable.STEP_FLAVOR);
        }
    }

    /** If any sub-steps of a sequence are selected, they <i>all</i> must be
        selected.  Also select all children when selecting an open sequence.
    */
    private class SelectionModel extends DefaultListSelectionModel {
        public SelectionModel() {
            setSelectionMode(SINGLE_INTERVAL_SELECTION);
        }
        private void fixSelection() {
            if (getSelectedRowCount() == 0
                || (getSelectedRowCount() == 1
                    && !model.isOpen(getSelectedRow()))) {
                return;
            }
            // Ensure the first selection has at maximum the minimum depth
            // Ensure the row after the last selection has at minimum the
            // minimum depth.
            int anchor = getAnchorSelectionIndex();
            int lead = getLeadSelectionIndex();
            int lo, hi;
            if (anchor < lead) {
                lo = anchor; hi = lead;
            }
            else {
                lo = lead; hi = anchor;
            }
            int loDepth = model.getNestingDepthAt(lo);
            int minDepth = loDepth;
            for (int i=lo+1;i <= hi;i++) {
                minDepth = Math.min(minDepth, model.getNestingDepthAt(i));
            }
            if (loDepth > minDepth) {
                for (int i=lo-1;i >= 0;i--) {
                    if (model.getNestingDepthAt(i) == minDepth) {
                        Log.debug("Changing low end to " + i);
                        if (lo == anchor)
                            setSelectionInterval(lo = i, hi);
                        else
                            setSelectionInterval(hi, lo = i);
                        break;
                    }
                }
            }
            int last = hi + 1;
            while (last < getRowCount()
                   && model.getNestingDepthAt(last) > minDepth) {
                ++last;
            }
            if (last > hi + 1) {
                Log.debug("Changing hi end to " + (last - 1));
                if (hi == lead)
                    setSelectionInterval(lo, last - 1);
                else
                    setSelectionInterval(last - 1, lo);
            }
        }
        public void addSelectionInterval(int index0, int index1) {
            super.addSelectionInterval(index0, index1);
            fixSelection();
        }
        public void removeSelectionInterval(int index0, int index1) {
            super.removeSelectionInterval(index0, index1);
            fixSelection();
        }
        public void setAnchorSelectionIndex(int index) {
            super.setAnchorSelectionIndex(index);
            fixSelection();
        }
        public void setLeadSelectionIndex(int index) {
            super.setLeadSelectionIndex(index);
            fixSelection();
        }
        public void setSelectionInterval(int index0, int index1) {
            super.setSelectionInterval(index0, index1);
            fixSelection();
        }
        public void insertIndexInterval(int index, int length, boolean bfore) {
            super.insertIndexInterval(index, length, bfore);
            fixSelection();
        }
        public void removeIndexInterval(int idx0, int idx1) {
            super.removeIndexInterval(idx0, idx1);
            fixSelection();
        }
    }
}
