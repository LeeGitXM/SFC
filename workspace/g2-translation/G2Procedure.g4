/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar G2Procedure;
/** Initial rule, begin parsing */
procedure: header docstring? declaration* body COMMENT* onerror? EOF;

/** ================== Fundamental Procedure Elements ======================= */
body: BEGIN statement* END                               # procedureBody
        ;
declaration: varlist COLON datatype SEMI                 # declarationUninitialized
        |    G2NAME COLON datatype EQU value SEMI        # declarationInitialized
        |    G2NAME COLON datatype EQU cagetter SEMI     # declarationInitializedByMember
        |    G2NAME COLON datatype EQU THISPROC SEMI     # declarationSelf
        |    COMMENT                                     # braceCommentInDeclaration
        ;
docstring: COMMENT                                       # procedureDocstring
        ;
onerror:  ONERR POPEN G2NAME PCLOSE statement+ END SEMI COMMENT?  # procedureOnError
        ;  
header:  G2NAME POPEN arglist PCLOSE rtndecl?            # procedureHeader
        ;
/** =============================== Statement =============================== */
statement: sfragment SEMI COMMENT*                       # statementRoot
         ;                    

sfragment: COMMENT sfragment                             # blockComment
        | G2NAME EQU expr                                # statementAssign
        | fnclause                                       # statementFunction
        | switchclause                                   # statementCase 
        | ifclause                                       # statementIf  
        | forclause                                      # statementFor
        | RTN expr?                                      # statementReturn 
        | BEGIN statement+ END blockerr?                 # statementBlock
        | varlist EQU CALL G2NAME POPEN exprlist? PCLOSE # statementCallWithReturn
        | CALL G2NAME POPEN exprlist? PCLOSE             # statementCall
        | CHANGE casetter TO cagetter                    # statementChange
        | CREATE G2NAME G2NAME                           # statementCreate
        | COLLECT POPEN TIMINGOUT expr PCLOSE statement+ END  # statementCollectData
        | CONCLUDE (casetter|variable) EQU expr           # statementConclusion
        | DELETE variable                                 # statementDelete
        | EXITIF expr                                     # statementExitIf  
        | POST STRING                                     # statementPost
        | REPEAT statement* END                           # statementRepeat
        | START G2NAME POPEN exprlist? PCLOSE             # statementStart
        | WAITFOR variable                                # statementWait
        | WAITFOR INTEGER TIMEUNIT                        # statementWaitUnit
        ;

/** ============================== Expressions ============================== */  
expr:  POPEN expr PCLOSE                                # exprParentheses
      | expr OPR expr                                   # exprOperator
      | SINGLEARGFN POPEN expr PCLOSE                   # expr1ArgFunction
      | EXISTS G2NAME G2NAME NAMED BY G2NAME            # exprExists
      | value                                           # exprValue
      | variable                                        # exprVariable
      | THE G2NAME OF G2NAME                            # exprClassMember
      | CALL G2NAME POPEN exprlist? PCLOSE              # exprCall
      | expr (LOPR|EQU|NEQU) expr                       # exprLogicalOperator
      | expr (ROPR|EQU|NEQU) expr                       # exprRelationalOperator
     ;
/** ====================== Secondary Syntax Elements ======================== */

arg: G2NAME COLON datatype                               # argDeclaration
        ;
arglist: arg                                             # firstArgInList
        | arglist COMMA arg                              # subsequentArgInList
        ;
blockerr:  ONERR POPEN G2NAME PCLOSE statement+ END      # blockErrorClause
        ;
cagetter: THE G2NAME OF G2NAME                           # classAttributeGetter
        ;   
casetter: THE G2NAME OF G2NAME                           # classAttributeSetter
        ; 
datatype: DATATYPE                                       # simpleDatatype
        | CLASS G2NAME                                   # classDatatype
        | (SEQUENCE|STRUCTURE|SYMBOL)                    # symbolDatatype
        ;
exprlist: expr                                           # firstExpressionInList
        | exprlist COMMA expr                            # subsequentExpressionInList
        ;
fnclause: variable EQU (SEQUENCE|STRUCTURE) POPEN PCLOSE # sequenceClause
        | variable EQU G2NAME POPEN exprlist? PCLOSE     # functionClause
        ;
forclause: FOR G2NAME EQU expr DOWNTO expr BY ivalue DO statement+ END  # forByDecreasing
        | FOR G2NAME EQU expr TO expr DO statement+ END                 # forLoop
        ;

ifclause: IF expr THEN sfragment elseifclause* elseclause?    # ifWithClauses
        ;
elseifclause: ELSE IF expr THEN sfragment                     # ifElseIfClause
        ;
elseclause: ELSE sfragment                                    # ifElseClause
        ;
ivalue: INTEGER
        ;
lvalue: TRUE                                                 # logicalTrue
        | FALSE                                              # logicalFalse
        ;
nvalue: ivalue                    
        | FLOAT
        ; 
rtndecl: EQU POPEN datatype COMMENT? PCLOSE
        ;
switchclause: CASE POPEN G2NAME PCLOSE OF COMMENT* switchcase+ COMMENT* otherwisecase? END  # caseRoot
        ;
switchcase: vallist COLON COMMENT* statement                           # caseClause
        ;
otherwisecase: OTHERWISE COLON statement                               # caseOtherwise
        ;
value: nvalue
        | lvalue
        | STRING
        | THE SYMBOL G2NAME
        | G2NAME BOPEN INTEGER BCLOSE
        ;
vallist: value                                         # firstValInList
        | vallist COMMA value                          # subsequentValInList
        ;
variable: G2NAME
        | G2NAME BOPEN expr BCLOSE 
        ;    
varlist: variable                                         # firstVarInList
        | varlist COMMA variable                          # subsequentVarInList
        ;

LOPR: AND|OR;                               // Logical operator - must precede AND/OR
SINGLEARGFN: 'abs'|'log'|'not';

/* ------------------- Keywords ------------------- */
AND:   'and';                     // Logical operator
BY:    'by';
BEGIN: 'begin';
CALL:  'call';
CASE:  'case';
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
OTHERWISE:'otherwise'|'Otherwise';
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

DATATYPE: 'float'|'integer'|'quantity'|'text'|'truth-value'|'Value';  // Also SEQUENCE,STRUCTURE,SYMBOL
SEQUENCE: 'sequence';
STRUCTURE:'structure';
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
TIMEUNIT: 'minutes'|'seconds';
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