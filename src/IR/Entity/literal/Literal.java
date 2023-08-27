package IR.Entity.literal;

import IR.Entity.Entity;
import IR.type.IRBoolType;
import IR.type.IRIntType;
import IR.type.IRType;

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
