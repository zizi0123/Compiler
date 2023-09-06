package IR.instruction;

import IR.IRVisitor;
import IR.type.IRType;
import IR.Entity.variable.LocalVar;
import IR.Entity.variable.RegVar;

public class AllocaIns extends Instruction {
    public IRType type;
    public String varName;


    public AllocaIns(LocalVar var) {
        this.type = var.type;
        this.varName = var.name;
    }

    public AllocaIns(RegVar var, IRType type) { //var:ptr regvar
        this.type = type;
        this.varName = var.name;
    }

    @Override
    public void Print() {
        System.out.println(varName + " = alloca " + type.toString());
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

}
