package assembly.Instruction;

import assembly.ASMFunction;
import assembly.ASMVisitor;
import assembly.operand.GlobalVal;
import assembly.operand.Reg;

public class Ret extends ASMIns {
    ASMFunction func;

    public Ret(ASMFunction func) {
        this.func = func;
    }

    @Override
    public String toString() {
        if (func.stack.isEmpty()) {
            return "ret\n";
        }
        int length = (func.stack.size()+3)/4*16;
        return "addi  sp, sp, " + length + "\n\tret\n";
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
