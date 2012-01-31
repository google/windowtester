
package abbot.tester.swt;

import junit.framework.Assert;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
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
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Label.
 */
public class LabelTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */	
	/**
	 * Proxy for {@link Label#getAlignment()}.
	 * <p/>
	 * @param l the Label under test.
	 * @return the label's alignment.
	 */
	public int getAlignment(final Label l) {
		Integer result = (Integer) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(l.getAlignment());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Label#getParent()}.
	 * <p/>
	 * @param l the Label under test.
	 * @return the label's parent.
	 */
	public Image getParent(final Label l) {
		Image result = (Image) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return l.getParent();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Label#getImage()}.
	 * <p/>
	 * @param l the Label under test.
	 * @return the image on the label.
	 */
	public Image getImage(final Label l) {
		Image result = (Image) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return l.getImage();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Label#getText()}.
	 * <p/>
	 * @param l the Label under test.
	 * @return the text on the label.
	 */
	public String getText(final Label l) {
		String result = (String) Robot.syncExec(l.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return l.getText();
			}
		});
		return result;
	}

	/* End getters */

	public boolean assertTextEquals(Label label, String text) {
		return assertTextEquals(label, text, false);
	}

	/**
	 * Fixes problem observed running CCT under TestCollector:
	 * getText(label) returning text minus trailing whitespace
	 */
	public boolean assertTextEquals(
		Label label, String textToMatch, boolean trim) {
		String gotText = getText(label);
		if (gotText == null) {
			return (textToMatch == null);
		} else if (trim) {
			textToMatch = textToMatch.trim();
			gotText = gotText.trim();
		}
		return gotText.equals(textToMatch);
	}

	/**
	 * Factory method.
	 */
	public static LabelTester getLabelTester() {
		return (LabelTester)(getTester(Label.class));
	}

	/**
	 * Get an instrumented <code>Label</code> from its <code>id</code> 
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>Label</code> must be 
	 * unique and findable with param.
	 */
	public static Label getInstrumentedLabel(String id) {
		return getInstrumentedLabel(id, null);
	}

	/**
	 * Get an instrumented <code>Label</code> from its <code>id</code>
	 * and the <code>title</code> of its shell (e.g. of the wizard
	 * containing it). 
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>Label</code> must be unique and findable with param.
	 */
	public static Label getInstrumentedLabel(String id, String title) {
		return getInstrumentedLabel(id, title, null);
	}	
	
	/**
	 * Get an instrumented <code>Label</code> from its 
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>Label</code> must be unique and findable with param.
	 */
	public static Label getInstrumentedLabel(
			String id, String title, String text) {
		return getInstrumentedLabel(id, title, text, null);
	}
	
	/**
	 * Get an instrumented <code>Label</code> from its 
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>Label</code> must be unique and findable with param.
	 */
	public static Label getInstrumentedLabel(
		String id, String title, String text, Shell shell) {
		Label ret = null;
		try {
			ret = catchInstrumentedLabel(id, title, text, shell);
		} catch (WidgetNotFoundException nf) {
			Assert.fail("no instrumented Label \"" + id + "\" found");
		} catch (MultipleWidgetsFoundException mf) {
			Assert.fail("many instrumented Labels \"" + id + "\" found");
		}
		Assert.assertNotNull("ERROR: null instrumented Label", ret);
		return ret;
	}
	
	/**
	 * Get an instrumented <code>Label</code>.
	 * Get it from its: 
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * but don't assume it can only be found!
	 */
	public static Label catchInstrumentedLabel(
		String id, String title, String text, Shell shell)
		throws WidgetNotFoundException, MultipleWidgetsFoundException {
//		WidgetReference ref = 
//			new InstrumentedLabelReference(id, null, title, text);
		Label ret = null;
		WidgetFinder finder = BasicFinder.getDefault();
		if (shell==null) {
			try {
					/* try to find the shell */
					shell = (Shell)finder.find(new TextMatcher(title));
				} catch (WidgetNotFoundException e) {
					shell = null;
				} catch (MultipleWidgetsFoundException e) {
					try {
						shell = (Shell) finder.find(new ClassMatcher(Shell.class));
					} catch (WidgetNotFoundException e1) {
						shell = null;
					} catch (MultipleWidgetsFoundException e1) {
						shell = null;
					}
				}			
		}
		/* Decide what to search on: first id, then text if id not available */
		Matcher labelMatcher;
		if (id!=null) {
			labelMatcher = new NameMatcher(id);
		} else {
			labelMatcher = new TextMatcher(text);
		}		
		try {
			if (shell == null) {
				ret = (Label)finder.find(labelMatcher);
			} else {
				ret = (Label)finder.find(shell, labelMatcher);
			}
		} catch (WidgetNotFoundException nf) {
			Assert.fail("no instrumented Label \"" + id + "\" found");
		} catch (MultipleWidgetsFoundException mf) {
			Assert.fail("many instrumented Labels \"" + id + "\" found");
		}

		
//		if (shell == null) {
//			ret = DefaultWidgetFinder.findLabel(ref);
//		} else {
//			ret = DefaultWidgetFinder.findLabelInShell(ref, shell);
//		}
		return ret;
	}
    

    /**
     * Proxy for {@link Label.setAlignment(int alignment).
     */
    public void setAlignment(final Label l, final int alignment) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.setAlignment(alignment);
            }
        });
    }

    /**
     * Proxy for {@link Label.setImage(Image i).
     */
    public void setImage(final Label l, final Image i) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.setImage(i);
            }
        });
    }

    /**
     * Proxy for {@link Label.setText(String text).
     */
    public void setText(final Label l, final String text) {
        Robot.syncExec(l.getDisplay(), null, new Runnable() {
            public void run() {
                l.setText(text);
            }
        });
    }
	
}
