package util.type;

public interface ConstType {
    Type stringType = new Type("string",null);
    Type voidType = new Type("void",null);
    Type intType = new Type("int",null);
    Type boolType = new Type("bool",null);
    Type thisType = new Type("this",null);
    Type nullType = new Type("null",null);
    Type functionType = new Type("function",null);

}
