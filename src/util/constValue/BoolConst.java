package util.constValue;

import util.type.Type;

public class BoolConst extends ConstValue{
    public boolean value;
    @Override
    public Type GetType() {
        return boolType;
    }

    public BoolConst(boolean value){
        this.value = value;
    }
}
