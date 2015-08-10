package com.ils.sfc.designer.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/**
 * The chart search cursor iterates over steps in a chart
 * @author chuckc
 *
 */
public class ChartSearchCursor extends SearchObjectCursor {
	private final String TAG = "ChartSearchCursor";
	private final DesignerContext context;
	private ChartDefinition chart = null; 
	private ElementDefinition element = null;
	private final LoggerEx log;
	private final ClientStepRegistry registry;
	private final ProjectResource res;
	private int index = 0;
	private int subindex = 0;
	
	public ChartSearchCursor(DesignerContext ctx,ClientStepRegistry reg,ProjectResource resource) {
		this.context = ctx;
		this.registry = reg;
		this.res = resource;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null;   // Search Object
		// Deserialize here - first time through only - return next block cursor
		if( index==0 ) {
			chart = deserializeResource(res);
			so = new ChartNameSearchObject(context,res);
			log.infof("%s.next %s",TAG,res.getName());
		}
		else if(index==1) {
			element = chart.getBeginElement();
			if( element instanceof StepDefinition ) {
				so = new StepSearchCursor(context,res.getName(),(StepDefinition) element);
			}
		}
		else {
			element = chart.getBeginElement();
			subindex = 1;
			StepDefinition chosenElement = visitChildren(element); 
			if( chosenElement!=null ) {
				so = new StepSearchCursor(context,res.getName(),chosenElement);
			}
		}
		index++;
		return so;
	}
	
	private StepDefinition visitChildren(ElementDefinition ed) {
		if( ed instanceof StepDefinition ) {
			if(subindex==index) return (StepDefinition)ed;
			subindex++;
			for(ElementDefinition def:ed.getNextElements() ) {
				StepDefinition child = visitChildren(def);
				if(child!=null ) return child;
			}
		}
		return null;
	}

	private ChartDefinition deserializeResource(ProjectResource res) {
		ChartDefinition definition = null;
		try {
			GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
			ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
			ChartCompiler compiler = new ChartCompiler(chartModel,registry);
			ChartCompilationResults ccr = compiler.compile();
			if(ccr.isSuccessful()) {
				definition = ccr.getChartDefinition();
			}
			else {
				log.warnf("%s.deserializeResource: Chart %s has compilation errors", TAG,res.getName());
			}
		}
		catch(IOException ioe ) {
			log.warnf("%s.deserializeResource: IO Exception for %s (%s)", TAG,res.getName(),ioe.getLocalizedMessage());
		}
		catch(NumberFormatException nfe ) {
			log.warnf("%s.deserializeResource: Chart instantiation error for %s (%s)", TAG,res.getName(),nfe.getLocalizedMessage());
		}
		catch(XMLParseException xpe ) {
			log.warnf("%s.deserializeResource: Parse Exception for %s (%s)", TAG,res.getName(),xpe.getLocalizedMessage());
		}

		return definition;
	}
}
