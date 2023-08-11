lexer grammar MxLexer;

//punctuation
SelfAdd: '++';
SelfSub: '--';

GEqual:'>=';
LEqual:'<=';
EEqual:'==';
NEqual:'!=';

RShift:'>>';
LShift:'<<';

Arrow:'->';
Quote:'"';

LAnd:'&&';
LOr:'||';
LNot:'!';

Add:'+';
Sub:'-';
Mul:'*';
Div:'/';
Mod:'%';

GThan:'>';
LThan:'<';

BAnd:'&';
BOr:'|';
BXor:'^';
BNot:'~';

Assign:'=';
Member:'.';
Que:'?';
Colon:':';
Comma:',';
SemiColon:';';

LBracket:'[';
RBracket:']';
LParen:'(';
RParen:')';
LBrace:'{';
RBrace:'}';


//keyword
Void:'void';
Bool:'bool';
Int:'int';
String:'string';
Class:'class';
New:'new';
Null: 'null';
True: 'true';
False: 'false';
This: 'this';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';


//identifier
Identifier:[A-Za-z][0-9A-Za-z_]*;

IntConst:[1-9][0-9]*|'0';
fragment ESC:'\\"'|'\\\\'|'\n';
StringConst:'"'(ESC|.)*?'"';


//skip
WhiteSpace: [\n\f\r\t ]+ -> skip;
CommentLine: '//'.*? '\r'?'\n' ->skip;
CommentPara: '/*'.*?'*/' -> skip;



