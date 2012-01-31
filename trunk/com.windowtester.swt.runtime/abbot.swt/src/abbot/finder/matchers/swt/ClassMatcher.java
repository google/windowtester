package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Widget;

/** Provides matching of components by class. */
public class ClassMatcher extends AbstractMatcher {
    private Class cls;
    //private boolean mustBeShowing;
    public ClassMatcher(Class cls) {
        this(cls, false);
    }
    public ClassMatcher(Class cls, boolean mustBeShowing) {
        this.cls = cls;
        //this.mustBeShowing = mustBeShowing;
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
}
