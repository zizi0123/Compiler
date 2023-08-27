package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Add extends BinaryOperationIns {

    public Add(Entity l,Entity r,Entity res){
        super(l,r,"add",res);
    }
}
