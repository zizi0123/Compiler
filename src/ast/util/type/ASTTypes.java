package ast.util.type;

public interface ASTTypes {
    ASTType stringType = new ASTType("string",null);
    ASTType voidType = new ASTType("void",null);
    ASTType intType = new ASTType("int",null);
    ASTType boolType = new ASTType("bool",null);
    ASTType thisType = new ASTType("this",null);
    ASTType nullType = new ASTType("null",null);
    ASTType functionType = new ASTType("function",null);

}
