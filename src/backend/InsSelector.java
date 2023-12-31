package backend;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.Entity.literal.*;
import IR.Entity.variable.RegVar;
import IR.IRFunction;
import IR.IRProgram;
import IR.IRVisitor;
import IR.instruction.*;
import assembly.*;
import assembly.Instruction.*;
import assembly.Module;
import assembly.operand.*;
import backend.regAllocate.RegAllocater;

import java.util.ArrayList;
import java.util.HashMap;


import static IR.type.IRTypes.irBoolType;
import static IR.type.IRTypes.irVoidType;
import static java.lang.Math.max;


public class InsSelector implements IRVisitor {

    Module currentModule;
    ASMFunction currentFunction;
    ASMBlock currentBlock;
    RegManager regManager;
    HashMap<String, ASMBlock> intermediateBlocks = new HashMap<>();


    public InsSelector(Module module) {
        this.currentModule = module;
        regManager = new RegManager();
    }

    void getOffset(ASMFunction func) { //确定栈中每个元素的位置
        for (int i = 0; i < func.stack.size(); ++i) {
            func.stack.get(i).offset = i * 4;
        }
        for (int i = 0; i < func.params.size(); ++i) {
            func.params.get(i).offset = (i + func.stack.size()) * 4;
        }
    }

    public Val getInitial(Literal literal) {
        if (literal instanceof BoolLiteral boolLiteral) {
            return new Imm(boolLiteral.value ? 1 : 0);
        }
        if (literal instanceof IntLiteral intLiteral) {
            return new Imm(intLiteral.value);
        }
        if (literal instanceof StringLiteral stringLiteral) {
            return currentModule.globalStrings.get(stringLiteral.name);
        }
        return new Imm(0);
    }

    Reg getReg(Entity entity) {
        if (entity instanceof RegVar regVar) {
            return regManager.getAsmReg(regVar.name);
        }
        if (entity instanceof BoolLiteral boolLiteral) {
            if (!boolLiteral.value) return regManager.getPReg("zero");
            Li li = new Li(regManager.getNewVirtualReg(), 1);
            currentBlock.addIns(li);
            return li.rd;
        }
        if (entity instanceof IntLiteral intLiteral) {
            if (intLiteral.value == 0) return regManager.getPReg("zero");
            Li li = new Li(regManager.getNewVirtualReg(), intLiteral.value);
            currentBlock.addIns(li);
            return li.rd;
        }
        if (entity instanceof StringLiteral stringLiteral) {
            La la = new La(regManager.getNewVirtualReg(), currentModule.globalStrings.get(stringLiteral.name));
            currentBlock.addIns(la);
            return la.rd;
        }
        return regManager.getPReg("zero");
    }

    void toExpectReg(Entity entity, Reg target, ASMBlock block, boolean inEnd) {
        ASMIns ins;
        if (entity instanceof RegVar regVar) {
            ins = new Mv(target, regManager.getAsmReg(regVar.name));
        } else if (entity instanceof BoolLiteral boolLiteral) {
            ins = new Li(target, boolLiteral.value ? 1 : 0);
        } else if (entity instanceof IntLiteral intLiteral) {
            ins = new Li(target, intLiteral.value);
        } else if (entity instanceof StringLiteral stringLiteral) {
            ins = new La(target, currentModule.globalStrings.get(stringLiteral.name));
        } else {
            ins = new Mv(target, regManager.getPReg("zero"));
        }
        if (inEnd) {
            block.exitInses.add(0, ins);
        } else {
            block.addIns(ins);
        }
    }

    Val getVal(Entity entity) {
        if (entity instanceof IntLiteral intLiteral) {
            if (intLiteral.value != 0) {
                return new Imm(intLiteral);
            } else {
                return regManager.getPReg("zero");
            }
        } else if (entity instanceof BoolLiteral boolLiteral) {
            if (!boolLiteral.value) {
                return regManager.getPReg("zero");
            } else {
                return new Imm(1);
            }
        } else if (entity instanceof NullLiteral) {
            return regManager.getPReg("zero");
        } else {
            return getReg(entity);
        }
    }


    Reg getReg(String irRegName) {
        return regManager.getAsmReg(irRegName);
    }

    void replaceBlock(ArrayList<ASMIns> exitInses, ASMBlock oldb, ASMBlock newb) {
        for (var ins : exitInses) {
            if (ins instanceof J j && j.jumpTo.equals(oldb)) {
                j.jumpTo = newb;
            } else if (ins instanceof Branch branch && branch.jumpTo.equals(oldb)) {
                branch.jumpTo = newb;
            }
        }
    }

    @Override
    public void visit(IRProgram node) {
        for (var strLiteral : node.stringLiterals) {
            GlobalString globalString = new GlobalString(strLiteral.val, "." + strLiteral.name.substring(1));
            currentModule.globalStrings.put(strLiteral.name, globalString);
        }
        for (var gv : node.globalVars.values()) {
            int byteSize = (gv.type.bitSize + 7) / 8;
            Val init = getInitial((Literal) gv.initVal);
            GlobalVal globalVal = new GlobalVal(gv.name.substring(1), init, byteSize);
            currentModule.gbIr2Asm.put(gv, globalVal);
        }
        for (var func : node.functions.values()) {
            currentFunction = new ASMFunction(func.irFuncName.substring(1));
            currentModule.functions.add(currentFunction);
            func.accept(this);
        }
        currentModule.print();
    }

    void functionInit(IRFunction node) {
        regManager.calleeSaveTo.clear();
        currentFunction.initBlock = new ASMBlock(".L-" + node.irFuncName.substring(1) + "-init", "init", 1, 0);
        currentFunction.blocks.add(currentFunction.initBlock);
        if (!currentFunction.funcName.equals("main")) {
            for (var reg : RegManager.calleeSaveRegs) {
                VirtualReg vReg = regManager.getNewVirtualReg();
                regManager.calleeSaveTo.put(reg, vReg);
                var mv = new Mv(vReg, reg);
                currentFunction.initBlock.addIns(mv);
            }
        }
        for (int i = 0; i < 8 && i < node.parameters.size(); ++i) {
            var paramVReg = getReg(node.parameters.get(i).name); //储存第i个参数的虚拟寄存器
            var paramPReg = regManager.getPReg("a" + i);
            currentFunction.initBlock.addIns(new Mv(paramVReg, paramPReg));
        }
        for (int i = 8; i < node.parameters.size(); ++i) {
            var paramVReg = getReg(node.parameters.get(i).name);
            var stackParam = new StackVal();
            currentFunction.params.add(stackParam);
            Lw lw = new Lw(paramVReg, regManager.getPReg("sp"), new StackOffset(stackParam), "get param\n");
            currentFunction.initBlock.addIns(lw);
        }
        currentFunction.raStack = new StackVal();
        currentFunction.stack.add(0, currentFunction.raStack); //ra
        currentFunction.exitBlock = new ASMBlock(".L-" + node.irFuncName.substring(1) + "-exit", "exit", 0, 1);
        currentFunction.blocks.add(currentFunction.exitBlock);
        if (!currentFunction.funcName.equals("main")) {
            for (var reg : RegManager.calleeSaveRegs) {
                currentFunction.exitBlock.addIns(new Mv(reg, regManager.calleeSaveTo.get(reg)));
            }
        }
    }

    void functionExit() { //在此处加入的指令是无需寄存器分配的指令，并且需要在寄存器分配全部结束以后才能确定栈的长度并执行
        currentBlock = currentFunction.exitBlock;
        if (currentFunction.raStack != null) {
            currentBlock.addIns(new Lw(regManager.getPReg("ra"), regManager.getPReg("sp"), new Imm(currentFunction.raStack.offset), "load return address\n"));
        }
        if (!currentFunction.stack.isEmpty()) {
            int length = (currentFunction.stack.size() + 3) / 4 * 16;
            currentBlock.addIns(new ASMarithmetic(regManager.getPReg("sp"), regManager.getPReg("sp"), new Imm(length), "add"));
        }
        currentFunction.exitBlock.exitInses.add(new Ret());
    }

    @Override
    public void visit(IRFunction node) {
        functionInit(node);
        for (var basicBlock : node.blocks) { //symbol collect
            ASMBlock block = new ASMBlock(".L-" + currentFunction.funcName + "-" + currentFunction.blocks.size(), basicBlock.label, basicBlock.succs.size(), basicBlock.loopDepth);
            currentFunction.blocks.add(block);
            currentFunction.irName2Block.put(basicBlock.label, block);
        }
        for (var basicBlock : node.blocks) {
            currentBlock = currentFunction.irName2Block.get(basicBlock.label);
            if (basicBlock == node.entryBlock) {
                currentFunction.initBlock.succs.add(currentBlock);
                currentFunction.initBlock.exitInses.add(new J(currentBlock));
                currentBlock.preds.add(currentFunction.initBlock);
            }
            visit(basicBlock);
        }
        if (currentFunction.maxArgNum >= 0) {//非叶函数
            StackOffset offset = new StackOffset(currentFunction.raStack);
            Sw raStore = new Sw(regManager.getPReg("ra"), regManager.getPReg("sp"), offset, "store ra\n");
            currentFunction.initBlock.addIns(raStore);
        } else {
            currentFunction.stack.remove(0); //删去raStack
            currentFunction.raStack = null;
        }
        //下面这段代码的正确性有待考证
        int stackarg = max(currentFunction.maxArgNum - 8, 0);
        for (int i = 0; i < stackarg; ++i) {
            currentFunction.stack.add(0, new StackVal());
        }
//        NaiveRegAllocator naiveRegAllocator = new NaiveRegAllocator(regManager);
//        naiveRegAllocator.visit(currentFunction); //将每个函数中的虚拟寄存器分配到栈上
        RegAllocater regAllocater = new RegAllocater(currentFunction,regManager);
        regAllocater.allocate();
        getOffset(currentFunction);
        if (!currentFunction.stack.isEmpty()) {
            int length = (currentFunction.stack.size() + 3) / 4 * 16;
            currentFunction.initBlock.addFirst(new ASMarithmetic(regManager.getPReg("sp"), regManager.getPReg("sp"), new Imm(-length), "add")); //此时才能确定栈的长度
        }
        functionExit();
        regManager.irReg2asmReg.clear();
        regManager.virtualRegs.clear();
        regManager.irVar2Stack.clear();
    }

    void collectBranchCond(BasicBlock node) {
        for (var ins : node.instructions) {
            if (ins instanceof BranchIns branchIns && branchIns.condition != null) {
                for (var inss : node.instructions) {
                    if (inss instanceof IcmpIns icmpIns && icmpIns.result == branchIns.condition) {
                        icmpIns.forBranch = true;
                        branchIns.icmpIns = icmpIns;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void visit(BasicBlock node) {
        collectBranchCond(node); //判断一个branch指令是否有对应的i1表达式
        for (var phi : node.phis) {
            visit(phi);
        }
        for (var ins : node.instructions) {
            ins.accept(this);
        }
    }

    @Override
    public void visit(AllocaIns node) {
        var stackVal = regManager.getStackVal(node.var.name);
        currentFunction.stack.add(stackVal);
    }

    @Override
    public void visit(LoadIns node) {
        Reg rd = getReg(node.value.toString());
        String comment = node.toString();
        int size = node.value.type.equals(irBoolType) ? 1 : 4;
        rd.size = size;
        if (currentModule.gbIr2Asm.containsKey(node.ptr)) { //从全局变量中load
            GlobalVal gv = currentModule.gbIr2Asm.get(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Lb(rd, gv, comment));
            } else {
                currentBlock.addIns(new Lw(rd, gv, comment));
            }
        } else if (regManager.irVar2Stack.containsKey(node.ptr)) { //从局部变量中Load
            StackVal sv = regManager.irVar2Stack.get(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Lb(rd, regManager.getPReg("sp"), new StackOffset(sv), comment));
            } else {
                currentBlock.addIns(new Lw(rd, regManager.getPReg("sp"), new StackOffset(sv), comment));
            }
        } else { //从一个储存在寄存器中的指针中load
            Reg rs = getReg(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Lb(rd, rs, new Imm(0), comment));
            } else {
                currentBlock.addIns(new Lw(rd, rs, new Imm(0), comment));
            }
        }
    }

    @Override
    public void visit(StoreIns node) {
        Reg rs = getReg(node.val);
        String comment = node.toString();
        int size = node.val.type.equals(irBoolType) ? 1 : 4;
        if (currentModule.gbIr2Asm.containsKey(node.ptr)) {
            GlobalVal gv = currentModule.gbIr2Asm.get(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Sb(rs, gv, regManager.getPReg("t6"), comment));
            } else {
                currentBlock.addIns(new Sw(rs, gv, regManager.getPReg("t6"), comment));
            }
        } else if (regManager.irVar2Stack.containsKey(node.ptr)) {
            StackVal sv = regManager.irVar2Stack.get(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Sb(rs, regManager.getPReg("sp"), new StackOffset(sv), comment));
            } else {
                currentBlock.addIns(new Sw(rs, regManager.getPReg("sp"), new StackOffset(sv), comment));
            }
        } else {
            Reg rd = getReg(node.ptr);
            if (size == 1) {
                currentBlock.addIns(new Sb(rs, rd, new Imm(0), comment));
            } else {
                currentBlock.addIns(new Sw(rs, rd, new Imm(0), comment));
            }
        }
    }

    @Override
    public void visit(BranchIns node) {
        ASMBlock block1 = currentFunction.irName2Block.get(node.trueBlock.label);
        if (node.condition == null) {
            currentBlock.exitInses.add(new J(block1));
            currentBlock.succs.add(block1);
            block1.preds.add(currentBlock);
        } else {
            Branch branch;
            if (node.icmpIns != null) {
                Reg rs1 = getReg(node.icmpIns.operand1);
                Reg rs2 = getReg(node.icmpIns.operand2);
                switch (node.icmpIns.operator) {
                    case "icmp eq" -> branch = new Branch(rs1, rs2, block1, "beq");
                    case "icmp ne" -> branch = new Branch(rs1, rs2, block1, "bne");
                    case "icmp sge" -> branch = new Branch(rs1, rs2, block1, "bge");
                    case "icmp sgt" -> branch = new Branch(rs2, rs1, block1, "blt");
                    case "icmp sle" -> branch = new Branch(rs2, rs1, block1, "bge");
                    default -> branch = new Branch(rs1, rs2, block1, "blt");
                }
            } else {
                Reg cond = getReg(node.condition);
                Reg r1 = getReg(new IntLiteral(1));
                branch = new Branch(cond, r1, block1, "beq");
            }
            currentBlock.exitInses.add(branch);
            ASMBlock block2 = currentFunction.irName2Block.get(node.falseBlock.label);
            currentBlock.exitInses.add(new J(block2));
            currentBlock.succs.add(block1);
            block1.preds.add(currentBlock);
            currentBlock.succs.add(block2);
            block2.preds.add(currentBlock);
        }
    }

    @Override
    public void visit(ArithmeticIns node) {
        Reg rd = getReg(node.result);
        Reg rs1 = getReg(node.operand1);
        ASMarithmetic ins;
        Val v2;
        if (node.operator.equals("add") || node.operator.equals("sub") || node.operator.equals("and") || node.operator.equals("ashr") || node.operator.equals("or") || node.operator.equals("shl") || node.operator.equals("xor")) {
            v2 = getVal(node.operand2); //可以使用立即数运算
        } else {
            v2 = getReg(node.operand2);  //必须在寄存器上运算
        }
        if (v2 instanceof Imm imm) {
            ins = new ASMarithmetic(rd, rs1, imm, node.operator);
        } else {
            ins = new ASMarithmetic(rd, rs1, (Reg) v2, node.operator);
        }
        currentBlock.addIns(ins);
    }

    @Override
    public void visit(IcmpIns node) {
        if (node.forBranch) return; //留到branch指令的时候再处理它
        Reg rd = getReg(node.result);
        Reg rs1 = getReg(node.operand1);
        Reg rs2 = getReg(node.operand2);
        Val v1 = getVal(node.operand1);
        Val v2 = getVal(node.operand2);
        if (node.operator.equals("icmp ne") || node.operator.equals("icmp eq")) {
            if (v2 instanceof Imm imm) {
                currentBlock.addIns(new ASMarithmetic(rd, rs1, imm, "xor"));
            } else {
                currentBlock.addIns(new ASMarithmetic(rd, rs1, (Reg) v2, "xor"));
            }
            if (node.operator.equals("icmp ne")) {
                currentBlock.addIns(new Slt(rd, rd, new Imm(1), true));
            } else {
                currentBlock.addIns(new Slt(rd, rd, regManager.getPReg("zero"), true));
            }
        } else {
            if (node.operator.equals("icmp slt") || node.operator.equals("icmp sge")) {
                if (v2 instanceof Imm imm) {
                    currentBlock.addIns(new Slt(rd, rs1, imm, false));
                } else {
                    currentBlock.addIns(new Slt(rd, rs1, (Reg) v2, false));
                }
            } else {
                if (v1 instanceof Imm imm) {
                    currentBlock.addIns(new Slt(rd, rs2, imm, false));
                } else {
                    currentBlock.addIns(new Slt(rd, rs2, (Reg) v1, false));
                }
            }
            if (node.operator.equals("icmp sle") || node.operator.equals("icmp sge")) {
                currentBlock.addIns(new Slt(rd, rd, new Imm(1), true));
            }
        }
    }

    @Override
    public void visit(CallIns node) {
        currentFunction.maxArgNum = max(currentFunction.maxArgNum, node.args.size());
        for (int i = 0; i < 8 && i < node.args.size(); ++i) {
            toExpectReg(node.args.get(i), regManager.getPReg("a" + i), currentBlock, false);
        }
        for (int i = 8; i < node.args.size(); ++i) {
            Sw sw = new Sw(getReg(node.args.get(i)), regManager.getPReg("sp"), new Imm((i - 8) * 4), "put param\n");
            currentBlock.addIns(sw);
        }
        Call call = new Call(node.funcName.substring(1),node.args.size());
        currentBlock.addIns(call);
        if (!node.returnType.equals(irVoidType)) {
            Mv mv = new Mv(getReg(node.result), regManager.getPReg("a0"));
            currentBlock.addIns(mv);
        }
    }

    @Override
    public void visit(GetElementPtrIns node) {
        Reg ptr = getReg(node.ptrVal);
        Reg rd = getReg(node.valuePtr);
        Reg idx1 = getReg(node.idx1);
        boolean ifShift = false;
        if (idx1 != regManager.getPReg("zero")) { //idx1!=0
            ifShift = true;
            if (!node.type.equals(irBoolType)) {
                Reg slidx1 = regManager.getNewVirtualReg();
                currentBlock.addIns(new ASMarithmetic(slidx1, idx1, new Imm(2), "shl"));
                currentBlock.addIns(new ASMarithmetic(rd, ptr, slidx1, "add", node.toString()));
            } else {
                currentBlock.addIns(new ASMarithmetic(rd, ptr, idx1, "add", node.toString()));
            }
        } else if (node.idx2 != null) {
            Reg idx2 = getReg(node.idx2);
            if (idx2 != regManager.getPReg("zero")) { //idx2!=0
                ifShift = true;
                Reg slidx2 = regManager.getNewVirtualReg();
                currentBlock.addIns(new ASMarithmetic(slidx2, idx2, new Imm(2), "shl"));
                currentBlock.addIns(new ASMarithmetic(rd, ptr, slidx2, "add", node.toString()));
            }
        }
        if (!ifShift) {
            currentBlock.addIns(new Mv(rd, ptr));
        }
    }


    @Override
    public void visit(ReturnIns node) {
        if (!node.type.equals(irVoidType)) {
            toExpectReg(node.value, regManager.getPReg("a0"), currentBlock, false);  //将返回值储存至ra寄存器
        }
        currentBlock.exitInses.add(new J(currentFunction.exitBlock));
        currentBlock.succs.add(currentFunction.exitBlock);
        currentBlock.succSize++;
        currentFunction.exitBlock.preds.add(currentBlock);
    }


    @Override
    public void visit(PhiIns node) {
        Reg rd = getReg(node.result);
        Reg tmp = regManager.getNewVirtualReg();
        ASMBlock block1 = currentFunction.irName2Block.get(node.blocks.get(0).label);
        ASMBlock block2 = currentFunction.irName2Block.get(node.blocks.get(1).label);
        if (block1.succSize == 1) {
            toExpectReg(node.values.get(0), tmp, block1, true);
        } else {
            ASMBlock mBlock = intermediateBlocks.get(block1.name + currentBlock.name);
            if (mBlock != null) {
                toExpectReg(node.values.get(0), tmp, mBlock, false);
            } else {
                mBlock = new ASMBlock(".L-" + currentFunction.funcName + "-" + currentFunction.blocks.size(), "intermediate block", 1, currentBlock.loopDepth);
                currentFunction.blocks.add(mBlock);
                replaceBlock(block1.exitInses, currentBlock, mBlock);
                block1.succs.remove(currentBlock); //succs也会更改！
                currentBlock.preds.remove(block1);
                block1.succs.add(mBlock);
                mBlock.preds.add(block1);
                mBlock.exitInses.add(new J(currentBlock));
                mBlock.succs.add(currentBlock);
                currentBlock.preds.add(mBlock);
                intermediateBlocks.put(block1.name + currentBlock.name, mBlock);
                toExpectReg(node.values.get(0), tmp, mBlock, false);
            }
        }
        if (block2.succSize == 1) {
            toExpectReg(node.values.get(1), tmp, block2, true);
        } else {
            ASMBlock mBlock = intermediateBlocks.get(block2.name + currentBlock.name);
            if (mBlock != null) {
                toExpectReg(node.values.get(1), tmp, mBlock, false);
            } else {
                mBlock = new ASMBlock(".L-" + currentFunction.funcName + "-" + currentFunction.blocks.size(), "intermediate block", 1, currentBlock.loopDepth);
                currentFunction.blocks.add(mBlock);
                replaceBlock(block2.exitInses, currentBlock, mBlock);
                block2.succs.remove(currentBlock); //succs也会更改！
                currentBlock.preds.remove(block2);
                block2.succs.add(mBlock);
                mBlock.preds.add(block2);
                mBlock.exitInses.add(new J(currentBlock));
                mBlock.succs.add(currentBlock);
                currentBlock.preds.add(mBlock);
                intermediateBlocks.put(block2.name + currentBlock.name, mBlock);
                toExpectReg(node.values.get(1), tmp, mBlock, false);
            }
        }
        currentBlock.addIns(new Mv(rd, tmp));
    }
}
