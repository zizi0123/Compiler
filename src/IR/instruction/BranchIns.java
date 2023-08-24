package IR.instruction;

import IR.BasicBlock;
import IR.Entity;

public class BranchIns extends Instruction {
    public Entity condition; //i1

    public BasicBlock trueBlock;

    public BasicBlock falseBlock;

    public BranchIns(BasicBlock jumpToBlock) {
        trueBlock = jumpToBlock;
    }

    public BranchIns(Entity condition, BasicBlock trueBlock, BasicBlock falseBlock) {
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }
}
