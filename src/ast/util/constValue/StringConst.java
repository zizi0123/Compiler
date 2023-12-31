package ast.util.constValue;


import ast.util.type.ASTType;

public class StringConst extends ConstValue {
    public String value;

    public ASTType GetType() {
        return stringType;
    }

    public StringConst(String val) {
        this.value = val.substring(1, val.length() - 1);
        this.value = this.value.replace("\\\\", "\\").replace("\\\"", "\"").replace("\\n", "\n");
    }
}
