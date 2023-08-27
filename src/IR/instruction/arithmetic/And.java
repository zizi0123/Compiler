package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class And extends BinaryOperationIns {
    public And(Entity l,Entity r,Entity res){
        super(l,r,"and",res);
    }
}
