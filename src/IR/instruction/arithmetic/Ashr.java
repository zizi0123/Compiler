package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Ashr extends ArithmeticIns {
    public Ashr(Entity l,Entity r,Entity res){
        super(l,r,"ashr",res);
    }
}
