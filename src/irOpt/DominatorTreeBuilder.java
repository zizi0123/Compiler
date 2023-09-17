package irOpt;

import IR.BasicBlock;
import IR.IRFunction;
import IR.IRProgram;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class DominatorTreeBuilder {
    IRProgram program;
    Queue<BasicBlock> bfsQueue = new LinkedList<>();

    public DominatorTreeBuilder(IRProgram program) {
        this.program = program;
    }

    public void build() {
        for (var func : program.functions.values()) {
            visit((func));
        }
    }

    public void visit(IRFunction func) {
        for (var block : func.blocks) {
            block.dom.addAll(func.blocks);
        }
        bfsQueue.add(func.entryBlock);
        BFS();
        for (var block : func.blocks) {
            for (var domBlock : block.dom) {
                if (domBlock.dom.size() == block.dom.size() - 1) {
                    block.domTreeDad = domBlock;
                    domBlock.domTreeSons.add(block);
                    break;
                }
            }
        }
        for (var block : func.blocks) {
            HashSet<BasicBlock> result = new HashSet<>();
            HashSet<BasicBlock> set1 = new HashSet<>(block.dom);
            set1.remove(block);
            for (var preBlock : block.preds) {
                HashSet<BasicBlock> set2 = new HashSet<>(preBlock.dom);
                set2.removeAll(set1);
                result.addAll(set2);
            }
            for (var domBlock : result) {
                domBlock.domFrontier.add(block);
            }
        }
    }

    void BFS() {
        while(!bfsQueue.isEmpty()) {
            BasicBlock block = bfsQueue.remove();
            HashSet<BasicBlock> union = new HashSet<>();
            if (!block.preds.isEmpty()) {
                union.addAll(block.preds.get(0).dom);
                for (int i = 1; i < block.preds.size(); ++i) {
                    union.retainAll(block.preds.get(i).dom);
                }
            }
            union.add(block);
            if (block.dom.size() == union.size() && !block.dom.retainAll(union)) {
                continue;
            }
            block.dom = union;
            bfsQueue.addAll(block.succs);
        }
    }
}
