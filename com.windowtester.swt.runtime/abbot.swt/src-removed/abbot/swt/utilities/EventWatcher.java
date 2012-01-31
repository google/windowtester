/*
 * Simple debugging utility that prints dispatched SWT events to stdout
 */
package abbot.swt.utilities;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;

public class EventWatcher {
	Shell shell;
	Text text;
	Display display;
	public static int[] eventTypes ={	SWT.None,		SWT.KeyDown, 		SWT.KeyUp,
										SWT.MouseDown,	SWT.MouseUp,		SWT.MouseMove,
										SWT.MouseEnter,	SWT.MouseExit,		SWT.MouseDoubleClick,
										SWT.Paint,		SWT.Move,			SWT.Resize,
										SWT.Dispose,	SWT.Selection,		SWT.DefaultSelection,
										SWT.FocusIn,	SWT.FocusOut,		SWT.Expand,
										SWT.Collapse,	SWT.Iconify,		SWT.Deiconify,
										SWT.Close,		SWT.Show,			SWT.Hide,
										SWT.Modify,		SWT.Verify,			SWT.Activate,
										SWT.Deactivate,	SWT.Help,			SWT.DragDetect,
										SWT.Arm,		SWT.Traverse,		SWT.MouseHover,
										SWT.HardKeyDown,SWT.HardKeyUp,		SWT.MenuDetect };
	
	public static String[] eventNames= {"None",			"KeyDown", 		"KeyUp",
										"MouseDown",	"MouseUp",		"MouseMove",
										"MouseEnter",	"MouseExit",	"MouseDoubleClick",
										"Paint",		"Move",			"Resize",
										"Dispose",		"Selection",	"DefaultSelection",
										"FocusIn",		"FocusOut",		"Expand",
										"Collapse",		"Iconify",		"Deiconify",
										"Close",		"Show",			"Hide",
										"Modify",		"Verify",		"Activate",
										"Deactivate",	"Help",			"DragDetect",
										"Arm",			"Traverse",		"MouseHover",
										"HardKeyDown",	"HardKeyUp",	"MenuDetect" };	

	public static String getEventName(Event e){
		String res= eventNames[e.type];
		final int NAME_LENGTH = 12;
		for(int i=eventNames[e.type].length(); i<NAME_LENGTH;i++){
			res+=" ";
		}
		return res;
	}
	
	static int getAccelerator(Event e){
		int accel = 0;
		
		if(e.keyCode!=0){
			accel |= SWT.KEYCODE_BIT;
			accel |= e.keyCode;	
			accel |= e.stateMask;
		}		
		else	
			accel |= e.character;
				
		return accel;
	}
	
	// Allows for filtering
	public boolean isSignificant(Event e){
		return true;
	}
	
	

	Listener recorder = new Listener(){
		public void handleEvent(org.eclipse.swt.widgets.Event e){
			if(isSignificant(e))
				System.out.println(getEventName(e)+
									" accel="+getAccelerator(e)+
									" button="+e.button+
									" count="+e.count+
									" detail="+e.detail+
									" end="+e.end+
									" data="+e.data+
									" char="+e.character+
									" keycode="+e.keyCode+	
									" stateMask="+e.stateMask+								
									" doit="+e.doit+
									" display="+e.display+
									" gc="+e.gc+
									" item="+e.item+									
									" height="+e.height+
									" start="+e.start+
									" text="+e.text+
									" time="+e.time+
									" type="+e.type+
									" width="+e.width+
									" location=("+e.x+","+e.y+")"+
									" widget="+e.widget);
			}
		};	

	public EventWatcher(Display display){
//		shell = new Shell(display,SWT.SHELL_TRIM);
//		shell.setText("Event Watcher");
//		text = new Text(shell,SWT.READ_ONLY);
//		text.setSize(250,75);
//		shell.pack();
//		
		for(int i=0; i<eventTypes.length;i++){
			display.addFilter(eventTypes[i],recorder);
		}				
	}
	
	public void removeEventWatcher(){
		for(int i=0; i<eventTypes.length;i++){
			display.addFilter(eventTypes[i],recorder);
		}	
	}
	
	
}
