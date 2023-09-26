package frontend.ast.util.constValue;


import frontend.ast.util.type.ASTType;

public class Null extends ConstValue{
    public ASTType GetType() {
        return nullType;
    }
}
