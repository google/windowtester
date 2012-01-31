package org.eclipse.draw2d.examples.uml;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A test class to display a UMLFigure
 */
public class UMLClassFigureTest {
 public static void main(String args[]){
	Display d = new Display();
	final Shell shell = new Shell(d);
	shell.setSize(400, 400);
	shell.setText("UMLClassFigure Test");
	LightweightSystem lws = new LightweightSystem(shell);
	Figure contents = new Figure();
	XYLayout contentsLayout = new XYLayout();
	contents.setLayoutManager(contentsLayout);
	
	Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
	Label classLabel1 = new Label("Table", new Image(d, 
		UMLClassFigureTest.class.getResourceAsStream("class_obj.gif")));
	classLabel1.setFont(classFont);
	
	Label classLabel2 = new Label("Column", new Image(d, 
	        UMLClassFigureTest.class.getResourceAsStream("class_obj.gif")));
	classLabel2.setFont(classFont);
	
	final UMLClassFigure classFigure = new UMLClassFigure(classLabel1);
	final UMLClassFigure classFigure2 = new UMLClassFigure(classLabel2);
	
	Label attribute1 = new Label("columns: Column[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute2 = new Label("rows: Row[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute3 = new Label("columnID: int", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));
	Label attribute4 = new Label("items: List", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("field_private_obj.gif")));

	classFigure.getAttributesCompartment().add(attribute1);
	classFigure.getAttributesCompartment().add(attribute2);
	classFigure2.getAttributesCompartment().add(attribute3);
	classFigure2.getAttributesCompartment().add(attribute4);

	Label method1 = new Label("getColumns(): Column[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method2 = new Label("getRows(): Row[]", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method3 = new Label("getColumnID(): int", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));
	Label method4 = new Label("getItems(): List", new Image(d, 
		UMLClassFigure.class.getResourceAsStream("methpub_obj.gif")));

	classFigure.getMethodsCompartment().add(method1);
	classFigure.getMethodsCompartment().add(method2);
	classFigure2.getMethodsCompartment().add(method3);
	classFigure2.getMethodsCompartment().add(method4);
					
	contentsLayout.setConstraint(classFigure, new Rectangle(10,10,-1,-1));
	contentsLayout.setConstraint(classFigure2, new Rectangle(200, 200, -1, -1));
	
	/* Creating the connection */
	PolylineConnection c = new PolylineConnection();
	ChopboxAnchor sourceAnchor = new ChopboxAnchor(classFigure);
	ChopboxAnchor targetAnchor = new ChopboxAnchor(classFigure2);
	c.setSourceAnchor(sourceAnchor);
	c.setTargetAnchor(targetAnchor);
	
	/* Creating the decoration */
	PolygonDecoration decoration = new PolygonDecoration();
	PointList decorationPointList = new PointList();
	decorationPointList.addPoint(0,0);
	decorationPointList.addPoint(-2,2);
	decorationPointList.addPoint(-4,0);
	decorationPointList.addPoint(-2,-2);
	decoration.setTemplate(decorationPointList);
	c.setSourceDecoration(decoration);
	
	/* Adding labels to the connection */
	ConnectionEndpointLocator targetEndpointLocator = 
	        new ConnectionEndpointLocator(c, true);
	targetEndpointLocator.setVDistance(15);
	Label targetMultiplicityLabel = new Label("1..*");
	c.add(targetMultiplicityLabel, targetEndpointLocator);

	ConnectionEndpointLocator sourceEndpointLocator = 
		new ConnectionEndpointLocator(c, false);
	sourceEndpointLocator.setVDistance(15);
	Label sourceMultiplicityLabel = new Label("1");
	c.add(sourceMultiplicityLabel, sourceEndpointLocator);

	ConnectionEndpointLocator relationshipLocator = 
		new ConnectionEndpointLocator(c,true);
	relationshipLocator.setUDistance(10);
	relationshipLocator.setVDistance(-20);
	Label relationshipLabel = new Label("contains");
	c.add(relationshipLabel,relationshipLocator);

	contents.add(classFigure);
	contents.add(classFigure2);
	contents.add(c);
	
	lws.setContents(contents);
	shell.open();
	while (!shell.isDisposed())
		while (!d.readAndDispatch())
			d.sleep();
 }
}