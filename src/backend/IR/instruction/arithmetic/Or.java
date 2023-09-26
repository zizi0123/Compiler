package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Or extends ArithmeticIns {
    public Or(Entity l,Entity r,Entity res){
        super(l,r,"or",res);
    }
}
