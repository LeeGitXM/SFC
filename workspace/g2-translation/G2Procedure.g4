/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar G2Procedure;
/** Initial rule, begin parsing */
procedure: header docstring? declaration* body EOF;

/** ================== Fundamental Procedure Elements ======================= */
body: BEGIN statement* END                               # procedureBody
        ;
declaration: varlist COLON datatype SEMI                 # uninitializedVariable
        |    G2NAME COLON datatype EQU value SEMI        # initializedVariable
        |    G2NAME COLON datatype EQU THISPROC SEMI     # selfVariable
        ;
docstring: COMMENT                                       # procedureDocstring
        ;
header:  G2NAME POPEN arglist PCLOSE rtndecl             # procedureHeader
        ;
/** =============================== Statement =============================== */
statement: COMMENT                                       # braceComment
        | G2NAME EQU expr SEMI                           # assignmentStatement
        | ifclause                                       # ifStatement  
        | forclause                                      # forStatement
        | RTN expr SEMI                                  # returnStatement 
        | BEGIN statement* END                           # beginBlock
        ;

/** ============================== Expressions ============================== */  
expr: iexpr
    | lexpr
    | nexpr
    ;
iexpr:  POPEN iexpr PCLOSE                                # intParentheses
      | iexpr OPR iexpr                                   # intExpressionOperator
      | ivalue                                            # intValueExpression
      | G2NAME                                            # intVariable
     ;
lexpr: COMMENT lexpr                                      # leadingComment
     | POPEN lexpr PCLOSE                                 # logicalParentheses
     | lvalue                                             # logicalValue
     | G2NAME                                             # logicalVariable
     | lexpr (LOPR|EQU|NEQU) lexpr                        # logicalOperator
     | nexpr (ROPR|EQU|NEQU) nexpr                        # relationalOperator
     ;
nexpr:  POPEN nexpr PCLOSE                                # parentheses
      | nexpr OPR nexpr                                   # expressionOperator
      | OPR nexpr                                         # negative
      | nvalue                                            # numericValue
      | G2NAME                                            # numericVariable
    ;
/** ====================== Secondary Syntax Elements ======================== */
arg: G2NAME COLON datatype                               # argDeclaration
        ;
arglist: arg                                             # firstArgInList
        | arglist COMMA arg                              # subsequentArgInList
        ;
datatype: DATATYPE                                       # simpleDatatype
        | CLASS G2NAME                                   # classDeclaration
        ;
forclause: FOR G2NAME EQU iexpr DOWNTO iexpr BY ivalue DO statement END SEMI  # countdownFor
        ;

ifclause: IF lexpr THEN statement                        # ifThenClause
        ;
ivalue: INTEGER                                          # integerValue
        ;
lvalue: TRUE                                             # logicalTrue
        | FALSE                                          # logicalFalse
        ;
nvalue: ivalue                    
        | FLOAT
        ;
rtndecl: EQU POPEN datatype COMMENT? PCLOSE
        ;
value: nvalue
        | lvalue
        ;
varlist: G2NAME                                         # firstVarInList
        | varlist COMMA G2NAME                          # subsequentVarInList
        ;

LOPR: AND|OR;                               // Logical operator - must precede AND/OR

/* ------------------- Keywords ------------------- */
AND:   'and';                     // Logical operator
BY:    'by';
BEGIN: 'begin';
CLASS: 'class';
DO:    'do'|'DO';
DOWNTO:'down to';
END:   'end';
FOR:   'for';
IF:    'if'; 
ONERR: 'on error';
OR:    'or';                     // Logical operator
RTN:   'return';
THEN:  'then';
THISPROC: 'this procedure';


COMMENT: BRACEOPEN .*? BRACECLOSE;
INLINECOMMENT: '//' .*? '\n' ->skip;                  // Must preced OPR

DATATYPE: 'float'|'integer'|'text'|'truth-value'|'symbol'|'Value';
FLOAT: DASH? DIGIT+[.]DIGIT*
     | DASH? [.]DIGIT+; 
INTEGER: DASH? DIGIT+; 
OPR: '+'|DASH|'*'|'/'|'%'|'^';              // Arithmetic operators
NEQU: '/=' ;                                // Equality operator
ROPR: '>='|'<='|'>'|'<';                    // Relational operators
EQU: '=';                                   // Must follow the above

G2NAME:  (UNDERBAR|CHAR) (UNDERBAR|CHAR|DIGIT|DASH)*;   // Expect a dash
 
FALSE: 'FALSE'|'False'|'false'|'none'|'NONE';
TRUE:  'TRUE'|'True'|'true';          // Logical constant


COLON: ':';
COMMA: ',';
PCLOSE: ')';
POPEN:  '(';
SEMI:  ';';

fragment BRACEOPEN: '{';
fragment BRACECLOSE: '}';
fragment DASH: '-';
fragment DIGIT: [0-9];
fragment CHAR:  [a-zA-Z];
fragment UNDERBAR: '_';
fragment WS:  [\s\t\r\n]+ ->skip;    // Ignore whitespace (mostly)