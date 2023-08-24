package IR.literal;

import IR.type.IRType;
import ast.util.constValue.BoolConst;

import static IR.type.IRTypes.*;

public class BoolLiteral extends Literal{
    boolean value;

    public BoolLiteral(BoolConst a){
        this.value = a.value;
        this.type = irBoolType;
    }

    public BoolLiteral(boolean value){
        this.value = value;
        this.type = irBoolType;
    }

    @Override
    public String getName() {
        return null;
    }


}
