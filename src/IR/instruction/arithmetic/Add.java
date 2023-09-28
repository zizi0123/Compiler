package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Add extends ArithmeticIns {

    public Add(Entity l,Entity r,Entity res){
        super(l,r,"add",res);
    }
}
