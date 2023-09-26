package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Ashr extends ArithmeticIns {
    public Ashr(Entity l,Entity r,Entity res){
        super(l,r,"ashr",res);
    }
}
