package abbot.tester.swt;

import java.util.Hashtable;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Kevin T Dale
 * 
 * Provides a means to find the coordinates of an SWT control in display-space, 
 * given an SWT Display object. 
 */

public class ControlFinder {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
		
	Hashtable controlsParents;
	
	/**
	 * Creates a new instance of this class, given a Display object.
	 * @param display the display object of the SWT application
	 */		
	 public ControlFinder(Display display){
		controlsParents = new Hashtable();
				
		// add all controls recursively
		Shell[] shells = display.getShells();
		for(int i=0; i<shells.length; i++)
			this.addControl(shells[i], display);						
	}
	
	/**
	 * Returns the top-left corner of a Control in display-space 
	 * coordinates.	
	 * @param c Control to be found
	 */
	protected Point getPoint(Control c){
		Composite parent = null;
		Point p = c.getLocation();
		try{
			parent = (Composite)(controlsParents.get(c));
		}
		catch(Exception ignored){//classCastException
		}
		if(parent==null)//parent is the display, not a composite
			return p;
		else
			return parent.toDisplay(p);
	}
	
	/**
	 * Returns the bounding box in display-space coordinates for a 
	 * given Control.
	 * @param c Control to be found
	 */
	protected Rectangle getRectangle(Control c){
		
		Composite parent = null;
		Rectangle r = c.getBounds();
		try{
			parent = (Composite)(controlsParents.get(c));
		}
		catch(Exception ignored){//classCastException
		}
		
		if(parent==null)
			return r;
		else{
			Point topLeftP = new Point(r.x,r.y);						// parent coords
			Point bottomRightP= new Point(r.x +r.width,r.y+r.height);  
			Point topLeftD = parent.toDisplay(topLeftP);				// display coords
			Point bottomRightD = parent.toDisplay(bottomRightP);				
			return new Rectangle(topLeftD.x,topLeftD.y,bottomRightD.x-topLeftD.x,bottomRightD.y-topLeftD.y);
		}
	}
	
	/**
	 * Adds a control, and all of its children controls, to the HashTable
	 * of control/parent pairs. 
	 * @param c Control to add
	 * @param parent parent Object to add
	 */
	protected void addControl(Control c, Object parent){
		if(c==null)
			return;
		if(parent==null)
			return;
		
		Control[] children = new Control[0];
		try{
			children = ((Composite)c).getChildren();
		}
		catch(Exception ignored){		// a classCastException, but if its not a composite		
		}						 		// then it has no children anyway
		for(int i=0; i<children.length;i++)
			this.addControl(children[i],c);
		
		controlsParents.put(c,parent);
	}
}
