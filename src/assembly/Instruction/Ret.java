package assembly.Instruction;

import assembly.ASMFunction;
import assembly.ASMVisitor;
import assembly.operand.Reg;

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
        return new HashSet<>();
    }

    @Override
    public Reg getDef() {
        return null;
    }

    @Override
    public void replace(Reg olde, Reg newe) {
    }
}
