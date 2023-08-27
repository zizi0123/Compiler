package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Xor extends BinaryOperationIns {
    public Xor(Entity l,Entity r,Entity res){
        super(l,r,"xor",res);
    }
}
