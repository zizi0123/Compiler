package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Sub extends ArithmeticIns {
    public Sub(Entity l,Entity r,Entity res){
        super(l,r,"sub",res);
    }
}
