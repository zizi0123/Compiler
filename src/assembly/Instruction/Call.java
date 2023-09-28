package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> getUse() {
        return new HashSet<>();
    }

    @Override
    public Reg getDef() {
        return null;
    }
}
