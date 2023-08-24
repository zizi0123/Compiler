package IR.instruction.arithmetic;

import IR.Entity;
import IR.instruction.Instruction;

public class And extends Instruction {
    public Entity lhs;

    public Entity rhs;

    public Entity result;

    public And(Entity l,Entity r,Entity res){
        lhs = l;
        rhs = r;
        result = res;
    }
}
