package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A matcher for the text of a widget.
 * <p/>
 * To speed up search, optionally a class type can be provided which a widget
 * must match.
 * @author Richard Birenheide
 * NOTE: *DOES _NOT_ MATCH ON TEXT VALUES*
 */
public class TextMatcher extends ClassMatcher {

  private final Class clazz;
	private final String text;
	private String wtext = null;
	private String wtexts[] = null;
	private final boolean mustBeShowing;
	private volatile boolean controlShowing = true;
    /**
     * Constructs a Matcher for the text given.
     * <p/>
     * The widget must be visible.
     * @param text the text to match.
     */
    public TextMatcher(String text) {
        this(text, true, Widget.class);
    }
    /**
     * Constructs a Matcher with the text and the visibility given.
     * <p/>
     * @param text the text to match.
     * @param mustBeShowing true if the widget must be visible.
     */
    public TextMatcher(String text, boolean mustBeShowing) {
    	this(text, mustBeShowing, Widget.class);
    }
    /**
     * Constructs a matcher with the text and the class given.
     * <p/>
     * The widget must be visible. Note that searches are considerably faster
     * when a class is provided to the matcher.
     * @param text  the text to match.
     * @param clazz the Class to match.
     */
    public TextMatcher(String text, Class clazz) {
    	this(text, true, clazz);
    }
    /**
     * Constructs a Matcher with the text, visibility and class given.
     * <p/>
     * Note that searches are considerably faster when a class is provided
     * to the matcher.
     * @param text the text to match.
     * @param mustBeShowing true if the widget must be visible.
     * @param clazz the class to match.
     */
    public TextMatcher(String text, boolean mustBeShowing, Class clazz) {
    	super(clazz);
    	this.clazz = clazz;
    	this.text = text;
    	this.mustBeShowing = mustBeShowing;
    }

    /**
     * {@inheritDoc}
     * @param w {@inheritDoc}
     * @return {@inheritDoc}
     * @see abbot.finder.swt.Matcher#matches(org.eclipse.swt.widgets.Widget)
     */
    public boolean matches(final Widget w) {
    	if (this.clazz != null) {
    		boolean superResult = super.matches(w);
    		if (!superResult) {
    			return false;
    		}
    	}
    	/* allow trimmed matches? (ignore whitespace in making comparisons) */
    	setWtext(null);
    	setWtexts(null);
    	controlShowing = true;
    	w.getDisplay().syncExec( new Runnable() {
    		public void run() {
		    	if (w instanceof Control) {
		    		controlShowing = ((Control)w).getVisible() && ((Control)w).getShell().getVisible();
		    		//System.out.println("Widget " + w + " showing: " + controlShowing);
		    	}
		    	if (w instanceof Button) {
		    		setWtext(((Button)w).getText());       	    		
		    	}
		    	//!pq: Combo values are too volatile for use in matching	
//		    	if (w instanceof Combo) {
//		    		setWtext(((Combo)w).getText());       	    		
//		    	}
		    	
		    	if (w instanceof Decorations) {
		    		setWtext(((Decorations)w).getText());       	    		
		    	}
		    	if (w instanceof Group) {
		    		setWtext(((Group)w).getText());       	    		
		    	}
		    	if (w instanceof Item) {
		    		if (w instanceof TableItem && ((TableItem)w).getParent().getColumnCount() > 0 ) {
		    			int columns = ((TableItem)w).getParent().getColumnCount();
		    			setWtext(((TableItem)w).getText(0));
		    			 
		    			String[] lWtexts = new String[columns];
		    			for (int i=0;i<lWtexts.length;i++) {
		    				lWtexts[i] = ((TableItem)w).getText(i);
		    			}
		    			setWtexts(lWtexts);
		    		} else {
		    			setWtext(((Item)w).getText());
		    		}
		    	}
		    	if (w instanceof Label) {
		    		setWtext(((Label)w).getText());       	    		
		    	}
		    	try {
					if (w instanceof Section) {
						setWtext(((Section) w).getText());
					}
				} catch (NoClassDefFoundError e) {
					// ignored -- this just means that forms are not on the
					// classpath
					// TODO: handle this more elegantly
				}
		    	if (w instanceof Link) {
		    		setWtext(((Link)w).getText());		    		
		    	}
// !pq: Text values are too volatile for use in matching
//		    	if (w instanceof Text) {
//		    		setWtext(((Text)w).getText());       	    		
//		    	}      	
    		}});
//    	System.out.println ("Matching: wtext, text");
//    	System.out.println ("wtext: " + wtext);
//    	System.out.println ("text: " + text);
    	String[] lWtexts = this.getWtexts();
    	if (lWtexts!=null) {
    		for (int i=0;i<lWtexts.length;i++) {
    			//System.out.println("wts:" + wtexts[i]);
    			if (stringsMatch(text,lWtexts[i])) return true;
    		}
    	}    
    	if (mustBeShowing && !controlShowing) return false;
    	if (this.getWtext()==null) return false;
    	if (text == null)
            return this.getWtext() == null; 
        return stringsMatch(text, getWtext());
    }

    public boolean matches (Dialog d) {
    	String dtext = d.getText();
    	if (text == null)
            return dtext == null; 
        return stringsMatch(text, dtext);    	
    }
    
    public String toString() {
        return "Text matcher (" + text + ")";
    }

  //Necessary because of JVM memory model requirements
	private synchronized String getWtext() {
		return this.wtext;
	}
  //Necessary because of JVM memory model requirements
	private synchronized void setWtext(String pWtext) {
		this.wtext = pWtext;
	}
  //Necessary because of JVM memory model requirements
	private synchronized String[] getWtexts() {
		return this.wtexts;
	}
  //Necessary because of JVM memory model requirements
	private synchronized void setWtexts(String[] pWtexts) {
		this.wtexts = pWtexts;
	}
	/**
	 * Retrieve the text of this matcher.
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
}
