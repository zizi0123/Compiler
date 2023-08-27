package IR.instruction.icmp;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;
import IR.type.IRType;

public class Sle extends BinaryOperationIns {
    public Sle(Entity l,Entity r,Entity res){
        super(l,r,"icmp sle",res);
    }
}
