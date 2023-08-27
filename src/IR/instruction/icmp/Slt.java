package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;
import IR.type.IRType;

public class Slt extends BinaryOperationIns {
    public Slt(Entity l,Entity r,Entity res){
        super(l,r,"icmp slt",res);
    }
}
