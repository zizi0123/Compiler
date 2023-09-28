package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Shl extends ArithmeticIns {
    public Shl(Entity l,Entity r,Entity res){
        super(l,r,"shl",res);
    }
}
