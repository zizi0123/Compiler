package IR.type;

import ast.util.type.ASTType;
import ast.util.type.TypeName;

import static IR.type.IRTypes.*;

public abstract class IRType {
    public String typeName;
    public int bitSize;
    public String toString(){
        return typeName;
    };

    public IRType(String typeName, int bitSize) {
        this.typeName = typeName;
        this.bitSize = bitSize;
    }

    public static IRType toIRType(ASTType type) {
        if (type.isArray || type.typeName.isClass || type.typeName.typeName.equals("string") || type.isThis || type.isNull) {
            return irPtrType;
        }
        if (type.typeName.typeName.equals("int")) return irIntType;
        if (type.typeName.typeName.equals("bool")) return irBoolType;
        if (type.isVoid) return irVoidType;
        throw new RuntimeException();
    }


    public static IRType toIRType(TypeName typeName) {
        if (typeName.typeName.equals("int")) {
            return irIntType;
        }
        if (typeName.typeName.equals("bool")) {
            return irBoolType;
        }
        return irPtrType;
    }

}
