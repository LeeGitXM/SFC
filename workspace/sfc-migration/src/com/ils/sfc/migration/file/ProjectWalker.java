/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ils.sfc.migration.ProjectBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Walk the output directory collecting files for incorporation
 * into the output project.
 */
public class ProjectWalker implements FileVisitor<Path>  {
	private final static String TAG = "ProjectWalker";
	private final static String ROOT = "947a0c86-3b70-45bf-b8f2-829d108ab928";  // SFC root node
	private final LoggerEx log;
	private final Path indir;
	private final ProjectBuilder delegate;   // Delegate for migrating individual files
	private Path currentDirectory = null;              // Current parent directory
	private final Map<String,String> uuidForPath;

	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public ProjectWalker(Path in,ProjectBuilder builder) {
		this.indir = in;
		this.delegate = builder;
		this.uuidForPath = new HashMap<>();
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/** 
	 * Each time we visit a directory, we create a corresponding folder in the project.
	 * We assume a top-down search which allows us to look up our parent's UUID.
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		// The root folder has ROOT as its parent
		log.infof("%s.previsitDirectory: %s ...",TAG,dir.toString());
		String dirname = "";
		String dirString = dir.toString();
		int index = dirString.lastIndexOf(File.separator);
		if( index>0 ) {
			dirname= dirString.substring(index+1);
		}
		String parentId = ROOT;
		if( dir.compareTo(indir)!=0) {
			// If not root,lookup parent Id
			String parent = dirString;
			if( index>0 ) {
				parent = dirString.substring(0,index);	
			}
			parentId = uuidForPath.get(parent);
		}
		if( parentId!=null) {
			currentDirectory = dir;
			String uuid = UUID.randomUUID().toString();
			uuidForPath.put(dirString, uuid);
			log.infof("%s.previsitDirectory: %s = %s.",TAG,dirString,uuid);
			delegate.addFolder(dirname,uuid,parentId);
		}
		else {
			log.errorf("%s.previsitDirectory: Error: No parent found for %s",TAG,dirString);
		}
		return FileVisitResult.CONTINUE;
	}
	

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		// Look up the UUID of the parent directory
		String parentUUID = uuidForPath.get(currentDirectory);
		delegate.addChart(file,parentUUID);
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
	
}
