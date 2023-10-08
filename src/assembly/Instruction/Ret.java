package assembly.Instruction;

import assembly.ASMFunction;
import assembly.ASMVisitor;
import assembly.operand.Reg;
import backend.RegManager;

import java.util.HashSet;

public class Ret extends ASMIns {

    public Ret() {
    }

    @Override
    public String toString() {
        return "ret\n";
    }

    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HashSet<Reg> getUse() {
        HashSet<Reg> result = new HashSet<>();
        result.add(RegManager.regs[10]);
        return result;
    }

    @Override
    public HashSet<Reg> getDef() {
        return new HashSet<>();
    }

    @Override
    public void replace(Reg olde, Reg newe) {
    }
}
