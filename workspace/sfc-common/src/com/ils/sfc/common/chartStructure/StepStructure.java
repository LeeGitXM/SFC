package com.ils.sfc.common.chartStructure;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.ParallelDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.definitions.TransitionDefinition;

/** 
 * A class to hold an SFC Step's relationships in a way that is handy for us. 
 */
public class StepStructure {
	private final String id;        // required, globally unique UUID
	private final String name;              // required; unique within chart
	private final String factoryId; // required
	private final ChartStructure chart;   // the chart that contains this step; required
	private final StepStructure previous; // preceding step, if any; nullable
	private final String enclosedChartName;     // the full path name of the enclosed chart; may be null or bogus
	private ChartStructure enclosedChart; // null unless enclosedChartName refers to a valid chart
	private final String expression;      // null unless this is a transition
	private final ElementDefinition.ElementType elementType;
	private PropertySet properties;
	
	/**
	 * Constructor for a StepDefinition. We are an enclosing step if we have a chart-path property
	 * @param chart
	 * @param stepDef
	 */
	public StepStructure(ChartStructure chart, StepStructure previous, StepDefinition stepDef) {
		this.chart = chart;
		this.elementType = stepDef.getElementType();
		this.id = stepDef.getElementId().toString();
		this.factoryId = stepDef.getFactoryId().toString();
		this.previous = previous;
		this.name = (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(), ChartStructureCompiler.NAME_PROPERTY);
		this.enclosedChartName =  (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(),ChartStructureCompiler.CHART_PATH_PROPERTY);
		this.expression   = null;
		this.properties = stepDef.getProperties();
	}
	/**
	 * Constructor for a ParallelDefinition
	 * @param chart
	 * @param stepDef
	 */
	public StepStructure(ChartStructure chart, StepStructure previous, ParallelDefinition pDef) {
		this.chart = chart;
		this.elementType = pDef.getElementType();
		this.id = pDef.getElementId().toString();
		this.factoryId = null;
		this.previous = previous;
		this.name = "";
		this.enclosedChartName = null;
		this.expression   = pDef.getCancelConditionExpression();
	}
	/**
	 * Constructor for a TransitionDefinition
	 * @param chart
	 * @param stepDef
	 */
	public StepStructure(ChartStructure chart, StepStructure previous, TransitionDefinition transDef) {
		this.chart = chart;
		this.elementType = transDef.getElementType();
		this.id = transDef.getElementId().toString();
		this.factoryId = null;
		this.previous = previous;
		this.name = "";
		this.enclosedChartName = null;
		this.expression   = transDef.getExpression();
	}
	public ChartStructure getChart() {return chart;}
	public String getFactoryId() {return factoryId;}
	public String getId() {return id;}
	public String getExpression() {return expression;}
	public String getName() {return name;}
	public PropertySet getProperties() {return properties;}
	public StepStructure getPrevious() {return previous;}
	public ElementDefinition.ElementType getElementType() { return elementType; }
	public ChartStructure getEnclosedChart() {return enclosedChart;}
	public String getEnclosedChartName() {return enclosedChartName;}
	public boolean isEnclosure() { return (this.enclosedChartName!=null); }
	public void setEnclosedChart(ChartStructure enclosedChart) {this.enclosedChart = enclosedChart;}
	
	/** 
	 * The "parent" of this step is the chart that owns it.
	 */
	public ChartStructure getParent() {
		return chart;
	}
}