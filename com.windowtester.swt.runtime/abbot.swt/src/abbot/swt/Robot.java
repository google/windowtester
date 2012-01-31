package abbot.swt;

import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Provides the functionality of <code>java.awt.Robot</code> for use 
 * in SWT environments.  Thus this class should be regarded as "equivalent to"
 * <code>java.awt.Robot</code>, not <code>abbot.tester.awt.Robot</code>.
 * 
 * @author Kevin Dale
 * @version $Id: Robot.java,v 1.3 2006-10-20 13:40:22 alexander_smirnoff Exp $
 * 	
 **/
public class Robot /*extends java.awt.Robot*/{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	/** the # of modifiers- like SWT.CTRL, SWT.ALT, etc- that are currently defined  */
	private static final int MODIFIER_COUNT= 7;
	
	/** mask used to identify if any mouse buttons were clicked */
	private static final int BUTTON_MASK = (SWT.BUTTON1|SWT.BUTTON2|SWT.BUTTON3);
	
	/** the display associated with this robot */
	private Display displayProperty;
	
	/** the java.awt.Robot that does all the leg-work for us */
	private java.awt.Robot robot;

	/** simple internal class for storing info about a keystroke **/
	class CharCode{
		public int keycode;
		public boolean shift;
		public char character;
		
		public CharCode(char character, int keycode, int shift){
			this.keycode = keycode;
			this.shift = (shift==1);
			this.character = character;
		}
		
	}
	
	/** key mappings from chars to keycodes **/
	private static int[][] mappings = {
		{SWT.ESC, KeyEvent.VK_ESCAPE,0},
		{SWT.DEL, KeyEvent.VK_DELETE,0},
		{ ' ', KeyEvent.VK_SPACE, 0, },
		{ '\t', KeyEvent.VK_TAB, 0, },
		{ '~', KeyEvent.VK_BACK_QUOTE, 1, },
		{ '`', KeyEvent.VK_BACK_QUOTE, 0, },
		{ '!', KeyEvent.VK_1, 1, },
		{ '@', KeyEvent.VK_2, 1, },
		{ '#', KeyEvent.VK_3, 1, },
		{ '$', KeyEvent.VK_4, 1, },
		{ '%', KeyEvent.VK_5, 1, },
		{ '^', KeyEvent.VK_6, 1, },
		{ '&', KeyEvent.VK_7, 1, },
		{ '*', KeyEvent.VK_8, 1, },
		{ '(', KeyEvent.VK_9, 1, },
		{ ')', KeyEvent.VK_0, 1, },
		{ '-', (SWT.getPlatform().equals("gtk"))?KeyEvent.VK_UNDERSCORE:KeyEvent.VK_MINUS, 0, },
		{ '_', (SWT.getPlatform().equals("gtk"))?KeyEvent.VK_UNDERSCORE:KeyEvent.VK_MINUS, 1, },
		{ '=', KeyEvent.VK_EQUALS, 0, },
		{ '+', KeyEvent.VK_EQUALS, 1, },
		{ '[', KeyEvent.VK_OPEN_BRACKET, 0, },
		{ '{', KeyEvent.VK_OPEN_BRACKET, 1, },
		{ ']', KeyEvent.VK_CLOSE_BRACKET, 0, },
		{ '}', KeyEvent.VK_CLOSE_BRACKET, 1, },
		{ '|', KeyEvent.VK_BACK_SLASH, 1, },
		{ ';', KeyEvent.VK_SEMICOLON, 0, },
		{ ':', KeyEvent.VK_SEMICOLON, 1, },
		{ ',', KeyEvent.VK_COMMA, 0, },
		{ '<', KeyEvent.VK_COMMA, 1, },
		{ '.', KeyEvent.VK_PERIOD, 0, }, 
		{ '>', (SWT.getPlatform().equals("gtk"))?KeyEvent.VK_GREATER:KeyEvent.VK_PERIOD, 1, },
		{ '/', KeyEvent.VK_SLASH, 0, },
		{ '?', KeyEvent.VK_SLASH, 1, },
		{ '\\', KeyEvent.VK_BACK_SLASH, 0, },
		{ '|', KeyEvent.VK_BACK_SLASH, 1, },
		{ '\'', KeyEvent.VK_QUOTE, 0, },
		{ '"', KeyEvent.VK_QUOTE, 1, },
		{ '\r', KeyEvent.VK_ENTER, 0},
		{'\t', KeyEvent.VK_TAB,0}
	};
	
	
	/** a mapping from unicode characters (non-letter,non-digit) to keycodes for US keyboards **/
	private static HashMap keycodes = new HashMap();
	
	/**
	 * Constructs a <code>Robot</code> object in the coordinate system of the primary screen.
	 * 
	 * @throws org.eclipse.swt.SWTException
	 */
	public Robot()throws SWTException{
		//displayProperty = Display.getDefault();
		try{
			robot = new java.awt.Robot();
		
			keycodes = new HashMap();
		
			for(int i=0; i<mappings.length;i++){
				keycodes.put(new Character((char)mappings[i][0]),new CharCode((char)mappings[i][0],mappings[i][1],mappings[i][2]));		
			}			
			
		}
		catch(AWTException awte){
			throw new SWTException("(Translated AWTException) "+awte.getMessage());
		}
	}
	
	/**
	 * CURRENTLY THIS IS FUNCTIONALLY THE SAME AS THE NO-PARAM CONSTRUCTOR
	 * 
	 * Creates a <code>Robot</code> for the given <code>Display</code>.
	 * 
	 * @param display the <code>Display</code> associated with this robot
	 * 
	 * @throws org.eclipse.swt.SWTException
	 */
	public Robot(Display display) throws SWTException {
		this.displayProperty = display;
		try{
			robot = new java.awt.Robot();
			
			keycodes = new HashMap();
		
			for(int i=0; i<mappings.length;i++){
				keycodes.put(new Character((char)mappings[i][0]),new CharCode((char)mappings[i][0],mappings[i][1],mappings[i][2]));		
			}
		}
		catch(AWTException awte){
			throw new SWTException("(Translated AWTException) "+awte.getMessage());
		}
	}
		
	/**
	 * Creates an image containing pixels read from the screen.  
	 * <p>
	 * NOTE: Application code must explicitly invoke the <code>Image.dispose()</code> 
 	 * method to release the operating system resources managed by each instance
 	 * when those instances are no longer required.
 	 * </p>
	 * 
	 * @param rect <code>Rectangle</code> to capture in screen coordinates
	 * 
	 * @return the captured <code>Image</code>
	 */		
	public synchronized Image createScreenCapture(Rectangle rect){
		PaletteData pData = new PaletteData(255<<16,	// red mask
											255<<8,		// green mask
											255);		// blue mask
											
		ImageData iData = new ImageData(rect.width,rect.height,24,pData);
		
		Color color;
		for(int x=0; x<rect.width; x++){
			for(int y=0; y<rect.height; y++){
				color = getPixelColor(rect.x+x,rect.y+y);
				iData.setPixel(x,y,color.getRed()<<16 |
								   color.getGreen()<<8 |
								   color.getBlue());
			}	
		}
		
		Image image = new Image(Display.getDefault(),iData);
		return image;
	}
	
	/**
	 * Returns the color of a pixel at the given screen coordinates.
	 * <p>
	 * NOTE: Application code must explicitly invoke the <code>Color.dispose()</code> 
 	 * method to release the operating system resources managed by each instance
 	 * when those instances are no longer required.
 	 * </p>
 	 * 
 	 * @param x X-position of pixel
 	 * @param y Y-position of pixel
 	 * 
 	 * @return color of the specified pixel
	 */
	public synchronized Color getPixelColor(int x, int y){
		java.awt.Color awtColor = robot.getPixelColor(x,y);
		return new Color(Display.getDefault(), awtColor.getRed(),
								  awtColor.getGreen(),
								  awtColor.getBlue());
	}
	
	/**
	 * Sleep for the specified amount of time, taking care to 
	 * NOT sleep a thread that has a corresponding Display object and 
	 * event loop associated with it.
	 * 
	 * @param delay the number of milliseconds to sleep
	 */
	public static void delay(int ms){
		trace("starting delay");
		final Display display = Display.getCurrent(); 
		if (display == null) {
			// Not in UI thread, so just wait. 
			trace("not in UI thread, so sleeping...");
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
			}
			return;
		}
		final boolean[] continueWait = new boolean[] { true };
		display.timerExec(ms, new Runnable() {
			public void run() {
				trace("turning flag off");
				continueWait[0] = false;
				// Makes sure we wake up and see the flag has been turned off. 
				trace("waking up ui");
				display.asyncExec(null);
				trace("done waking up ui");
			}
		});
		
		while (continueWait[0]) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		trace("ending delay");
	}
	
	/**
	 * Returns the number of milliseconds this <code>Robot</code> sleeps after 
	 * generating an event.
	 * 
	 */
	public synchronized int getAutoDelay(){
		return robot.getAutoDelay();
	}
	
	/**
	 * Returns whether this <code>Robot</code> automatically invokes 
	 * <code>waitForIdle</code> after generating an event.
	 * 
	 * @return whether <code>waitForIdle</code> is automatically called
	 */
	public synchronized boolean isAutoWaitForIdle(){
		return robot.isAutoWaitForIdle();
	}
	
	/**
	 * Rotates the scroll wheel on wheel-equipped mice. 
	 * 
	 * @param wheelAmt number of "notches" to move the mouse wheel 
	 * Negative values indicate movement up/away from the user, positive values 
	 * indicate movement down/towards the user.
	 * 
	 */
	/* NOT IN JDK 1.3.1
	public synchronized void mouseWheel(int wheelAmt){
		robot.mouseWheel(wheelAmt);
	}
	*/
	
	/**
	 * Sets the number of milliseconds this <code>Robot</code> sleeps after 
	 * generating an event.
	 * 
	 * @param ms time to sleep in milliseconds 
	 */
	public synchronized void setAutoDelay(int ms){
		robot.setAutoDelay(ms);
	}
	
	/**
	 * Sets whether this <code>Robot</code> automatically invokes <code>waitForIdle</code> 
     * after generating an event.
     * 
     * @param isOn Whether <code>waitForIdle</code> is automatically invoked
	 */	
	public synchronized void setAutoWaitForIdle(boolean isOn){
		robot.setAutoWaitForIdle(isOn); 
	}
	
	/**
	 * Returns a string representation of this <code>Robot</code>.
	 * 
	 * @returns The <code>String</code> representation
	 */	
	public synchronized String toString(){
		String params = "autoDelay = "+getAutoDelay()+", "+"autoWaitForIdle = "+isAutoWaitForIdle();
		return getClass().getName() + "[ " + params + " ]";
	}
	
	/**
	 * Dispatches events from the OS's event queue until all events
	 * in the queue, prior to calling this method, have been handled. 
	 */
	public synchronized void waitForIdle() {
		if(displayProperty.getThread()==Thread.currentThread()){
			while(displayProperty.readAndDispatch()) ;		
		}
		else{
			displayProperty.syncExec(new Runnable() {
				public void run() {
					while(displayProperty.readAndDispatch());
				}
			});
		}
	}
	
	public /*synchronized*/ void waitForIdle(final Display display){
		display.syncExec(new Runnable() {
			public void run() {
				while(display.readAndDispatch());
			}
		});		
	}
	
	/**
	 * Presses all keys in a given accelerator.  To type a character or digit,
	 * just use the unicode value.  For example, <code> accelerator = 'k', 
	 * accelerator = 'K', accelerator = '5' </code>.  Note that this is case-
	 * sensitive, so the <code>SWT.SHIFT</code> key is implied if the character 
	 * is uppercase or otherwise requires the <code>SWT.SHIFT</code> key.  	 
	 * 
	 * For non-character keys, use the keycodes defined in org.eclipse.swt.SWT.
	 * For example, to type F1, <code> accelerator = SWT.F1; </code>
	 * 
	 * Note that an accelerator can contain multiple modifier-key masks- like 
	 * <code>accelerator = SWT.CTRL | SWT.ALT | SWT.SHIFT;</code>- but at most 
	 * one character or keycode.  Also, mouse-button masks are ignored here.
	 * 
	 * This method ignores characters that do not appear on a US keyboard.
	 * 
	 * @param accelerator SWT accelerator containing the keys to be pressed
	 * 
	 * @see org.eclipse.swt.SWT for more info on accelerators
	 */
	public synchronized void keyPress(int accelerator){
		//int[] keys = SWTCodeToAWT(accelerator);
		int[] keys = getVirtualKeycode(accelerator);
		boolean shift = false;
		for(int i=0; i<keys.length;i++){
			//System.out.print(keys[i]+",");
			if(keys[i]!=0 && keys[i]!=KeyEvent.VK_UNDEFINED){// make sure that this entry is not empty and not invalid
				if(keys[i]==KeyEvent.VK_SHIFT && shift) ;// skip it; shift has already been pressed
				else try{ 
//					if(keys[i]==KeyEvent.VK_PERIOD)
//						robot.keyPress(KeyEvent.VK_DECIMAL);
					//else if(keys[i]==KeyEvent.VK_MINUS)
					//	robot.keyPress(KeyEvent.VK_SUBTRACT);	
					//else
						robot.keyPress(keys[i]);
				}
				catch(IllegalArgumentException iae){
					System.err.println("IllegalArgumentException: keystroke :"+keys[i]
										+"\nAccelerator:"+accelerator
										+"\nCast as a char: "+(char)accelerator);
										
				}				
        	if(keys[i]==KeyEvent.VK_SHIFT){
					shift = true;
				}
			}
		}
		//System.out.println();
	}

	/**
	 * Releases all keys in a given accelerator. 
	 * 
	 * @param accelerator SWT accelerator containing the keys to be released
	 * 
	 * This method ignores characters that do not appear on a US keyboard.
	 * 
	 * @see org.eclipse.swt.SWT for more info on accelerators
	 * @see keyPress(int accelerator)
	 */
	public synchronized void keyRelease(int accelerator){
//		int[] keys = SWTCodeToAWT(accelerator);
		int[] keys = getVirtualKeycode(accelerator);
		boolean shift = false;
		for(int i=0; i<keys.length;i++){
			if(keys[i]!=0){// make sure that this entry is not empty
				if(keys[i]==KeyEvent.VK_SHIFT && shift) ;// skip it; shift has already been pressed
//				else if(keys[i]==KeyEvent.VK_PERIOD)
//					robot.keyRelease(KeyEvent.VK_DECIMAL);
//				else if(keys[i]==KeyEvent.VK_MINUS)
//					robot.keyRelease(KeyEvent.VK_SUBTRACT);
				else
					robot.keyRelease(keys[i]);
				
				if(keys[i]==KeyEvent.VK_SHIFT){
					shift = true;
				}
			}
		}
	}
	
	/**
	 * Moves mouse pointer to given screen coordinates.
	 * 
	 * @param x X-position
	 * @param y Y-position
	 */
	public synchronized void mouseMove(int x, int y){
		robot.mouseMove(x,y);
	}
	
	/**
	 * Presses all mouse buttons contained in a given accelerator, which 
	 * can include any/all of the following: <code> SWT.BUTTON1, SWT.BUTTON2, SWT.BUTTON3</code>.
	 * 
	 * Note that this method will not generate any keystrokes, only mouse
	 * button presses.
	 * 
	 * @param accelerator SWT accelerator containing the mouse buttons to be pressed
	 * 
	 * @see org.eclipse.swt.SWT for more info on accelerators
	 * @see keyPress(int accelerator)	 
	 */
	public synchronized void mousePress(int accelerator){
		accelerator &= BUTTON_MASK;
		if((accelerator&SWT.BUTTON1)==SWT.BUTTON1)
			robot.mousePress(InputEvent.BUTTON1_MASK);
		if((accelerator&SWT.BUTTON2)==SWT.BUTTON2)
			robot.mousePress(InputEvent.BUTTON2_MASK);
		if((accelerator&SWT.BUTTON3)==SWT.BUTTON3)
			robot.mousePress(InputEvent.BUTTON3_MASK);
	}
	
	/**
	 * Releases all mouse buttons contained in a given accelerator.
	 * <p>
	 * Note that this method will not release any keys, only mouse buttons.
	 * </p> 
	 * @param accelerator SWT accelerator containing the mouse buttons to be released
	 * 
	 * @see org.eclipse.swt.SWT for more infor on accelerators
	 * @see mousePress(int accelerator)
	 * @see keyPress(int accelerator)
	 */
	public synchronized void mouseRelease(int accelerator){
		accelerator &= BUTTON_MASK;
		if((accelerator&SWT.BUTTON1)==SWT.BUTTON1)
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		if((accelerator&SWT.BUTTON2)==SWT.BUTTON2)
			robot.mouseRelease(InputEvent.BUTTON2_MASK);
		if((accelerator&SWT.BUTTON3)==SWT.BUTTON3)
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
	}
	
	/* Converts a single accelerator code into multiple virtual keycodes.  
	 * This ignores mouse buttons.
	 */
	private int[] getVirtualKeycode(int code){
		//System.out.println(Integer.toBinaryString(code));
		int[] res = new int[MODIFIER_COUNT+2];// one extra for the keycode/character(last pos) and VK_SHIFT may occupy more than one pos
		Arrays.fill(res,0);
		int idx = 0;
		if((SWT.MODIFIER_MASK & code)!=0){//check for all modifier keys
			if((code&SWT.ALT)==SWT.ALT)		res[idx++]=KeyEvent.VK_ALT;
			if((code&SWT.SHIFT)==SWT.SHIFT)	res[idx++]=KeyEvent.VK_SHIFT;
			if((code&SWT.CTRL)==SWT.CTRL)	res[idx++]=KeyEvent.VK_CONTROL;
		}

		int keyCode = code & SWT.KEY_MASK;

		if((SWT.KEYCODE_BIT & keyCode)!=0){// code contains a keycode- set the last array element
			switch(keyCode){
				case SWT.ARROW_UP:			res[MODIFIER_COUNT+1]= KeyEvent.VK_UP; break;
				case SWT.ARROW_DOWN:		res[MODIFIER_COUNT+1]= KeyEvent.VK_DOWN; break;
				case SWT.ARROW_LEFT:		res[MODIFIER_COUNT+1]= KeyEvent.VK_LEFT; break;
				case SWT.ARROW_RIGHT:		res[MODIFIER_COUNT+1]= KeyEvent.VK_RIGHT; break;
				case SWT.PAGE_UP:			res[MODIFIER_COUNT+1]= KeyEvent.VK_PAGE_UP; break;
				case SWT.PAGE_DOWN:			res[MODIFIER_COUNT+1]= KeyEvent.VK_PAGE_DOWN; break;
				case SWT.HOME:				res[MODIFIER_COUNT+1]= KeyEvent.VK_HOME; break;
				case SWT.END:				res[MODIFIER_COUNT+1]= KeyEvent.VK_END; break;
				case SWT.INSERT:			res[MODIFIER_COUNT+1]= KeyEvent.VK_INSERT; break;

				case SWT.F1:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F1; break;
				case SWT.F2:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F2; break;
				case SWT.F3:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F3; break;
				case SWT.F4:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F4; break;
				case SWT.F5:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F5; break;
				case SWT.F6:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F6; break;
				case SWT.F7:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F7; break;
				case SWT.F8:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F8; break;
				case SWT.F9:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F9; break;
				case SWT.F10:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F10; break;
				case SWT.F11:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F11; break;
				case SWT.F12:				res[MODIFIER_COUNT+1]= KeyEvent.VK_F12; break;
				default:					res[MODIFIER_COUNT+1]= KeyEvent.VK_UNDEFINED; break;
			}			
		}
		else{// code contains a unicode character or digit (or ESC or DEL)						
			
			// handle ESC or DEL
			if(keyCode==SWT.DEL)
				res[MODIFIER_COUNT+1] = KeyEvent.VK_DELETE;
			else if(keyCode==SWT.ESC)
				res[MODIFIER_COUNT+1] = KeyEvent.VK_ESCAPE;
			
			// handle unicode chars
			else{
				if(Character.isDigit((char)keyCode)){		// digits
					res[MODIFIER_COUNT+1] = (keyCode - (int)'0') + KeyEvent.VK_0;
				}
				
				else if( (keyCode>='a' &&keyCode<='z') || (keyCode>='A'&&keyCode<='Z')){	// letters 'a'-'z' and 'A' - 'Z'
					if(Character.isUpperCase((char)keyCode)){
						res[MODIFIER_COUNT] = KeyEvent.VK_SHIFT;
						keyCode	= (int)Character.toLowerCase((char)keyCode);
					}
					res[MODIFIER_COUNT+1] = (keyCode - (int)'a') + KeyEvent.VK_A;
				}
				else{										// all other chars on US keyboard
					CharCode cc = (CharCode)keycodes.get(new Character((char) keyCode));	
					if(cc==null)
						res[MODIFIER_COUNT+1] = KeyEvent.VK_UNDEFINED;
					else{
						if(cc.shift)
							res[MODIFIER_COUNT] = KeyEvent.VK_SHIFT;
						res[MODIFIER_COUNT+1] = cc.keycode;
					}
				}
			}
		}
		return res;
	}	
	
	/**
	 * Get the <code>Display</code> object with which this robot is
	 * synchronized.
	 * 
	 * @return the <code>Display</code> associated with this <code>Robot</code>
	 */
	public Display getDisplay(){
		return displayProperty;
	}

	/**
	 * Set the <code>Display</code> object with which this robot is
	 * synchronized.
	 * 
	 * @param display the <code>Display</code> to associate with this <code>Robot</code>
	 */	
	public void setDisplay(Display display){
		this.displayProperty = display;
	}	
	
	//!pq: a debug flag to turn on verbose delay timing info
	private static final boolean DEBUG_DELAY_INFO = false;
	
	private static void trace(String message) {
		if (DEBUG_DELAY_INFO)
			System.out.println(message);
	}	
}
