package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Sub extends ArithmeticIns {
    public Sub(Entity l,Entity r,Entity res){
        super(l,r,"sub",res);
    }
}
