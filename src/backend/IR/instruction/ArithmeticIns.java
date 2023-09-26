package backend.IR.instruction;

import backend.IR.Entity.Entity;
import backend.IR.IRVisitor;
import backend.IR.type.IRType;

import java.util.HashSet;

public abstract class ArithmeticIns extends Instruction {
    public Entity result;
    public String operator;

    public IRType type;
    public Entity operand1;
    public Entity operand2;

    public ArithmeticIns(Entity op1, Entity op2, String op, Entity result) {
        operand1 = op1;
        operand2 = op2;
        operator = op;
        this.type = op1.type;
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString() + " = " + operator + " " + type.toString() + " " + operand1.toString() + ", " + operand2.toString() + '\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt
    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(operand1);
        result.add(operand2);
        return result;
    }

    @Override
    public Entity getDef() {
        return result;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        operand1 = operand1.equals(olde) ? newe : operand1;
        operand2 = operand2.equals(olde) ? newe : operand2;
    }

}
