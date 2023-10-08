package backend.regAllocate;

import assembly.ASMBlock;
import assembly.ASMFunction;
import assembly.Instruction.ASMIns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class LivenessAnalyzer {
    public ASMFunction function;

    public LivenessAnalyzer(ASMFunction f) {
        function = f;
    }

    public void work() { //按照CFG逆向收集每个块的活跃变量
        for (ASMBlock block : function.blocks) {
            block.use.clear();
            block.def.clear();
            block.in.clear();
            block.out.clear();
            block.processed = false;
            ArrayList<ASMIns> inses = new ArrayList<>(block.instructions);
            inses.addAll(block.exitInses);
            for (var inst : inses) {
                for(var use:inst.getUse()){
                    if(!block.def.contains(use)){
                        block.use.add(use);
                    }
                }
                for(var def:inst.getDef()){
                    block.def.add(def);
                }
            }
        }
        Stack<ASMBlock> toProcess = new Stack<>();
        toProcess.add(function.exitBlock);
        boolean change = true;
        while (change) {
            change = false;
            while (!toProcess.isEmpty()) {
                ASMBlock top = toProcess.pop();
                var oldIn = new HashSet<>(top.in);
                var oldOut = new HashSet<>(top.out);
                top.in.clear();
                top.out.clear();
                for (var s : top.succs) {
                    top.out.addAll(s.in);
                }
                top.in.addAll(top.use);
                var tmp = new HashSet<>(top.out);
                tmp.removeAll(top.def);
                top.in.addAll(tmp);
                if (!top.processed || !top.in.equals(oldIn) || !top.out.equals(oldOut)) {
                    change = true;
                    top.processed = true;
                    toProcess.addAll(top.preds);
                }
            }
        }
    }
}
