/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.visitor;

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
/**
 * Walk the input directory writing converted files into the second.
 */
public class CopyWalker extends AbstractPathWalker implements FileVisitor<Path>  {
	private final static String TAG = "CopyWalker";
	private final String inRoot;         // Original G2 root directory (munged)
	private final Path outRoot;
	private final Converter delegate;   // Delegate for migrating individual files

	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public CopyWalker(Path g2Root,Path igRoot, Converter migrator) {
		this.delegate = migrator;
		this.inRoot = delegate.toCamelCase(g2Root.toString());
		this.outRoot = igRoot;
	}

	/** 
	 * Create any necessary output subdirectories, if needed
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		log.debugf("%s.preVisitDirectory: visiting %s",TAG,dir.toString());
		String relative = relativize(inRoot,delegate.toCamelCase(dir.toString()));
		// Before visiting entries in a directory we create the output directory
		Path newdir = Paths.get(outRoot.toString(),relative);
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
		try {
			if( !Files.exists(newdir)) {
				Files.createDirectory(newdir,attr);
			}
		}
		catch(IOException ioe) {
			log.errorf("%s.previsitDirectory: Unable to create: %s",TAG, newdir.toString());
			return FileVisitResult.SKIP_SUBTREE;
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		String relative = relativize(inRoot,delegate.toCamelCase(file.toString()));
		//log.infof("%s.visitFile: %s relative=%s",TAG,file.toString(),relative);
		Path newfile = Paths.get(outRoot.toString(),relative);
		// Make sure that the new file does not exist ... 
		// if so try append alpha until we get a free name.
		int version = 0;
		Path target = newfile;
		while(Files.exists(target)) {
			target = Paths.get(String.format("%s%c",newfile.toString(),'a'+version));
		}
		target = Paths.get(target.toString()+".xml");
		log.infof("%s.visitFile: %s -> %s",TAG,file.toString(),target.toString());
		delegate.convertFile(file, target);
		return FileVisitResult.CONTINUE;
	}
}
