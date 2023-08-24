package IR.literal;

import IR.Entity;
import IR.type.IRBoolType;
import IR.type.IRIntType;
import IR.type.IRType;
import ast.util.constValue.BoolConst;
import ast.util.constValue.ConstValue;
import ast.util.constValue.IntConst;
import ast.util.constValue.Null;

public abstract class Literal extends Entity {


    public static Literal defaultVal(IRType type){
        if(type instanceof IRIntType){
            return new IntLiteral(0);
        }else if (type instanceof IRBoolType){
            return new BoolLiteral(false);
        }else{
            return new NullLiteral();
        }
    }
}
