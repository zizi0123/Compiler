package irOpt;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.Entity.variable.LocalVar;
import IR.Entity.variable.RegVar;
import IR.IRProgram;
import IR.instruction.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Mem2Reg {

    IRProgram program;

    HashMap<LocalVar, ArrayList<BasicBlock>> allocDefBlocks = new HashMap<>();

    HashMap<LocalVar, Entity> curLocalVarDef = new HashMap<>();

    HashMap<Entity, Entity> curRegRename = new HashMap<>();

    public Mem2Reg(IRProgram program) {
        this.program = program;
    }

    public void Mem2RegOpt() {
        new CFGBuilder(program).build();
        new DominatorTreeBuilder(program).build();
        collectAlloca();
        for (var lv : allocDefBlocks.keySet()) {
            insertPhi(lv);
        }
        for (var func : program.functions.values()) {
            curRegRename.clear();
            reName(func.entryBlock);
        }
        for (var func : program.functions.values()) {
            curRegRename.clear();
            simplifyPhi(func.entryBlock);
        }
    }


    void collectAlloca() {
        for (var func : program.functions.values()) {
            for (var block : func.blocks) {
                for (var ins : block.instructions) {
                    if (ins instanceof AllocaIns allocaIns) {
                        allocDefBlocks.put(allocaIns.var, new ArrayList<>());
                    }
                }
            }
        }
        for (var func : program.functions.values()) {
            for (var block : func.blocks) {
                for (var ins : block.instructions) {
                    for (var use : ins.getUse()) {
                        if (use instanceof LocalVar && allocDefBlocks.containsKey(use)) {
                            if (ins instanceof StoreIns) {
                                allocDefBlocks.get(use).add(block);
                            } else if (!(ins instanceof LoadIns)) { //有除了store和load以外的其他use,则这个allca变量不能删去
                                allocDefBlocks.remove(use);
                            }
                        }
                    }
                }
            }
        }
    }

    void insertPhi(LocalVar var) {
        HashSet<BasicBlock> inserted = new HashSet<>();
        HashSet<BasicBlock> processed = new HashSet<>();

        ArrayList<BasicBlock> toProcess = new ArrayList<>(allocDefBlocks.get(var));
        while (!toProcess.isEmpty()) {
            BasicBlock target = toProcess.remove(0);
            for (BasicBlock domFrontier : target.domFrontier) {
                if (!inserted.contains(domFrontier)) {
                    domFrontier.phis.add(new PhiIns(var, new RegVar(var.type, var.name + "_phiValIn_" + domFrontier.label)));
                    inserted.add(domFrontier);
                }
                if (!processed.contains(domFrontier)) {
                    toProcess.add(domFrontier);
                }
            }
            processed.add(target);
        }
    }

    void reName(BasicBlock block) {
        var parentVar = new HashMap<>(curLocalVarDef);
        var parentReg = new HashMap<>(curRegRename);
        for (var phi : block.phis) {
            if (phi.srcVar != null) {
                curLocalVarDef.put(phi.srcVar, phi.result);
            }
        }
        ArrayList<Instruction> newInstructions = new ArrayList<>();
        for (var ins : block.instructions) {
            if (ins instanceof StoreIns storeIns && storeIns.ptr instanceof LocalVar lv && allocDefBlocks.containsKey(lv)) {
                Entity newVal = curRegRename.get(storeIns.val);
                if (newVal == null) {
                    curLocalVarDef.put(lv, storeIns.val);
                } else {
                    curLocalVarDef.put(lv, newVal);
                }
            } else if (ins instanceof LoadIns loadIns && loadIns.ptr instanceof LocalVar lv && allocDefBlocks.containsKey(lv)) {
                curRegRename.put(loadIns.value, curLocalVarDef.get(lv));
            } else if (!(ins instanceof AllocaIns)) {
                for (var use : ins.getUse()) {
                    if (curRegRename.containsKey(use)) {
                        ins.replace(use, curRegRename.get(use));
                    }
                }
                newInstructions.add(ins);
            }
        }
        block.instructions = newInstructions;
        for (var succ : block.succs) {
            for (var phi : succ.phis) {
                for (var use : phi.getUse()) {
                    if (curRegRename.containsKey(use)) {
                        phi.replace(use, curRegRename.get(use));
                    }
                }
                Entity varVal = curLocalVarDef.get(phi.srcVar);
                if (varVal != null && !varVal.equals(phi.result)) {
                    phi.addPair(varVal, block);
                }
            }
        }
        for (var child : block.domTreeSons) {
            reName(child);
        }
        curRegRename = parentReg;
        curLocalVarDef = parentVar;
    }

    void simplifyPhi(BasicBlock block) {
        var parentReg = new HashMap<>(curRegRename);
        ArrayList<PhiIns> newPhis = new ArrayList<>();
        for (var phi : block.phis) {
            if (phi.values.size() == 1) {
                curRegRename.put(phi.result, phi.values.get(0));
            } else {
                Entity result0 = phi.values.get(0);
                boolean same = true;
                for (int i = 1; i < phi.values.size(); ++i) {
                    if (!phi.values.get(i).equals(result0)) {
                        same = false;
                        break;
                    }
                }
                if (!same) {
                    newPhis.add(phi);
                } else {
                    curRegRename.put(phi.result, result0);
                }
            }
        }
        block.phis = newPhis;
        if (!curRegRename.isEmpty()) {
            for (var ins : block.instructions) {
                for (var use : ins.getUse()) {
                    if (curRegRename.containsKey(use)) {
                        ins.replace(use, curRegRename.get(use));
                    }
                }
            }
            for (var ins : block.phis) {
                for (var use : ins.getUse()) {
                    if (curRegRename.containsKey(use)) {
                        ins.replace(use, curRegRename.get(use));
                    }
                }
            }
            for (var succ : block.succs) {
                for (var phi : succ.phis) {
                    for (var use : phi.getUse()) {
                        if (curRegRename.containsKey(use)) {
                            phi.replace(use, curRegRename.get(use));
                        }
                    }
                }
            }
        }
        for (var child : block.domTreeSons) {
            simplifyPhi(child);
        }
        curRegRename = parentReg;
    }
}


