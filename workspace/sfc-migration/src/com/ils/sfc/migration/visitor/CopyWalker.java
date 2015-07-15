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

import org.w3c.dom.Element;

import com.ils.sfc.migration.Converter;
/**
 * Walk the input directory writing converted files into the second.
 */
public class CopyWalker extends AbstractPathWalker implements FileVisitor<Path>  {
	private final static String TAG = "CopyWalker";
	private final String inRoot;         // Original G2 root directory (camelized)
	private final Path outRoot;
	private final Converter delegate;   // Delegate for migrating individual files

	 /**
	  * 
	  * @param in
	  * @param out
	  */
	public CopyWalker(Path g2Root,Path igRoot, Converter converter) {
		this.delegate = converter;
		this.inRoot = delegate.toCamelCase(g2Root.toString());
		this.outRoot = igRoot;
	}

	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//Ignore the OSX resource marker
		if(file.toString().endsWith(".DS_Store")) return FileVisitResult.CONTINUE;
		log.infof("%s.visitFile: file: %s",TAG, file.toString());
		log.infof("%s.visitFile: outroot: %s",TAG, outRoot.toString());
		String infile = file.getFileName().toString();
		int pos = infile.lastIndexOf(".");
		if( pos>0 ) infile = infile.substring(0,pos);
		log.infof("%s.visitFile: relative: %s",TAG, infile);
		Path newfile = Paths.get(outRoot.toString(),delegate.partialPathFromInfile(infile));
		log.infof("%s.visitFile: newFile: %s",TAG, newfile.toString());
		// Make sure that the new file does not exist ... 
		// if so try append alpha until we get a free name.
		int version = 0;
		Path target = newfile;
		while(Files.exists(target)) {
			target = Paths.get(String.format("%s%c",newfile.toString(),'a'+version));
		}
		target = Paths.get(target.toString()+".xml");
		if( log.isTraceEnabled()) {
			log.tracef("%s.visitFile: %s -> %s",TAG,file.toString(),target.toString());
		}
		else {
			log.info("\n=============================================================================================================");
			log.infof("%s.visitFile: processing %s",TAG,file.toString());
		}
		delegate.convertFile(file,target);
		return FileVisitResult.CONTINUE;
	}
}