package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Mul extends ArithmeticIns {
    public Mul(Entity l,Entity r,Entity res){
        super(l,r,"mul",res);
    }
}
