package Opt;

import IR.IRProgram;

public class irOptimizer {
    IRProgram program;

    public irOptimizer(IRProgram program){
       this.program = program;
    }

    public void work(){
        new Global2Local(program).work();
        new CFGBuilder(program).build();
        new DominatorTreeBuilder(program).build();
        new Mem2Reg(program).Mem2RegOpt();
    }

}
