package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Add extends ArithmeticIns {

    public Add(Entity l,Entity r,Entity res){
        super(l,r,"add",res);
    }
}
