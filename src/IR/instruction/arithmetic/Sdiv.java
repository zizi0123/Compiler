package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.ArithmeticIns;

public class Sdiv extends ArithmeticIns {
    public Sdiv(Entity l,Entity r,Entity res){
        super(l,r,"sdiv",res);
    }
}
