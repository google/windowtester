package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.MultiMatcher;
import abbot.finder.swt.MultipleWidgetsFoundException;

/**
 * Provides multiple matching of components by class.  
 * The element at the given index (1-based) is returned.
 * @author Henry McEuen
 * @version $Id: ClassMultiMatcher.java,v 1.1 2005-12-19 20:28:31 pq Exp $
 */
public class ClassMultiMatcher extends AbstractMatcher implements MultiMatcher {
    private Class cls;
    private int index;
    private boolean throwException = false;
    public ClassMultiMatcher(Class cls) {
        this(cls, 1);
    }
    public ClassMultiMatcher(Class cls, int index) {
        this.cls = cls;
        this.index = index;
    }
    public ClassMultiMatcher(Class cls, boolean throwException) {
    	/* This constructor allows you to get all widgets matching 
    	 * a class back via a multiple widgets found exception */
    	this (cls, 1); 
    	this.throwException = throwException;
    }
    public boolean matches(final Widget w) {
    	boolean result = cls.isAssignableFrom(w.getClass());
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
        return "Class matcher (" + cls.getName() + ")";
    }
	/* (non-Javadoc)
	 * @see abbot.finder.swt.MultiMatcher#bestMatch(org.eclipse.swt.widgets.Widget[])
	 */
	public Widget bestMatch(Widget[] candidates) throws MultipleWidgetsFoundException {
		// TODO Auto-generated method stub
		if (throwException) {
			/* ignore the passed index */
			throw new MultipleWidgetsFoundException(candidates);			
		}
		if (candidates.length < index) 
			throw new MultipleWidgetsFoundException(candidates);
		else 
			return candidates[index-1];
	}
}
