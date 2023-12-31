package ast.util.constValue;

import ast.util.type.ASTType;

public class BoolConst extends ConstValue{
    public boolean value;
    @Override
    public ASTType GetType() {
        return boolType;
    }

    public BoolConst(boolean value){
        this.value = value;
    }
}
