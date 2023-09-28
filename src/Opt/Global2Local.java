package Opt;

import IR.Entity.variable.GlobalVar;
import IR.Entity.variable.LocalVar;
import IR.IRFunction;
import IR.IRProgram;
import IR.instruction.AllocaIns;
import IR.instruction.StoreIns;

import java.util.HashMap;

public class Global2Local {
    IRProgram program;

    public Global2Local(IRProgram program) {
        this.program = program;
    }

    public void work() {
        HashMap<String, GlobalVar> newGV = new HashMap<>();
        for (var gv : program.globalVars.values()) {
            if (gv.InitFunc) {
                newGV.put(gv.name, gv);
            } else {
                IRFunction inFunc = null;
                boolean add = false;
                for (var func : program.functions.values()) {
                    boolean used = false;
                    for (var block : func.blocks) {
                        for (var ins : block.instructions) {
                            if (ins.getUse().contains(gv)) {
                                used = true;
                                break;
                            }
                        }
                        if (!used) {
                            for (var ins : block.phis) {
                                if (ins.getUse().contains(gv)) {
                                    used = true;
                                    break;
                                }
                            }
                        }
                        if (used) break;
                    }
                    if (used) {
                        if (inFunc == null) {
                            inFunc = func;
                        } else {
                            add = true;
                            break;
                        }
                    }
                }
                if (add) { //in >=2 func
                    newGV.put(gv.name, gv);
                } else if (inFunc != null) {//in 1 func ,
                    if (inFunc == program.initFunction || inFunc.irFuncName.equals("@main")) {//func is called only once
                        LocalVar lv = new LocalVar("%gv." + gv.name.substring(1), gv.type);
                        inFunc.localVars.put(lv.name, lv);
                        inFunc.entryBlock.instructions.add(0, new AllocaIns(lv));
                        inFunc.entryBlock.instructions.add(1, new StoreIns(gv.initVal, lv));
                        for (var block : inFunc.blocks) {
                            for (var ins : block.instructions) {
                                if (ins.getUse().contains(gv)) {
                                    ins.replace(gv, lv);
                                }
                            }
                            for (var ins : block.phis) {
                                if (ins.getUse().contains(gv)) {
                                    ins.replace(gv, lv);
                                }
                            }
                        }
                    } else {
                        newGV.put(gv.name, gv);
                    }
                }
            }
        }
        program.globalVars = newGV;
    }
}
