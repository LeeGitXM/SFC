// Generated from G2Expression.g4 by ANTLR 4.0
package com.ils.aed.expression;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class G2ExpressionParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__9=1, T__8=2, T__7=3, T__6=4, T__5=5, T__4=6, T__3=7, T__2=8, T__1=9, 
		T__0=10, NoArgFn=11, SingleArgFn=12, SingleModelFn=13, SingleArgLogicalFn=14, 
		DoubleArgFn=15, DoubleModelFn=16, ThreeArgFn=17, FiveArgFn=18, TimePeriodFn=19, 
		TimePeriodDurationFn=20, L1FN=21, L2FN=22, StringFn=23, TimePeriodLogicalFn=24, 
		VarArgFn=25, DOM=26, DOW=27, HOUR=28, MONTH=29, MINUTE=30, AGO=31, ASOF=32, 
		HOURS=33, MINUTES=34, STATUS=35, SYMBOL=36, VALUEOF=37, VALUE=38, DATESYMBOL=39, 
		COMMA=40, OPR=41, EOPR=42, ROPR=43, LOPR=44, IF=45, THEN=46, ELSE=47, 
		IS=48, BAD=49, GOOD=50, TRUE=51, FALSE=52, AND=53, OR=54, INT=55, FLOAT=56, 
		TIMESTAMP=57, STRING=58, ARG=59, COMMENT=60, PCLOSE=61, POPEN=62, DBLQUOTE=63, 
		SNGLQUOTE=64;
	public static final String[] tokenNames = {
		"<INVALID>", "'H'", "'+-'", "'m'", "'mi'", "'D'", "'d'", "'h'", "'hr'", 
		"'M'", "'hour'", "NoArgFn", "SingleArgFn", "SingleModelFn", "SingleArgLogicalFn", 
		"DoubleArgFn", "DoubleModelFn", "ThreeArgFn", "FiveArgFn", "TimePeriodFn", 
		"TimePeriodDurationFn", "L1FN", "L2FN", "'GET-FROM-TEXT'", "TimePeriodLogicalFn", 
		"VarArgFn", "DOM", "DOW", "HOUR", "MONTH", "MINUTE", "AGO", "ASOF", "HOURS", 
		"MINUTES", "'the status of'", "SYMBOL", "VALUEOF", "'the current value of'", 
		"DATESYMBOL", "','", "OPR", "EOPR", "ROPR", "LOPR", "IF", "THEN", "ELSE", 
		"IS", "BAD", "GOOD", "TRUE", "FALSE", "AND", "OR", "INT", "FLOAT", "TIMESTAMP", 
		"STRING", "ARG", "COMMENT", "')'", "'('", "'\"'", "'''"
	};
	public static final int
		RULE_expression = 0, RULE_exprs = 1, RULE_lexpr = 2, RULE_expr = 3, RULE_timeunit = 4, 
		RULE_period = 5, RULE_lvalue = 6, RULE_value = 7, RULE_g2time = 8, RULE_g2Status = 9, 
		RULE_g2asofExpression = 10, RULE_fuzz = 11, RULE_fuzzdash = 12, RULE_isgoodorbad = 13, 
		RULE_g2Symbol = 14, RULE_vararg = 15;
	public static final String[] ruleNames = {
		"expression", "exprs", "lexpr", "expr", "timeunit", "period", "lvalue", 
		"value", "g2time", "g2Status", "g2asofExpression", "fuzz", "fuzzdash", 
		"isgoodorbad", "g2Symbol", "vararg"
	};

	@Override
	public String getGrammarFileName() { return "G2Expression.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public G2ExpressionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressionContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(G2ExpressionParser.EOF, 0); }
		public ExprsContext exprs() {
			return getRuleContext(ExprsContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32); exprs();
			setState(33); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprsContext extends ParserRuleContext {
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprs; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitExprs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprsContext exprs() throws RecognitionException {
		ExprsContext _localctx = new ExprsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_exprs);
		try {
			setState(37);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(35); expr(0);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(36); lexpr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexprContext extends ParserRuleContext {
		public int _p;
		public LexprContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public LexprContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_lexpr; }
	 
		public LexprContext() { }
		public void copyFrom(LexprContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class TimePeriodLogicalFunctionContext extends LexprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode TimePeriodLogicalFn() { return getToken(G2ExpressionParser.TimePeriodLogicalFn, 0); }
		public TerminalNode COMMA() { return getToken(G2ExpressionParser.COMMA, 0); }
		public PeriodContext period() {
			return getRuleContext(PeriodContext.class,0);
		}
		public TimePeriodLogicalFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTimePeriodLogicalFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimeFunctionStringCompareContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode TimePeriodLogicalFn() { return getToken(G2ExpressionParser.TimePeriodLogicalFn, 0); }
		public TerminalNode COMMA() { return getToken(G2ExpressionParser.COMMA, 0); }
		public PeriodContext period() {
			return getRuleContext(PeriodContext.class,0);
		}
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public TimeFunctionStringCompareContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTimeFunctionStringCompare(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalParenthesesContext extends LexprContext {
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public LogicalParenthesesContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ThreeArgFunctionContext extends LexprContext {
		public TerminalNode COMMA(int i) {
			return getToken(G2ExpressionParser.COMMA, i);
		}
		public TerminalNode ThreeArgFn() { return getToken(G2ExpressionParser.ThreeArgFn, 0); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(G2ExpressionParser.COMMA); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ThreeArgFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitThreeArgFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class G2TimeExpressionContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode INT() { return getToken(G2ExpressionParser.INT, 0); }
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public TerminalNode SYMBOL() { return getToken(G2ExpressionParser.SYMBOL, 0); }
		public G2timeContext g2time() {
			return getRuleContext(G2timeContext.class,0);
		}
		public TerminalNode DATESYMBOL() { return getToken(G2ExpressionParser.DATESYMBOL, 0); }
		public TerminalNode IS() { return getToken(G2ExpressionParser.IS, 0); }
		public G2TimeExpressionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitG2TimeExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalOperatorContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public RelationalOperatorContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitRelationalOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalSymbolContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public G2SymbolContext g2Symbol() {
			return getRuleContext(G2SymbolContext.class,0);
		}
		public LogicalSymbolContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalSymbol(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuzzyOperatorWithDashContext extends LexprContext {
		public FuzzdashContext fuzzdash() {
			return getRuleContext(FuzzdashContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FuzzyOperatorWithDashContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitFuzzyOperatorWithDash(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalTagStringFunctionContext extends LexprContext {
		public TerminalNode L2FN() { return getToken(G2ExpressionParser.L2FN, 0); }
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public LogicalTagStringFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalTagStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringFunctionExpressionContext extends LexprContext {
		public TerminalNode StringFn() { return getToken(G2ExpressionParser.StringFn, 0); }
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public List<TerminalNode> INT() { return getTokens(G2ExpressionParser.INT); }
		public TerminalNode COMMA(int i) {
			return getToken(G2ExpressionParser.COMMA, i);
		}
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public List<TerminalNode> COMMA() { return getTokens(G2ExpressionParser.COMMA); }
		public TerminalNode INT(int i) {
			return getToken(G2ExpressionParser.INT, i);
		}
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public StringFunctionExpressionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitStringFunctionExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalValueContext extends LexprContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public LogicalValueContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalStringFunctionContext extends LexprContext {
		public TerminalNode L1FN() { return getToken(G2ExpressionParser.L1FN, 0); }
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public LogicalStringFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalStringFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalOperatorContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public List<LexprContext> lexpr() {
			return getRuleContexts(LexprContext.class);
		}
		public TerminalNode LOPR() { return getToken(G2ExpressionParser.LOPR, 0); }
		public LexprContext lexpr(int i) {
			return getRuleContext(LexprContext.class,i);
		}
		public LogicalOperatorContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleArgLogicalFunctionContext extends LexprContext {
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public TerminalNode SingleArgLogicalFn() { return getToken(G2ExpressionParser.SingleArgLogicalFn, 0); }
		public SingleArgLogicalFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitSingleArgLogicalFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StatusSymbolContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public G2StatusContext g2Status() {
			return getRuleContext(G2StatusContext.class,0);
		}
		public G2SymbolContext g2Symbol() {
			return getRuleContext(G2SymbolContext.class,0);
		}
		public TerminalNode IS() { return getToken(G2ExpressionParser.IS, 0); }
		public StatusSymbolContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitStatusSymbol(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalTagContext extends LexprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public LogicalTagContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalTag(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TagStringContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public TagStringContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTagString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalOperatorWithDashContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode OPR() { return getToken(G2ExpressionParser.OPR, 0); }
		public LogicalOperatorWithDashContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLogicalOperatorWithDash(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LeadingCommentContext extends LexprContext {
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public TerminalNode COMMENT() { return getToken(G2ExpressionParser.COMMENT, 0); }
		public LeadingCommentContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLeadingComment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FiveArgFunctionContext extends LexprContext {
		public TerminalNode COMMA(int i) {
			return getToken(G2ExpressionParser.COMMA, i);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(G2ExpressionParser.COMMA); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public TerminalNode FiveArgFn() { return getToken(G2ExpressionParser.FiveArgFn, 0); }
		public FiveArgFunctionContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitFiveArgFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArgGoodOrBadContext extends LexprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public IsgoodorbadContext isgoodorbad() {
			return getRuleContext(IsgoodorbadContext.class,0);
		}
		public ArgGoodOrBadContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitArgGoodOrBad(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuzzyOperatorContext extends LexprContext {
		public FuzzContext fuzz() {
			return getRuleContext(FuzzContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public FuzzyOperatorContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitFuzzyOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class G2TimeExpressionQuotedContext extends LexprContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public TerminalNode SYMBOL() { return getToken(G2ExpressionParser.SYMBOL, 0); }
		public G2timeContext g2time() {
			return getRuleContext(G2timeContext.class,0);
		}
		public TerminalNode DATESYMBOL() { return getToken(G2ExpressionParser.DATESYMBOL, 0); }
		public TerminalNode IS() { return getToken(G2ExpressionParser.IS, 0); }
		public TerminalNode DBLQUOTE(int i) {
			return getToken(G2ExpressionParser.DBLQUOTE, i);
		}
		public List<TerminalNode> DBLQUOTE() { return getTokens(G2ExpressionParser.DBLQUOTE); }
		public G2TimeExpressionQuotedContext(LexprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitG2TimeExpressionQuoted(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LexprContext lexpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LexprContext _localctx = new LexprContext(_ctx, _parentState, _p);
		LexprContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, RULE_lexpr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				_localctx = new LeadingCommentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(40); match(COMMENT);
				setState(41); lexpr(23);
				}
				break;

			case 2:
				{
				_localctx = new LogicalParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42); match(POPEN);
				setState(43); lexpr(0);
				setState(44); match(PCLOSE);
				}
				break;

			case 3:
				{
				_localctx = new LogicalValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46); lvalue();
				}
				break;

			case 4:
				{
				_localctx = new StatusSymbolContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47); g2Status();
				setState(48);
				_la = _input.LA(1);
				if ( !(_la==EOPR || _la==IS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(49); g2Symbol();
				}
				break;

			case 5:
				{
				_localctx = new ArgGoodOrBadContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(51); match(ARG);
				setState(52); isgoodorbad();
				}
				break;

			case 6:
				{
				_localctx = new FuzzyOperatorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(53); expr(0);
				setState(54); fuzz();
				}
				break;

			case 7:
				{
				_localctx = new RelationalOperatorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(56); expr(0);
				setState(57);
				_la = _input.LA(1);
				if ( !(_la==EOPR || _la==ROPR) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(58); expr(0);
				}
				break;

			case 8:
				{
				_localctx = new TagStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(60); match(ARG);
				setState(61); match(EOPR);
				setState(62); match(STRING);
				}
				break;

			case 9:
				{
				_localctx = new LogicalStringFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(63); match(L1FN);
				setState(64); match(POPEN);
				setState(65); match(STRING);
				setState(66); match(PCLOSE);
				}
				break;

			case 10:
				{
				_localctx = new LogicalTagStringFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(67); match(L2FN);
				setState(68); match(POPEN);
				setState(69); match(STRING);
				setState(70); match(COMMA);
				setState(71); match(ARG);
				setState(72); match(PCLOSE);
				}
				break;

			case 11:
				{
				_localctx = new SingleArgLogicalFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(73); match(SingleArgLogicalFn);
				setState(74); match(POPEN);
				setState(75); lexpr(0);
				setState(76); match(PCLOSE);
				}
				break;

			case 12:
				{
				_localctx = new ThreeArgFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(78); match(ThreeArgFn);
				setState(79); match(POPEN);
				setState(80); expr(0);
				setState(81); match(COMMA);
				setState(82); expr(0);
				setState(83); match(COMMA);
				setState(84); expr(0);
				setState(85); match(PCLOSE);
				}
				break;

			case 13:
				{
				_localctx = new FiveArgFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(87); match(FiveArgFn);
				setState(88); match(POPEN);
				setState(89); expr(0);
				setState(90); match(COMMA);
				setState(91); expr(0);
				setState(92); match(COMMA);
				setState(93); expr(0);
				setState(94); match(COMMA);
				setState(95); expr(0);
				setState(96); match(COMMA);
				setState(97); expr(0);
				setState(98); match(PCLOSE);
				}
				break;

			case 14:
				{
				_localctx = new TimePeriodLogicalFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(100); match(TimePeriodLogicalFn);
				setState(101); match(POPEN);
				setState(102); match(ARG);
				setState(103); match(COMMA);
				setState(104); period();
				setState(105); match(PCLOSE);
				}
				break;

			case 15:
				{
				_localctx = new TimeFunctionStringCompareContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(107); match(TimePeriodLogicalFn);
				setState(108); match(POPEN);
				setState(109); match(ARG);
				setState(110); match(COMMA);
				setState(111); period();
				setState(112); match(PCLOSE);
				setState(113); match(EOPR);
				setState(114); match(STRING);
				}
				break;

			case 16:
				{
				_localctx = new LogicalTagContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116); match(ARG);
				}
				break;

			case 17:
				{
				_localctx = new G2TimeExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(117); g2time();
				setState(118);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EOPR) | (1L << ROPR) | (1L << IS))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(120);
				_la = _input.LA(1);
				if (_la==SYMBOL) {
					{
					setState(119); match(SYMBOL);
					}
				}

				setState(122);
				_la = _input.LA(1);
				if ( !(_la==DATESYMBOL || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				break;

			case 18:
				{
				_localctx = new G2TimeExpressionQuotedContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(124); g2time();
				setState(125);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EOPR) | (1L << ROPR) | (1L << IS))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(127);
				_la = _input.LA(1);
				if (_la==SYMBOL) {
					{
					setState(126); match(SYMBOL);
					}
				}

				setState(129); match(DBLQUOTE);
				setState(130); match(DATESYMBOL);
				setState(131); match(DBLQUOTE);
				}
				break;

			case 19:
				{
				_localctx = new LogicalOperatorWithDashContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(133); expr(0);
				setState(134);
				_la = _input.LA(1);
				if ( !(_la==EOPR || _la==ROPR) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(135); match(OPR);
				setState(136); value();
				}
				break;

			case 20:
				{
				_localctx = new FuzzyOperatorWithDashContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(138); expr(0);
				setState(139); fuzzdash();
				}
				break;

			case 21:
				{
				_localctx = new StringFunctionExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(141); match(StringFn);
				setState(142); match(POPEN);
				setState(143); match(ARG);
				setState(144); match(COMMA);
				setState(145); match(INT);
				setState(146); match(COMMA);
				setState(147); match(INT);
				setState(148); match(PCLOSE);
				setState(149); match(EOPR);
				setState(150); match(STRING);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(161);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(159);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new LogicalOperatorContext(new LexprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_lexpr);
						setState(153);
						if (!(20 >= _localctx._p)) throw new FailedPredicateException(this, "20 >= $_p");
						setState(154);
						_la = _input.LA(1);
						if ( !(_la==EOPR || _la==LOPR) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						setState(155); lexpr(21);
						}
						break;

					case 2:
						{
						_localctx = new LogicalSymbolContext(new LexprContext(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_lexpr);
						setState(156);
						if (!(19 >= _localctx._p)) throw new FailedPredicateException(this, "19 >= $_p");
						setState(157); match(EOPR);
						setState(158); g2Symbol();
						}
						break;
					}
					} 
				}
				setState(163);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public int _p;
		public ExprContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExprContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class ParenthesesContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenthesesContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitParentheses(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionOperatorContext extends ExprContext {
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public TerminalNode OPR() { return getToken(G2ExpressionParser.OPR, 0); }
		public ExpressionOperatorContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitExpressionOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AsofexpressionContext extends ExprContext {
		public G2asofExpressionContext g2asofExpression() {
			return getRuleContext(G2asofExpressionContext.class,0);
		}
		public AsofexpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitAsofexpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ValueExpressionContext extends ExprContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ValueExpressionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitValueExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimePeriodDurationFunctionContext extends ExprContext {
		public TerminalNode COMMA(int i) {
			return getToken(G2ExpressionParser.COMMA, i);
		}
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public List<TerminalNode> COMMA() { return getTokens(G2ExpressionParser.COMMA); }
		public PeriodContext period(int i) {
			return getRuleContext(PeriodContext.class,i);
		}
		public List<PeriodContext> period() {
			return getRuleContexts(PeriodContext.class);
		}
		public TerminalNode TimePeriodDurationFn() { return getToken(G2ExpressionParser.TimePeriodDurationFn, 0); }
		public TimePeriodDurationFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTimePeriodDurationFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleModelFunctionContext extends ExprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode SingleModelFn() { return getToken(G2ExpressionParser.SingleModelFn, 0); }
		public SingleModelFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitSingleModelFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoubleDoubleQuotedModelFunctionContext extends ExprContext {
		public TerminalNode DoubleModelFn() { return getToken(G2ExpressionParser.DoubleModelFn, 0); }
		public TerminalNode STRING(int i) {
			return getToken(G2ExpressionParser.STRING, i);
		}
		public List<TerminalNode> STRING() { return getTokens(G2ExpressionParser.STRING); }
		public DoubleDoubleQuotedModelFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitDoubleDoubleQuotedModelFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NoArgumentFunctionContext extends ExprContext {
		public TerminalNode NoArgFn() { return getToken(G2ExpressionParser.NoArgFn, 0); }
		public NoArgumentFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitNoArgumentFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoubleModelFunctionContext extends ExprContext {
		public TerminalNode DoubleModelFn() { return getToken(G2ExpressionParser.DoubleModelFn, 0); }
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DoubleModelFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitDoubleModelFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleArgumentFunctionContext extends ExprContext {
		public TerminalNode SingleArgFn() { return getToken(G2ExpressionParser.SingleArgFn, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public SingleArgumentFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitSingleArgumentFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoubleQuotedModelFunctionContext extends ExprContext {
		public TerminalNode DoubleModelFn() { return getToken(G2ExpressionParser.DoubleModelFn, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public DoubleQuotedModelFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitDoubleQuotedModelFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericTagContext extends ExprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public NumericTagContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitNumericTag(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableArgumentFunctionContext extends ExprContext {
		public VarargContext vararg() {
			return getRuleContext(VarargContext.class,0);
		}
		public TerminalNode VarArgFn() { return getToken(G2ExpressionParser.VarArgFn, 0); }
		public VariableArgumentFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitVariableArgumentFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimePeriodFunctionContext extends ExprContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode COMMA() { return getToken(G2ExpressionParser.COMMA, 0); }
		public TerminalNode TimePeriodFn() { return getToken(G2ExpressionParser.TimePeriodFn, 0); }
		public PeriodContext period() {
			return getRuleContext(PeriodContext.class,0);
		}
		public TimePeriodFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTimePeriodFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoubleArgumentFunctionContext extends ExprContext {
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode DoubleArgFn() { return getToken(G2ExpressionParser.DoubleArgFn, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public DoubleArgumentFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitDoubleArgumentFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConditionalContext extends ExprContext {
		public LexprContext lexpr() {
			return getRuleContext(LexprContext.class,0);
		}
		public TerminalNode THEN() { return getToken(G2ExpressionParser.THEN, 0); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public TerminalNode ELSE() { return getToken(G2ExpressionParser.ELSE, 0); }
		public TerminalNode IF() { return getToken(G2ExpressionParser.IF, 0); }
		public ConditionalContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitConditional(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class QuotedModelFunctionContext extends ExprContext {
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public TerminalNode SingleModelFn() { return getToken(G2ExpressionParser.SingleModelFn, 0); }
		public QuotedModelFunctionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitQuotedModelFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegativeContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode OPR() { return getToken(G2ExpressionParser.OPR, 0); }
		public NegativeContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitNegative(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class QuotedStringContext extends ExprContext {
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public QuotedStringContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitQuotedString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState, _p);
		ExprContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, RULE_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				_localctx = new NegativeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(165); match(OPR);
				setState(166); expr(17);
				}
				break;

			case 2:
				{
				_localctx = new ParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(167); match(POPEN);
				setState(168); expr(0);
				setState(169); match(PCLOSE);
				}
				break;

			case 3:
				{
				_localctx = new ConditionalContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(171); match(IF);
				setState(172); lexpr(0);
				setState(173); match(THEN);
				setState(174); expr(0);
				setState(175); match(ELSE);
				setState(176); expr(0);
				}
				break;

			case 4:
				{
				_localctx = new NoArgumentFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(178); match(NoArgFn);
				setState(179); match(POPEN);
				setState(180); match(PCLOSE);
				}
				break;

			case 5:
				{
				_localctx = new SingleArgumentFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(181); match(SingleArgFn);
				setState(182); match(POPEN);
				setState(183); expr(0);
				setState(184); match(PCLOSE);
				}
				break;

			case 6:
				{
				_localctx = new SingleModelFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(186); match(SingleModelFn);
				setState(187); match(POPEN);
				setState(188); match(ARG);
				setState(189); match(PCLOSE);
				}
				break;

			case 7:
				{
				_localctx = new QuotedModelFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(190); match(SingleModelFn);
				setState(191); match(POPEN);
				setState(192); match(STRING);
				setState(193); match(PCLOSE);
				}
				break;

			case 8:
				{
				_localctx = new DoubleArgumentFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(194); match(DoubleArgFn);
				setState(195); match(POPEN);
				setState(196); expr(0);
				setState(197); match(COMMA);
				setState(198); expr(0);
				setState(199); match(PCLOSE);
				}
				break;

			case 9:
				{
				_localctx = new DoubleModelFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(201); match(DoubleModelFn);
				setState(202); match(POPEN);
				setState(203); match(ARG);
				setState(204); match(COMMA);
				setState(205); expr(0);
				setState(206); match(PCLOSE);
				}
				break;

			case 10:
				{
				_localctx = new DoubleQuotedModelFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(208); match(DoubleModelFn);
				setState(209); match(POPEN);
				setState(210); match(STRING);
				setState(211); match(COMMA);
				setState(212); expr(0);
				setState(213); match(PCLOSE);
				}
				break;

			case 11:
				{
				_localctx = new DoubleDoubleQuotedModelFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(215); match(DoubleModelFn);
				setState(216); match(POPEN);
				setState(217); match(STRING);
				setState(218); match(COMMA);
				setState(219); match(STRING);
				setState(220); match(PCLOSE);
				}
				break;

			case 12:
				{
				_localctx = new TimePeriodFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(221); match(TimePeriodFn);
				setState(222); match(POPEN);
				setState(223); match(ARG);
				setState(224); match(COMMA);
				setState(225); period();
				setState(226); match(PCLOSE);
				}
				break;

			case 13:
				{
				_localctx = new TimePeriodDurationFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(228); match(TimePeriodDurationFn);
				setState(229); match(POPEN);
				setState(230); match(ARG);
				setState(231); match(COMMA);
				setState(232); period();
				setState(233); match(COMMA);
				setState(234); period();
				setState(235); match(PCLOSE);
				}
				break;

			case 14:
				{
				_localctx = new VariableArgumentFunctionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237); match(VarArgFn);
				setState(238); match(POPEN);
				setState(239); vararg(0);
				setState(240); match(PCLOSE);
				}
				break;

			case 15:
				{
				_localctx = new NumericTagContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(242); match(ARG);
				}
				break;

			case 16:
				{
				_localctx = new QuotedStringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(243); match(STRING);
				}
				break;

			case 17:
				{
				_localctx = new AsofexpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244); g2asofExpression();
				}
				break;

			case 18:
				{
				_localctx = new ValueExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245); value();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(253);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExpressionOperatorContext(new ExprContext(_parentctx, _parentState, _p));
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(248);
					if (!(18 >= _localctx._p)) throw new FailedPredicateException(this, "18 >= $_p");
					setState(249); match(OPR);
					setState(250); expr(19);
					}
					} 
				}
				setState(255);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TimeunitContext extends ParserRuleContext {
		public TerminalNode VarArgFn() { return getToken(G2ExpressionParser.VarArgFn, 0); }
		public TimeunitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timeunit; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitTimeunit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeunitContext timeunit() throws RecognitionException {
		TimeunitContext _localctx = new TimeunitContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_timeunit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 3) | (1L << 4) | (1L << 5) | (1L << 6) | (1L << 7) | (1L << 8) | (1L << 9) | (1L << 10) | (1L << VarArgFn))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PeriodContext extends ParserRuleContext {
		public PeriodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_period; }
	 
		public PeriodContext() { }
		public void copyFrom(PeriodContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PeriodFromStringContext extends PeriodContext {
		public TerminalNode STRING() { return getToken(G2ExpressionParser.STRING, 0); }
		public PeriodFromStringContext(PeriodContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitPeriodFromString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PeriodContext period() throws RecognitionException {
		PeriodContext _localctx = new PeriodContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_period);
		try {
			_localctx = new PeriodFromStringContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(258); match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalueContext extends ParserRuleContext {
		public TerminalNode FALSE() { return getToken(G2ExpressionParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(G2ExpressionParser.TRUE, 0); }
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitLvalue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_lvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(G2ExpressionParser.FLOAT, 0); }
		public TerminalNode INT() { return getToken(G2ExpressionParser.INT, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==FLOAT) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class G2timeContext extends ParserRuleContext {
		public G2timeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_g2time; }
	 
		public G2timeContext() { }
		public void copyFrom(G2timeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class G2TimeFunctionContext extends G2timeContext {
		public TerminalNode DOM() { return getToken(G2ExpressionParser.DOM, 0); }
		public TerminalNode MINUTE() { return getToken(G2ExpressionParser.MINUTE, 0); }
		public TerminalNode HOUR() { return getToken(G2ExpressionParser.HOUR, 0); }
		public TerminalNode MONTH() { return getToken(G2ExpressionParser.MONTH, 0); }
		public TerminalNode DOW() { return getToken(G2ExpressionParser.DOW, 0); }
		public G2TimeFunctionContext(G2timeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitG2TimeFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final G2timeContext g2time() throws RecognitionException {
		G2timeContext _localctx = new G2timeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_g2time);
		int _la;
		try {
			_localctx = new G2TimeFunctionContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			_la = _input.LA(1);
			if (_la==POPEN) {
				{
				setState(264); match(POPEN);
				}
			}

			setState(267);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DOM) | (1L << DOW) | (1L << HOUR) | (1L << MONTH) | (1L << MINUTE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(269);
			_la = _input.LA(1);
			if (_la==PCLOSE) {
				{
				setState(268); match(PCLOSE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class G2StatusContext extends ParserRuleContext {
		public G2StatusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_g2Status; }
	 
		public G2StatusContext() { }
		public void copyFrom(G2StatusContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StatusArgContext extends G2StatusContext {
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode STATUS() { return getToken(G2ExpressionParser.STATUS, 0); }
		public StatusArgContext(G2StatusContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitStatusArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final G2StatusContext g2Status() throws RecognitionException {
		G2StatusContext _localctx = new G2StatusContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_g2Status);
		try {
			_localctx = new StatusArgContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(271); match(STATUS);
			setState(272); match(ARG);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class G2asofExpressionContext extends ParserRuleContext {
		public G2asofExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_g2asofExpression; }
	 
		public G2asofExpressionContext() { }
		public void copyFrom(G2asofExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class HValueContext extends G2asofExpressionContext {
		public TerminalNode MINUTES() { return getToken(G2ExpressionParser.MINUTES, 0); }
		public TerminalNode AGO() { return getToken(G2ExpressionParser.AGO, 0); }
		public TerminalNode INT() { return getToken(G2ExpressionParser.INT, 0); }
		public TerminalNode ARG() { return getToken(G2ExpressionParser.ARG, 0); }
		public TerminalNode VALUEOF() { return getToken(G2ExpressionParser.VALUEOF, 0); }
		public TerminalNode ASOF() { return getToken(G2ExpressionParser.ASOF, 0); }
		public TerminalNode HOURS() { return getToken(G2ExpressionParser.HOURS, 0); }
		public HValueContext(G2asofExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitHValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final G2asofExpressionContext g2asofExpression() throws RecognitionException {
		G2asofExpressionContext _localctx = new G2asofExpressionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_g2asofExpression);
		int _la;
		try {
			_localctx = new HValueContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(274); match(VALUEOF);
			setState(275); match(ARG);
			setState(276); match(ASOF);
			setState(277); match(INT);
			setState(278);
			_la = _input.LA(1);
			if ( !(_la==HOURS || _la==MINUTES) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(279); match(AGO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuzzContext extends ParserRuleContext {
		public FuzzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fuzz; }
	 
		public FuzzContext() { }
		public void copyFrom(FuzzContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuzzyExpressionContext extends FuzzContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public FuzzyExpressionContext(FuzzContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitFuzzyExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuzzContext fuzz() throws RecognitionException {
		FuzzContext _localctx = new FuzzContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_fuzz);
		int _la;
		try {
			_localctx = new FuzzyExpressionContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			_la = _input.LA(1);
			if ( !(_la==EOPR || _la==ROPR) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(282); expr(0);
			setState(283); match(POPEN);
			setState(284); match(2);
			setState(285); expr(0);
			setState(286); match(PCLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuzzdashContext extends ParserRuleContext {
		public FuzzdashContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fuzzdash; }
	 
		public FuzzdashContext() { }
		public void copyFrom(FuzzdashContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuzzyExpressionWithDashContext extends FuzzdashContext {
		public TerminalNode EOPR() { return getToken(G2ExpressionParser.EOPR, 0); }
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode ROPR() { return getToken(G2ExpressionParser.ROPR, 0); }
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public TerminalNode OPR() { return getToken(G2ExpressionParser.OPR, 0); }
		public FuzzyExpressionWithDashContext(FuzzdashContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitFuzzyExpressionWithDash(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuzzdashContext fuzzdash() throws RecognitionException {
		FuzzdashContext _localctx = new FuzzdashContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_fuzzdash);
		int _la;
		try {
			_localctx = new FuzzyExpressionWithDashContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			_la = _input.LA(1);
			if ( !(_la==EOPR || _la==ROPR) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(289); match(OPR);
			setState(290); value();
			setState(291); match(POPEN);
			setState(292); match(2);
			setState(293); value();
			setState(294); match(PCLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IsgoodorbadContext extends ParserRuleContext {
		public IsgoodorbadContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_isgoodorbad; }
	 
		public IsgoodorbadContext() { }
		public void copyFrom(IsgoodorbadContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class GoodOrBadContext extends IsgoodorbadContext {
		public TerminalNode SYMBOL() { return getToken(G2ExpressionParser.SYMBOL, 0); }
		public TerminalNode GOOD() { return getToken(G2ExpressionParser.GOOD, 0); }
		public TerminalNode FALSE() { return getToken(G2ExpressionParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(G2ExpressionParser.TRUE, 0); }
		public TerminalNode IS() { return getToken(G2ExpressionParser.IS, 0); }
		public TerminalNode BAD() { return getToken(G2ExpressionParser.BAD, 0); }
		public GoodOrBadContext(IsgoodorbadContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitGoodOrBad(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IsgoodorbadContext isgoodorbad() throws RecognitionException {
		IsgoodorbadContext _localctx = new IsgoodorbadContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_isgoodorbad);
		int _la;
		try {
			_localctx = new GoodOrBadContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(296); match(IS);
			setState(298);
			_la = _input.LA(1);
			if (_la==SYMBOL) {
				{
				setState(297); match(SYMBOL);
				}
			}

			setState(300);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BAD) | (1L << GOOD) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class G2SymbolContext extends ParserRuleContext {
		public G2SymbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_g2Symbol; }
	 
		public G2SymbolContext() { }
		public void copyFrom(G2SymbolContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SymbolContext extends G2SymbolContext {
		public TerminalNode SYMBOL() { return getToken(G2ExpressionParser.SYMBOL, 0); }
		public TerminalNode GOOD() { return getToken(G2ExpressionParser.GOOD, 0); }
		public TerminalNode DATESYMBOL() { return getToken(G2ExpressionParser.DATESYMBOL, 0); }
		public TerminalNode FALSE() { return getToken(G2ExpressionParser.FALSE, 0); }
		public TerminalNode TRUE() { return getToken(G2ExpressionParser.TRUE, 0); }
		public TerminalNode BAD() { return getToken(G2ExpressionParser.BAD, 0); }
		public SymbolContext(G2SymbolContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitSymbol(this);
			else return visitor.visitChildren(this);
		}
	}

	public final G2SymbolContext g2Symbol() throws RecognitionException {
		G2SymbolContext _localctx = new G2SymbolContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_g2Symbol);
		int _la;
		try {
			_localctx = new SymbolContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			_la = _input.LA(1);
			if (_la==SYMBOL) {
				{
				setState(302); match(SYMBOL);
				}
			}

			setState(305);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DATESYMBOL) | (1L << BAD) | (1L << GOOD) | (1L << TRUE) | (1L << FALSE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarargContext extends ParserRuleContext {
		public int _p;
		public VarargContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public VarargContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_vararg; }
	 
		public VarargContext() { }
		public void copyFrom(VarargContext ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class VarArgExpressionContext extends VarargContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public VarArgExpressionContext(VarargContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitVarArgExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarArgRecursiveContext extends VarargContext {
		public VarargContext vararg() {
			return getRuleContext(VarargContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(G2ExpressionParser.COMMA, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public VarArgRecursiveContext(VarargContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof G2ExpressionVisitor ) return ((G2ExpressionVisitor<? extends T>)visitor).visitVarArgRecursive(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarargContext vararg(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		VarargContext _localctx = new VarargContext(_ctx, _parentState, _p);
		VarargContext _prevctx = _localctx;
		int _startState = 30;
		enterRecursionRule(_localctx, RULE_vararg);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new VarArgExpressionContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(308); expr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(315);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new VarArgRecursiveContext(new VarargContext(_parentctx, _parentState, _p));
					pushNewRecursionContext(_localctx, _startState, RULE_vararg);
					setState(310);
					if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
					setState(311); match(COMMA);
					setState(312); expr(0);
					}
					} 
				}
				setState(317);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2: return lexpr_sempred((LexprContext)_localctx, predIndex);

		case 3: return expr_sempred((ExprContext)_localctx, predIndex);

		case 15: return vararg_sempred((VarargContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean vararg_sempred(VarargContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3: return 1 >= _localctx._p;
		}
		return true;
	}
	private boolean lexpr_sempred(LexprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 20 >= _localctx._p;

		case 1: return 19 >= _localctx._p;
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return 18 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\2\3B\u0141\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4"+
		"\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20"+
		"\4\21\t\21\3\2\3\2\3\2\3\3\3\3\5\3(\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\5\4{\n\4\3\4\3\4\3\4\3\4\3\4\5\4\u0082\n\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\5\4\u009a\n\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4\u00a2\n\4\f\4\16\4\u00a5"+
		"\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u00f9\n\5\3"+
		"\5\3\5\3\5\7\5\u00fe\n\5\f\5\16\5\u0101\13\5\3\6\3\6\3\7\3\7\3\b\3\b\3"+
		"\t\3\t\3\n\5\n\u010c\n\n\3\n\3\n\5\n\u0110\n\n\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\17\3\17\5\17\u012d\n\17\3\17\3\17\3\20\5\20\u0132"+
		"\n\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\7\21\u013c\n\21\f\21\16"+
		"\21\u013f\13\21\3\21\2\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \2\22"+
		"\4,,\62\62\3,-\4,-\62\62\4))99\4,-\62\62\3,-\4,,..\5\3\3\5\f\33\33\3\65"+
		"\66\39:\3\34 \3#$\3,-\3,-\3\63\66\4))\63\66\u0160\2\"\3\2\2\2\4\'\3\2"+
		"\2\2\6\u0099\3\2\2\2\b\u00f8\3\2\2\2\n\u0102\3\2\2\2\f\u0104\3\2\2\2\16"+
		"\u0106\3\2\2\2\20\u0108\3\2\2\2\22\u010b\3\2\2\2\24\u0111\3\2\2\2\26\u0114"+
		"\3\2\2\2\30\u011b\3\2\2\2\32\u0122\3\2\2\2\34\u012a\3\2\2\2\36\u0131\3"+
		"\2\2\2 \u0135\3\2\2\2\"#\5\4\3\2#$\7\1\2\2$\3\3\2\2\2%(\5\b\5\2&(\5\6"+
		"\4\2\'%\3\2\2\2\'&\3\2\2\2(\5\3\2\2\2)*\b\4\1\2*+\7>\2\2+\u009a\5\6\4"+
		"\2,-\7@\2\2-.\5\6\4\2./\7?\2\2/\u009a\3\2\2\2\60\u009a\5\16\b\2\61\62"+
		"\5\24\13\2\62\63\t\2\2\2\63\64\5\36\20\2\64\u009a\3\2\2\2\65\66\7=\2\2"+
		"\66\u009a\5\34\17\2\678\5\b\5\289\5\30\r\29\u009a\3\2\2\2:;\5\b\5\2;<"+
		"\t\3\2\2<=\5\b\5\2=\u009a\3\2\2\2>?\7=\2\2?@\7,\2\2@\u009a\7<\2\2AB\7"+
		"\27\2\2BC\7@\2\2CD\7<\2\2D\u009a\7?\2\2EF\7\30\2\2FG\7@\2\2GH\7<\2\2H"+
		"I\7*\2\2IJ\7=\2\2J\u009a\7?\2\2KL\7\20\2\2LM\7@\2\2MN\5\6\4\2NO\7?\2\2"+
		"O\u009a\3\2\2\2PQ\7\23\2\2QR\7@\2\2RS\5\b\5\2ST\7*\2\2TU\5\b\5\2UV\7*"+
		"\2\2VW\5\b\5\2WX\7?\2\2X\u009a\3\2\2\2YZ\7\24\2\2Z[\7@\2\2[\\\5\b\5\2"+
		"\\]\7*\2\2]^\5\b\5\2^_\7*\2\2_`\5\b\5\2`a\7*\2\2ab\5\b\5\2bc\7*\2\2cd"+
		"\5\b\5\2de\7?\2\2e\u009a\3\2\2\2fg\7\32\2\2gh\7@\2\2hi\7=\2\2ij\7*\2\2"+
		"jk\5\f\7\2kl\7?\2\2l\u009a\3\2\2\2mn\7\32\2\2no\7@\2\2op\7=\2\2pq\7*\2"+
		"\2qr\5\f\7\2rs\7?\2\2st\7,\2\2tu\7<\2\2u\u009a\3\2\2\2v\u009a\7=\2\2w"+
		"x\5\22\n\2xz\t\4\2\2y{\7&\2\2zy\3\2\2\2z{\3\2\2\2{|\3\2\2\2|}\t\5\2\2"+
		"}\u009a\3\2\2\2~\177\5\22\n\2\177\u0081\t\6\2\2\u0080\u0082\7&\2\2\u0081"+
		"\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\7A"+
		"\2\2\u0084\u0085\7)\2\2\u0085\u0086\7A\2\2\u0086\u009a\3\2\2\2\u0087\u0088"+
		"\5\b\5\2\u0088\u0089\t\7\2\2\u0089\u008a\7+\2\2\u008a\u008b\5\20\t\2\u008b"+
		"\u009a\3\2\2\2\u008c\u008d\5\b\5\2\u008d\u008e\5\32\16\2\u008e\u009a\3"+
		"\2\2\2\u008f\u0090\7\31\2\2\u0090\u0091\7@\2\2\u0091\u0092\7=\2\2\u0092"+
		"\u0093\7*\2\2\u0093\u0094\79\2\2\u0094\u0095\7*\2\2\u0095\u0096\79\2\2"+
		"\u0096\u0097\7?\2\2\u0097\u0098\7,\2\2\u0098\u009a\7<\2\2\u0099)\3\2\2"+
		"\2\u0099,\3\2\2\2\u0099\60\3\2\2\2\u0099\61\3\2\2\2\u0099\65\3\2\2\2\u0099"+
		"\67\3\2\2\2\u0099:\3\2\2\2\u0099>\3\2\2\2\u0099A\3\2\2\2\u0099E\3\2\2"+
		"\2\u0099K\3\2\2\2\u0099P\3\2\2\2\u0099Y\3\2\2\2\u0099f\3\2\2\2\u0099m"+
		"\3\2\2\2\u0099v\3\2\2\2\u0099w\3\2\2\2\u0099~\3\2\2\2\u0099\u0087\3\2"+
		"\2\2\u0099\u008c\3\2\2\2\u0099\u008f\3\2\2\2\u009a\u00a3\3\2\2\2\u009b"+
		"\u009c\6\4\2\3\u009c\u009d\t\b\2\2\u009d\u00a2\5\6\4\2\u009e\u009f\6\4"+
		"\3\3\u009f\u00a0\7,\2\2\u00a0\u00a2\5\36\20\2\u00a1\u009b\3\2\2\2\u00a1"+
		"\u009e\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2"+
		"\2\2\u00a4\7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00a7\b\5\1\2\u00a7\u00a8"+
		"\7+\2\2\u00a8\u00f9\5\b\5\2\u00a9\u00aa\7@\2\2\u00aa\u00ab\5\b\5\2\u00ab"+
		"\u00ac\7?\2\2\u00ac\u00f9\3\2\2\2\u00ad\u00ae\7/\2\2\u00ae\u00af\5\6\4"+
		"\2\u00af\u00b0\7\60\2\2\u00b0\u00b1\5\b\5\2\u00b1\u00b2\7\61\2\2\u00b2"+
		"\u00b3\5\b\5\2\u00b3\u00f9\3\2\2\2\u00b4\u00b5\7\r\2\2\u00b5\u00b6\7@"+
		"\2\2\u00b6\u00f9\7?\2\2\u00b7\u00b8\7\16\2\2\u00b8\u00b9\7@\2\2\u00b9"+
		"\u00ba\5\b\5\2\u00ba\u00bb\7?\2\2\u00bb\u00f9\3\2\2\2\u00bc\u00bd\7\17"+
		"\2\2\u00bd\u00be\7@\2\2\u00be\u00bf\7=\2\2\u00bf\u00f9\7?\2\2\u00c0\u00c1"+
		"\7\17\2\2\u00c1\u00c2\7@\2\2\u00c2\u00c3\7<\2\2\u00c3\u00f9\7?\2\2\u00c4"+
		"\u00c5\7\21\2\2\u00c5\u00c6\7@\2\2\u00c6\u00c7\5\b\5\2\u00c7\u00c8\7*"+
		"\2\2\u00c8\u00c9\5\b\5\2\u00c9\u00ca\7?\2\2\u00ca\u00f9\3\2\2\2\u00cb"+
		"\u00cc\7\22\2\2\u00cc\u00cd\7@\2\2\u00cd\u00ce\7=\2\2\u00ce\u00cf\7*\2"+
		"\2\u00cf\u00d0\5\b\5\2\u00d0\u00d1\7?\2\2\u00d1\u00f9\3\2\2\2\u00d2\u00d3"+
		"\7\22\2\2\u00d3\u00d4\7@\2\2\u00d4\u00d5\7<\2\2\u00d5\u00d6\7*\2\2\u00d6"+
		"\u00d7\5\b\5\2\u00d7\u00d8\7?\2\2\u00d8\u00f9\3\2\2\2\u00d9\u00da\7\22"+
		"\2\2\u00da\u00db\7@\2\2\u00db\u00dc\7<\2\2\u00dc\u00dd\7*\2\2\u00dd\u00de"+
		"\7<\2\2\u00de\u00f9\7?\2\2\u00df\u00e0\7\25\2\2\u00e0\u00e1\7@\2\2\u00e1"+
		"\u00e2\7=\2\2\u00e2\u00e3\7*\2\2\u00e3\u00e4\5\f\7\2\u00e4\u00e5\7?\2"+
		"\2\u00e5\u00f9\3\2\2\2\u00e6\u00e7\7\26\2\2\u00e7\u00e8\7@\2\2\u00e8\u00e9"+
		"\7=\2\2\u00e9\u00ea\7*\2\2\u00ea\u00eb\5\f\7\2\u00eb\u00ec\7*\2\2\u00ec"+
		"\u00ed\5\f\7\2\u00ed\u00ee\7?\2\2\u00ee\u00f9\3\2\2\2\u00ef\u00f0\7\33"+
		"\2\2\u00f0\u00f1\7@\2\2\u00f1\u00f2\5 \21\2\u00f2\u00f3\7?\2\2\u00f3\u00f9"+
		"\3\2\2\2\u00f4\u00f9\7=\2\2\u00f5\u00f9\7<\2\2\u00f6\u00f9\5\26\f\2\u00f7"+
		"\u00f9\5\20\t\2\u00f8\u00a6\3\2\2\2\u00f8\u00a9\3\2\2\2\u00f8\u00ad\3"+
		"\2\2\2\u00f8\u00b4\3\2\2\2\u00f8\u00b7\3\2\2\2\u00f8\u00bc\3\2\2\2\u00f8"+
		"\u00c0\3\2\2\2\u00f8\u00c4\3\2\2\2\u00f8\u00cb\3\2\2\2\u00f8\u00d2\3\2"+
		"\2\2\u00f8\u00d9\3\2\2\2\u00f8\u00df\3\2\2\2\u00f8\u00e6\3\2\2\2\u00f8"+
		"\u00ef\3\2\2\2\u00f8\u00f4\3\2\2\2\u00f8\u00f5\3\2\2\2\u00f8\u00f6\3\2"+
		"\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00ff\3\2\2\2\u00fa\u00fb\6\5\4\3\u00fb"+
		"\u00fc\7+\2\2\u00fc\u00fe\5\b\5\2\u00fd\u00fa\3\2\2\2\u00fe\u0101\3\2"+
		"\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\t\3\2\2\2\u0101\u00ff"+
		"\3\2\2\2\u0102\u0103\t\t\2\2\u0103\13\3\2\2\2\u0104\u0105\7<\2\2\u0105"+
		"\r\3\2\2\2\u0106\u0107\t\n\2\2\u0107\17\3\2\2\2\u0108\u0109\t\13\2\2\u0109"+
		"\21\3\2\2\2\u010a\u010c\7@\2\2\u010b\u010a\3\2\2\2\u010b\u010c\3\2\2\2"+
		"\u010c\u010d\3\2\2\2\u010d\u010f\t\f\2\2\u010e\u0110\7?\2\2\u010f\u010e"+
		"\3\2\2\2\u010f\u0110\3\2\2\2\u0110\23\3\2\2\2\u0111\u0112\7%\2\2\u0112"+
		"\u0113\7=\2\2\u0113\25\3\2\2\2\u0114\u0115\7\'\2\2\u0115\u0116\7=\2\2"+
		"\u0116\u0117\7\"\2\2\u0117\u0118\79\2\2\u0118\u0119\t\r\2\2\u0119\u011a"+
		"\7!\2\2\u011a\27\3\2\2\2\u011b\u011c\t\16\2\2\u011c\u011d\5\b\5\2\u011d"+
		"\u011e\7@\2\2\u011e\u011f\7\4\2\2\u011f\u0120\5\b\5\2\u0120\u0121\7?\2"+
		"\2\u0121\31\3\2\2\2\u0122\u0123\t\17\2\2\u0123\u0124\7+\2\2\u0124\u0125"+
		"\5\20\t\2\u0125\u0126\7@\2\2\u0126\u0127\7\4\2\2\u0127\u0128\5\20\t\2"+
		"\u0128\u0129\7?\2\2\u0129\33\3\2\2\2\u012a\u012c\7\62\2\2\u012b\u012d"+
		"\7&\2\2\u012c\u012b\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012e\3\2\2\2\u012e"+
		"\u012f\t\20\2\2\u012f\35\3\2\2\2\u0130\u0132\7&\2\2\u0131\u0130\3\2\2"+
		"\2\u0131\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\t\21\2\2\u0134"+
		"\37\3\2\2\2\u0135\u0136\b\21\1\2\u0136\u0137\5\b\5\2\u0137\u013d\3\2\2"+
		"\2\u0138\u0139\6\21\5\3\u0139\u013a\7*\2\2\u013a\u013c\5\b\5\2\u013b\u0138"+
		"\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"!\3\2\2\2\u013f\u013d\3\2\2\2\17\'z\u0081\u0099\u00a1\u00a3\u00f8\u00ff"+
		"\u010b\u010f\u012c\u0131\u013d";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
	}
}