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
	 * With each file visited, record the current partial path for later
	 * use as the location of a file. 
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		String chartName = delegate.chartNameFromPath(file);
		String partialPath = relativize(root,file.toString());
		log.tracef("%s.visitFile: path map of %s = %s",TAG,chartName,partialPath);
		pathMap.put(chartName, String.format("%s/%s",partialPath,chartName));
		return FileVisitResult.CONTINUE;
	}
}
