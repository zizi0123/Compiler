package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

import java.util.HashSet;

public class StoreIns extends Instruction {
    public Entity val;  //可能是一个寄存器变量，也可能是一个字面量
    public Entity ptr;

    public IRType type;

    public StoreIns(Entity val, Entity ptr) {
        this.val = val;
        this.ptr = ptr;
        this.type = val.type;
    }

    @Override
    public String toString() {
        return "store " + type.toString() + " " + val.toString() + ", ptr " + ptr + '\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }


    //opt
    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(val);
        result.add(ptr);
        return result;
    }

    @Override
    public Entity getDef() {
        return null;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        if (val.equals(olde)) {
            val = newe;
        }
        if (ptr.equals(olde)) {
            ptr = newe;
        }
    }
}
