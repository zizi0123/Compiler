package assembly.Instruction;

import assembly.ASMVisitor;
import assembly.operand.Reg;
import backend.RegManager;

import java.util.Arrays;
import java.util.HashSet;

public class Call extends ASMIns {
    String funcName;
    int argNum;

    public Call(String f, int n) {
        funcName = f;
        argNum = n;
    }

    @Override
    public String toString() {
        return "call  " + funcName + '\n';
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Reg> getUse() {
        int num = 8;
        if (argNum < 8) {
            num = argNum;
        }
        return new HashSet<Reg>(Arrays.asList(RegManager.regs).subList(10, num + 10));
    }

    @Override
    public HashSet<Reg> getDef() {
        return new HashSet<>(RegManager.callerSaveRegs); //在函数调用过程中，callerSave regs 可能会被改变
    }

    @Override
    public void replace(Reg olde, Reg newe) {
    }
}
