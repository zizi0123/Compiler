package util.constValue;


import util.type.Type;

public class StringConst extends ConstValue{
    String value;
    public Type GetType() {
        return stringType;
    }

    public StringConst(String val){
        this.value = val;
    }
}
