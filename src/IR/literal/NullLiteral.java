package IR.literal;
import IR.type.IRPtrType;

import static IR.type.IRTypes.*;

public class NullLiteral extends Literal{
    public NullLiteral(){
        this.type = irPtrType;
    }

    @Override
    public String getName() {
        return null;
    }
}
