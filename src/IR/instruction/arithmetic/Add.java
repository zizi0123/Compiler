package IR.instruction.arithmetic;

import IR.Entity;
import IR.instruction.Instruction;

public class Add extends Instruction {
    public Entity lhs;

    public Entity rhs;

    public Entity result;

    public Add(Entity l,Entity r,Entity res){
        lhs = l;
        rhs = r;
        result = res;
    }
}
