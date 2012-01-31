package abbot.tester.swt;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;

public class CaretTester extends WidgetTester {
    
    /**
     * Proxy for {@link Caret#getBounds()}.
     */
    public Rectangle getBounds(final Caret w){
        Rectangle result = (Rectangle)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getBounds();
            }
        });
        return result; 
    }   
    
    /**
     * Proxy for {@link Caret#getFont()}.
     */
    public Font getFont(final Caret w){
        Font result = (Font)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getFont();
            }
        });
        return result; 
    }   

    /**
     * Proxy for {@link Caret#getImage()}.
     */
    public Image getImage(final Caret w){
        Image result = (Image)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getImage();
            }
        });
        return result; 
    } 

    /**
     * Proxy for {@link Caret#getLocation()}.
     */
    public Point getLocation(final Caret w){
        Point result = (Point)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getLocation();
            }
        });
        return result; 
    } 
    
    /**
     * Proxy for {@link Caret#getParent()}.
     */
    public Canvas getParent(final Caret w){
        Canvas result = (Canvas)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getParent();
            }
        });
        return result; 
    } 
    
    /**
     * Proxy for {@link Caret#getSize()}.
     */
    public Point getSize(final Caret w){
        Point result = (Point)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return w.getSize();
            }
        });
        return result; 
    } 
    
    /**
     * Proxy for {@link Caret#getVisible()}.
     */
    public boolean getVisible(final Caret w){
        Boolean result = (Boolean)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Boolean(w.getVisible());
            }
        });
        return result.booleanValue(); 
    } 
    
    /**
     * Proxy for {@link Caret#isVisible()}.
     */
    public boolean isVisible(final Caret w){
        Boolean result = (Boolean)Robot.syncExec(w.getDisplay(), new RunnableWithResult() {
            public Object runWithResult() {
                return new Boolean(w.isVisible());
            }
        });
        return result.booleanValue(); 
    } 
    
    /**
     * Proxy for {@link Caret#setBounds (int x, int y, int width, int height)}.
     */
    public void setBounds(final Caret w, final int x, final int y, final int width, final int height) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setBounds(x,y,width,height);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setBounds (Rectangle rectangle)}.
     */
    public void setBounds(final Caret w, final Rectangle rectangle) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setBounds(rectangle);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setFont (Font font)}.
     */
    public void setFont(final Caret w, final Font font) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setFont(font);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setImage (int x, int y, int width, int height)}.
     */
    public void setImage(final Caret w, final Image image) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setImage(image);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setLocation (int x, int y)}.
     */
    public void setLocation(final Caret w, final int x, final int y) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setLocation(x,y);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setLocation (Point p)}.
     */
    public void setLocation(final Caret w, final Point p) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setLocation(p);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setSize (int x, int y)}.
     */
    public void setSize(final Caret w, final int x, final int y) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setSize(x,y);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setLocation (Point p)}.
     */
    public void setSize(final Caret w, final Point p) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setSize(p);
            }
        });
    }
    
    /**
     * Proxy for {@link Caret#setVisible (boolean visible)}.
     */
    public void setVisible(final Caret w, final boolean visible) {
        Robot.syncExec(w.getDisplay(), null, new Runnable() {
            public void run() {
                w.setVisible(visible);
            }
        });
    }
}
