package abbot.tester.swt;

import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets
 * of type Control.
 */
public class ControlTester extends WidgetTester {
    public static final String copyright = "Licensed Materials	-- Property of IBM\n"
            + "(c) Copyright International Business Machines Corporation, 2003\nUS Government "
            + "Users Restricted Rights - Use, duplication or disclosure restricted by GSA "
            + "ADP Schedule Contract with IBM Corp.";

    /*
     * These getter methods return a particular property of the given widget.
     * 
     * @see the corresponding member function in class Widget
     */
    /* Begin getters */

    /**
     * Proxy for {@link Control#getAccessible()()}.
     */
    public Accessible getAccessible(final Control c) {
        Accessible result = (Accessible) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getAccessible();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getBackground()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the background color.
     */
    public Color getBackground(final Control c) {
        Color result = (Color) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getBackground();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getBorderWidth()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the border width.
     */
    public int getBorderWidth(final Control c) {
        Integer result = (Integer) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(c.getBorderWidth());
                    }
                });
        return result.intValue();
    }

    /**
     * Proxy for {@link Control#getBounds()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the bounds of the widget.
     */
    public Rectangle getBounds(final Control c) {
        Rectangle result = (Rectangle) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getBounds();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getEnabled()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return true if the Control is enabled.
     */
    public boolean getEnabled(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.getEnabled());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#getFont()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the font associated with the control.
     */
    public Font getFont(final Control c) {
        Font result = (Font) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getFont();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getForeground()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the foreground color.
     */
    public Color getForeground(final Control c) {
        Color result = (Color) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getForeground();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getLayoutData()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the layout data.
     */
    public Object getLayoutData(final Control c) {
        Object result = Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getLayoutData();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getLocation()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the location of the control.
     */
    public Point getLocation(final Control c) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getLocation();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getMenu()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the receiver's menu.
     */
    public Menu getMenu(final Control c) {
        Menu result = (Menu) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getMenu();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getParent()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the control's parent.
     */
    public Composite getParent(final Control c) {
        Composite result = (Composite) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getParent();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getShell()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the control's shell.
     */
    public Shell getShell(final Control c) {
        Shell result = (Shell) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getShell();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getSize()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the size of the control.
     */
    public Point getSize(final Control c) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getSize();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getToolTipText()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return the tool tip associated with the control.
     */
    public String getToolTipText(final Control c) {
        String result = (String) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.getToolTipText();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#getVisible()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return true if this control is visible.
     */
    public boolean getVisible(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.getVisible());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#isVisible()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return true if this control and all of its ancestor's are visible.
     */
    public boolean isVisible(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.isVisible());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#isEnabled()}.
     */
    public boolean isEnabled(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.isEnabled());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#isFocusControl()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return true if this control has focus.
     */
    public boolean isFocusControl(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.isFocusControl());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#isReparentable()}. <p/>
     * 
     * @param c
     *            the control under test.
     * @return true if this Control is reparentable.
     */
    public boolean isReparentable(final Control c) {
        Boolean result = (Boolean) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(c.isReparentable());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Control#toControl(int, int)}. <p/>
     * 
     * @param c
     *            the control under test.
     * @param x
     *            the x coordinate to be translated.
     * @param y
     *            the y coordinate to be translated.
     * @return the translated coordinates.
     */
    public Point toControl(final Control c, final int x, final int y) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.toControl(x, y);
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#toControl(org.eclipse.swt.graphics.Point)}.
     * <p/>
     * 
     * @param c
     *            the control under test.
     * @param point
     *            the point to be translated.
     * @return the translated coordinates.
     */
    public Point toControl(final Control c, final Point point) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.toControl(point);
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#toDisplay(int, int)}. <p/>
     * 
     * @param c
     *            the control under test.
     * @param x
     *            the x coordinate to be translated.
     * @param y
     *            the y coordinate to be translated.
     * @return the translated coordinates.
     */
    public Point toDisplay(final Control c, final int x, final int y) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.toDisplay(x, y);
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Control#toDisplay(org.eclipse.swt.graphics.Point)}.
     * <p/>
     * 
     * @param c
     *            the control under test.
     * @param point
     *            the point to be translated.
     * @return the translated coordinates.
     */
    public Point toDisplay(final Control c, final Point point) {
        Point result = (Point) Robot.syncExec(c.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return c.toDisplay(point);
                    }
                });
        return result;
    }

    /* End getters */
    /*
     * Add and remove listeners. This is mainly intended for adding listeners to
     * enable JUnit checks that certain events are issued.
     */
    /**
     * Proxy for
     * {@link Control#addControlListener(org.eclipse.swt.events.ControlListener)}.
     * <p/>
     * 
     * @param c
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addControlListener(final Control c,
            final ControlListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.addControlListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeControlListener(org.eclipse.swt.events.ControlListener)}.
     * <p/>
     * 
     * @param c
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeControlListener(final Control c,
            final ControlListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.removeControlListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addFocusListener(org.eclipse.swt.events.FocusListener)}.
     * <p/>
     * 
     * @param c
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addFocusListener(final Control c, final FocusListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.addFocusListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeFocusListener(org.eclipse.swt.events.FocusListener)}.
     * <p/>
     * 
     * @param c
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeFocusListener(final Control c,
            final FocusListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.removeFocusListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addHelpListener(org.eclipse.swt.events.HelpListener)}.
     * <p/>
     * 
     * @param c
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addHelpListener(final Control c, final HelpListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.addHelpListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeHelpListener(org.eclipse.swt.events.HelpListener)}.
     * <p/>
     * 
     * @param c
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeHelpListener(final Control c, final HelpListener listener) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.removeHelpListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addKeyListener(org.eclipse.swt.events.KeyListener)}. <p/>
     * 
     * @param control
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addKeyListener(final Control control, final KeyListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addKeyListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeKeyListener(org.eclipse.swt.events.KeyListener)}.
     * <p/>
     * 
     * @param control
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeKeyListener(final Control control,
            final KeyListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removeKeyListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addMouseListener(org.eclipse.swt.events.MouseListener)}.
     * <p/>
     * 
     * @param control
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addMouseListener(final Control control,
            final MouseListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addMouseListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeMouseListener(org.eclipse.swt.events.MouseListener)}.
     * <p/>
     * 
     * @param control
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeMouseListener(final Control control,
            final MouseListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removeMouseListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)}.
     * <p/>
     * 
     * @param control
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addMouseMoveListener(final Control control,
            final MouseMoveListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addMouseMoveListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)}.
     * <p/>
     * 
     * @param control
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeMouseMoveListener(final Control control,
            final MouseMoveListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removeMouseMoveListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)}.
     * <p/>
     * 
     * @param control
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addMouseTrackListener(final Control control,
            final MouseTrackListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addMouseTrackListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removeMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)}.
     * <p/>
     * 
     * @param control
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removeMouseTrackListener(final Control control,
            final MouseTrackListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removeMouseTrackListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#addPaintListener(org.eclipse.swt.events.PaintListener)}.
     * <p/>
     * 
     * @param control
     *            the control to add the listener to.
     * @param listener
     *            the listener to add.
     */
    public void addPaintListener(final Control control,
            final PaintListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addPaintListener(listener);
            }
        });
    }

    /**
     * Proxy for
     * {@link Control#removePaintListener(org.eclipse.swt.events.PaintListener)}.
     * <p/>
     * 
     * @param control
     *            the control to remove the listener from.
     * @param listener
     *            the listener to remove
     */
    public void removePaintListener(final Control control,
            final PaintListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removePaintListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link Control#addTraverseListener(org.eclipse.swt.events.TraverseListener)}.
     * <p/>
     * @param control the control to add the listener to.
     * @param listener the listener to add.
     */
    public void addTraverseListener(final Control control,
            final TraverseListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.addTraverseListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link Control#removeTraverseListener(org.eclipse.swt.events.TraverseListener)}.
     * <p/>
     * @param control the control to remove the listener from.
     * @param listener the listener to remove.
     */
    public void removeTraverseListener(final Control control,
            final TraverseListener listener) {
        Robot.syncExec(control.getDisplay(), null, new Runnable() {
            public void run() {
                control.removeTraverseListener(listener);
            }
        });
    }

    /*
     * End add and remove listeners. 
     */

    /**
     * Proxy for {@link Control#setBackground(Color color)}.
     */
    public void setBackground(final Control c, final Color color) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setBackground(color);
            }
        });
    }

    /**
     * Proxy for {@link Control#setBounds(Rectangle bounds)}.
     */
    public void setBounds(final Control c, final Rectangle bounds) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setBounds(bounds);
            }
        });
    }

    /**
     * Proxy for {@link Control#setBounds(int x, int y, int width, int height)}.
     */
    public void setBounds(final Control c, final int x, final int y,
            final int width, final int height) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setBounds(x, y, width, height);
            }
        });
    }

    /**
     * Proxy for {@link Control#setCursor(Cursor cursor)}.
     */
    public void setCursor(final Control c, final Cursor cursor) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setCursor(cursor);
            }
        });
    }

    /**
     * Proxy for {@link Control#setCapture(boolean b)}.
     */
    public void setCapture(final Control c, final boolean b) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setCapture(b);
            }
        });
    }

    /**
     * Proxy for {@link Control#setEnabled(boolean b)}.
     */
    public void setEnabled(final Control c, final boolean b) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setEnabled(b);
            }
        });
    }

    /**
     * Proxy for {@link Control#setFocus()}.
     */
    public void setFocus(final Control c) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setFocus();
            }
        });
    }

    /**
     * Proxy for {@link Control#setFont(Font font)}.
     */
    public void setFont(final Control c, final Font font) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setFont(font);
            }
        });
    }

    /**
     * Proxy for {@link Control#setForeground(Color color)}.
     */
    public void setForeground(final Control c, final Color color) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setForeground(color);
            }
        });
    }

    /**
     * Proxy for {@link Control#setLayoutData(Object o)}.
     */
    public void setLayoutData(final Control c, final Object o) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setLayoutData(o);
            }
        });
    }

    /**
     * Proxy for {@link Control#setLocation(Point p)}.
     */
    public void setLocation(final Control c, final Point p) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setLocation(p);
            }
        });
    }

    /**
     * Proxy for {@link Control#setLocation(int x, int y)}.
     */
    public void setLocation(final Control c, final int x, final int y) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setLocation(x,y);
            }
        });
    }

    /**
     * Proxy for {@link Control#setMenu(Menu m}.
     */
    public void setMenu(final Control c, final Menu m) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setMenu(m);
            }
        });
    }

    /**
     * Proxy for {@link Control#setParent(Composite composite}.
     */
    public void setParent(final Control c, final Composite composite) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setParent(composite);
            }
        });
    }

    /**
     * Proxy for {@link Control#setRedraw(boolean b}.
     */
    public void setRedraw(final Control c, final boolean b) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setRedraw(b);
            }
        });
    }

    /**
     * Proxy for {@link Control#setSize(Point p)}.
     */
    public void setSize(final Control c, final Point p) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setSize(p);
            }
        });
    }

    /**
     * Proxy for {@link Control#setSize(int x, int y)}.
     */
    public void setSize(final Control c, final int x, final int y) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setSize(x,y);
            }
        });
    }

    /**
     * Proxy for {@link Control#setToolTipText(String text)}.
     */
    public void setToolTipText(final Control c, final String text) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setToolTipText(text);
            }
        });
    }

    /**
     * Proxy for {@link Control#setVisible(boolean b)}.
     */
    public void setVisible(final Control c, final boolean b) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.setVisible(b);
            }
        });
    }

    /**
     * Proxy for {@link Control#traverse(int traversal)}.
     */
    public void traverse(final Control c, final int traversal) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.traverse(traversal);
            }
        });
    }

    /**
     * Proxy for {@link Control#update()}.
     */
    public void update(final Control c) {
        Robot.syncExec(c.getDisplay(), null, new Runnable() {
            public void run() {
                c.update();
            }
        });
    }
}
