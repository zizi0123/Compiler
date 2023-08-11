parser grammar MxParser;

options {
    tokenVocab = MxLexer;
}

program: definition* EOF;

definition
    :funcDef
    |classDef
    |varDef
    |SemiColon;

funcDef: returnType Identifier '(' functionParameterList ')' '{' blockStmt '}';
returnType: type | Void;
functionParameterList: (type Identifier)?(Comma type Identifier)*;
blockStmt:statement*;


classDef: Class Identifier '{' (varDef|funcDef|constructorDef)* '}';
constructorDef: Identifier '(' ')' '{' blockStmt '}';

type: typeName('['']')*;
typeName:Int|String|Bool|Identifier;

varDef: type defAndAssign (Comma defAndAssign)* SemiColon;
defAndAssign: Identifier ('=' expression)?;

statement
    :'{'blockStmt'}'
    |varDef
    |ifStmt
    |whileStmt
    |forStmt
    |returnStmt
    |breakStmt
    |continueStmt
    |exprStmt;

ifStmt:If'('condition = expression')'trueStmt = statement(Else falseStmt = statement)?;
whileStmt:While '('condition = expression')'statement;
forStmt
    :For'('init = varDef condition = expression? SemiColon step = expression?')'statement
    |For'('initt = expression? SemiColon condition = expression? SemiColon step = expression?')'statement;
returnStmt: Return expression? SemiColon;
breakStmt: Break SemiColon;
continueStmt: Continue SemiColon;
exprStmt:expression ? SemiColon;

expression
    :'('expression')'                                                                       #parenExpr
    |(IntConst|StringConst|True|False|Null|This)                                            #constExpr
    |varExpr = Identifier                                                                   #varExpr
    |New newType ('('')')?                                                                  #newExpr
    |expression'['expression']'                                                             #arrayExpr
    |expression'(' expression? (Comma expression)* ')'                                      #funcCallExpr
    |expression '.' Identifier ')'                                                          #memberExpr
    |<assoc = right> op = ('++'|'--') rhs = expression                                      #preOpExpr
    |lhs = expression op = ('++'|'--')                                                      #unaryExpr
    |<assoc = right> op = ('+' | '-' | '!' | '~') rhs =  expression                         #unaryExpr
    |lhs = expression op = ('*' | '/' | '%') rhs = expression                               #binaryExpr
    |lhs = expression op = ('+' | '-') rhs = expression                                     #binaryExpr
    |lhs = expression op = ('<<' | '>>') rhs = expression                                   #binaryExpr
    |lhs = expression op = ('<' | '<=' | '>' | '>=') rhs = expression                       #binaryExpr
    |lhs = expression op = ('==' | '!=') rhs = expression                                   #binaryExpr
    |lhs = expression op = '&' rhs = expression                                             #binaryExpr
    |lhs = expression op = '^' rhs = expression                                             #binaryExpr
    |lhs = expression op = '|' rhs = expression                                             #binaryExpr
    |lhs = expression op = '&&' rhs = expression                                            #binaryExpr
    |lhs = expression op = '||' rhs = expression                                            #binaryExpr
    |<assoc = right> lexpr = expression '?' mexpr = expression ':' rexpr = expression       #ternaryExpr
    |<assoc = right> lhs = expression '=' rhs = expression                                  #assignExpr;


newType
    :Identifier                                                                             # newClass
    | typeName LBracket expression RBracket (LBracket expression? RBracket)*                # newArray;






