package abbot.tester.swt;

import junit.framework.Assert;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
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
 * widgets of type StyledText.
 *
 * @author nntp_ds@fastmail.fm
 * @version $Id: StyledTextTester.java,v 1.1 2005-12-19 20:28:31 pq Exp $
 */
public class StyledTextTester extends CanvasTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
    
    /**
     * Proxy for {@link StyledText.addSelectionListener(SelectionListener listener).
     */
    public void addSelectionListener(final StyledText s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link StyledText#getCaretOffset()}.
     */
    public int getCaretOffset(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getCaretOffset());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getCharCount()}.
     */
    public int getCharCount(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getCharCount());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getDoubleClickEnabled()}.
     */
    public boolean getDoubleClickEnabled(final StyledText s) {
        Boolean result = (Boolean) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Boolean(s.getDoubleClickEnabled());
            }
        });
        return result.booleanValue();
    }
    
    /**
     * Proxy for {@link StyledText#getEnabled()}.
     */
    public boolean getEnabled(final StyledText s) {
        Boolean result = (Boolean) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Boolean(s.getEnabled());
            }
        });
        return result.booleanValue();
    }
    
    /**
     * Proxy for {@link StyledText#getLineCount()}.
     */
    public int getLineCount(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getLineCount());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getLineDelimiter()}.
     */
    public String getLineDelimiter(final StyledText s) {
        String result = (String) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getLineDelimiter();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getLineHeight()}.
     */
    public int getLineHeight(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getLineHeight());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getSelection()}.
     */
    public Point getSelection(final StyledText s) {
        Point result = (Point) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getSelection();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getSelectionCount()}.
     */
    public int getSelectionCount(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getSelectionCount());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getSelectionText()}.
     */
    public String getSelectionText(final StyledText s) {
        String result = (String) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getSelectionText();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getTabs()}.
     */
    public int getTabs(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getTabs());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getText()}.
     */
    public String getText(final StyledText s) {
        String result = (String) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getText();
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getText(int,int)}.
     */
    public String getText(final StyledText s, final int start, final int end) {
        String result = (String) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return s.getText(start,end);
            }
        });
        return result;
    }
    
    /**
     * Proxy for {@link StyledText#getTextLimit()}.
     */
    public int getTextLimit(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getTextLimit());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getTopIndex()}.
     */
    public int getTopIndex(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getTopIndex());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText#getTopPixel()}.
     */
    public int getTopPixel(final StyledText s) {
        Integer result = (Integer) Robot.syncExec(s.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Integer(s.getTopPixel());
            }
        });
        return result.intValue();
    }
    
    /**
     * Proxy for {@link StyledText.removeSelectionListener(SelectionListener listener).
     */
    public void removeSelectionListener(final StyledText s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.removeSelectionListener(listener);
            }
        });
    }
	
	public void actionEnterText(final StyledText widget, final String text){
        //@todo: actionEnterTest should use keystrokes
		actionFocus(widget);
		Robot.syncExec(widget.getDisplay(),null,new Runnable(){
			public void run(){
				widget.setText(text);
			}
		});
		actionWaitForIdle(widget.getDisplay());
	}

	public void actionSelect(final StyledText widget, final int start, final int end){
		actionFocus(widget);
		Robot.syncExec(widget.getDisplay(),null,new Runnable(){
			public void run(){
				widget.setSelection(start,end);		
			}
		});
		actionWaitForIdle(widget.getDisplay());
	}

	public boolean assertTextEquals(StyledText widget, String text){
		String gotText = getText(widget);
		if (gotText==null)
			return text==null;
		return gotText.equals(text);
	}
	
	// Did NOT implement click(StyledText,int)...
	// TODO_Kevin: MAYBE HAVE AN ASSERTION FOR A RANGE OF TEXT???
	
	/**
	 * Factory method.
	 */
	public static StyledTextTester getStyledTextTester() {
		return (StyledTextTester)(getTester(StyledText.class));
	}

//	 TODO_TOM: copy/mod of method in TextTester
	/**
	 * Get an instrumented <code>StyledText</code> from its <code>id</code> 
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>StyledText</code> must be unique and findable with param.
	 */
	public static StyledText getInstrumentedStyledText(String id) {
		return getInstrumentedStyledText(id, null);
	}

	/**
	 * Get an instrumented <code>StyledText</code> from its <code>id</code>
	 * and the <code>title</code> of its shell (e.g. of the wizard
	 * containing it). 
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>StyledText</code> must be unique and findable with param.
	 */
	public static StyledText getInstrumentedStyledText(String id, String title) {
		return getInstrumentedStyledText(id, title, null);
	}	
	
	/**
	 * Get an instrumented <code>StyledText</code> from its 
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>StyledText</code> must be unique and findable with param.
	 */
	public static StyledText getInstrumentedStyledText(
			String id, String title, String text) {
		return getInstrumentedStyledText(id, title, text, null);
	}
	
	/**
	 * Get an instrumented <code>StyledText</code> from its 
	 * <ol>
	 * <li><code>id</code></li>
	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
	 * <li><code>shell</code> that contains it</li>
	 * </ol>
	 * Because we instrumented it, we assume it not only can be found,
	 * but is unique, so we don't even try to catch the *Found exceptions.
	 * CONTRACT: instrumented <code>StyledText</code> must be unique and findable with param.
	 */
	// Ported to new-style by tlroche
	public static StyledText getInstrumentedStyledText(
			String id, String title, String text, Shell shell) {
//		WidgetReference ref = 
//			new InstrumentedStyledTextReference(id, null, title, text);
		WidgetFinder finder = BasicFinder.getDefault();
		StyledText t = null;
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
		Matcher stMatcher;
		if (id!=null) {
			stMatcher = new NameMatcher(id);
		} else {
			stMatcher = new TextMatcher(text);
		}		
		try {
			if (shell == null) {
				t = (StyledText)finder.find(stMatcher);
			} else {
				t = (StyledText)finder.find(shell, stMatcher);
			}
		} catch (WidgetNotFoundException nf) {
			Assert.fail("no instrumented StyledText \"" + id + "\" found");
		} catch (MultipleWidgetsFoundException mf) {
			Assert.fail("many instrumented StyledTexts \"" + id + "\" found");
		}
		Assert.assertNotNull("ERROR: null StyledText", t);
		return t;
	}

}
