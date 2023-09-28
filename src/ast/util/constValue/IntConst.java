package ast.util.constValue;

import ast.util.type.ASTType;

public class IntConst extends ConstValue{
    public int value;
    public ASTType GetType() {
        return intType;
    }

    public IntConst(int val){
        this.value = val;
    }
}
