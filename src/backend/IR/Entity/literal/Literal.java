package backend.IR.Entity.literal;

import backend.IR.Entity.Entity;
import backend.IR.type.IRBoolType;
import backend.IR.type.IRIntType;
import backend.IR.type.IRType;

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
