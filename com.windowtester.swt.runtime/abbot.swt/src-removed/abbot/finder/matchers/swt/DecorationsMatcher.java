package abbot.finder.matchers.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Widget;


/** Provides matching of a Decorations by title or component name. */

public class DecorationsMatcher extends ClassMatcher {
	private String id;
    private boolean mustBeShowing;
	private boolean controlShowing;
	private String wtext;
    
	public DecorationsMatcher() {
		this (null, true);
	}
       
    public DecorationsMatcher(String id) {
        this(id, true);
    }

    public DecorationsMatcher(String id, boolean mustBeShowing) {
        super(Decorations.class);
        this.id = id;
        this.mustBeShowing = mustBeShowing;
    }
    public boolean matches(final Widget w) {
    	wtext = null;
    	controlShowing = true;
    	w.getDisplay().syncExec( new Runnable() {
    		public void run() {
		    	if (w instanceof Decorations) {
		    		controlShowing = ((Control)w).getVisible() && ((Control)w).getShell().getVisible();
		       		wtext = ((Decorations)w).getText();       	    		
		    	} 
    		}
    	}
    	);
    	// TODO: dialog case
        return super.matches(w)
            && (controlShowing || !mustBeShowing)
            && (stringsMatch(id, wtext));
    }
    public String toString() {
        return "Decorations matcher (id=" + id + ")";
    }
}
