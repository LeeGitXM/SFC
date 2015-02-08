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
	private final Path indir;
	private final Path outdir;
	private final Converter delegate;   // Delegate for migrating individual files
	private final LoggerEx log;

	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public CopyWalker(Path in,Path out,Converter migrator) {
		this.indir = in;
		this.outdir = out;
		this.delegate = migrator;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/** 
	 * Create any necessary output subdirectories, if needed
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		
		// before visiting entries in a directory we copy the directory
		Path newdir = outdir.resolve(indir.relativize(dir));
		newdir = delegate.mungeFileName(newdir);
		log.infof("%s.preVisitDirectory: creating %s",TAG,newdir.toString());
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
		delegate.convertFile(file, outdir.resolve(indir.relativize(file)));
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
