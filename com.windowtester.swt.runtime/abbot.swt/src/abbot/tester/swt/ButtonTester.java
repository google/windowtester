package abbot.tester.swt;

import junit.framework.Assert;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import abbot.finder.matchers.swt.ClassMatcher;
import abbot.finder.matchers.swt.NameMatcher;
import abbot.finder.matchers.swt.TextMatcher;
import abbot.finder.swt.BasicFinder;
import abbot.finder.swt.Matcher;
import abbot.finder.swt.MultipleWidgetsFoundException;
import abbot.finder.swt.WidgetFinder;
import abbot.finder.swt.WidgetNotFoundException;

/**
 * Provides widget-specific actions, assertions, and getter methods for widgets
 * of type Button.
 */
public class ButtonTester extends ControlTester {
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
     * Proxy for {@link Button#getAlignment()}. <p/>
     * 
     * @param b
     *            the button under test.
     * @return the button's alignment.
     */
    public int getAlignment(final Button b) {
        Integer result = (Integer) Robot.syncExec(b.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return new Integer(b.getAlignment());
                    }
                });
        return result.intValue();
    }

    /**
     * Proxy for {@link Button#getImage()}. <p/>
     * 
     * @param b
     *            the button under test.
     * @return the image on the button.
     */
    public Image getImage(final Button b) {
        Image result = (Image) Robot.syncExec(b.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return b.getImage();
                    }
                });
        return result;
    }

    /**
     * Proxy for {@link Button#getSelection()}. <p/>
     * 
     * @param b
     *            the button under test.
     * @return true if the button is selected
     */
    public boolean getSelection(final Button b) {
        Boolean result = (Boolean) Robot.syncExec(b.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return Boolean.valueOf(b.getSelection());
                    }
                });
        return result.booleanValue();
    }

    /**
     * Proxy for {@link Button#getText()}. <p/>
     * 
     * @param b
     *            the button under test.
     * @return the text on the button.
     */
    public String getText(final Button b) {
        String result = (String) Robot.syncExec(b.getDisplay(),
                new RunnableWithResult() {
                    public Object runWithResult() {
                        return b.getText();
                    }
                });
        return result;
    }

    // TODO_TOM: copy/mod of method in TextTester
    /**
     * Factory method.
     */
    public static ButtonTester getButtonTester() {
        return (ButtonTester) (getTester(Button.class));
    }

//    // TODO_TOM: copy/mod of method in TextTester
//    /**
//     * Get an instrumented <code>Button</code> from its <code>id</code>
//     * Because we instrumented it, we assume it not only can be found, but is
//     * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
//     * instrumented <code>Button</code> must be unique and findable with
//     * param.
//     */
//    public static Button getInstrumentedButton(String id) {
//        return getInstrumentedButton(id, null);
//    }

//    // TODO_TOM: copy/mod of method in TextTester
//    /**
//     * Get an instrumented <code>Button</code> from its <code>id</code> and
//     * the <code>title</code> of its shell (e.g. of the wizard containing it).
//     * Because we instrumented it, we assume it not only can be found, but is
//     * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
//     * instrumented <code>Button</code> must be unique and findable with
//     * param.
//     */
//    public static Button getInstrumentedButton(String id, String title) {
//        return getInstrumentedButton(id, title, null);
//    }

    // TODO_TOM: copy/mod of method in TextTester
//    /**
//     * Get an instrumented <code>Button</code> from its
//     * <ol>
//     * <li><code>id</code></li>
//     * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//     * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//     * </ol>
//     * Because we instrumented it, we assume it not only can be found, but is
//     * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
//     * instrumented <code>Button</code> must be unique and findable with
//     * param.
//     */
//    public static Button getInstrumentedButton(String id, String title,
//            String text) {
//        return getInstrumentedButton(id, title, text, null);
//    }

//    /**
//     * Get an instrumented <code>Button</code> from its
//     * <ol>
//     * <li><code>id</code></li>
//     * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//     * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//     * <li><code>shell</code> that contains it</li>
//     * </ol>
//     * Because we instrumented it, we assume it not only can be found, but is
//     * unique, so we don't even try to catch the *Found exceptions. CONTRACT:
//     * instrumented <code>Button</code> must be unique and findable with
//     * param. TODO: Clean this up.
//     */
//    public static Button getInstrumentedButton(String id, String title,
//            String text, Shell shell) {
//        // WidgetReference ref =
//        // new InstrumentedButtonReference(id, null, title, text);
//        WidgetFinder finder = BasicFinder.getDefault();
//        Button t = null;
//        if (shell == null) {
//            try {
//                /* try to find the shell */
//                shell = (Shell) finder.find(new TextMatcher(title));
//            } catch (WidgetNotFoundException e) {
//                shell = null;
//            } catch (MultipleWidgetsFoundException e) {
//                try {
//                    shell = (Shell) finder.find(new ClassMatcher(Shell.class));
//                } catch (WidgetNotFoundException e1) {
//                    shell = null;
//                } catch (MultipleWidgetsFoundException e1) {
//                    shell = null;
//                }
//            }
//        }
//        /* Decide what to search on: first id, then text if id not available */
//        Matcher buttonMatcher;
//        if (id != null) {
//            buttonMatcher = new NameMatcher(id);
//        } else {
//            buttonMatcher = new TextMatcher(text);
//        }
//        try {
//            if (shell == null) {
//                t = (Button) finder.find(buttonMatcher);
//            } else {
//                t = (Button) finder.find(shell, buttonMatcher);
//            }
//        } catch (WidgetNotFoundException nf) {
//            Assert.fail("no instrumented Button \"" + id + "\" found");
//        } catch (MultipleWidgetsFoundException mf) {
//            Assert.fail("many instrumented Buttons \"" + id + "\" found");
//        }
//        Assert.assertNotNull("ERROR: null Button", t);
//        return t;
//    }

    /* End getters */

    /**
     * Proxy for {@link Button.addSelectionListener(SelectionListener}.
     */
    public void addSelectionListener(final Button b, final SelectionListener listener) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.addSelectionListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link Button.removeSelectionListener(SelectionListener}.
     */
    public void removeSelectionListener(final Button b, final SelectionListener listener) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.removeSelectionListener(listener);
            }
        });
    }

    /**
     * Proxy for {@link Button.setAlignment(int i).
     */
    public void setAlignment(final Button b, final int i) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.setAlignment(i);
            }
        });
    }

    /**
     * Proxy for {@link Button.setImage(Image i).
     */
    public void setImage(final Button b, final Image i) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.setImage(i);
            }
        });
    }

    /**
     * Proxy for {@link Button.setSelection(boolean selected).
     */
    public void setSelection(final Button b, final boolean selected) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.setSelection(selected);
            }
        });
    }

    /**
     * Proxy for {@link Button.setText(String text).
     */
    public void setText(final Button b, final String text) {
        Robot.syncExec(b.getDisplay(), null, new Runnable() {
            public void run() {
                b.setText(text);
            }
        });
    }
}
