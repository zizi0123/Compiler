package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Xor extends ArithmeticIns {
    public Xor(Entity l,Entity r,Entity res){
        super(l,r,"xor",res);
    }
}
