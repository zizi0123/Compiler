package IR.Entity.literal;

import IR.BasicBlock;
import ast.util.constValue.StringConst;

import static IR.type.IRTypes.irPtrType;

public class StringLiteral extends Literal{
    public String val;
    public int id;
    public String name;
    public static int strNum = 0;

    public String toIrVal(){
        return val.replace("\\","\\\\").replace("\n","\\0A").replace("\"","\\22");
    }

    public StringLiteral(StringConst str) {
        this.type = irPtrType;
        this.val = str.value;
        ++strNum;
        this.id = strNum;
        this.name = "@str."+this.id;
    }

    @Override
    public String toString() {
        return name;
    }
}
