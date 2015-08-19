package com.ils.sfc.designer.search;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.inductiveautomation.ignition.client.util.gui.ErrorUtil;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.definitions.ParallelDefinition;
import com.inductiveautomation.sfc.definitions.TransitionDefinition;
/**
 * Simply return the stepname for editing.
 * @author chuckc
 *
 */
public class StepExpressionSearchObject implements SearchObject {

	private final String chartPath;
	private final long chartResourceId;
	private final String expression;
	private final String type;
	private final DesignerContext context;
	private final ResourceBundle rb;
	
	public StepExpressionSearchObject(DesignerContext ctx,String path,long resid,ParallelDefinition pdef) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.chartPath = path;
		this.expression = pdef.getCancelConditionExpression();
		this.type = "parallel";
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	public StepExpressionSearchObject(DesignerContext ctx,String path,long resid,TransitionDefinition tdef) {
		this.context = ctx;
		this.chartResourceId = resid;
		this.chartPath = path;
		this.expression = tdef.getExpression();
		this.type = "transition";
		this.rb = ResourceBundle.getBundle("com.ils.sfc.designer.designer");  // designer.properties
	}
	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/step.png"));
		return icon;
	}

	@Override
	public String getName() {
		return type;
	}

	@Override
	public String getOwnerName() {
		return chartPath;
	}

	@Override
	public String getText() {
		return expression;
	}

	@Override
	public void locate() {
		ChartLocator locator = new ChartLocator(context);
		locator.locate(chartResourceId);
	}

	@Override
	public void setText(String arg0) throws IllegalArgumentException {
		ErrorUtil.showWarning(rb.getString("Locator.ExpressionChangeWarning"), rb.getString("Locator.WarningTitle"));
		
	}

}
