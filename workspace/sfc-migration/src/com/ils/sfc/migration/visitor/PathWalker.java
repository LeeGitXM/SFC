/**
 *   (c) 2015-2016  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.ils.sfc.migration.Converter;
/**
 * Before we can link encapsulations via path, we need to create a
 * complete path map. Traverse the input tree, convert the names in the same
 * way as the final converter, then make two lookups:
 *   1)  lookup of relative path, versus chart name.
 */
public class PathWalker extends AbstractPathWalker implements FileVisitor<Path>  {
	private final Converter delegate;
	 /**
	  * @param converter
	  */
	public PathWalker(Converter converter) {
		this.delegate = converter;
	}

	/** 
	 * Each file visited represents a chart. Record its name versus the normalized
	 * path from the root. This is ultimately used for the reference path within 
	 * encapsulations that might reference the chart. 
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		// Ignore directories
		if(file.toFile().isDirectory())           return FileVisitResult.CONTINUE;
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		String fname = fileName(file);
		if( fname.equalsIgnoreCase("/") )         return FileVisitResult.CONTINUE;
		delegate.analyzePath(file,fname);
		return FileVisitResult.CONTINUE;
	}
}
