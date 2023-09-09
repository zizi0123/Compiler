package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

public class StoreIns extends Instruction {
    public Entity val;  //可能是一个寄存器变量，也可能是一个字面量
    public String ptrName;

    public IRType type;

    public StoreIns(Entity val, String ptrName) {
        this.val = val;
        this.ptrName = ptrName;
        this.type = val.type;
    }

    @Override
    public String toString() {
        return "store " + type.toString() + " " + val.toString() + ", ptr " + ptrName + '\n';
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
