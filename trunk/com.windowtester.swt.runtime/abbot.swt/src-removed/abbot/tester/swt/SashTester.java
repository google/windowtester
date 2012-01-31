
package abbot.tester.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Sash;

/**
 * Provides widget-specific actions, assertions, and getter methods for
 * widgets of type Sash.  NOTE: All action methods in this class are
 * platform-dependent
 */
public class SashTester extends ControlTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Set the location of the sash to the given location, relative
	 * to the Sash's parent.  Note- for horizontal sashes, the location
	 * represents the new y-coordinate, and for vertical sashes, the
	 * location represents the new x-coordinate (where the origin is the
	 * top left-hand corner of the sash's parent).
	 * 
	 * Negative values for location are automatically set to 0 , and
	 * those that are larger than the reciever's parent are set to
	 * the width of the parent.
	 */
	public void actionSetSashLocation(final Sash sash, int location){
		actionFocus(sash);
		
		int style = getStyle(sash);
		final Point p = getLocation(sash);
		final Rectangle parentBounds = getBounds(getParent(sash));
					
		Point moveFrom,moveTo;
		if( (style&SWT.VERTICAL)==SWT.VERTICAL){
			location = 	(location<0)? 0 : 
						(location>parentBounds.width)? parentBounds.width:
						location;
			final int setTo = location;
			
			// convert to global coords
			moveFrom = getGlobalLocation(sash);
			moveFrom.y = moveFrom.y+getBounds(sash).height/2;
			moveTo = getParent(sash).toDisplay(setTo,p.y);
			moveTo.y = moveFrom.y;
		}		
		else{//style==SWT.HORIZONTAL
			location = 	(location<0)? 0 : 
						(location>parentBounds.height)? parentBounds.height:
						location;
			final int setTo = location;
			
			// convert to global coordinates
			moveFrom = getGlobalLocation(sash);
			moveFrom.x = moveFrom.x+getBounds(sash).width/2;
			moveTo = getParent(sash).toDisplay(p.x,setTo);
			moveTo.x = moveFrom.x;
		}	

		mouseMove(moveFrom.x,moveFrom.y);
		mousePress(SWT.BUTTON1);
		mouseMove(moveTo.x,moveTo.y);
		mouseRelease(SWT.BUTTON1);
			
		actionWaitForIdle(sash.getDisplay());
	}
	
	/**
	 * Moves the sash by the given amount, or to the edge of the 
	 * reciver's parent. 
	 */
	public void actionMoveSashBy(Sash sash, int amount){
		int style = getStyle(sash);
		if( (style&SWT.VERTICAL)==SWT.VERTICAL){
			int currentX = getLocation(sash).x;
			int moveToX = currentX+amount;
			actionSetSashLocation(sash,moveToX);
		}
		else{
			int currentY = getLocation(sash).y;
			int moveToY = currentY+amount;
			actionSetSashLocation(sash,moveToY);				
		}
	}
    
    /**
     * Proxy for {@link Sash.addSelectionListener(SelectionListener listener).
     */
    public void addSelectionListener(final Sash s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.addSelectionListener(listener);
            }
        });
    }
    
    /**
     * Proxy for {@link Sash.removeSelectionListener(SelectionListener listener).
     */
    public void removeSelectionListener(final Sash s, final SelectionListener listener) {
        Robot.syncExec(s.getDisplay(), null, new Runnable() {
            public void run() {
                s.removeSelectionListener(listener);
            }
        });
    }
	
}
