package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

public abstract class IcmpIns extends Instruction {
    public Entity result;
    public String operator;

    public IRType type;
    public Entity operand1;
    public Entity operand2;

    public boolean forBranch; //result is the condition of it;

    public IcmpIns(Entity op1, Entity op2, String op, Entity result){
        operand1 = op1;
        operand2 = op2;
        operator = op;
        this.type = op1.type;
        this.result = result;
        forBranch = false;
    }

    @Override
    public String toString(){
        return result.toString()+" = "+operator+" "+type.toString()+" "+operand1.toString()+", "+operand2.toString()+'\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
