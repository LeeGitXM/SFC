/*
 * Define a formal grammar for the G2-based method/procedure language.
 * This will be used to assist conversion of G2 to python.
 */

grammar G2Procedure;

/** Initial rule, begin parsing */
procedure: header COMMENT? declaration* body handler?;

header:   NAME POPEN arglist PCLOSE EQUAL rtnType;
declaration: NAME COLON DATATYPE SEMICOLON               # uninitializedVariable
           | NAME COLON DATATYPE EQUAL value SEMICOLON   # initializedVariable

    ;
handler: ONERR  END              # handleError
    ;

arglist: arg 
       | arglist COMMA arg
       ;
arg: NAME COMMA DATATYPE         # argInList
   ;
body: BEGIN statement* END       # procedureBody
    ;
        
rtnType: POPEN DATATYPE PCLOSE  # returnClause
         ;

statement: COMMENT                   # braceComment
         | RETURN DATATYPE SEMICOLON # returnStatement 
     ;

value: FLOAT
     | STRING
     | DASH? DIGIT+                        # integerValue
     ;


/* ------------------- Keywords ------------------- */
BEGIN: 'begin';
END:   'end';
ONERR: 'on error';
RETURN: 'return';

COMMENT: OPENBRACE .*? CLOSEBRACE;
DATATYPE: 'float'|'integer';
NAME:  (UNDERBAR|CHAR) (UNDERBAR|CHAR|DIGIT|DASH)*;
STRING: DBLQUOTE .*? DBLQUOTE;






COMMA: ',';
DASH: '-';
EQUAL:     '=';
SEMICOLON: ';';
PCLOSE: ')';
POPEN:  '(';
DBLQUOTE:  '"';
SNGLQUOTE: '\'';
fragment DIGIT: [0-9];
fragment CHAR:  [a-zA-Z];
fragment OPENBRACE: '{';
fragment CLOSEBRACE: '}';
fragment PERIOD: '.';
fragment UNDERBAR: '_';
fragment WS:  [\s\t\r\n]+ ->skip;              // Whitespace only matters around operators

