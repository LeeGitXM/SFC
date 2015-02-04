/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.ils.sfc.migration.ProjectBuilder;
/**
 * Walk the output directory collecting files for incorporation
 * into the output project.
 */
public class ProjectWalker implements FileVisitor<Path>  {
	private final static String TAG = "ProjectWalker";
	private final Path indir;
	private final ProjectBuilder delegate;   // Delegate for migrating individual files


	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public ProjectWalker(Path in,ProjectBuilder builder) {
		this.indir = in;
		this.delegate = builder;
	}

	/** 
	 * Create any necessary output subdirectories, if needed
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		// before visiting entries in a directory we copy the directory
		/*
		Path newdir = outdir.resolve(indir.relativize(dir));
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
		FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
		try {
			Files.createDirectory(newdir,attr);
		}
		catch(IOException ioe) {
			System.err.format("preVisitDirectory.Unable to create: %s (%s)", newdir,ioe.getMessage());
			return FileVisitResult.SKIP_SUBTREE;
		}
		*/
		return FileVisitResult.CONTINUE;
	}
	

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		/*
		delegate.convertFile(file, outdir.resolve(indir.relativize(file)));
		*/
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
