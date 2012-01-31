
package abbot.tester.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import abbot.Log;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;
import com.windowtester.runtime.swt.internal.operation.SWTDisplayLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;

/**
 * Provides widget-specific actions for testing a scrollBar.
 * 
 * @version $Id: ScrollBarTester.java,v 1.3 2007-11-27 17:17:39 pq Exp $
 */ 
public class ScrollBarTester extends WidgetTester {
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * These getter methods return a particular property of the given widget.
	 * @see the corresponding member function in class Widget   
	 */ 
	/* Begin getters */
	/**
	 * Proxy for {@link ScrollBar#getEnabled()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the enabled state.
	 */
	public boolean getEnabled(final ScrollBar bar) {
		Boolean result = (Boolean) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(bar.getEnabled());
			}
		});
		return result.booleanValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getIncrement()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the increment.
	 */
	public int getIncrement(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getIncrement());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getMaximum()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the maximum.
	 */
	public int getMaximum(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getMaximum());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getMinimum()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the minimum.
	 */
	public int getMinimum(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getMinimum());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getPageIncrement()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the page increment.
	 */
	public int getPageIncrement(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getPageIncrement());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getParent()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the parent.
	 */
	public Scrollable getParent(final ScrollBar bar) {
		Scrollable result = (Scrollable) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return bar.getParent();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link ScrollBar#getSelection()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the selection.
	 */
	public int getSelection(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getSelection());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getSize()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the point describing the size.
	 */
	public Point getSize(final ScrollBar bar) {
		Point result = (Point) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return bar.getSize();
			}
		});
		return result;
	}

	/**
	 * Proxy for {@link ScrollBar#getThumb()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the thumb value.
	 */
	public int getThumb(final ScrollBar bar) {
		Integer result = (Integer) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return new Integer(bar.getThumb());
			}
		});
		return result.intValue();
	}

	/**
	 * Proxy for {@link ScrollBar#getVisible()}.
	 * <p/>
	 * @param bar the bar under test.
	 * @return the visible state.
	 */
	public boolean getVisible(final ScrollBar bar) {
		Boolean result = (Boolean) Robot.syncExec(bar.getDisplay(), new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(bar.getVisible());
			}
		});
		return result.booleanValue();
	}
	/* End getters */

	// platform-dependent fields based on the rendering of the scrollbar
	public final static int BUTTON_SIZE = 17;
	public final static int THUMB_OFFSET = 2;

	/** 
	 * Scroll the given amount, where amount==the number of 
	 * times the arrow is pressed.  Positive amounts scroll down 
	 * for vertical scrollbars and right for horizontal ones.
	 */
    public void actionScroll(final ScrollBar bar, final int amount){
        actionFocus(bar);
        final Rectangle bounds = SWTWorkarounds.getBounds(bar);
        
        // convert bounds to global bounds
        Point p = getParent(bar).toDisplay(bounds.x,bounds.y);
        bounds.x = p.x;
        bounds.y = p.y;
        
        Log.debug("BOUNDS: "+bounds);
        if(amount<0){
            mouseMove2(bounds.x+BUTTON_SIZE/2,bounds.y+BUTTON_SIZE/2);
            for(int i=amount; i<=0; i++){
//                mousePress2(SWT.BUTTON1);
//                mouseRelease2(SWT.BUTTON1);  
                new SWTMouseOperation(WT.BUTTON1).execute();
            }               
        }
        else if(amount>0){
            mouseMove2(  bounds.x+bounds.width - BUTTON_SIZE/2,
                        bounds.y+bounds.height - BUTTON_SIZE/2);
            for(int i=0; i<amount;i++){
//                mousePress2(SWT.BUTTON1);
//                mouseRelease2(SWT.BUTTON1); 
                new SWTMouseOperation(WT.BUTTON1).execute(); 
            }       
        }   
        actionWaitForIdle(bar.getDisplay());
    }

	/**
	 * Page the given amount, where amount==# of times that the
	 * bar is clicked in the page area
	 **/
    public synchronized void actionPageScroll(final ScrollBar bar, int amount){
        actionFocus(bar);
        final Rectangle bounds = SWTWorkarounds.getBounds(bar);
        
        // convert bounds to global bounds
        Point p = getParent(bar).toDisplay(bounds.x,bounds.y);
        bounds.x = p.x;
        bounds.y = p.y;
        
        //System.out.println("BOUNDS: "+bounds);
        int style = getStyle(bar);
        
        if(amount<0){
            if((style&SWT.HORIZONTAL)==SWT.HORIZONTAL)
                mouseMove2(bounds.x+BUTTON_SIZE,bounds.y+BUTTON_SIZE/2);
            else
                mouseMove2(bounds.x+BUTTON_SIZE/2,bounds.y+BUTTON_SIZE);
            for(int i=0; i<amount;i++){
//                mousePress2(SWT.BUTTON1);
//                mouseRelease2(SWT.BUTTON1);  
                new SWTMouseOperation(WT.BUTTON1).execute();
            }                               
        }
        else if(amount > 0){
            if((style&SWT.HORIZONTAL)==SWT.HORIZONTAL)
                mouseMove2(  bounds.x+bounds.width - BUTTON_SIZE,
                            bounds.y+bounds.height - BUTTON_SIZE/2);
            else
                mouseMove(  bounds.x+bounds.width - BUTTON_SIZE/2,
                            bounds.y+bounds.height - BUTTON_SIZE);              
            for(int i=0; i<amount;i++){
//                mousePress2(SWT.BUTTON1);
//                mouseRelease2(SWT.BUTTON1);  
                new SWTMouseOperation(WT.BUTTON1).execute();
            }                   
        }
        actionWaitForIdle(bar.getDisplay());
    }
    
	/** 
	 * Sets the selection to the given value, or as close as possible, by dragging
	 * the slider.  The smaller the ratio of bar.getThumb()/(bar.getMaximum()-bar.getMinimum()),
	 * the less accurate this method is.
	 **/
	// TODO FIXME method loses the lock now between calls to syncExec, so 
	// we need to do everything in one big syncExec block
    public synchronized void actionScrollSetSelection(final ScrollBar bar, final int val){
        actionFocus(bar);
        final Rectangle bounds = SWTWorkarounds.getBounds(bar);
        
        // convert bounds to global bounds
        Point p = getParent(bar).toDisplay(bounds.x,bounds.y);
        bounds.x = p.x;
        bounds.y = p.y;
        
        // get info about current selection
        int style = getStyle(bar);
        int selection = getSelection(bar);
        int increment = getIncrement(bar);
        int minimum   = getMinimum(bar);
        int maximum   = getMaximum(bar);
        int thumb     = getThumb(bar);
        actionWaitForIdle(bar.getDisplay());
        int setTo = val;
        if(setTo<minimum)setTo = minimum;
        else if(setTo>maximum-thumb)setTo = maximum-thumb;
        
        if((style&SWT.HORIZONTAL)==SWT.HORIZONTAL){     
            double thumbEdge = (double)((double)selection/(double)(maximum-minimum));
            double thumbWidth = (double)((double)thumb/(double)(maximum-minimum));
//            double setEdge = (double)((double)setTo/(double)(maximum-minimum));
            double delta = (double)((double)increment/(double)(maximum-minimum));
            
            int stripWidth = bounds.width - 2*BUTTON_SIZE;
            int thumbLocPixels = (int)(thumbEdge*stripWidth);
            int thumbWidthPixels = (int)(thumbWidth*stripWidth);
//            int setEdgePixels = (int)(setEdge*stripWidth);
            int deltaPixels = (int)(delta*stripWidth+1);
            //System.out.println(deltaPixels);
            //System.out.println("THUMB:"+thumb+" MAX: "+max+"SELECTION:"+selection+" THUMBEDGE:"+thumbEdge+" stripWidth:"+stripWidth+" THUMBLOC: "+thumbLoc);
            Point thumbPoint = new Point(bounds.x+BUTTON_SIZE+thumbLocPixels+thumbWidthPixels/2,bounds.y+bounds.height/2);
            Point offset = new Point(0,0);
            
//            Point moveTo = new Point(bounds.x+BUTTON_SIZE+setEdgePixels+thumbWidthPixels/2,bounds.y+bounds.height/2);
            mouseMove2(thumbPoint.x,thumbPoint.y);           
//          mousePress(SWT.BUTTON1);
//          mouseMove(moveTo.x,moveTo.y);
//          mouseRelease(SWT.BUTTON1);
            actionWaitForIdle(bar.getDisplay());
            
            int oldSelection = setTo;
            
            if(setTo<selection){
                while(true){
                    
                    selection = getSelection(bar);
                    
                    if(oldSelection==selection){
                        //System.out.println("Exiting b/c oldSelection==selection");
                        break;
                    }
                                                                            
                    if(selection<=setTo){
                        //System.out.println("Exiting b/c selection<=setTo");
                        break;
                    }   
                    offset.x-=deltaPixels;
//                    mousePress2(SWT.BUTTON1);
//                    mouseMove2(thumbPoint.x+offset.x,thumbPoint.y+offset.y);
//                    mouseRelease2(SWT.BUTTON1);
					new SWTMouseOperation(WT.BUTTON1)
						.at(new SWTDisplayLocation().offset(thumbPoint))
						.dragTo(new SWTDisplayLocation().offset(thumbPoint.x + offset.x, thumbPoint.y + offset.y))
						.execute();
                    actionWaitForIdle(bar.getDisplay());
                
                    oldSelection = selection;
                }
            }
            
            else if(setTo>selection){
                while(true){
                    selection = getSelection(bar);
                    
                    if(oldSelection==selection)
                        break;
                    if(selection>=setTo)
                        break;
                    
                    offset.x+=deltaPixels;
//                    mousePress2(SWT.BUTTON1);
//                    mouseMove2(thumbPoint.x+offset.x,thumbPoint.y+offset.y);
//                    mouseRelease2(SWT.BUTTON1);
                    new SWTMouseOperation(WT.BUTTON1)
                    	.at(new SWTDisplayLocation().offset(thumbPoint))
                    	.dragTo(new SWTDisplayLocation().offset(thumbPoint.x+offset.x, thumbPoint.y+offset.y))
                    	.execute();
                    actionWaitForIdle(bar.getDisplay());
                
                    oldSelection = selection;
                }
            }           
        }

        else{// style==SWT.VERTICAL
            double thumbEdge = (double)((double)selection/(double)(maximum-minimum));
            double thumbWidth = (double)((double)thumb/(double)(maximum-minimum));
            double delta = (double)((double)increment/(double)(maximum-minimum));
            int stripWidth = bounds.height - 2*BUTTON_SIZE;
            int thumbLocPixels = (int)(thumbEdge*stripWidth);
            int thumbWidthPixels = (int)(thumbWidth*stripWidth);
            int deltaPixels = (int)(delta*stripWidth+1);
            //System.out.println("THUMB:"+thumb+" MAX: "+max+"SELECTION:"+selection+" THUMBEDGE:"+thumbEdge+" stripWidth:"+stripWidth+" THUMBLOC: "+thumbLoc);
            Point thumbPoint = new Point(bounds.x+bounds.width/2,bounds.y+BUTTON_SIZE+thumbLocPixels+thumbWidthPixels/2);
            Point offset = new Point(0,0);
            mouseMove(thumbPoint.x,thumbPoint.y);           
            actionWaitForIdle(bar.getDisplay());
        
            int oldSelection = setTo;
        
            if(setTo<selection){
                while(true){
                    
                    selection = getSelection(bar);
                
                    if(oldSelection==selection){
                        //System.out.println("Exiting b/c oldSelection==selection");
                        break;
                    }
                                                                        
                    if(selection<=setTo){
                        //System.out.println("Exiting b/c selection<=setTo");
                        break;
                    }   
                    offset.y-=deltaPixels;
                    mousePress2(SWT.BUTTON1);
                    mouseMove2(thumbPoint.x+offset.x,thumbPoint.y+offset.y);
                    mouseRelease2(SWT.BUTTON1);
                    actionWaitForIdle(bar.getDisplay());
            
                    oldSelection = selection;
                }
            }
        
            else if(setTo>selection){
                while(true){
                    selection = getSelection(bar);
                    //System.out.println((thumbPoint.x+offset.x)+","+(thumbPoint.y+offset.y));              
                    //System.out.println("setTo="+setTo+" selection="+selection);
                    if(oldSelection==selection){
                        //System.out.println("Exiting b/c oldSelection==selection");
                        break;
                    }
                    if(selection>=setTo){
                        //System.out.println("Exiting b/c selection>=setTo");
                        break;
                    }
                    
                    offset.y+=deltaPixels;
                    mousePress2(SWT.BUTTON1);
                    //actionDelay(500);
                    mouseMove2(thumbPoint.x+offset.x,thumbPoint.y+offset.y);
                    mouseRelease2(SWT.BUTTON1);
                    //actionDelay(500);
                    actionWaitForIdle(bar.getDisplay());
            
                    oldSelection = selection;
                }
            }               
        }
        
        // make sure that we actually changed the scroll position
        selection = getSelection(bar);
        
//      int rangeMin = setTo - increment;
//      int rangeMax = setTo + increment;
//      if( !(selection>=rangeMin && selection<=rangeMax) )
//          Log.warn("Failed to set scrollbar appropriately (setTo="+setTo+" selection="+selection);
        
        actionWaitForIdle(bar.getDisplay());    
    }   
}
