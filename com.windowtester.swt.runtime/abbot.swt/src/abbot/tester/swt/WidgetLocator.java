package abbot.tester.swt;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import abbot.Platform;

import com.windowtester.runtime.swt.internal.abbot.SWTWorkarounds;

/**
 * Provides a means to find the coordinates of an SWT Widget in display-space, 
 * given an SWT Display object.
 * 
 * @author Kevin T Dale
 * @version $Id: WidgetLocator.java,v 1.6 2008-01-15 18:01:53 pq Exp $
 */
public class WidgetLocator{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/**
	 * Returns the location of the top left-hand corner of a the client area
	 * of a given Widget in display space.
	 * 
	 * @param w the Widget to be found
	 * @return the location of Widget w
	 */
	public static Point getLocation(Widget w){
		return getLocation(w,true);	
	}
	
	/**
	 * Returns the location of the top left-hand corner of a given Widget in display 
	 * space, optionally ignoring the Widget's trimmings.  For example, to obtain  
	 * the location of the area of the Widget that can receive mouse actions, use
	 * <code>ignoreBorder==true</code>. 
	 * 
	 * @param w the Widget to be found
	 * @param ignoreBorder should the border be considered?	 
	 * @return the location of Widget w
	 */	
	public static Point getLocation(Widget w, boolean ignoreBorder){
		if (w == null || w.isDisposed())
			return null;
		Rectangle bounds; 
		try {
			bounds = getBounds(w,ignoreBorder);
		} catch(NullPointerException npe){
			return null;
		} catch (SWTException e) {
			return null;
		}
		if (bounds == null)
			return null;
		return new Point(bounds.x,bounds.y);
	}
	
	/**
	 * Finds the display-space rectangle that bounds the given Widget's client area.
	 * 
	 * @param w the Widget to be found
	 * @return the bounds of the widget's client area
	 */
	public static Rectangle getBounds(Widget w){
		return getBounds(w,true);	
	}
	/**
	 * Finds the display space rectangle that bounds the given Widget, optionally
	 * ignoring the Widget's trimmings.  
	 * 
	 * @param w the Widget to be found
	 * @param ignoreBorder should the border be considered?
	 * @return the bounding rectangle
	 */	
	public static Rectangle getBounds(Widget w, boolean ignoreBorder){
		if(w instanceof Control){
			if(ignoreBorder && w instanceof Decorations){
				Rectangle bounds = ((Decorations)w).getBounds();
				Rectangle clientArea = ((Decorations)w).getClientArea();
				Rectangle calced = ((Decorations)w).computeTrim(bounds.x,bounds.y,
												  clientArea.width,clientArea.height);
				Rectangle correct = new Rectangle(2*bounds.x-calced.x,
												  2*bounds.y-calced.y,
												  clientArea.width-1,	//bug workaround 
												  clientArea.height-1);	
				return toDisplay(correct,((Decorations)w));							
			}
			if(ignoreBorder && w instanceof Button){
				Rectangle bounds = ((Button)w).getBounds();
				bounds.width-=1;
				bounds.height-=1;
				return toDisplay(bounds,(Control)w);				
			} else if (ignoreBorder && w instanceof Text) {
			//!!pq:
				Text t = (Text)w;
				Rectangle bounds = ((Control)t).getBounds();
//				Composite parent = t.getParent();
//				Layout layout = parent.getLayout();
//				layout.
//				if (bounds.height == 0 || bounds.width == 0) {
//					Object layoutData = t.getLayoutData();
//					if (layoutData != null && layoutData instanceof Layout)
//						bounds = SWTWorkarounds.getBounds(t, (Layout)layoutData);
//				}			
				
				//TODO: this is not working: ow to get the bounds of  Text object?
				
				return toDisplay(bounds,(Control)w);
			} else if (w instanceof List && Platform.isOSX()) {
				Control c = (Control) w;
				Rectangle r = c.getDisplay().map(c, null, c.getBounds());
				return r;
			} else{
				return toDisplay(((Control)w).getBounds(),((Control)w));
			}
		}
		// The following block exists to workaround problems with Widget.getBounds()
		if(w instanceof CTabItem){
			return SWTWorkarounds.getBounds((CTabItem)w);
		}
		if(w instanceof MenuItem){
//			MenuItem item = (MenuItem)w;
//			Rectangle bounds = SWTWorkarounds.getBounds(item);
//			Point p = item.getParent().getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38436#c113
			// thanks Veronika Irvine
			return SWTWorkarounds.getBounds((MenuItem)w);
		}
		if(w instanceof TabItem){
// moved to SWTWorkarounds.getBounds(TabItem)
//			TabItem item = (TabItem)w;
//			Rectangle bounds = SWTWorkarounds.getBounds(item);
//			Point p = item.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((TabItem)w);
		}
		if(w instanceof TableColumn){
// moved to SWTWorkarounds.getBounds(TableColumn)
//			TableColumn col = (TableColumn)w;
//			Rectangle bounds = SWTWorkarounds.getBounds(col);
//			Point p = col.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((TableColumn)w);
		}
//		if(w instanceof ScrollBar){
//			ScrollBar bar = (ScrollBar)w;
//			Rectangle bounds = bar.getBounds();
//			Point p = bar.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
//		}		
		if(w instanceof ToolItem){
// moved to SWTWorkarounds.getBounds(ToolItem)
//			ToolItem item = (ToolItem)w;
//			Rectangle bounds = item.getBounds();
//			Point p = item.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((ToolItem)w);
		}
		if(w instanceof CoolItem){
// moved to SWTWorkarounds.getBounds(CoolItem)
//			CoolItem item = (CoolItem)w;
//			Rectangle bounds = item.getBounds();
//			Point p = item.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((CoolItem)w);
		}
		if(w instanceof TreeItem){
// moved to SWTWorkarounds.getBounds(TreeItem)
//			TreeItem item = (TreeItem)w;
//			Rectangle bounds = item.getBounds();
//			Point p = item.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((TreeItem)w);
		}
		// Tao Weng 04/25/2005 10:39:05 AM: thanks!
		if(w instanceof TableItem){
// moved to SWTWorkarounds.getBounds(TreeItem)
//			TableItem item = (TableItem)w;
//			Rectangle bounds = SWTWorkarounds.getBounds(item);
//			Point p = item.getParent().toDisplay(bounds.x,bounds.y);
//			return new Rectangle(p.x,p.y,bounds.width,bounds.height);
			return SWTWorkarounds.getBounds((TableItem)w);
		}

		//TODO: look for ALL items that have a getBounds() method in 3.0:
		//TODO: (cont'd) ToolItem, CoolItem, TableItem, TreeItem, etc		
//		System.err.println(
//			"Unable to find coordinates of \""+w.getClass()+"\"; returning NULL");
		return null;
	}

	
	
//	private static Rectangle getBounds(Widget w, Layout layout) {
//		//return SWTWorkarounds.getBounds(w, layout);
//	}


	
	
	
	/**
	 * Helper method to convert a Rectangle, given in the coordinate system of
	 * the given Control's parent, to display-cordinates.
	 */
	public static Rectangle toDisplay(Rectangle r, Control c){
		Point topLeft = new Point(r.x,r.y);
		Point bottomRight = new Point(r.x+r.width,r.y+r.height);
		
		if(c.getParent()!=null){// if not a top-level shell
			topLeft = c.getParent().toDisplay(topLeft.x,topLeft.y);
			bottomRight = c.getParent().toDisplay(bottomRight.x,bottomRight.y);
		}
		
		return new Rectangle(topLeft.x,topLeft.y,bottomRight.x-topLeft.x,bottomRight.y-topLeft.y);
	}

}
