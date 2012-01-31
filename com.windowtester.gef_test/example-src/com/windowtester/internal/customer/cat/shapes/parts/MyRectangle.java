package com.windowtester.internal.customer.cat.shapes.parts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.internal.customer.cat.shapes.model.Shape;

public class MyRectangle extends org.eclipse.draw2d.Shape {

	private final String name;
	
	public MyRectangle() {
		this.name = "!NO_NAME!";
	}
	
	public MyRectangle(String name) {
		this.name = name;
	}
	
	/**
	 * @see Shape#fillShape(Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.fillRectangle(getBounds());
		graphics.drawText(name, getBounds().x+1, getBounds().y+getBounds().height-15);
	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		Rectangle r = getBounds();
		int x = r.x + lineWidth / 2;
		int y = r.y + lineWidth / 2;
		int w = r.width - Math.max(1, lineWidth);
		int h = r.height - Math.max(1, lineWidth);
		graphics.drawRectangle(x, y, w, h);
	}
	
	public String getText() {
		return name;
	}
}