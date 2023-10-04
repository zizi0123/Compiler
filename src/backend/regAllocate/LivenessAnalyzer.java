package backend.regAllocate;

import assembly.ASMBlock;
import assembly.ASMFunction;

import java.util.HashSet;
import java.util.Stack;

public class LivenessAnalyzer {
    public ASMFunction function;

    public LivenessAnalyzer(ASMFunction f){
        function = f;
    }

    public void work(){ //按照CFG逆向收集每个块的活跃变量
        Stack<ASMBlock> toProcess = new Stack<>();
        toProcess.add(function.exitBlock);
        boolean change = true;
        while(change) {
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
                top.in.addAll(top.out);
                var tmp = new HashSet<>(top.out);
                tmp.removeAll(top.def);
                top.in.addAll(tmp);
                if (!top.in.equals(oldIn) || !top.out.equals(oldOut)) {
                    change = true;
                    toProcess.addAll(top.preds);
                }
            }
        }
    }
}