package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Or extends ArithmeticIns {
    public Or(Entity l,Entity r,Entity res){
        super(l,r,"or",res);
    }
}
