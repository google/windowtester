package com.windowtester.gef.test.views;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

public class DNDDraw2DView extends ViewPart
{
    public static final String CANVAS1_NAME = "DNDDraw2DView.canvas1";
    public static final String CANVAS2_NAME = "DNDDraw2DView.canvas2";

    private static final Color RED   = new Color(null, 255,  0,   0);
    private static final Color GREEN = new Color(null, 0,  255,   0);
    private static final Color BLUE  = new Color(null, 0,    0, 255);

    private static int _DROP_COUNT = 0;
    private static int _CONTEXT_COUNT = 0;
    
    private FigureCanvas _canvas1;
    
    @Override
    public void createPartControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(3, true);
        gl.marginHeight=10;
        gl.marginWidth=10;
        gl.verticalSpacing=10;
        container.setLayout(gl);
        
        int operations = DND.DROP_COPY;
        Transfer[] types = new Transfer[] { Draw2dTestTransfer.getInstance() };
        
        _canvas1 = new FigureCanvas(container);
        _canvas1.setBackground(RED);
        _canvas1.setData("name", CANVAS1_NAME);
        
        RectangleFigure figure = new RectangleFigure();
        figure.setBounds(new Rectangle(0, 0, 200, 200));
        figure.setBackgroundColor(BLUE);
        figure.addMouseListener(new PanelMouseAdapter());
        _canvas1.setContents(figure);
        
        GridData gd = new GridData();
        gd.heightHint = 200;
        gd.widthHint = 200;
        _canvas1.setLayoutData(gd);
        
        DragSourceListener dragSourceListener = new DragSourceListener()
        {
            public void dragStart(DragSourceEvent event)
            {
                event.doit = true;
            }
            
            public void dragSetData (DragSourceEvent event)
            {
                
            }
            
            public void dragFinished(DragSourceEvent event)
            {
                
            }
        };
        
        DragSource source = new DragSource(_canvas1, operations);
        source.setTransfer(types);
        source.addDragListener (dragSourceListener);
        
        Label middleLabel = new Label(container, SWT.NONE);
        middleLabel.setText("---DRAG FROM LEFT RO RIGHT-->");
        
        FigureCanvas canvas2 = new FigureCanvas(container);
        canvas2.setBackground(GREEN);
        canvas2.setData("name", CANVAS2_NAME);
        
        gd = new GridData();
        gd.heightHint = 200;
        gd.widthHint = 200;
        canvas2.setLayoutData(gd);
        
        DropTarget target = new DropTarget(canvas2, operations);
        target.setTransfer(types);
        
        target.addDropListener( new DropTargetAdapter() 
        {    
            public void dragEnter(DropTargetEvent event)
            {
                event.detail = DND.DROP_COPY;
                return;
            }
            
            public void drop(DropTargetEvent event)
            {
                _DROP_COUNT++;
                System.out.println("--> DROPPED: " + _DROP_COUNT);
            }
        });
    }

    public void renderContextMenu(int x, int y)
    {
        final Menu menu = new Menu(_canvas1);
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        TestAction action = new TestAction();
        EventHandler handler = new EventHandler(action);
        mi.addSelectionListener(handler);
        mi.setText("Do &Something");
        mi.setEnabled(true);
        
        Point p = _canvas1.toDisplay( x, y );
        menu.setLocation( p );                  
        menu.setVisible( true );
    }
    
    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub
        
    }
    
    public static int getContextCount()
    {
        return _CONTEXT_COUNT;
    }
    
    public static int getDropCount()
    {
        return _DROP_COUNT;
    }
    
    private class PanelMouseAdapter extends MouseListener.Stub
    {
        public void mouseReleased(MouseEvent e)
        {
            if(e.button == 3)
            {
                renderContextMenu(e.x, e.y);
            }
        }
    }
    
    private class EventHandler implements SelectionListener
    {
        private IAction _action;
        
        public EventHandler(IAction action)
        {
            _action = action;
        }
        
        public void widgetSelected(SelectionEvent e)
        {
            _action.run();
        }

        public void widgetDefaultSelected(SelectionEvent e)
        {
        }
        
    }
    
    private class TestAction extends Action
    {
        public void run() 
        {
            _CONTEXT_COUNT++;
            System.err.println("====> " + _CONTEXT_COUNT);
        }
    }

}

