package assembly.Instruction;

import IR.Entity.Entity;
import assembly.ASMVisitor;
import assembly.operand.Reg;

import java.util.HashSet;

public abstract class ASMIns {
    String comment;
    public abstract String toString();
    public abstract void accept(ASMVisitor visitor);
    public abstract HashSet<Reg> getUse();
    public abstract HashSet<Reg> getDef();

    public abstract void replace(Reg olde, Reg newe);


}
