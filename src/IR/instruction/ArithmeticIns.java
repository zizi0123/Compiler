package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

public abstract class ArithmeticIns extends Instruction {
    public Entity result;
    public String operator;

    public IRType type;
    public Entity operand1;
    public Entity operand2;

    public ArithmeticIns(Entity op1, Entity op2, String op, Entity result){
        operand1 = op1;
        operand2 = op2;
        operator = op;
        this.type = op1.type;
        this.result = result;
    }

    @Override
    public void Print() {
        System.out.println(result.toString()+" = "+operator+" "+type.toString()+" "+operand1.toString()+", "+operand2.toString());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
