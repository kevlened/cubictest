/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;


/**
 * @author Christian Schwarz
 *
 */
public class SubTestFigure extends AbstractTransitionNodeFigure {
	

	public SubTestFigure(String name){
		label = new CubicTestHeaderLabel(name, this, false);
		label.setTextPlacement(PositionConstants.NORTH);
		add(label);
		FlowLayout manager = ViewUtil.getFlowLayout();
		manager.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		manager.setMajorAlignment(FlowLayout.ALIGN_CENTER);
		setLayoutManager(manager);	
		
		setBackgroundColor(ColorConstants.darkBlue);
		setForegroundColor(ColorConstants.white);
		setBorder(new LineBorder(ColorConstants.black));
		setOpaque(true);
	}
	
	
	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		Dimension d = super.getMinimumSize(wHint, hHint).getCopy();
		d.height = 18;
		d.width = d.width + 10; 
		return d;
	}
	
}
