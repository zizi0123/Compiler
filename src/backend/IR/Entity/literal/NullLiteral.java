package backend.IR.Entity.literal;

import static backend.IR.type.IRTypes.*;

public class NullLiteral extends Literal{
    public NullLiteral(){
        this.type = irPtrType;
    }

    @Override
    public String toString() {
        return "null";
    }
}
