package com.windowtester.examples.gef.uml.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import com.windowtester.examples.gef.common.model.AbstractModelElement;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassElement extends AbstractModelElement {
	
	private static final long serialVersionUID = 1L;
	
	public static final String LOCATION_PROP = "Class.Location";
	public static final String SIZE_PROP = "Class.Size";	
	public static final String SOURCE_CONNECTIONS_PROP = "Class.SourceConn";
	public static final String TARGET_CONNECTIONS_PROP = "Class.TargetConn";
	
	private InheritsRelationship _super;
	private String _name;
	
	private final Point _location = new Point(0,0);
	
	private final Dimension _dimension = new Dimension();
	
	private final List<InheritsRelationship> _subs = new ArrayList<InheritsRelationship>();
	
	
	public InheritsRelationship getSuper() {
		return _super;
	}
	
	public void setSuper(InheritsRelationship superClass) {
		_super = superClass;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		getLocation().setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, getLocation());
	}

	public Point getLocation() {
		return _location;
	}

	public Dimension getSize() {
		return _dimension;
	}
	
	public void addSub(InheritsRelationship sub) {
		if (sub == null)
			throw new IllegalArgumentException();
		getSubs().add(sub);
		firePropertyChange(SOURCE_CONNECTIONS_PROP, null, sub);
	}
	
	public List<InheritsRelationship> getSubs() {
		return _subs;
	}

	public void removeSub(InheritsRelationship sub) {
		if (sub == null)
			throw new IllegalArgumentException();
		getSubs().remove(sub);
		firePropertyChange(TARGET_CONNECTIONS_PROP, null, sub);
	}
	
}
