package abbot.tester.swt;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Text.
 */
public class TextTester extends ScrollableTester{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/* Widget properties that are obtainable by member getter functions */
//	protected int caretLineNumber, caretPosition, charCount, lineCount,
//		lineHeight, selectionCount, tabs, textLimit, topIndex, topPixel;
//	protected Point caretLocation, selection;
//	protected boolean doubleClickEnabled, editable;
//	protected char echoChar;
//	protected String lineDelimiter, selectionText, text;

	/**
	 * Sets the above properties to their current values for the given widget. 
	 * NOTE: This should be called in a block of code synchronized on this
	 * tester.
	 */	
//	protected synchronized void getProperties(final Text t){
//		super.getProperties(t);
//		Robot.syncExec(t.getDisplay(),this,new Runnable(){
//			public void run(){
//				text = t.getText();
//				caretLineNumber = t.getCaretLineNumber();
//				caretPosition = t.getCaretPosition();
//				charCount = t.getCharCount();
//				lineCount = t.getLineCount();
//				lineHeight = t.getLineHeight();
//				selectionCount = t.getSelectionCount();
//				tabs = t.getTabs();
//				textLimit = t.getTextLimit();
//				topIndex = t.getTopIndex();
//				topPixel = t.getTopPixel();
//				//caretLocation = t.getCaretLocation();
//				selection = t.getSelection();
//				doubleClickEnabled = t.getDoubleClickEnabled();
//				editable = t.getEditable();
//				echoChar = t.getEchoChar();
//				lineDelimiter = t.getLineDelimiter();
//				selectionText = t.getSelectionText();
//			}
//		});			
//	}

	/*
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */

	/**
	 * Proxy for {@link Text#getCaretLineNumber()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the caret line number.
	 */
	public int getCaretLineNumber(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getCaretLineNumber());
			}
		});
		return result.intValue();
	}

	/** 
	 * Proxy for {@link Text#getCaretLocation()}.
	 * <p/>
	 * Currently returns null.  There is a bug with SWT 3.0
	 * that causes all sorts of trouble when text.getCaretLocation()
	 * is called.
	 * @param t the text under test.
	 * @return The caret location.
	 */
	public Point getCaretLocation(final Text t) {
		Point result = (Point) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getCaretLocation();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Text#getCaretPosition()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the caret position.
	 */
	public int getCaretPosition(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getCaretPosition());
			}
		});
		return result.intValue();
	}
	
	/**
	 * Proxy for {@link Text#getCharCount()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the number of characters entered.
	 */
	public int getCharCount(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getCharCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getDoubleClickEnabled()}.
	 * <p/>
	 * @param t the text under test.
	 * @return true if double click default selection is enabled.
	 */
	public boolean getDoubleClickEnabled(final Text t) {
		Boolean result = (Boolean) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(t.getDoubleClickEnabled());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Text#getEchoChar()}.
	 * <p/>
	 * @param t the text under test.
	 * @return teh echo character.
	 */
	public char getEchoChar(final Text t) {
		Character result = (Character) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Character(t.getEchoChar());
			}
		});
		return result.charValue();
	}

	/**
	 * Proxy for {@link Text#getEditable()}.
	 * <p/>
	 * @param t the text under test.
	 * @return true if the text is editable.
	 */
	public boolean getEditable(final Text t) {
		Boolean result = (Boolean) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(t.getEditable());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link Text#getLineCount()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the number of lines entered.
	 */
	public int getLineCount(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getLineCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getLineDelimiter()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the line delimiter.
	 */
	public String getLineDelimiter(final Text t) {
		String result = (String) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getLineDelimiter();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Text#getLineHeight()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the line height.
	 */
	public int getLineHeight(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getLineHeight());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getSelection()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the start and the end of the selection.
	 */
	public Point getSelection(final Text t) {
		Point result = (Point) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getSelection();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Text#getSelectionCount()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the number of selected characters.
	 */
	public int getSelectionCount(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getSelectionCount());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getSelectionText()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the selected text.
	 */
	public String getSelectionText(final Text t) {
		String result = (String) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getSelectionText();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link Text#getTabs()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the number of tab characters.
	 */
	public int getTabs(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getTabs());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getText()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the text entered.
	 */
	public String getText(final Text t) {
		String result = (String) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getText();
			}
		});
		return result;									
	}
	/**
	 * Proxy for {@link Text#getText(int, int)}.
	 * <p/>
	 * @param t the text under test.
	 * @param start the start of the range.
	 * @param end the end of the range.
	 * @return the text between start and end.
	 */
	public String getText(final Text t, final int start, final int end) {
		String result = (String) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return t.getText(start, end);
			}
		});
		return result;									
	}
	/**
	 * Proxy for {@link Text#getTextLimit()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the text limit.
	 */
	public int getTextLimit(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getTextLimit());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getTopIndex()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the index of the top line.
	 */
	public int getTopIndex(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getTopIndex());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link Text#getTopPixel()}.
	 * <p/>
	 * @param t the text under test.
	 * @return the pixel position of the top line.
	 */
	public int getTopPixel(final Text t) {
		Integer result = (Integer) Robot.syncExec(t.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(t.getTopPixel());
			}
		});
		return result.intValue();
	}
	/* End getters */

	public void actionEnterText(final Text widget, final String text){
		actionFocus(widget);
		Robot.syncExec(widget.getDisplay(),null,new Runnable(){
			public void run(){
				widget.setText(text);
			}
		});
		actionWaitForIdle(widget.getDisplay());
	}

	public void actionSelect(final Text widget, final int start, final int end){
		actionFocus(widget);
		Robot.syncExec(widget.getDisplay(),null,new Runnable(){
			public void run(){
				widget.setSelection(start,end);		
			}
		});
		actionWaitForIdle(widget.getDisplay());
	}
//	
//	public synchronized String getText(final Text widget){
//		objT = null;
//		widget.getDisplay().syncExec(new Runnable(){
//			public void run(){
//				objT = widget.getText();
//			}
//		});
//		
//		return (String)objT;
//	}
//	
//	public String getText(Text widget, int start, int end){
//		String text = getText(widget);
//		if(text==null)
//			return null;
//		return text.substring(start,end);
//	}
//	
	public boolean assertTextEquals(Text widget, String text){
		String gotText = getText(widget);
		if (gotText==null)
			return text==null;
		return gotText.equals(text);
	}
	
	// Did NOT implement click(Text,int)...
	// TODO_Kevin: MAYBE HAVE AN ASSERTION FOR A RANGE OF TEXT???
	
	/**
	 * Factory method.
	 */
	public static TextTester getTextTester() {
		return (TextTester)(getTester(Text.class));
	}

//	/**
//	 * Get an instrumented <code>Text</code> from its <code>id</code> 
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>Text</code> must be unique and findable with param.
//	 */
//	public static Text getInstrumentedText(String id) {
//		return getInstrumentedText(id, null);
//	}
//
//	/**
//	 * Get an instrumented <code>Text</code> from its <code>id</code>
//	 * and the <code>title</code> of its shell (e.g. of the wizard
//	 * containing it). 
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>Text</code> must be unique and findable with param.
//	 */
//	public static Text getInstrumentedText(String id, String title) {
//		return getInstrumentedText(id, title, null);
//	}	
//	
//	/**
//	 * Get an instrumented <code>Text</code> from its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * </ol>
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>Text</code> must be unique and findable with param.
//	 */
//	public static Text getInstrumentedText(
//			String id, String title, String text) {
//		return getInstrumentedText(id, title, text, null);
//	}
//	
//	/**
//	 * Get an instrumented <code>Text</code> from its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * <li><code>shell</code> that contains it</li>
//	 * </ol>
//	 * Because we instrumented it, we assume it not only can be found,
//	 * but is unique, so we don't even try to catch the *Found exceptions.
//	 * CONTRACT: instrumented <code>Text</code> must be unique and findable with param.
//	 */
//	public static Text getInstrumentedText(
//		String id, String title, String text, Shell shell) {
//		Text ret = null;
//		try {
//			ret = catchInstrumentedText(id, title, text, shell);
//		} catch (WidgetNotFoundException nf) {
//			Assert.fail("no instrumented Text \"" + id + "\" found");
//		} catch (MultipleWidgetsFoundException mf) {
//			Assert.fail("many instrumented Texts \"" + id + "\" found");
//		}
//		Assert.assertNotNull("ERROR: null instrumented Text", ret);
//		return ret;
//	}
//	
//	/**
//	 * Get an instrumented <code>Text</code>.
//	 * 
//	 * Look in its 
//	 * <ol>
//	 * <li><code>id</code></li>
//	 * <li><code>title</code> of its shell (e.g. of the wizard containing it)</li>
//	 * <li><code>text</code> that it contains (<code>""</code> if none)</li>
//	 * <li><code>shell</code> that contains it</li>
//	 * </ol>
//	 * but don't assume it can only be found!
//	 */
//	public static Text catchInstrumentedText(
//		String id, String title, String text, Shell shell)
//		throws WidgetNotFoundException, MultipleWidgetsFoundException {
////		WidgetReference ref = 
////			new InstrumentedTextReference(id, null, title, text);
//		Text ret = null;
//		
//		
//		WidgetFinder finder = BasicFinder.getDefault();
//		if (shell==null) {
//			try {
//					/* try to find the shell */
//					shell = (Shell)finder.find(new TextMatcher(title));
//				} catch (WidgetNotFoundException e) {
//					shell = null;
//				} catch (MultipleWidgetsFoundException e) {
//					try {
//						shell = (Shell) finder.find(new ClassMatcher(Shell.class));
//					} catch (WidgetNotFoundException e1) {
//						shell = null;
//					} catch (MultipleWidgetsFoundException e1) {
//						shell = null;
//					}
//				}			
//		}
//		/* Decide what to search on: first id, then text if id not available */
//		Matcher textMatcher;
//		if (id!=null) {
//			textMatcher = new NameMatcher(id);
//		} else {
//			textMatcher = new TextMatcher(text);
//		}		
//		try {
//			if (shell == null) {
//				ret = (Text)finder.find(textMatcher);
//			} else {
//				ret = (Text)finder.find(shell, textMatcher);
//			}
//		} catch (WidgetNotFoundException nf) {
//			Assert.fail("no instrumented Text \"" + id + "\" found");
//		} catch (MultipleWidgetsFoundException mf) {
//			Assert.fail("many instrumented Texts \"" + id + "\" found");
//		}
//		
////		if (shell == null) {
////			ret = DefaultWidgetFinder.findText(ref);
////		} else {
////			ret = DefaultWidgetFinder.findTextInShell(ref, shell);
////		}
//		return ret;
//	}
//	
}
