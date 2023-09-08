package assembly.Instruction;

import assembly.ASMVisitor;

public abstract class ASMIns {
    String comment;

    public abstract String toString();

    public abstract void accept(ASMVisitor visitor);
}
