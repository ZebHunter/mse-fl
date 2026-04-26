grammar Kocaml;

program
    : statement* EOF
    ;

statement
    : variableDeclaration SEMI
    | assignment SEMI
    | functionDeclaration
    | ifStatement
    | expression SEMI
    ;

variableDeclaration
    : LET IDENTIFIER (COLON typeRef)? (ASSIGN expression)?
    ;

assignment
    : IDENTIFIER ASSIGN expression
    ;

functionDeclaration
    : FUN IDENTIFIER LPAREN parameterList? RPAREN (COLON typeRef)? block
    ;

parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : IDENTIFIER COLON typeRef
    ;

ifStatement
    : IF LPAREN expression RPAREN block (ELSE block)?
    ;

block
    : LBRACE statement* RBRACE
    ;

expression
    : equalityExpression
    ;

equalityExpression
    : comparisonExpression ((EQ | NEQ) comparisonExpression)*
    ;

comparisonExpression
    : additiveExpression ((GT | GTE | LT | LTE) additiveExpression)*
    ;

additiveExpression
    : multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*
    ;

multiplicativeExpression
    : unaryExpression ((STAR | DIV | MOD) unaryExpression)*
    ;

unaryExpression
    : (PLUS | MINUS) unaryExpression
    | primaryExpression
    ;

primaryExpression
    : literal
    | IDENTIFIER
    | functionCall
    | LPAREN expression RPAREN
    ;

functionCall
    : IDENTIFIER LPAREN argumentList? RPAREN
    ;

argumentList
    : expression (COMMA expression)*
    ;

literal
    : INT_LITERAL
    | FLOAT_LITERAL
    | STRING_LITERAL
    | BOOL_LITERAL
    ;

typeRef
    : TYPE_INT
    | TYPE_FLOAT
    | TYPE_STRING
    | TYPE_BOOL
    | TYPE_UNIT
    ;

LET: 'let';
FUN: 'fun';
IF: 'if';
ELSE: 'else';

TYPE_INT: 'Int';
TYPE_FLOAT: 'Float';
TYPE_STRING: 'String';
TYPE_BOOL: 'Bool';
TYPE_UNIT: 'Unit';

BOOL_LITERAL: 'true' | 'false';

EQ: '==';
NEQ: '!=';
GTE: '>=';
LTE: '<=';
GT: '>';
LT: '<';
ASSIGN: '=';

PLUS: '+';
MINUS: '-';
STAR: '*';
DIV: '/';
MOD: '%';

LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
COLON: ':';
COMMA: ',';
SEMI: ';';

INT_LITERAL: [0-9]+;
FLOAT_LITERAL: [0-9]+ '.' [0-9]+;
STRING_LITERAL: '"' (ESC | ~["\\\r\n])* '"';
fragment ESC: '\\' ["\\/bfnrt];

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
WS: [ \t\r\n]+ -> skip;
