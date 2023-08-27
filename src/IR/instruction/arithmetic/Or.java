package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Or extends BinaryOperationIns {
    public Or(Entity l,Entity r,Entity res){
        super(l,r,"or",res);
    }
}
