package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class And extends ArithmeticIns {
    public And(Entity l,Entity r,Entity res){
        super(l,r,"and",res);
    }
}
