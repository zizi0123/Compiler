package IR.Entity.literal;

import ast.util.constValue.BoolConst;

import static IR.type.IRTypes.*;

public class BoolLiteral extends Literal {
    public boolean value;

    public BoolLiteral(BoolConst a) {
        this.value = a.value;
        this.type = irBoolType;
    }

    public BoolLiteral(boolean value) {
        this.value = value;
        this.type = irBoolType;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
