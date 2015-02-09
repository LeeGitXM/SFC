/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import com.ils.sfc.migration.Converter;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Walk the input directory writing converted files into the second.
 */
public class CopyWalker implements FileVisitor<Path>  {
	private final static String TAG = "CopyWalker";
	private final String inRoot;    // Original root directory (munged)
	private final Path outRoot;
	private final Converter delegate;   // Delegate for migrating individual files
	private final LoggerEx log;

	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public CopyWalker(Path g2Root,Path igRoot, Converter migrator) {
		this.delegate = migrator;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.inRoot = delegate.toCamelCase(g2Root.toString());
		this.outRoot = igRoot;
	}

	/** 
	 * Create any necessary output subdirectories, if needed
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		log.infof("%s.preVisitDirectory: visiting %s",TAG,dir.toString());
		String relative = relativize(inRoot,delegate.toCamelCase(dir.toString()));
		// Before visiting entries in a directory we create the output directory
		Path newdir = Paths.get(outRoot.toString(),relative);
		log.infof("%s.preVisitDirectory: resolved/creating %s",TAG,newdir.toString());
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
		try {
			if( !Files.exists(newdir)) {
				Files.createDirectory(newdir,attr);
			}
		}
		catch(IOException ioe) {
			System.err.format("preVisitDirectory.Unable to create: %s\n", newdir);
			return FileVisitResult.SKIP_SUBTREE;
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		String relative = relativize(inRoot,delegate.toCamelCase(file.toString()));
		Path newfile = Paths.get(outRoot.toString(),relative);
		log.infof("%s.visitFile: %s -> %s",TAG,file.toString(),newfile.toString());
		// Make sure that the new file does not exist ... 
		// if so try append alpha until we get a free name. 
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
		log.infof("%s.relativize: %s to %s",TAG,root,extended);
		String result = extended;
		if( extended.length()>root.length()+1) {
			result = extended.substring(root.length()+1);
		}
		log.infof("%s.relativize: yields %s",TAG,result);
		return result;
	}
}
