/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * This is the base class for a collection of several classes that walk 
 * a directory tree. The walker does a depth-first traversal. 
 */
public abstract class AbstractPathWalker implements FileVisitor<Path>  {
	private final static String TAG = "AbstractPathWalker";
	protected final LoggerEx log;

	 /**
	  * Constructor:
	  */
	public AbstractPathWalker() {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		
	}

	/** 
	 * This is called as each directory is visited 
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public abstract FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException;

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException ioe) throws IOException {
		log.errorf("%s.visitFileFailed: Error processing %s (%s)", TAG, file.toString(), ioe.getLocalizedMessage());
		return FileVisitResult.CONTINUE;
	}

	/**
	 * The default does nothing in the post-visit phase.
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * The FileVisitor's relativize didn't perform like I expected. Here we simply
	 * subtract off the root from the extended path. Both paths have been "camelized".
	 * 
	 * @param extended
	 * @return
	 */
	protected String relativize(String root,String extended) {
		//log.infof("%s.relativize: %s to %s",TAG,root,extended);
		String result = extended;
		if( extended.length()>root.length()+1) {
			result = extended.substring(root.length()+1);
		}
		//log.tracef("%s.relativize: %s -> %s",TAG,extended,result);
		return result;
	}
	/**
	 * Extract the file name from the path. Strip off any trailing extension.
	 * 
	 * @param path full path
	 * @return
	 */
	protected String fileName(Path path) {
		//log.infof("%s.relativize: %s to %s",TAG,root,extended);
		Path fname = path.getFileName();
		String result = fname.toString();
		int pos = result.lastIndexOf(".");
		if( pos>0 ) result = result.substring(0,pos);
		return result;
	}
}
