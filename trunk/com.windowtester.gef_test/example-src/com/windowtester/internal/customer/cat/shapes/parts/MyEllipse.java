package com.windowtester.internal.customer.cat.shapes.parts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Constructs a new Ellipse with the default values of a Shape.
 */
public class MyEllipse extends org.eclipse.draw2d.Shape {
	
	private final String name;
	
	public MyEllipse() {
		this.name = "!NO_NAME!";
	}
	
	public MyEllipse(String name) {
		this.name = name;
	}
	
	/**
	 * Returns <code>true</code> if the given point (x,y) is contained within this ellipse.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return <code>true</code>if the given point is contained
	 */
	public boolean containsPoint(int x, int y) {
		if (!super.containsPoint(x, y))
			return false;
		Rectangle r = getBounds();
		long ux = x - r.x - r.width / 2;
		long uy = y - r.y - r.height / 2;
		return ((ux * ux) << 10) / (r.width * r.width) 
			 + ((uy * uy) << 10) / (r.height * r.height) <= 256;
	}
	
	/**
	 * Fills the ellipse.
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.fillOval(getBounds());
		graphics.drawText(name, getBounds().x, getBounds().y+getBounds().height-15);
	}
	
	/**
	 * Outlines the ellipse.
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds());
		r.width--;
		r.height--;
		r.shrink((lineWidth - 1) / 2, (lineWidth - 1) / 2);
		graphics.drawOval(r);
	}
	public String getText() {
		return name;
	}
}