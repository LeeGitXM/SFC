/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
package com.ils.python.translation;

import java.util.HashMap;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** Instead of recovering from exceptions, log the information to
 * a .
 */
public class ProcedureErrorStrategy extends DefaultErrorStrategy {
	private static final String TAG = "ProcedureErrorStrategy: ";
	private final LoggerEx log;
	private final HashMap<String,Object> errorDictionary;
	
	public ProcedureErrorStrategy(HashMap<String,Object> table) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.errorDictionary = table;
	}
	
	/**
	 * This appears to be a top-level view of things...
	 */
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
    	super.recover(recognizer,e);
    	//log.trace(TAG+": RECOVER");
    	//recordError(recognizer,e);  // Moved to reportError() override
    }

    /** Make sure we don't attempt to recover inline; if the parser
     *  successfully recovers, it won't throw an exception.
     */
    @Override
    public Token recoverInline(Parser recognizer)  {
    	//log.tracef("%s: RECOVER-INLINE",TAG);
    	recordError(recognizer,new InputMismatchException(recognizer));
    	return super.recoverInline(recognizer);
    }
    
    /**
	 * This appears to be a top-level view of things...
	 */
    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
    	//log.tracef("%s: REPORT-ERROR",TAG);
    	recordError(recognizer,e);
    }


    /** Make sure we don't attempt to recover from problems in sub-rules. */
    @Override
    public void sync(Parser recognizer) { }
    
    protected void recordError(Recognizer<?,?> recognizer, RecognitionException re) {
    	// In each case the expected tokens are an expression. Don't bother to list
    	
    	Token offender = re.getOffendingToken();
    	if( offender != null ) {
    		String msg = String.format("Mismatch %d:%d: expecting an expression, got \'%s\'",offender.getLine(),offender.getCharPositionInLine(),offender.getText());
    		log.info(TAG+msg);
    		errorDictionary.put(TranslationConstants.ERR_MESSAGE, msg);
    		errorDictionary.put(TranslationConstants.ERR_LINE, Integer.toString(offender.getLine()));
    		errorDictionary.put(TranslationConstants.ERR_POSITION,Integer.toString(offender.getCharPositionInLine()));
    		errorDictionary.put(TranslationConstants.ERR_TOKEN, offender.getText());
    	}
    	else {
    		String msg = "SYTNTAX ERROR: No information";
    		log.debug(TAG+msg);
    		errorDictionary.put(TranslationConstants.ERR_MESSAGE, msg);
    	}
    }
}

