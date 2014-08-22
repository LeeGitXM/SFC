// Generated from G2Expression.g4 by ANTLR 4.0
package com.ils.aed.expression;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface G2ExpressionVisitor<T> extends ParseTreeVisitor<T> {
	T visitTimePeriodLogicalFunction(G2ExpressionParser.TimePeriodLogicalFunctionContext ctx);

	T visitExpression(G2ExpressionParser.ExpressionContext ctx);

	T visitLogicalParentheses(G2ExpressionParser.LogicalParenthesesContext ctx);

	T visitExpressionOperator(G2ExpressionParser.ExpressionOperatorContext ctx);

	T visitG2TimeExpression(G2ExpressionParser.G2TimeExpressionContext ctx);

	T visitStatusArg(G2ExpressionParser.StatusArgContext ctx);

	T visitTimePeriodDurationFunction(G2ExpressionParser.TimePeriodDurationFunctionContext ctx);

	T visitFuzzyOperatorWithDash(G2ExpressionParser.FuzzyOperatorWithDashContext ctx);

	T visitNumericTag(G2ExpressionParser.NumericTagContext ctx);

	T visitStatusSymbol(G2ExpressionParser.StatusSymbolContext ctx);

	T visitValue(G2ExpressionParser.ValueContext ctx);

	T visitLogicalOperatorWithDash(G2ExpressionParser.LogicalOperatorWithDashContext ctx);

	T visitDoubleArgumentFunction(G2ExpressionParser.DoubleArgumentFunctionContext ctx);

	T visitExprs(G2ExpressionParser.ExprsContext ctx);

	T visitG2TimeExpressionQuoted(G2ExpressionParser.G2TimeExpressionQuotedContext ctx);

	T visitThreeArgFunction(G2ExpressionParser.ThreeArgFunctionContext ctx);

	T visitSymbol(G2ExpressionParser.SymbolContext ctx);

	T visitFuzzyExpressionWithDash(G2ExpressionParser.FuzzyExpressionWithDashContext ctx);

	T visitVarArgRecursive(G2ExpressionParser.VarArgRecursiveContext ctx);

	T visitNoArgumentFunction(G2ExpressionParser.NoArgumentFunctionContext ctx);

	T visitLogicalOperator(G2ExpressionParser.LogicalOperatorContext ctx);

	T visitDoubleModelFunction(G2ExpressionParser.DoubleModelFunctionContext ctx);

	T visitLvalue(G2ExpressionParser.LvalueContext ctx);

	T visitLogicalTag(G2ExpressionParser.LogicalTagContext ctx);

	T visitTimePeriodFunction(G2ExpressionParser.TimePeriodFunctionContext ctx);

	T visitConditional(G2ExpressionParser.ConditionalContext ctx);

	T visitPeriodFromString(G2ExpressionParser.PeriodFromStringContext ctx);

	T visitQuotedString(G2ExpressionParser.QuotedStringContext ctx);

	T visitParentheses(G2ExpressionParser.ParenthesesContext ctx);

	T visitTimeFunctionStringCompare(G2ExpressionParser.TimeFunctionStringCompareContext ctx);

	T visitAsofexpression(G2ExpressionParser.AsofexpressionContext ctx);

	T visitValueExpression(G2ExpressionParser.ValueExpressionContext ctx);

	T visitHValue(G2ExpressionParser.HValueContext ctx);

	T visitSingleModelFunction(G2ExpressionParser.SingleModelFunctionContext ctx);

	T visitLogicalTagStringFunction(G2ExpressionParser.LogicalTagStringFunctionContext ctx);

	T visitStringFunctionExpression(G2ExpressionParser.StringFunctionExpressionContext ctx);

	T visitLogicalValue(G2ExpressionParser.LogicalValueContext ctx);

	T visitTimeunit(G2ExpressionParser.TimeunitContext ctx);

	T visitDoubleQuotedModelFunction(G2ExpressionParser.DoubleQuotedModelFunctionContext ctx);

	T visitFiveArgFunction(G2ExpressionParser.FiveArgFunctionContext ctx);

	T visitLeadingComment(G2ExpressionParser.LeadingCommentContext ctx);

	T visitQuotedModelFunction(G2ExpressionParser.QuotedModelFunctionContext ctx);

	T visitArgGoodOrBad(G2ExpressionParser.ArgGoodOrBadContext ctx);

	T visitFuzzyOperator(G2ExpressionParser.FuzzyOperatorContext ctx);

	T visitRelationalOperator(G2ExpressionParser.RelationalOperatorContext ctx);

	T visitLogicalSymbol(G2ExpressionParser.LogicalSymbolContext ctx);

	T visitGoodOrBad(G2ExpressionParser.GoodOrBadContext ctx);

	T visitG2TimeFunction(G2ExpressionParser.G2TimeFunctionContext ctx);

	T visitLogicalStringFunction(G2ExpressionParser.LogicalStringFunctionContext ctx);

	T visitDoubleDoubleQuotedModelFunction(G2ExpressionParser.DoubleDoubleQuotedModelFunctionContext ctx);

	T visitSingleArgLogicalFunction(G2ExpressionParser.SingleArgLogicalFunctionContext ctx);

	T visitSingleArgumentFunction(G2ExpressionParser.SingleArgumentFunctionContext ctx);

	T visitVariableArgumentFunction(G2ExpressionParser.VariableArgumentFunctionContext ctx);

	T visitTagString(G2ExpressionParser.TagStringContext ctx);

	T visitVarArgExpression(G2ExpressionParser.VarArgExpressionContext ctx);

	T visitNegative(G2ExpressionParser.NegativeContext ctx);

	T visitFuzzyExpression(G2ExpressionParser.FuzzyExpressionContext ctx);
}