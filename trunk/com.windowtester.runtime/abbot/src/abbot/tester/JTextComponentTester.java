package abbot.tester;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import abbot.Log;
import abbot.i18n.Strings;

/** Provides actions and assertions {@link JTextComponent}-based
 * components.
 */ 
public class JTextComponentTester extends JComponentTester {

    /**
     * Type the given text into the given component, replacing any existing
     * text already there.  If the empty string or <code>null</code> is given,
     * simply removes all existing text.
     */
    public void actionEnterText(Component c, String text) {
        scrollToVisible(c, 0);
        actionActionMap(c, DefaultEditorKit.selectAllAction);
        if (text == null || "".equals(text)) {
            actionActionMap(c, DefaultEditorKit.deletePrevCharAction);
        }
        else {
            actionKeyString(c, text);
        }
    }

    /** Click at the given index position. */
    public void actionClick(Component tc, int index) {
        Point where = scrollToVisible(tc, index);
        actionClick(tc, where.x, where.y);
    }

    public void actionSetCaretPosition(Component tc, int index) {
        actionClick(tc, index);
    }

    /** Move the pointer to the given index location.  Takes care of
     * auto-scrolling through text.
     * @since 3.8.1
     */
    // TODO move this to a JTextComponentLocation and rely on existing
    // mechanisms to do the scrolling.
    public Point scrollToVisible(Component c, int index) {
        JTextComponent tc = (JTextComponent)c;
        try {
            Rectangle visible = tc.getVisibleRect();
            Rectangle rect = tc.modelToView(index);
            Log.debug("visible=" + visible + ", index="
                      + index + " is at " + rect);
            if (rect == null) {
                String msg = Strings.get("tester.zero_size");
                throw new ActionFailedException(msg);
            }
            // Autoscroll on JTextComponent is a bit flakey
            if (!visible.contains(rect.x, rect.y)) {
                scrollRectToVisible(tc, rect);
                visible = tc.getVisibleRect();
                rect = tc.modelToView(index);
                Log.debug("visible=" + visible + " caret=" + rect);
                if (!visible.contains(rect.x, rect.y)) {
                    String msg = Strings.get("tester.JComponent.not_visible",
                                             new Object[] { 
                                                 new Integer(rect.x),
                                                 new Integer(rect.y), 
                                                 tc,
                                             });
                    throw new ActionFailedException(msg);
                }
            }
            return new Point(rect.x + rect.width/2,
                             rect.y + rect.height/2);
        }
        catch(BadLocationException ble) {
            String msg = Strings.get("tester.JTextComponent.bad_location",
                                     new Object[] { 
                                         ble.getMessage(), 
                                         new Integer(index),
                                         tc.getText()
                                     });
            throw new ActionFailedException(msg);
        }
    }

    /** Account for differences in scrolling {@link javax.swing.JTextField}.
        @see JComponentTester#scrollRectToVisible
        @see JComponent#scrollRectToVisible
    */
    protected void scrollRectToVisible(JComponent c, Rectangle rect) {
        super.scrollRectToVisible(c, rect);
        // Taken from JComponent
        if (!isVisible(c, rect) && c instanceof JTextField) {
            int dx = c.getX();
            int dy = c.getY();
            Container parent;
            for (parent = c.getParent();
                 !(parent == null)
                 && !(parent instanceof JComponent)
                 && !(parent instanceof CellRendererPane);
                 parent = parent.getParent()) {
                 Rectangle bounds = parent.getBounds();
                 dx += bounds.x;
                 dy += bounds.y;
            }
            if (!(parent == null) && !(parent instanceof CellRendererPane)) {
                rect.x += dx;
                rect.y += dy;
                super.scrollRectToVisible((JComponent)parent, rect);
                rect.x -= dx;
                rect.y -= dy;
            }
        }
    }

    /** Equivalent to JTextComponent.setCaretPosition(int), but operates
     * through the UI.
     */
    protected void startSelection(Component comp, int index) {
        final JTextComponent tc = (JTextComponent)comp;
        // Avoid automatic drag/drop if the selection start is already
        // part of a selection (OSX has setDragEnabled true by default). 
        if (tc.getSelectionStart() != tc.getSelectionEnd()) {
            invokeAndWait(new Runnable() {
                public void run() {
                    tc.setCaretPosition(0);
                    tc.moveCaretPosition(0);
                }
            });
        }
        Point where = scrollToVisible(comp, index);
        mousePress(comp, where.x, where.y);
    }

    /** Equivalent to JTextComponent.moveCaretPosition(int), but operates
     * through the UI.
     */
    protected void endSelection(Component comp, int index) {
        Point where = scrollToVisible(comp, index);
        mouseMove(comp, where.x, where.y);
        mouseRelease();
    }

    /** Start a selection at the given index. */
    public void actionStartSelection(Component comp, int index) {
        startSelection(comp, index);
        waitForIdle();
    }

    /** Terminate a selection on the given index. */
    public void actionEndSelection(Component comp, int index) {
        endSelection(comp, index);
        waitForIdle();
    }

    /** Select the given text range.
        @deprecated Use actionSelectText instead.
     */
    public void actionSelect(Component comp, int start, int end) {
        actionSelectText(comp, start, end);
    }

    /** Select the given text range. */
    public void actionSelectText(Component comp, int start, int end) {
        // An idle wait is sometimes required, otherwise the mouse press is
        // never registered (w32, 1.4) 
        actionStartSelection(comp, start);
        actionEndSelection(comp, end);

        // Verify the selection was properly made
        JTextComponent tc = (JTextComponent)comp;
        if (!(tc.getSelectionStart() == Math.min(start, end)
              && tc.getSelectionEnd() == Math.max(start, end))) {
            String msg = Strings.get("tester.JTextComponent.selection_failed",
                                     new Object[] {
                                         new Integer(start), new Integer(end),
                                         new Integer(tc.getSelectionStart()),
                                         new Integer(tc.getSelectionEnd()),
                                     });
            throw new ActionFailedException(msg);
        }
    }
}
