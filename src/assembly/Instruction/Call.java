package assembly.Instruction;

import IR.instruction.CallIns;

public class Call extends ASMIns{
    String funcName;

    public Call(String f){
        funcName = f;
    }

    @Override
    public String toString() {
        return "call  "+funcName+'\n';
    }
}
