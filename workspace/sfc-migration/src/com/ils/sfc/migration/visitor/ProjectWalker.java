/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.visitor;

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
/**
 * Walk the output directory collecting files for incorporation
 * into the output project.
 */
public class ProjectWalker extends AbstractPathWalker implements FileVisitor<Path>  {
	private final static String TAG = "ProjectWalker";
	private final static String ROOT = "947a0c86-3b70-45bf-b8f2-829d108ab928";  // SFC root node
	private final Path indir;
	private final ProjectBuilder delegate;   // Delegate for migrating individual files
	private final Map<String,String> uuidForPath;

	 /**
	  * 
	  * @param in the root of the directory containing ignition-style XML.
	  * @param out
	  */
	public ProjectWalker(Path in,ProjectBuilder builder) {
		this.indir = in;
		this.delegate = builder;
		this.uuidForPath = new HashMap<>();
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
			String uuid = UUID.randomUUID().toString();
			uuidForPath.put(dirString, uuid);
			log.infof("%s.previsitDirectory: directory %s = %s.",TAG,dirString,uuid);
			delegate.addFolder(dirname,uuid,parentId);
		}
		else {
			log.errorf("%s.previsitDirectory: Error: No parent found for %s",TAG,dirString);
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		// Look up the UUID of the parent directory
		String directory = directoryForFile(file);
		String parentUUID = uuidForPath.get(directory);
		log.infof("%s.visitFile: parent for %s = %s.",TAG,directory,parentUUID);
		delegate.addChart(file,parentUUID);
        return FileVisitResult.CONTINUE;
	}
	
	// String off the last path segment
	private String directoryForFile(Path file) {
		String dir = file.toString();
		int pos = dir.lastIndexOf(File.separator);
		if( pos>0 ) dir = dir.substring(0,pos);
		return dir;
	}
}
