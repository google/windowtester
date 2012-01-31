package abbot.editor.recorder;

import java.awt.AWTEvent;
import java.awt.event.*;

import javax.swing.JInternalFrame;
import javax.swing.event.*;

public abstract class AbstractInternalFrameWatcher
    extends InternalFrameAdapter implements ComponentListener {
    public AbstractInternalFrameWatcher(JInternalFrame f) {
        f.addInternalFrameListener(this);
        f.addComponentListener(this);
    }
    protected abstract void dispatch(AWTEvent e);
    public void componentHidden(ComponentEvent e) {
        // Ensure this listener doesn't hang around after the frame goes
        // away. 
        e.getComponent().removeComponentListener(this);
        ((JInternalFrame)e.getComponent()).removeInternalFrameListener(this);
    }
    public void internalFrameClosing(InternalFrameEvent e) {
        dispatch(e);
        e.getInternalFrame().removeInternalFrameListener(this);
        e.getInternalFrame().removeComponentListener(this);
    }
    public void internalFrameIconified(InternalFrameEvent e) {
        dispatch(e);
    }
    public void internalFrameDeiconified(InternalFrameEvent e) {
        dispatch(e);
    }
    
    public void componentShown(ComponentEvent e) { }
    public void componentResized(ComponentEvent e) { }
    public void componentMoved(ComponentEvent e) { }
}
