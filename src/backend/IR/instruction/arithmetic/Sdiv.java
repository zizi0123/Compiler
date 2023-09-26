package backend.IR.instruction.arithmetic;

import backend.IR.Entity.Entity;
import backend.IR.instruction.ArithmeticIns;

public class Sdiv extends ArithmeticIns {
    public Sdiv(Entity l,Entity r,Entity res){
        super(l,r,"sdiv",res);
    }
}
