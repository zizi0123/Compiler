package IR.Entity.literal;

import static IR.type.IRTypes.*;

public class NullLiteral extends Literal{
    public NullLiteral(){
        this.type = irPtrType;
    }

    @Override
    public String toString() {
        return "null";
    }
}
