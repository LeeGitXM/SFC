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
        |    G2NAME COLON datatype EQU cagetter SEMI     # initializedByMember
        |    G2NAME COLON datatype EQU THISPROC SEMI     # selfVariable
        |    COMMENT                                     # braceCommentInDeclaration
        ;
docstring: COMMENT                                       # procedureDocstring
        ;
header:  G2NAME POPEN arglist PCLOSE rtndecl             # procedureHeader
        ;
/** =============================== Statement =============================== */
statement: COMMENT                                       # braceComment
        | fnclause SEMI                                  # statementFunction
        | G2NAME EQU expr SEMI                           # statementAssign
        | ifclause                                       # statementIf  
        | forclause                                      # statementFor
        | RTN expr SEMI                                  # statementReturn 
        | BEGIN statement* END SEMI                      # statementBlock
        | varlist EQU CALL G2NAME POPEN exprlist? PCLOSE SEMI   # statementCallWithReturn
        | CALL G2NAME POPEN exprlist? PCLOSE SEMI        # statementCall
        | CHANGE casetter TO cagetter SEMI               # statementChange
        | CREATE G2NAME G2NAME SEMI                      # statementCreate
        | COLLECT POPEN TIMINGOUT expr PCLOSE statement+ END SEMI # statementCollectData
        | CONCLUDE (casetter|variable) EQU expr SEMI     # statementConclusion
        | DELETE variable SEMI                           # statementDelete
        | EXITIF expr SEMI                               # statementExitIf  
        | POST STRING SEMI                               # statementPost
        | REPEAT statement* END SEMI                     # statementRepeat
        | START G2NAME POPEN exprlist? PCLOSE SEMI       # statementStart
        | WAITFOR variable SEMI                          # statementWait
        ;

/** ============================== Expressions ============================== */  
expr:  POPEN expr PCLOSE                                # exprParentheses
      | expr OPR expr                                   # exprOperator
      | SINGLEARGFN POPEN expr PCLOSE                   # expr1ArgFunction
      | EXISTS G2NAME G2NAME NAMED BY G2NAME            # exprExists
      | value                                           # exprValue
      | variable                                        # exprVariable
      | THE G2NAME OF G2NAME                            # exprClassMember
      | CALL POPEN exprlist? PCLOSE                     # exprCall
      | expr (LOPR|EQU|NEQU) expr                       # exprLogicalOperator
      | expr (ROPR|EQU|NEQU) expr                       # exprRelationalOperator
     ;
/** ====================== Secondary Syntax Elements ======================== */

arg: G2NAME COLON datatype                               # argDeclaration
        ;
arglist: arg                                             # firstArgInList
        | arglist COMMA arg                              # subsequentArgInList
        ;
cagetter: THE G2NAME OF G2NAME                           # classAttributeGetter
        ;   
casetter: THE G2NAME OF G2NAME                           # classAttributeSetter
        ; 
datatype: DATATYPE                                       # simpleDatatype
        | CLASS G2NAME                                   # classDatatype
        | SYMBOL                                         # symbolDatatype
        ;
exprlist: expr                                           # firstExpressionInList
        | exprlist COMMA expr                            # subsequentExpressionInList
        ;
fnclause: variable EQU G2NAME POPEN exprlist? PCLOSE     # functionClause
        ;
forclause: FOR G2NAME EQU expr DOWNTO expr BY ivalue DO statement+ END SEMI  # forByDecreasing
        | FOR G2NAME EQU expr TO expr DO statement+ END SEMI                 # forLoop
        ;

ifclause: IF expr THEN statement                                            # ifSingleStatement
        | IF expr THEN BEGIN statement END elseifclause* elseclause? SEMI    # ifWithClauses
        ;
elseifclause: ELSE IF expr THEN BEGIN statement+ END                        # ifElseIfClause
        ;
elseclause: ELSE BEGIN statement+ END                             # ifElseClause
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
        | STRING
        | THE SYMBOL G2NAME
        ;
variable: G2NAME
        | G2NAME BOPEN expr BCLOSE 
        ;    
varlist: variable                                         # firstVarInList
        | varlist COMMA variable                          # subsequentVarInList
        ;

LOPR: AND|OR;                               // Logical operator - must precede AND/OR
SINGLEARGFN: 'abs'|'not';

/* ------------------- Keywords ------------------- */
AND:   'and';                     // Logical operator
BY:    'by';
BEGIN: 'begin';
CALL:  'call';
CHANGE: 'change';
CLASS: 'class';
COLLECT: 'collect data';
CREATE: 'create a';
CONCLUDE: 'conclude that';
DELETE: 'delete';
DO:    'do'|'DO';
DOWNTO:'down to';
ELSE:   'else';
END:   'end';
EXISTS: 'there exists a';
EXITIF: 'exit if';
FOR:   'for';
IF:    'if'; 
NAMED: 'named';
OF:    'of'; 
ONERR: 'on error';
OR:    'or';                     // Logical operator
POST:  'post';
REPEAT: 'repeat';
RTN:   'return';
START: 'start';
THE:   'the';
THEN:  'then';
THISPROC: 'this procedure';
TIMINGOUT: 'timing out after';
TO: 'to';
WAITFOR: 'wait for';


COMMENT: BRACEOPEN .*? BRACECLOSE;
INLINECOMMENT: '//' .*? '\n' ->skip;                  // Must preced OPR

DATATYPE: 'float'|'integer'|'quantity'|'sequence'|'structure'|'text'|'truth-value'|'Value';  // Also SYMBOL
SYMBOL: 'symbol';
FLOAT: DASH? DIGIT+[.]DIGIT*
     | DASH? [.]DIGIT+; 
INTEGER: DASH? DIGIT+; 
OPR: '+'|DASH|'*'|'/'|'%'|'^';              // Arithmetic operators
NEQU: '/=' ;                                // Equality operator
ROPR: '>='|'<='|'>'|'<';                    // Relational operators
EQU: '=';                                   // Must follow the above
FALSE: 'FALSE'|'False'|'false'|'none'|'NONE';
TRUE:  'TRUE'|'True'|'true';          // Logical constant
// NOTE: Must appear after any keyword definitions.
G2NAME:  (UNDERBAR|CHAR) (UNDERBAR|CHAR|DIGIT|DASH)*;   // Expect a dash
STRING: QUOTE .*? QUOTE;
BCLOSE: ']';
BOPEN:  '[';
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
fragment QUOTE: '"';
fragment UNDERBAR: '_';
fragment WS:  [\s\t\r\n]+ ->skip;    // Ignore whitespace (mostly)