package IR.instruction.arithmetic;

import IR.Entity.Entity;
import IR.instruction.BinaryOperationIns;
import IR.instruction.Instruction;

public class Srem extends BinaryOperationIns {
    public Srem(Entity l,Entity r,Entity res){
        super(l,r,"srem",res);
    }
}
