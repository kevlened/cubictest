/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.policies.TestContainerEditPolicy;
import org.cubictest.ui.gef.policies.TestXYLayoutEditPolicy;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.TestFigure;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IFileEditorInput;


/**
 * Root edit part and contoller for the <code>Test</code> model.
 * 
 * @author SK Skytteren
 */
public class TestEditPart extends PropertyChangePart{

	/**
	 * Constructor for <code>TestEditPart</code>.
	 * @param test the model
	 */
	public TestEditPart(Test test) {
		setModel(test);
	}
	
	@Override
	public void activate() {
		super.activate();
		try {
			//reveal file in package explorer:
			IFile file = ((Test) getModel()).getFile();
			if(PackageExplorerPart.getFromActivePerspective() != null)
				PackageExplorerPart.getFromActivePerspective().tryToReveal(file);	
		} catch(Exception ignore) {
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		
		TestFigure fig = new TestFigure();
		String name = ((Test)getModel()).getName();
		if(((Test)getModel()).getDescription() != "") {
			name += ": \n    " + ((Test)getModel()).getDescription();
		}
		Label nameLabel = new Label(name);
		nameLabel.setLocation(new Point(250,15));
		nameLabel.setTextAlignment(Label.TOP);
		nameLabel.setLabelAlignment(Label.LEFT);
		nameLabel.setSize(300, 50);
		nameLabel.setFont(new Font(null,"tahoma", 15, 1));
		nameLabel.setForegroundColor(ColorConstants.lightGray);
		fig.addNameLabel(nameLabel);
		return fig;
	}
	
	
	public void propertyChange(PropertyChangeEvent evt){
		super.propertyChange(evt);
		String property = evt.getPropertyName();
		if (PropertyAwareObject.NAME.equals(property)) {
			refresh();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals(){
		super.refreshVisuals();
		Test test = (Test) getModel();
		TestFigure figure = (TestFigure) getFigure();
		Label desc = figure.getNameLabel();
		desc.setText(test.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getEditPolicy(java.lang.Object)
	 */
	public EditPolicy getEditPolicy(Object key) {
		
		return super.getEditPolicy(key);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new TestContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TestXYLayoutEditPolicy());
	}

	protected List getModelChildren(){
		List<Object> children = new ArrayList<Object>();
		ConnectionPoint cp = ((Test)getModel()).getStartPoint();
		if (cp != null)
			children.add(cp);
		children.addAll(((Test)getModel()).getPages());
		children.addAll(((Test)getModel()).getExtensionPoints());
		children.addAll(((Test)getModel()).getSubTests());
		children.addAll(((Test)getModel()).getCustomTestSteps());
		return children;
	}
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		super.addChildVisual(childEditPart, index+1);
	}

	public void updateParams() {
		for (Object obj : getChildren()){
			if(obj instanceof UrlStartPointEditPart)
				((UrlStartPointEditPart)obj).updateParams();
		}
	}
	
	@Override
	public DragTracker getDragTracker(Request request) {
		return new MarqueeDragTracker();
	}
	
	@Override
	public boolean isCopyable() {
		return false;
	}
	
	@Override
	public boolean isCuttable() {
		return false;
	}
	
		
}
