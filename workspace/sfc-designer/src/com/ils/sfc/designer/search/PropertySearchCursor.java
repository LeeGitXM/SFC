package com.ils.sfc.designer.search;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
public class PropertySearchCursor extends SearchObjectCursor {
	private final String TAG = "PropertySearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private int index = 0;
	private final String chartPath;
	private final String chartType ;
	private final Element parent;
	private final ProjectResourceId resourceId;
	private final List<Element> children;

	public PropertySearchCursor(DesignerContext ctx,String path,ProjectResourceId resId,Element element) {
		this.context = ctx;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		this.chartPath = path;
		this.parent = element;
		this.chartType = element.getAttribute("factory-id");
		this.resourceId = resId;
		this.children = new ArrayList<>();
		NodeList nodes = element.getChildNodes();
		for( int i=0;i<nodes.getLength();i++) {
			Node node = nodes.item(i);
			if( node instanceof Element ) {
				children.add((Element)node);
			}
		}
	}
	@Override
	public Object next() {
		Object so = null; // SearchObject
		if( index==0 ) {
			String name = parent.getAttribute("name");
			so = new StepNameSearchObject(context,chartPath,chartType,resourceId,name);
		}
		// Depending on the binding, return either the value or binding string
		else {
			so = new StepPropertySearchObject(context,chartPath,chartType,resourceId,children.get(index-1));
		}
		
		index++;
		return so;
	}

	// Index 0 is the name
	public boolean hasNext() {
		if( index<=children.size() ) return true;
		return false;
	}
}