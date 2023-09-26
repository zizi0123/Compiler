package backend.IR.Entity.literal;

import static backend.IR.type.IRTypes.irVoidType;

public class voidLiteral extends Literal {
    public voidLiteral(){
        type = irVoidType;
    }

    @Override
    public String toString() {
        return "";
    }
}
