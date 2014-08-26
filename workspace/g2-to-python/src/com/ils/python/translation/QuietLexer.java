/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
package com.ils.python.translation;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;

import com.ils.g2.procedure.G2ProcedureLexer;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 * This class is a failed attempt to quiet the lexer complaints about 
 * token recognition errors whenever we hit whitespace. 
 */
public class QuietLexer extends G2ProcedureLexer {
	private final static String TAG = "QuietLexer: ";
	private final LoggerEx log;
	public QuietLexer(CharStream input) {
		super(input);
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/**
	 * Since we have elected to ignore whitespace, recover() is called
	 * to skip over an space that is encountered. Let it recover silently.
	 * 
	 * line 1:13 token recognition error at: ' '
	 */
    @Override
    public void recover(LexerNoViableAltException e) {
    	super.recover(e);
    	// log.info(TAG+"AFTER recover LexerNoViableAltException");
    }
    @Override
    public void recover(RecognitionException e) {
    	super.recover(e);
    	log.info(TAG+"recover recognitionException");
    }
}

