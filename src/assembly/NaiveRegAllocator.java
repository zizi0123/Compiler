package assembly;

import assembly.Instruction.*;
import assembly.operand.Reg;
import assembly.operand.StackOffset;
import assembly.operand.StackVal;
import assembly.operand.VirtualReg;
import com.sun.source.tree.NewClassTree;

import java.util.ArrayList;
import java.util.HashMap;

public class NaiveRegAllocator implements ASMVisitor {

    HashMap<VirtualReg, StackVal> vreg2Stack = new HashMap<>();

    protected ASMFunction currentFunction;
    protected ASMBlock currentBlock;
    ValueAllocator valueAllocator;

    public NaiveRegAllocator(ValueAllocator valueAllocator) {
        this.valueAllocator = valueAllocator;
    }

    StackVal getStack(VirtualReg vreg) {
        if (vreg2Stack.containsKey(vreg)) {
            return vreg2Stack.get(vreg);
        }
        StackVal stackVal = new StackVal();
        currentFunction.stack.add(stackVal);
        vreg2Stack.put(vreg, stackVal);
        return stackVal;
    }

    Reg loadInt1(VirtualReg vreg) { //把值从vreg对应的栈中load出来，放入一个临时寄存器t1
        StackVal stackVal = getStack(vreg);
        Reg rd = valueAllocator.getPReg("t1");
        Reg rs = valueAllocator.getPReg("sp");
        if (vreg.size == 1) {
            currentBlock.addIns(new Lb(rd, rs, new StackOffset(stackVal), "load in t1\n"));
        } else {
            currentBlock.addIns(new Lw(rd, rs, new StackOffset(stackVal), "load in t1\n"));
        }
        return rd;
    }

    Reg loadInt2(VirtualReg vreg) { //把值从vreg对应的栈中load出来，放入一个临时寄存器t2
        StackVal stackVal = getStack(vreg);
        Reg rd = valueAllocator.getPReg("t2");
        Reg rs = valueAllocator.getPReg("sp");
        if (vreg.size == 1) {
            currentBlock.addIns(new Lb(rd, rs, new StackOffset(stackVal), "load in t1\n"));
        } else {
            currentBlock.addIns(new Lw(rd, rs, new StackOffset(stackVal), "load in t1\n"));
        }
        return rd;
    }

    Reg storeFromt0(VirtualReg vreg) {
        Reg rs = valueAllocator.getPReg("t0");
        Reg rd = valueAllocator.getPReg("sp");
        StackVal stackVal = getStack(vreg);
        if (vreg.size == 1) {
            currentBlock.addIns(new Sb(rs, rd, new StackOffset(stackVal), "store from t0\n"));
        } else {
            currentBlock.addIns(new Sw(rs, rd, new StackOffset(stackVal), "store from t0\n"));
        }
        return rs;
    }



    @Override
    public void visit(Module node) {
    }

    @Override
    public void visit(ASMFunction node) {
        currentFunction = node;
        for (var block : node.blocks) {
            currentBlock = block;
            visit(block);
        }
    }

    @Override
    public void visit(ASMBlock node) {
        ArrayList<ASMIns> newInstructions = new ArrayList<>();
        newInstructions.addAll(node.instructions);
        if(!node.exitInses.isEmpty()){
            newInstructions.addAll(node.exitInses);
        }
        node.instructions.clear();
        node.exitInses.clear();
        for (var ins : newInstructions) {
            ins.accept(this);
        }
    }

    @Override
    public void visit(ASMarithmetic node) {
        if (node.rs1 instanceof VirtualReg v1) {
            node.rs1 = loadInt1(v1);
        }
        if (node.rs2 instanceof VirtualReg v2) {
            node.rs2 = loadInt2(v2);
        }
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Branch node) {
        if (node.rs1 instanceof VirtualReg v1) {
            node.rs1 = loadInt1(v1);
        }
        if (node.rs2 instanceof VirtualReg v2) {
            node.rs2 = loadInt2(v2);
        }
        currentBlock.instructions.add(node);
    }

    @Override
    public void visit(Call node) {
        currentBlock.instructions.add(node);
    }

    @Override
    public void visit(J node) {
        currentBlock.instructions.add(node);
    }

    @Override
    public void visit(La node) {
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Lb node) {
        if(node.rs!=null && node.rs instanceof VirtualReg v1){
            node.rs = loadInt1(v1);
        }
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Li node) {
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Lw node) {
        if(node.rs!=null && node.rs instanceof VirtualReg v1){
            node.rs = loadInt1(v1);
        }
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Mv node) {
        if(node.rs instanceof VirtualReg v1){
            node.rs = loadInt1(v1);
        }
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Ret node) {
        currentBlock.instructions.add(node);
    }

    @Override
    public void visit(Sb node) {
        if(node.rs instanceof VirtualReg v1){
            node.rs = loadInt1(v1);
        }
        if(node.rd instanceof VirtualReg v2){
            node.rd = loadInt2(v2);
        }
        currentBlock.instructions.add(node);
    }

    @Override
    public void visit(Slt node) {
        if (node.rs1 instanceof VirtualReg v1) {
            node.rs1 = loadInt1(v1);
        }
        if (node.rs2 instanceof VirtualReg v2) {
            node.rs2 = loadInt2(v2);
        }
        currentBlock.instructions.add(node);
        if (node.rd instanceof VirtualReg vd) {
            node.rd = storeFromt0(vd);
        }
    }

    @Override
    public void visit(Sw node) {
        if(node.rs instanceof VirtualReg v1){
            node.rs = loadInt1(v1);
        }
        if(node.rd instanceof VirtualReg v2){
            node.rd = loadInt2(v2);
        }
        currentBlock.instructions.add(node);
    }
}
