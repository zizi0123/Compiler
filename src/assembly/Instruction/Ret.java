package assembly.Instruction;

import assembly.ASMFunction;
import assembly.ASMVisitor;
import assembly.operand.Reg;

public class Ret extends ASMIns{
    ASMFunction func;

    public Ret(ASMFunction func){
        this.func = func;
    }

    @Override
    public String toString() {
        return  "addi  sp, sp, "+func.stack.size()*4+"\nret\n";
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
