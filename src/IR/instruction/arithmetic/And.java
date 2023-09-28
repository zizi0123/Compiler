package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class And extends ArithmeticIns {
    public And(Entity l,Entity r,Entity res){
        super(l,r,"and",res);
    }
}
