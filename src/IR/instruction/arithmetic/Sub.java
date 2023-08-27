package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Sub extends BinaryOperationIns {
    public Sub(Entity l,Entity r,Entity res){
        super(l,r,"sub",res);
    }
}
