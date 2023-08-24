package IR.instruction.arithmetic;

import IR.Entity;
import IR.instruction.Instruction;
import ast.expr.PreOpExprNode;

public class Xor extends Instruction {
    public Entity lhs;

    public Entity rhs;

    public Entity result;

    public Xor(Entity l,Entity r,Entity res){
        lhs = l;
        rhs = r;
        result = res;
    }

}
