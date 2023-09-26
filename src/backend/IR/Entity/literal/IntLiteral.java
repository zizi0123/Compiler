package backend.IR.Entity.literal;

import frontend.ast.util.constValue.IntConst;

import static backend.IR.type.IRTypes.*;

public class IntLiteral extends Literal {
    public int value;

    public IntLiteral(IntConst a) {
        this.value = a.value;
        this.type = irIntType;
    }

    public IntLiteral(int value) {
        this.value = value;
        this.type = irIntType;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
