package ast.util.constValue;


import ast.util.type.ASTType;

public class StringConst extends ConstValue{
    public String value;
    public ASTType GetType() {
        return stringType;
    }

    public StringConst(String val){
        this.value = val;
    }
}
