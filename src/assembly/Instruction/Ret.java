package assembly.Instruction;

import assembly.ASMFunction;
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
}
