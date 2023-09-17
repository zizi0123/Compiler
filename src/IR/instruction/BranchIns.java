package IR.instruction;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.IRVisitor;

import java.util.HashSet;

public class BranchIns extends Instruction {
    public Entity condition; //i1

    public BasicBlock trueBlock;

    public BasicBlock falseBlock;

    public IcmpIns icmpIns;//asm

    public BranchIns(BasicBlock jumpToBlock) {
        trueBlock = jumpToBlock;
    }

    public BranchIns(Entity condition, BasicBlock trueBlock, BasicBlock falseBlock) {
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    @Override
    public String toString() {
        if (condition == null) {
            return "br label %" + trueBlock.label + '\n';
        } else {
            return "br i1 " + condition.toString() + ", label %" + trueBlock.label + ", label %" + falseBlock.label + '\n';
        }
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    //opt

    @Override
    public HashSet<Entity> getUse() {
        HashSet<Entity> result = new HashSet<>();
        result.add(condition);
        return result;
    }

    @Override
    public Entity getDef() {
        return null;
    }

    @Override
    public void replace(Entity olde, Entity newe) {
        condition = condition.equals(olde) ? newe : condition;
    }

}
