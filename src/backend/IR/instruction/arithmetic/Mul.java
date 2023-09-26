package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Mul extends ArithmeticIns {
    public Mul(Entity l,Entity r,Entity res){
        super(l,r,"mul",res);
    }
}
