package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

import java.util.HashSet;

public class ReturnIns extends Instruction {
    public IRType type;
    public Entity value;

    public ReturnIns(Entity val) {
        if (val != null) {
            this.type = val.type;
            this.value = val;
        }
    }

    @Override
    public String toString() {
        return "ret " + type.toString() + " " + value.toString() + '\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt
    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(value);
        return result;
    }

    @Override
    public Entity getDef() {
        return null;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        if (value.equals(olde)) {
            value = newe;
        }
    }
}