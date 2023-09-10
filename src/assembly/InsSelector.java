package assembly;

import IR.BasicBlock;
import IR.Entity.Entity;
import IR.Entity.literal.*;
import IR.Entity.variable.RegVar;
import IR.IRFunction;
import IR.IRProgram;
import IR.IRVisitor;
import IR.instruction.*;
import assembly.Instruction.*;
import assembly.operand.*;

import java.io.File;

import static IR.type.IRTypes.irBoolType;
import static IR.type.IRTypes.irVoidType;
import static java.lang.Math.max;


public class InsSelector implements IRVisitor {

    Module currentModule;
    ASMFunction currentFunction;
    ASMBlock currentBlock;
    ValueAllocator valueAllocator;


    public InsSelector(Module module) {
        this.currentModule = module;
        valueAllocator = new ValueAllocator();
    }

    void getOffset(ASMFunction func) {
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
            return valueAllocator.getAsmReg(regVar.name);
        }
        if (entity instanceof BoolLiteral boolLiteral) {
            if (!boolLiteral.value) return valueAllocator.getPReg("zero");
            Li li = new Li(valueAllocator.getNewVirtualReg(), 1);
            currentBlock.addIns(li);
            return li.rd;
        }
        if (entity instanceof IntLiteral intLiteral) {
            if (intLiteral.value == 0) return valueAllocator.getPReg("zero");
            Li li = new Li(valueAllocator.getNewVirtualReg(), intLiteral.value);
            currentBlock.addIns(li);
            return li.rd;
        }
        if (entity instanceof StringLiteral stringLiteral) {
            La la = new La(valueAllocator.getNewVirtualReg(), currentModule.globalStrings.get(stringLiteral.name));
            currentBlock.addIns(la);
            return la.rd;
        }
        return valueAllocator.getPReg("zero");
    }

    void toExpectReg(Entity entity, Reg target, ASMBlock block) {
        if (entity instanceof RegVar regVar) {
            block.addIns(new Mv(target, valueAllocator.getAsmReg(regVar.name)));
        } else if (entity instanceof BoolLiteral boolLiteral) {
            Li li = new Li(target, boolLiteral.value ? 1 : 0);
            block.addIns(li);
        } else if (entity instanceof IntLiteral intLiteral) {
            Li li = new Li(target, intLiteral.value);
            block.addIns(li);
        } else if (entity instanceof StringLiteral stringLiteral) {
            La la = new La(target, currentModule.globalStrings.get(stringLiteral.name));
            block.addIns(la);
        } else {
            Mv mv = new Mv(target, valueAllocator.getPReg("zero"));
            block.addIns(mv);
        }
    }

    Val getVal(Entity entity) {
        if (entity instanceof IntLiteral intLiteral) {
            if (intLiteral.value != 0) {
                return new Imm(intLiteral);
            } else {
                return valueAllocator.getPReg("zero");
            }
        } else if (entity instanceof BoolLiteral boolLiteral) {
            if (!boolLiteral.value) {
                return valueAllocator.getPReg("zero");
            } else {
                return new Imm(1);
            }
        } else if (entity instanceof NullLiteral) {
            return valueAllocator.getPReg("zero");
        } else {
            return getReg(entity);
        }
    }


    Reg getReg(String irRegName) {
        return valueAllocator.getAsmReg(irRegName);
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
            currentModule.irName2GbVal.put(gv.name, globalVal);
        }
        for (var func : node.functions.values()) {
            currentFunction = new ASMFunction(func.irFuncName.substring(1));
            currentModule.functions.add(currentFunction);
            func.accept(this);
        }
        currentModule.print();
    }

    void functionInit(IRFunction node) {
        valueAllocator.calleeSaveTo.clear();
        ASMBlock initBlock = new ASMBlock(".L-" + node.irFuncName.substring(1) + "-init", "init");
        currentFunction.blocks.add(initBlock);
        currentFunction.initBlock = initBlock;
        if (!currentFunction.funcName.equals("main")) {
            for (var reg : ValueAllocator.calleeSaveRegs) {
                VirtualReg vReg = valueAllocator.getNewVirtualReg();
                valueAllocator.calleeSaveTo.put(reg, vReg);
                var mv = new Mv(vReg, reg);
                initBlock.addIns(mv);
            }
        }
        for (int i = 0; i < 8 && i < node.parameters.size(); ++i) {
            var paramVReg = getReg(node.parameters.get(i).name); //储存第i个参数的虚拟寄存器
            var paramPReg = valueAllocator.getPReg("a" + i);
            initBlock.addIns(new Mv(paramVReg, paramPReg));
        }
        for (int i = 8; i < node.parameters.size(); ++i) {
            var paramVReg = getReg(node.parameters.get(i).name);
            var stackParam = new StackVal();
            currentFunction.params.add(stackParam);
            Lw lw = new Lw(paramVReg, valueAllocator.getPReg("sp"), new StackOffset(stackParam), "get param\n");
            initBlock.addIns(lw);
        }
        currentFunction.raStack = new StackVal();
        currentFunction.stack.add(0, currentFunction.raStack); //ra
    }

    @Override
    public void visit(IRFunction node) {
        functionInit(node);
        for (var basicBlock : node.blocks) { //symbol collect
            ASMBlock block = new ASMBlock(".L-" + currentFunction.funcName + "-" + currentFunction.blocks.size(), basicBlock.label);
            currentFunction.blocks.add(block);
            currentFunction.irName2Block.put(basicBlock.label, block);
        }
        for (var basicBlock : node.blocks) {
            currentBlock = currentFunction.irName2Block.get(basicBlock.label);
            visit(basicBlock);
        }
        if (currentFunction.maxArgNum >= 0) {//非叶函数
            StackOffset offset = new StackOffset(currentFunction.raStack);
            Sw raStore = new Sw(valueAllocator.getPReg("ra"), valueAllocator.getPReg("sp"), offset, "store ra\n");
            currentFunction.initBlock.addIns(raStore);
        }else{
            currentFunction.stack.remove(0); //删去raStack
            currentFunction.raStack = null;
        }
        //下面这段代码的正确性有待考证
        int stackarg = max(currentFunction.maxArgNum - 8, 0);
        for (int i = 0; i < stackarg; ++i) {
            currentFunction.stack.add(0, new StackVal());
        }
        valueAllocator.irReg2asmReg.clear();
        valueAllocator.virtualRegs.clear();
        valueAllocator.irVar2Stack.clear();
        NaiveRegAllocator naiveRegAllocator = new NaiveRegAllocator(valueAllocator);
        naiveRegAllocator.visit(currentModule);
        getOffset(currentFunction);
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
        collectBranchCond(node);
        for (var ins : node.instructions) {
            ins.accept(this);
        }
    }

    @Override
    public void visit(AllocaIns node) {
        var stackVal = valueAllocator.getStackVal(node.varName);
        currentFunction.stack.add(stackVal);
    }

    @Override
    public void visit(LoadIns node) {
        Reg rd = getReg(node.value.toString());
        String comment = node.toString();
        int size = node.value.type.equals(irBoolType) ? 1 : 4;
        rd.size = size;
        if (currentModule.irName2GbVal.containsKey(node.ptrName)) { //从全局变量中load
            GlobalVal gv = currentModule.irName2GbVal.get(node.ptrName);
            if (size == 1) {
                currentBlock.addIns(new Lb(rd, gv, comment));
            } else {
                currentBlock.addIns(new Lw(rd, gv, comment));
            }
        } else if (valueAllocator.irVar2Stack.containsKey(node.ptrName)) { //从局部变量中Load
            StackVal sv = valueAllocator.irVar2Stack.get(node.ptrName);
            if (size == 1) {
                currentBlock.addIns(new Lb(rd, valueAllocator.getPReg("sp"), new StackOffset(sv), comment));
            } else {
                currentBlock.addIns(new Lw(rd, valueAllocator.getPReg("sp"), new StackOffset(sv), comment));
            }
        } else { //从一个储存在寄存器中的指针中load
            Reg rs = getReg(node.ptrName);
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
        if (currentModule.irName2GbVal.containsKey(node.ptrName)) {
            GlobalVal gv = currentModule.irName2GbVal.get(node.ptrName);
            if (size == 1) {
                currentBlock.addIns(new Sb(rs, gv, valueAllocator.getPReg("t6"), comment));
            } else {
                currentBlock.addIns(new Sw(rs, gv, valueAllocator.getPReg("t6"), comment));
            }
        } else if (valueAllocator.irVar2Stack.containsKey(node.ptrName)) {
            StackVal sv = valueAllocator.irVar2Stack.get(node.ptrName);
            if (size == 1) {
                currentBlock.addIns(new Sb(rs, valueAllocator.getPReg("sp"), new StackOffset(sv), comment));
            } else {
                currentBlock.addIns(new Sw(rs, valueAllocator.getPReg("sp"), new StackOffset(sv), comment));
            }
        } else {
            Reg rd = getReg(node.ptrName);
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
        }
    }

    @Override
    public void visit(ArithmeticIns node) {
        Reg rd = getReg(node.result);
        Reg rs1 = getReg(node.operand1);
        ASMarithmetic ins;
        Val v2;
        if (node.operator.equals("add") || node.operator.equals("sub") || node.operator.equals("and") || node.operator.equals("ashr") || node.operator.equals("or") || node.operator.equals("shl") || node.operator.equals("xor")) {
            v2 = getVal(node.operand2);
        } else {
            v2 = getReg(node.operand2);
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
                currentBlock.addIns(new Slt(rd, rd, valueAllocator.getPReg("zero"), true));
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
            toExpectReg(node.args.get(i), valueAllocator.getPReg("a" + i), currentBlock);
        }
        for (int i = 8; i < node.args.size(); ++i) {
            Sw sw = new Sw(getReg(node.args.get(i)), valueAllocator.getPReg("sp"), new Imm((i - 8) * 4), "put param\n");
            currentBlock.addIns(sw);
        }
        Call call = new Call(node.funcName.substring(1));
        currentBlock.addIns(call);
        if (!node.returnType.equals(irVoidType)) {
            Mv mv = new Mv(getReg(node.result), valueAllocator.getPReg("a0"));
            currentBlock.addIns(mv);
        }
    }

    @Override
    public void visit(GetElementPtrIns node) {
        Reg ptr = getReg(node.ptrVal);
        Reg rd = getReg(node.valuePtrName);
        Reg idx1 = getReg(node.idx1);
        Reg tmp = valueAllocator.getNewVirtualReg();
        boolean ifShift = false;
        if (idx1 != valueAllocator.getPReg("zero")) { //idx1!=0
            ifShift = true;
            if (!node.type.equals(irBoolType)) {
                currentBlock.addIns(new ASMarithmetic(tmp, idx1, new Imm(2), "shl"));
                currentBlock.addIns(new ASMarithmetic(rd, ptr, tmp, "add", node.toString()));
            }else{
                currentBlock.addIns(new ASMarithmetic(rd, ptr, idx1, "add", node.toString()));
            }
        } else if (node.idx2 != null) {
            Reg idx2 = getReg(node.idx2);
            if (idx2 != valueAllocator.getPReg("zero")) { //idx2!=0
                ifShift = true;
                currentBlock.addIns(new ASMarithmetic(tmp, idx2, new Imm(2), "shl"));
                currentBlock.addIns(new ASMarithmetic(rd, ptr, tmp, "add", node.toString()));
            }
        }
        if (!ifShift) {
            currentBlock.addIns(new Mv(rd, ptr));
        }
    }


    @Override
    public void visit(ReturnIns node) {
        if(!node.type.equals(irVoidType)) {
            toExpectReg(node.value, valueAllocator.getPReg("a0"), currentBlock);
        }
        if (!currentFunction.funcName.equals("main")) {
            for(var reg : ValueAllocator.calleeSaveRegs){
                currentBlock.addIns(new Mv(reg,valueAllocator.calleeSaveTo.get(reg)));
            }
        }
        currentBlock.exitInses.add(new Ret(currentFunction));
    }


    @Override
    public void visit(PhiIns node) {
        Reg rd = getReg(node.result);
        ASMBlock block1 = currentFunction.irName2Block.get(node.blocks.get(0).label);
        ASMBlock block2 = currentFunction.irName2Block.get(node.blocks.get(1).label);
        toExpectReg(node.values.get(0), rd, block1);
        toExpectReg(node.values.get(1), rd, block2);
    }
}
