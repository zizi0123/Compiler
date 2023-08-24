package IR.instruction.arithmetic;

import IR.Entity;
import IR.instruction.Instruction;

public class Mul extends Instruction {
    public Entity lhs;

    public Entity rhs;

    public Entity result;

    public Mul(Entity l,Entity r,Entity res){
        lhs = l;
        rhs = r;
        result = res;
    }
}
