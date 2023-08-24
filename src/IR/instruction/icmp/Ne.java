package IR.instruction.icmp;

import IR.Entity;
import IR.instruction.Instruction;
import IR.type.IRType;

public class Ne extends Instruction {
    IRType type;

    Entity lhs;

    Entity rhs;

    Entity result;

    public Ne(Entity l,Entity r,Entity res){
        type = l.type;
        lhs = l;
        rhs = r;
        result = res;
    }
}
