package Opt;

import backend.IR.IRProgram;

public class irOptimizer {
    IRProgram program;

    public irOptimizer(IRProgram program){
       this.program = program;
    }

    public void work(){
        new CFGBuilder(program).build();
        new DominatorTreeBuilder(program).build();
        new Mem2Reg(program).Mem2RegOpt();
    }

}
