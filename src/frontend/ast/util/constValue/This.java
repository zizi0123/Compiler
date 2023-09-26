package frontend.ast.util.constValue;


import frontend.ast.util.type.ASTType;

public class This extends ConstValue{
    public ASTType GetType() {
        return thisType;
    }
}
