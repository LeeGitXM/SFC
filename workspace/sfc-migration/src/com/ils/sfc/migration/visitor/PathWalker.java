/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import com.ils.sfc.migration.Converter;
/**
 * Before we can link encapsulations via path, we need to create a
 * complete path map. Traverse the input tree, convert the names in the same
 * way as the final converter, then make a lookup of relative path, versus chart name.
 */
public class PathWalker extends AbstractPathWalker implements FileVisitor<Path>  {
	private final static String TAG = "PathWalker";
	private final Map<String,String> pathMap;
	private final String root;
	private final Converter delegate;
	 /**
	  * @param g2Root
	  * @param map the re
	  * @param converter
	  */
	public PathWalker(Path g2Root,Map<String,String> map, Converter converter) {
		this.delegate = converter;
		this.root = delegate.toCamelCase(g2Root.toString());
		this.pathMap = map;
	}

	/** 
	 * Each file visited represents a chart. Record its name versus the normalized
	 * path from the root. This is ultimately used for the reference path within 
	 * encapsulations that might reference the chart. 
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		String chartName = delegate.chartNameFromPath(file);
		String partialPath = relativize(root,file.toString());
		// Remove the file extension, camel-case
		if( partialPath.endsWith(".xml")) partialPath = partialPath.substring(0, partialPath.length()-4);
		partialPath = delegate.toCamelCase(partialPath);
		log.infof("%s.visitFile: path map of %s = %s",TAG,chartName,partialPath);
		pathMap.put(chartName, partialPath);
		return FileVisitResult.CONTINUE;
	}
}
