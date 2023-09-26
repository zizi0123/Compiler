package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Shl extends ArithmeticIns {
    public Shl(Entity l,Entity r,Entity res){
        super(l,r,"shl",res);
    }
}
