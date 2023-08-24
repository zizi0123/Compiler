package IR.literal;

import ast.util.constValue.IntConst;

import static IR.type.IRTypes.*;

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
    public String getName() {
        return null;
    }
}
