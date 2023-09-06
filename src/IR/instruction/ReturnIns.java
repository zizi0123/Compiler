package IR.instruction;

import IR.Entity.Entity;
import IR.IRVisitor;
import IR.type.IRType;

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
    public void Print() {
        System.out.println("ret " + type.toString() + " " + value.toString());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
