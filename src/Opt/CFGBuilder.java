package Opt;

import IR.BasicBlock;
import IR.IRFunction;
import IR.IRProgram;
import IR.instruction.BranchIns;

public class CFGBuilder {
    IRProgram program;

    public CFGBuilder(IRProgram program) {
        this.program = program;
    }

    void build() {
        for (var func : program.functions.values()) {
            visit(func);
        }
    }

    void visit(IRFunction function) {
        for (var block : function.blocks) {
            visit(block);
        }
    }

    void visit(BasicBlock block) {
        if (block.exitInstruction instanceof BranchIns branchIns) {
            block.succs.add(branchIns.trueBlock);
            branchIns.trueBlock.preds.add(block);
            if (branchIns.falseBlock != null) {
                block.succs.add(branchIns.falseBlock);
                branchIns.falseBlock.preds.add(block);
            }
        }
    }
}
