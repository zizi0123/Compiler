package IR.literal;

import ast.util.constValue.StringConst;

import static IR.type.IRTypes.irPtrType;

public class StringLiteral extends Literal{
    public String val;
    public int id;

    public String name;

    public static int strNum = 0;

    public StringLiteral(String val) {
        this.type = irPtrType;
        this.val = val;
        ++strNum;
        this.id = strNum;
        this.name = "@str."+this.id;
    }

    public StringLiteral(StringConst str) {
        this.type = irPtrType;
        this.val = str.value;
        ++strNum;
        this.id = strNum;
        this.name = "@str."+this.id;
    }

    @Override
    public String getName() {
        return name;
    }


}
