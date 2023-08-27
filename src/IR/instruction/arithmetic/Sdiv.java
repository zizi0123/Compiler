package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Sdiv extends BinaryOperationIns {
    public Sdiv(Entity l,Entity r,Entity res){
        super(l,r,"sdiv",res);
    }
}
