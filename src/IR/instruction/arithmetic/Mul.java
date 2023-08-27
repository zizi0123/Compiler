package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Mul extends BinaryOperationIns {
    public Mul(Entity l,Entity r,Entity res){
        super(l,r,"mul",res);
    }
}
