package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Ashr extends BinaryOperationIns {
    public Ashr(Entity l,Entity r,Entity res){
        super(l,r,"ashr",res);
    }
}
