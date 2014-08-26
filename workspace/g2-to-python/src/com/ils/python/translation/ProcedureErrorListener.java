/**
 * Based on examples from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
**/
package com.ils.python.translation;
import java.util.HashMap;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 * As errors are detected, add their parameters to the dictionary.
 */
public class ProcedureErrorListener extends BaseErrorListener {
	private static String TAG = "ProcedureErrorListener: ";
	private final LoggerEx log;
	private final HashMap<String,Object> errorDictionary;
	
	public ProcedureErrorListener(HashMap<String,Object> table) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.errorDictionary = table;
	}
	@Override
	public void syntaxError(Recognizer<?,?> recognizer,
				Object offendingSymbol,
				int line, int charPositionInLine,
				String msg,
				RecognitionException e)
    {
		recordError(recognizer,(Token)offendingSymbol,
                       line, charPositionInLine);
    }

    protected void recordError(Recognizer<?,?> recognizer,
                                  Token offendingToken, int line,
                                  int charPositionInLine) {
    	// Defer to the parser.
    	if( errorDictionary.get(TranslationConstants.ERR_MESSAGE)==null ) {
    		if( offendingToken != null ) {
    			String msg = String.format("Syntax error after col %d: \'%s\'",offendingToken.getStartIndex(),offendingToken.getText());
    			log.info(TAG+msg);
    			errorDictionary.put(TranslationConstants.ERR_MESSAGE, msg);
    			errorDictionary.put(TranslationConstants.ERR_LINE, Integer.toString(offendingToken.getLine()));
    			errorDictionary.put(TranslationConstants.ERR_POSITION,Integer.toString(offendingToken.getCharPositionInLine()));
    			errorDictionary.put(TranslationConstants.ERR_TOKEN, offendingToken.getText());
    		}
    		// We get here if we're listening to the lexer - which we are not
    		else {
    			String msg = "SYTNTAX ERROR: No information";
    			log.tracef(TAG+msg);
    			errorDictionary.put(TranslationConstants.ERR_MESSAGE, msg);
    		}
    	}
    }
}
