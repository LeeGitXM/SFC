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
declaration: VARNAME COLON DATATYPE SEMI                 # uninitializedVariable
        |    VARNAME COLON DATATYPE EQU value SEMI       # initializedVariable
        ;
docstring: COMMENT                                       # procedureDocstring
        ;
header:   procname POPEN arglist PCLOSE rtndecl          # procedureHeader
        ;
/** =============================== Statement =============================== */
statement: COMMENT                                       # braceComment
        | VARNAME EQU expr SEMI                          # assignmentStatement
        | ifclause                                       # ifStatement  
        | forclause                                      # forStatement
        | RTN expr SEMI                                  # returnStatement 
     ;

/** ============================== Expressions ============================== */
expr: iexpr
    | lexpr
    | nexpr
    ;
iexpr:  '(' iexpr ')'                                     # intParentheses
      | iexpr OPR iexpr                                   # intExpressionOperator
      | OPR iexpr                                         # intNegative
      | ivalue                                            # intValueExpression
      | VARNAME                                           # intVariable
     ;
lexpr: COMMENT lexpr                                      # leadingComment
     | '(' lexpr ')'                                      # logicalParentheses
     | lvalue                                             # logicalValue
     | lexpr (LOPR|EQU|NEQU) lexpr                        # logicalOperator
     | nexpr (ROPR|EQU|NEQU) nexpr                        # relationalOperator
     ;
nexpr:  '(' nexpr ')'                                     # parentheses
      | nexpr OPR nexpr                                   # expressionOperator
      | OPR nexpr                                         # negative
      | nvalue                                            # numericValue
      | VARNAME                                           # numericVariable
    ;
/** ====================== Secondary Syntax Elements ======================== */
arg: VARNAME COLON DATATYPE                              # argDeclaration
        ;
arglist: arg                                             # firstArgInList
        | arglist COMMA arg                              # subsequentArgInList
        ;
forclause: FOR VARNAME EQU iexpr  DOWNTO iexpr BY ivalue DO statement END SEMI  # countdownFor
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
procname: PNAME                                           # procedureName
        ;
rtndecl: EQU POPEN DATATYPE PCLOSE
        ;
value: nvalue
        ;

LOPR: AND|OR;                               // Logical operator - must precede AND/OR

/* ------------------- Keywords ------------------- */
AND:   'and';                     // Logical operator
BY:    'by';
BEGIN: 'begin';
DO:    'do'|'DO';
DOWNTO:'down to';
END:   'end';
FOR:   'for';
IF:    'if'; 
ONERR: 'on error';
OR:    'or';                     // Logical operator
RTN:   'return';
THEN:  'then';


COMMENT: BRACEOPEN .*? BRACECLOSE;
INLINECOMMENT: '//' .*? '\n' ->skip;                  // Must preced OPR

DATATYPE: 'float'|'integer';
FLOAT: DASH? DIGIT+[.]DIGIT*
     | DASH? [.]DIGIT+; 
INTEGER: DASH? DIGIT+; 
OPR: '+'|DASH|'*'|'/'|'%'|'^';              // Arithmetic operators
NEQU: '/=' ;                                // Equality operator
ROPR: '>='|'<='|'>'|'<';                    // Relational operators
EQU: '=';                                   // Must follow the above


VARNAME:  CHAR (CHAR|DIGIT)*;                          // Must precede PNAME
PNAME:  (UNDERBAR|CHAR) (UNDERBAR|CHAR|DIGIT|DASH)+;   // Expect a dash
 
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