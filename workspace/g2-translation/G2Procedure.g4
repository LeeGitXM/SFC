/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

grammar G2Procedure;
/** Initial rule, begin parsing */
procedure: header docstring? declaration* block EOF;

/** ================== Fundamental Procedure Elements ======================= */
block: BEGIN statement* END COMMENT* blockerr?           # statementBlock
        ;
declaration: declist COLON datatype SEMI                 # declarationUninitialized
        |    G2NAME COLON datatype EQU value SEMI        # declarationInitialized
        |    G2NAME COLON datatype EQU cagetter SEMI     # declarationInitializedByMember
        |    G2NAME COLON datatype EQU THISPROC SEMI     # declarationSelf
        |    COMMENT                                     # braceCommentInDeclaration
        ;
docstring: COMMENT                                       # procedureDocstring
        ;
 
header:  G2NAME POPEN arglist PCLOSE rtndecl?            # procedureHeader
        ;
/** =============================== Statement =============================== */
statement: (sfragment|block) SEMI COMMENT*               # statementRoot
         ;                    

sfragment: COMMENT sfragment                             # blockComment
        | G2NAME EQU expr                                # statementAssign
        | fnclause                                       # statementFunction
        | switchclause                                   # statementCase 
        | ifclause                                       # statementIf  
        | forclause                                      # statementFor
        | RTN expr?                                      # statementReturn 
        | varlist EQU CALL G2NAME POPEN exprlist? PCLOSE # statementCallWithReturn
        | CALL G2NAME POPEN exprlist? PCLOSE             # statementCall
        | CHANGE casetter TO (cagetter|expr)             # statementChange
        | CREATE G2NAME G2NAME                           # statementCreate
        | COLLECT POPEN TIMINGOUT expr TIMEUNIT? PCLOSE statement+ END  # statementCollectData
        | CONCLUDE (casetter|variable) EQU expr           # statementConclusion
        | DELETE variable                                 # statementDelete
        | EXITIF expr                                     # statementExitIf  
        | POST STRING                                     # statementPost
        | INFORM timeClause? THAT STRING                  # statementInform
        | REPEAT statement* END                           # statementRepeat
        | START G2NAME POPEN exprlist? PCLOSE             # statementStart
        | WAITFOR variable                                # statementWait
        | WAITFOR INTEGER TIMEUNIT                        # statementWaitUnit
        ;

/** ============================== Expressions ============================== */  
expr:  POPEN expr PCLOSE                                # exprParentheses
      | expr OPR expr                                   # exprOperator
      | expr (LOPR|EQU|NEQU) expr                       # exprLogicalOperator
      | expr (ROPR|EQU|NEQU) expr                       # exprRelationalOperator
      | SINGLEARGFN POPEN expr PCLOSE                   # expr1ArgFunction
      | EXISTSA G2NAME G2NAME NAMED BY G2NAME           # exprExists
      | EXISTSA G2NAME G2NAME suchthat                  # exprExistsSuchThat
      | CALL G2NAME POPEN exprlist? PCLOSE              # exprCall
      | THE (G2NAME|POST) OF G2NAME EXISTS              # exprClassMemberExists
      | THE (G2NAME|POST) OF G2NAME                     # exprClassMember
      | value                                           # exprValue
      | variable                                        # exprVariable
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
declist: G2NAME                                        
        | declist COMMA G2NAME                         
        ;
exprlist: expr                                           # firstExpressionInList
        | exprlist COMMA expr                            # subsequentExpressionInList
        ;
fnclause: variable EQU (SEQUENCE|STRUCTURE) POPEN PCLOSE # sequenceClause
        | variable EQU G2NAME POPEN exprlist? PCLOSE     # functionClause
        ;
forclause:FOR G2NAME EQU EACH G2NAME IN G2NAME DO statement+ END       # forInSet
        | FOR G2NAME EQU expr DOWNTO expr BY ivalue DO statement+ END  # forByDecreasing
        | FOR G2NAME EQU expr TO expr DO statement+ END                # forLoop
        ;

ifclause: IF expr THEN (sfragment|block) elseifclause* elseclause? # ifWithClauses
        ;
elseifclause: ELSE IF expr THEN (sfragment|block)                  # ifElseIfClause
        ;
elseclause: ELSE (sfragment|block)                                 # ifElseClause
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
suchthat: SUCHTHAT expr                                      #suchThatPredicate
        ;
switchclause: CASE POPEN G2NAME PCLOSE OF COMMENT* switchcase+ COMMENT* otherwisecase? END  # caseRoot
        ;
switchcase: vallist COLON COMMENT* statement         # caseClause
        ;
timeClause: FOR THE NEXT expr TIMEUNIT                # informTime
        ;
otherwisecase: OTHERWISE COLON statement              # caseOtherwise
        ;
value: nvalue                                         # valueNumeric
        | lvalue                                      # valueLogical
        | STRING                                      # valueString
        | THE SYMBOL G2NAME                           # valueSymbol
        | G2NAME BOPEN INTEGER BCLOSE                 # valueArray
        ;
vallist: value                                        # firstValInList
        | vallist COMMA value                         # subsequentValInList
        ;
variable: G2NAME                                      # variableNamed
        | G2NAME BOPEN expr BCLOSE                    # variableArray
        | THE G2NAME UPONSWS variable                 # variableOnSubworkspace
        ;    
varlist: variable                                         # firstVarInList
        | varlist COMMA variable                          # subsequentVarInList
        ;

LOPR: AND|OR;                               // Logical operator - must precede AND/OR
SINGLEARGFN: 'abs'|'log'|'not'|'round';

/* ------------------- Keywords ------------------- */
EXISTSA: 'there exists a';

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
EACH:  'each';
ELSE:   'else';
END:   'end';
EXISTS: 'exists';
EXITIF: 'exit if';
FOR:   'for';
IF:    'if'|'If';
INFORM: 'inform the operator';
IN:    'in';
NAMED: 'named';
NEXT: 'next';
OF:    'of'; 
ONERR: 'on error';
OR:    'or';                     // Logical operator
OTHERWISE:'otherwise'|'Otherwise';
POST:  'post';
REPEAT: 'repeat';
RTN:   'return';
SUCHTHAT: 'such that';
START: 'start';
THAT:  'that';
THE:   'the';
THEN:  'then';
THISPROC: 'this procedure';
TIMINGOUT: 'timing out after';
TO: 'to';
UPONSWS: 'upon the subworkspace of';
WAITFOR: 'wait for';


COMMENT: BRACEOPEN .*? BRACECLOSE;
INLINECOMMENT: '//' .*? '\n' ->skip;                  // Must preced OPR

DATATYPE: 'float'|'integer'|'quantity'|'text'|'truth-value'|'Value';  // Also SEQUENCE,STRUCTURE,SYMBOL
SEQUENCE: 'sequence';
STRUCTURE:'structure';
SYMBOL: 'symbol';
FLOAT: DASH? DIGIT+[.]DIGIT*
     | DASH? [.]DIGIT+; 
INTEGER: (DASH|DASH \s)? DIGIT+; 
OPR: '+'|DASH|'*'|'/'|'%'|'^';              // Arithmetic operators
NEQU: '/=' ;                                // Equality operator
ROPR: '>='|'<='|'>'|'<';                    // Relational operators
EQU: '=';                                   // Must follow the above
FALSE: 'FALSE'|'False'|'false'|'none'|'NONE';
TRUE:  'TRUE'|'True'|'true';          // Logical constant
TIMEUNIT: 'minutes'|'minute'|'seconds'|'second';
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