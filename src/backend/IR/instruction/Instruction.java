package backend.IR.instruction;

import backend.IR.Entity.Entity;
import backend.IR.IRVisitor;

import java.util.HashSet;

public abstract class Instruction {
    public abstract String toString();
    public abstract void accept(IRVisitor visitor);

    //optimize

    public abstract HashSet<Entity> getUse();

    public abstract Entity getDef();

    public abstract void replace(Entity olde,Entity newe);

}
