package IR.instruction;

import IR.BasicBlock;
import IR.Entity.Entity;

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

    @Override
    public void Print() {
        if(condition == null){
            System.out.println("br label %"+trueBlock.label);
        }else{
            System.out.println("br i1 "+condition.toString()+", label %"+trueBlock.label+", label %"+falseBlock.label);
        }
    }
}
