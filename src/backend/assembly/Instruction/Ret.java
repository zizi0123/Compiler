package backend.assembly.Instruction;

import backend.assembly.ASMFunction;
import backend.assembly.ASMVisitor;

public class Ret extends ASMIns {
    ASMFunction func;

    public Ret(ASMFunction func) {
        this.func = func;
    }

    @Override
    public String toString() {
        String result = "";
        if (func.raStack != null) {
            result = "lw  ra, " + func.raStack.offset + "(sp)\n";
        }
        if (func.stack.isEmpty()) {
            if(result.isEmpty()){
                return "ret\n";
            }else{
                return result + "\tret\n";
            }
        }
        int length = (func.stack.size() + 3) / 4 * 16;
        if(result.isEmpty()){
            return "addi  sp, sp, " + length + "\n\tret\n";
        }else{
            return result + "\taddi  sp, sp, " + length + "\n\tret\n";
        }
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
