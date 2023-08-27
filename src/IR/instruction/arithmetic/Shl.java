package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Shl extends BinaryOperationIns {
    public Shl(Entity l,Entity r,Entity res){
        super(l,r,"shl",res);
    }
}
