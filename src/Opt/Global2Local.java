package Opt;

import backend.IR.Entity.variable.GlobalVar;
import backend.IR.Entity.variable.LocalVar;
import backend.IR.IRFunction;
import backend.IR.IRProgram;
import backend.IR.instruction.AllocaIns;

import java.util.HashMap;

public class Global2Local {
    IRProgram program;

    public Global2Local(IRProgram program) {
        this.program = program;
    }

    public void work() {
        HashMap<String, GlobalVar> newGV = new HashMap<>();
        for (var gv : program.globalVars.values()) {
            if(gv.InitFunc){
                newGV.put(gv.name,gv);
            }else{
                IRFunction inFunc = null;
                boolean add = false;
                for(var func:program.functions.values()){
                    boolean used = false;
                    for(var block:func.blocks){
                        for(var ins:block.instructions){
                            if(ins.getUse().contains(gv)){
                                used = true;
                                break;
                            }
                        }
                        if(!used) {
                            for (var ins : block.phis) {
                                if (ins.getUse().contains(gv)) {
                                    used = true;
                                    break;
                                }
                            }
                        }
                        if(used) break;
                    }
                    if(used) {
                        if (inFunc == null) {
                            inFunc = func;
                        } else {
                            add = true;
                            break;
                        }
                    }
                }
                if(add){ //in >=2 func
                   newGV.put(gv.name,gv);
                }else{
                    if(inFunc!=null){//in 1 func
                        LocalVar lv = new LocalVar("%gv."+gv.name.substring(1),gv.type);
                        inFunc.entryBlock.instructions.add(0,new AllocaIns(lv));
                        for(var block:inFunc.blocks){
                            for(var ins:block.instructions){
                                ins.replace(gv,lv);
                            }
                            for(var ins:block.phis){
                                ins.replace(gv,lv);
                            }
                        }
                    }
                }
            }
        }
        program.globalVars = newGV;
    }
}
