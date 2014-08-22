/*
 * Define a formal grammar for the G2-based Expression language. This
 * serves as a basis for migration to the user-expression syntax -
 * and from there to valid Ignition syntax.
 */

grammar G2Expression;

/** Initial rule, begin parsing */
expression: exprs EOF;
exprs:   expr
     |   lexpr
     ;

lexpr: COMMENT lexpr                # leadingComment
     | '(' lexpr ')'                # logicalParentheses
     | lvalue                       # logicalValue
     | lexpr (LOPR|EOPR) lexpr      # logicalOperator
     | lexpr EOPR g2Symbol          # logicalSymbol
     | g2Status (EOPR|IS) g2Symbol  # statusSymbol
     | ARG isgoodorbad              # argGoodOrBad
     | expr fuzz                    # fuzzyOperator
     | expr (ROPR|EOPR) expr        # relationalOperator
     | ARG EOPR STRING              # tagString
     | L1FN '(' STRING ')'          # logicalStringFunction
     | L2FN '(' STRING ',' ARG ')'  # logicalTagStringFunction
     | SingleArgLogicalFn '(' lexpr ')'    # singleArgLogicalFunction
     | ThreeArgFn '(' expr COMMA expr COMMA expr ')'                       # threeArgFunction
     | FiveArgFn '(' expr COMMA expr COMMA expr COMMA expr COMMA expr ')'  # fiveArgFunction
     | TimePeriodLogicalFn '(' ARG COMMA period ')'                        # timePeriodLogicalFunction
     | TimePeriodLogicalFn '(' ARG COMMA period ')'  EOPR STRING           # timeFunctionStringCompare
     | ARG                            # logicalTag
     | g2time (IS|EOPR|ROPR) SYMBOL? (DATESYMBOL|INT)                       # g2TimeExpression
     | g2time (IS|EOPR|ROPR) SYMBOL? DBLQUOTE DATESYMBOL DBLQUOTE           # g2TimeExpressionQuoted
     | expr (ROPR|EOPR) OPR value    # logicalOperatorWithDash
     | expr  fuzzdash                # fuzzyOperatorWithDash
     | StringFn '(' ARG COMMA INT COMMA INT  ')' EOPR STRING               # stringFunctionExpression
     ;

expr:  '(' expr ')'                           # parentheses
      | expr OPR expr                         # expressionOperator
      | OPR expr                              # negative
      | IF  lexpr THEN expr ELSE expr         # conditional
      | NoArgFn '(' ')'                       # noArgumentFunction
      | SingleArgFn '(' expr ')'              # singleArgumentFunction
      | SingleModelFn '(' ARG ')'             # singleModelFunction
      | SingleModelFn '(' STRING ')'          # quotedModelFunction
      | DoubleArgFn '(' expr ',' expr ')'                        # doubleArgumentFunction
      | DoubleModelFn '(' ARG ',' expr ')'                       # doubleModelFunction
      | DoubleModelFn '(' STRING ',' expr ')'   # doubleQuotedModelFunction
      | DoubleModelFn '(' STRING ',' STRING ')' # doubleDoubleQuotedModelFunction
      | TimePeriodFn '(' ARG COMMA period ')'   # timePeriodFunction
      | TimePeriodDurationFn '(' ARG COMMA period COMMA period ')' # timePeriodDurationFunction
      | VarArgFn '(' vararg ')'                 # variableArgumentFunction
      | ARG                                     # numericTag
      | STRING              # quotedString
      | g2asofExpression    # asofexpression
      | value               # valueExpression
      //| TIMESTAMP '(' string ')' '-' TIMESTAMP '(' string ')' # timestampDifference
    ;

// Must make patterns that are context-sensitive rules for the parser
// -- the VarArgFn picks up "min"
timeunit: 'M'|'H'|'D'|'m'|'h'|'d'|'hour'|'hr'|'mi'|VarArgFn;

period:  STRING                             # periodFromString
      ;

lvalue: TRUE|FALSE;
value:  INT|FLOAT;
g2time: '('? (DOW|DOM|HOUR|MONTH|MINUTE) ')'?   # g2TimeFunction
      ;

g2Status:  STATUS ARG                           # statusArg
    ;

g2asofExpression: VALUEOF ARG ASOF INT (MINUTES|HOURS) AGO   # hValue
;
fuzz: (ROPR|EOPR) expr '(' '+-' expr ')'               # fuzzyExpression
    ;

fuzzdash: (ROPR|EOPR) OPR value '(' '+-' value ')'      # fuzzyExpressionWithDash
    ;

isgoodorbad: IS SYMBOL? (GOOD|BAD|TRUE|FALSE)           # goodOrBad
    ;

g2Symbol: SYMBOL? (GOOD|BAD|TRUE|FALSE|DATESYMBOL) # symbol
    ;
vararg: expr                            # varArgExpression
      | vararg COMMA expr               # varArgRecursive
      ;
// No argument numeric function
NoArgFn: 'rta-current-day-of-year' |'RTA-CURRENT-DAY-OF-YEAR' | 'rta-current-week-number'|'RTA-CURRENT-WEEK-NUMBER'|'PI'|'pi'
       ;
// Single argument numeric function
SingleArgFn: 'ABS'|'abs'|'Abs'|'ARCTAN'|'arctan'|'CEILING'|'ceiling'|'COS'|'cos'
           |'EXP'|'exp'|'FLOOR'|'floor'|'LN'|'ln'|'LOG'|'log'
           |'REMAINDER'|'remainder'
           |'ROUND'|'round'|'SIN'|'sin'|'SQRT'|'sqrt'|'TAN'|'tan'|'TRUNCATE'|'truncate'
           ;
// Numeric function that takes a model name as its argument
SingleModelFn: 'AED-CERTAINTY'|'aed-certainty'|'AED-Certainty'|'INFER-ESTIMATE'
             | 'PCA-Q2'|'PCA-T2'|'PID-ACE'|'pid-ace'|'PID-OFFSET-DURATION'|'pid-offset-duration'
             | 'TIMESTAMP'| 'ALERT-DURATION' | 'alert-duration'
             |'VFM-BIAS'|'VFM-ESTIMATED-FLOW'|'VFM-NORMALIZED-DEVIATION'|'VFM-RESIDUAL'|'vfm-residual'
             ;
// Single Arg logical function
SingleArgLogicalFn:'NOT'|'Not'|'not';

// Double numeric arguments
DoubleArgFn: 'EXPT'|'expt'|'rta-week-number'|'quotient'|'random';
// Double argument tag/numeric function
DoubleModelFn: 'AED-SUBMODEL-CERTAINTY'|'aed-submodel-certainty'
             |'PCA-RESIDUAL'|'PCA-SCORE'|'PCA-SPEX-TAG'
             ;
// Three argument logical function
ThreeArgFn: 'Is-Outside'|'is-outside'|'IS-OUTSIDE'|'IS-Outside'
          |'is-between'|'Is-Between'|'IS-BETWEEN'
          | 'rta-day-of-year'|'RTA-DAY-OF-YEAR'
          ;
FiveArgFn: 'Is-Outside-With-Deadband'|'is-outside-with-deadband'|'IS-OUTSIDE-WITH-DEADBAND'
          |'is-between-with-deadband'|'Is-Between-With-Deadband'|'IS-BETWEEN-WITH-DEADBAND';
// Numeric functions taking a tag argument followed by a time period
TimePeriodFn: 'CHANGE-OVER-TIME'|'change-over-time'|'Change-Over-Time'|'Change-over-time'|'change-Over-Time'
            | 'PERCENTAGE-CHANGE-OVER-TIME'|'percentage-change-over-time'|'Percentage-Change-Over-Time'|'Percentage-change-over-time'
            | 'LCL'|'UCL'
            | 'rate-of-change-per-minute' | 'Rate-of-change-per-minute'|'rate-of-change'|'RATE-OF-CHANGE'
            | 'RATE-OF-CHANGE-PER-MINUTE' |'SIGMA'|'sum'
            | 'TMAX' | 'tmax'|'Tmax'| 'TMIN' | 'tmin'|'Tmin'|'INT'
            | 'TRANGE'|'trange'|'TRUNCATE'|'truncate'
            |'tavg'|'TAVG'|'Tavg'|'TaVG'|'TVAL'|'tval'|'Tval'|'stdev'|'STDEV'
;
TimePeriodDurationFn:
          'havg'|'HAVG'|'hsigma'|'HSIGMA'|'HMAX'|'hmax'|'HMIN'|'hmin'
;
// Logical functions that accept a string
L1FN: 'AED-USER-ONLINE'|'aed-user-online';
// Logical functions that accept a tag and a string
L2FN: 'is-contained-in-text' | 'IS-CONTAINED-IN-TEXT';
// String functions taking a tag argument followed by two integers
StringFn: 'GET-FROM-TEXT';
// Logical functions taking a tag argument followed by a time period
TimePeriodLogicalFn: 'nochg'|'NOCHG'|'nochg-text';
// Function with a variable number of arguments
VarArgFn: 'AVERAGE'|'average'|'MAX'|'max'|'MIN'|'min';

// G2 Syntax
DOM: 'the current day of the month'|'THE CURRENT DAY OF THE MONTH'|'QUANTITY(THE CURRENT DAY OF THE MONTH)'
| 'DAY-OF-THE-MONTH(THE CURRENT REAL TIME)'|'DAY-OF-THE-MONTH (THE CURRENT REAL TIME)'|'currentDayOfMonth()';
DOW: 'the current day of the week' |'THE CURRENT DAY OF THE WEEK'
        |'DAY-OF-THE-WEEK(THE CURRENT TIME)'|'DAY-OF-THE-WEEK(THE CURRENT REAL TIME)'
|'DAY-OF-THE-WEEK (THE CURRENT TIME)'|'currentDayOfWeek()';
HOUR: 'the current hour'           |'THE CURRENT HOUR'|'hour(the current real time)'
        |'HOUR(THE CURRENT REAL TIME)'|'HOUR (THE CURRENT REAL TIME)';
MONTH: 'the current month'         |'THE CURRENT MONTH'|'currentMonth()';
MINUTE: 'the current minute'       |'THE CURRENT MINUTE';

// G2 Syntax with arg
AGO: 'AGO'|'ago';
ASOF: 'AS OF'|'as of';
HOURS: 'HOURS'|'hours'|'hour'|'HOUR';
MINUTES: 'MINUTES'|'minutes'|'minute'|'MINUTE';
STATUS: 'the status of';
SYMBOL: 'the symbol'|'THE SYMBOL';
VALUEOF: 'THE VALUE OF'|'the value of';
VALUE: 'the current value of';
// Date-specific symbols
DATESYMBOL: 'SUNDAY'|'MONDAY'|'TUESDAY'|'WEDNESDAY'|'THURSDAY'|'FRIDAY'|'SATURDAY'
           |'sunday'|'monday'|'tuesday'|'wednesday'|'thursday'|'friday'|'saturday'
           |'Sunday'|'Monday'|'Tuesday'|'Wednesday'|'Thursday'|'Friday'|'Saturday'
           |'JANUARY'|'FEBRUARY'|'MARCH'|'APRIL'|'MAY'|'JUNE'|'JULY'|'AUGUST'
           |'SEPTEMBER'|'OCTOBER'|'NOVEMBER'|'DECEMBER';

// Other parts of speech
COMMA: ',';

OPR: '+'|DASH|'*'|'/'|'%'|'^';                // Arithmetic operators
EOPR: '=' | '/=' ;                        // Equality operators
ROPR: '>='|'<='|'>'|'<';                  // Relational operators
LOPR: AND|OR;                             // Logical operators

IF:  'if'|'IF';                           // Conditional command
THEN:  'then'|'THEN';
ELSE:  'else'|'ELSE';
IS:  'is'|'IS'|'HAS'|'has';
BAD: 'Bad'|'BAD'|'bad'|'no current value'|'NO CURRENT VALUE'|'no value'|'NO VALUE';
GOOD: 'Good'|'GOOD'|'good'|'not bad'|'a current value';
TRUE:  'TRUE'|'True'|'true';          // Logical constant
FALSE: 'FALSE'|'False'|'false'|'none'|'NONE';

AND:  'and' | 'And' | 'AND';                   // Logical operator
OR: 'or'| 'Or'| 'OR';                          // Logical operator
INT: DASH?  DIGIT+;                         // An integer
FLOAT: DASH? DIGIT+[.]DIGIT*                // Need to handle ugly case with space between dash and number
     | DASH? [.]DIGIT+;                     // A float
TIMESTAMP: ('TSTAMP'|'tstamp');

STRING: DBLQUOTE .*? DBLQUOTE
      ;
// The ordering of ARG/INT/OPR is critical
// In the G2 version, we don't allow slash in the name
ARG: CHAR ('_'|'-'|PERIOD|':'|DIGIT|CHAR)+                                    // Function argument (tag or model name)
   | DIGIT+ ('_'|'-'|PERIOD|':'|CHAR) ('_'|'-'|PERIOD|':'|DIGIT|CHAR)*        // Do not allow all digits
   ;

COMMENT: OPENBRACE .*? CLOSEBRACE;


fragment DIGIT: [0-9];
fragment CHAR:  [a-zA-Z];
fragment DASH: '-';
fragment OPENBRACE: '{';
fragment CLOSEBRACE: '}';
fragment PERIOD: '.';
PCLOSE: ')';
POPEN:  '(';
DBLQUOTE:  '"';
SNGLQUOTE: '\'';
fragment WS:  [\s\t\r\n]+ ->skip;              // Whitespace only matters around operators

