package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Xor extends ArithmeticIns {
    public Xor(Entity l,Entity r,Entity res){
        super(l,r,"xor",res);
    }
}
