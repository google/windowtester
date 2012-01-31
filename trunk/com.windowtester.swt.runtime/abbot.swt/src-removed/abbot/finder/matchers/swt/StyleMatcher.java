package abbot.finder.matchers.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Widget;

/** Provides matching of components by widget style. */
/* design decision to make (defer): how are we going to compose matchers?  Should 
 * eatch matcher provide a minimum of functionality (ex. style matches only style 
 * and not class) or should style matchers require class to be specified as well? 
 * */
public class StyleMatcher extends AbstractMatcher {	
	private Class cls;
//    private boolean mustBeShowing;
    private boolean exactMatch; /* if false, returns true if style1 | style2 is true */
    private int style;
    private int wstyle;
    public StyleMatcher(Class cls) {
        this(cls, SWT.NONE);
    }
    public StyleMatcher(Class cls, int style) {
        this(cls, style, false);
    }
    public StyleMatcher(Class cls, int style, boolean exactMatch) {
        this.cls = cls;
        this.style = style;
        this.exactMatch = exactMatch;
//        this.mustBeShowing = false;
    }
    
    public synchronized boolean matches(final Widget w) {
    	boolean result = cls.isAssignableFrom(w.getClass());
    	if (result) {
        	w.getDisplay().syncExec( new Runnable() {
        		public void run() {
        			wstyle = w.getStyle();
        		}
        	});
    		if (exactMatch) {
    			result = (wstyle==style); 
    		} else {
    			result = ((wstyle & style)!=0);
    		}
    	}
    	// TODO: begin debug code
//    	System.out.println ("Widget:" + w + " Class: " + cls + " Result: " + result);
//    	if (result) {
//    		System.out.println("true");
//    		w.getDisplay().syncExec( new Runnable() { public void run() {
//    			if (w instanceof Slider) {System.out.println ( "slider " +((Slider)w).getSelection());}
//    			System.out.println("Widget: " + w);
//    		}});
//    	}
    	// TODO: end debug code 
        return result;
        	// TODO: add support for must be showing case
            //&& (!mustBeShowing || c.isShowing());
    }
    public String toString() {
        return "Style matcher (" + cls.getName() + ")";
    }
}
