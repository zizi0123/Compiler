package assembly.Instruction;

import IR.instruction.CallIns;
import assembly.ASMVisitor;

public class Call extends ASMIns{
    String funcName;

    public Call(String f){
        funcName = f;
    }

    @Override
    public String toString() {
        return "call  "+funcName+'\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
