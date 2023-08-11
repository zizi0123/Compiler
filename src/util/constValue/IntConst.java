package util.constValue;


import util.type.Type;

public class IntConst extends ConstValue{
    public int value;
    public Type GetType() {
        return stringType;
    }

    public IntConst(int val){
        this.value = val;
    }
}
