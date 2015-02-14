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
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Before we can link encapsulations via path, we need to create a
 * complete path map. Traverse the input tree, convert the names in the same
 * way as the final converter, then make a lookup of path, versus chaart name.
 */
public class PathWalker implements FileVisitor<Path>  {
	private final static String TAG = "PathWalker";
	private final String inRoot;        // Original root directory (munged)
	private final Converter delegate;   // Delegate for name conversion
	private final LoggerEx log;
	private final Map<String,String> pathMap;
	private String currentPartialPath = "";    // Current partial path
	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public PathWalker(Path g2Root,Map<String,String> map, Converter migrator) {
		this.delegate = migrator;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.inRoot = delegate.toCamelCase(g2Root.toString());
		this.pathMap = map;
	}

	/** 
	 * With each directory visited, record the current partial path for later
	 * use as the location of a file. 
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		//log.infof("%s.preVisitDirectory: visiting %s",TAG,dir.toString());
		currentPartialPath = relativize(inRoot,delegate.toCamelCase(dir.toString()));
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		String chartName = delegate.chartNameFromPath(file);
		log.infof("%s.visitFile: path map of %s = %s",TAG,chartName,currentPartialPath);
		pathMap.put(chartName, String.format("%s/%s",currentPartialPath,chartName));
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException ioe) throws IOException {
		System.err.format("Error converting %s: %s", file, ioe.getMessage());
		return FileVisitResult.CONTINUE;
	}



	/**
	 * Nothing is required in the post-visit phase.
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * The FileVisitor's relativize didn't perform like I expected. Here we simply
	 * subtract off the root from the extended path. Both paths have been "camelized".
	 * 
	 * @param root
	 * @param extended
	 * @return
	 */
	private String relativize(String root,String extended) {
		//log.infof("%s.relativize: %s to %s",TAG,root,extended);
		String result = extended;
		if( extended.length()>root.length()+1) {
			result = extended.substring(root.length()+1);
		}
		log.tracef("%s.relativize: yields %s",TAG,result);
		return result;
	}
}
