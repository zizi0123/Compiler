package ast.util.constValue;


import ast.util.type.ASTType;

public class This extends ConstValue{
    public ASTType GetType() {
        return thisType;
    }
}
