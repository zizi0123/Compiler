package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;
import IR.type.IRType;

public class Ne extends BinaryOperationIns {
    public Ne(Entity l,Entity r,Entity res){
        super(l,r,"icmp ne",res);
    }
}
