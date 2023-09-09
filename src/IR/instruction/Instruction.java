package IR.instruction;

import IR.IRVisitor;
import ast.ASTVisitor;

public abstract class Instruction {
    public abstract String toString();

    public abstract void accept(IRVisitor visitor);
}
