package backend.IR.instruction;

import backend.IR.Entity.Entity;
import backend.IR.IRVisitor;
import backend.IR.type.IRType;
import backend.IR.Entity.variable.LocalVar;

import java.util.HashSet;

public class AllocaIns extends Instruction {
    public IRType type;
    public LocalVar var;


    public AllocaIns(LocalVar var) {
        this.type = var.type;
        this.var = var;
    }

    @Override
    public String toString() {
        return var.name + " = alloca " + type.toString()+"\n";
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Entity> getUse() {
        return new HashSet<>();
    }

    @Override
    public Entity getDef() {
        return var;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
    }

}
