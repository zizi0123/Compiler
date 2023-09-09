package IR;

import IR.Entity.Entity;
import IR.instruction.*;
import IR.instruction.arithmetic.*;
import IR.instruction.icmp.*;
import IR.Entity.literal.*;
import IR.type.IRClassType;
import IR.type.IRType;
import IR.Entity.variable.GlobalVar;
import IR.Entity.variable.LocalVar;
import IR.Entity.variable.RegVar;
import ast.*;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import ast.util.constValue.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static IR.Entity.literal.Literal.defaultVal;
import static IR.type.IRType.toIRType;
import static IR.type.IRTypes.*;
import static ast.util.scope.GlobalScope.*;

public class IRBuilder implements ASTVisitor {


    IRClassType currentClass;
    public IRProgram irProgram;
    BasicBlock currentBlock, continueToBlock, breakToBlock;
    IR.IRFunction currentFunction;

    File irFile = new File("result/output.ll");
    PrintWriter out1;

    public IRBuilder() {
        irProgram = new IRProgram();
    }

    void IRSymbolCollect(ProgramNode node) {
        for (var def : node.defNodes) {
            if (def instanceof ClassDefNode classDefNode) {
                IRClassType classType = new IRClassType("%class." + classDefNode.className, classDefNode.members.size() << 2);
                for (var member : classDefNode.members.values()) {
                    classType.addType(member);
                }
                irProgram.classes.put(classType.typeName, classType);
                if (classDefNode.constructor != null) {
                    IRFunction func = new IRFunction("@" + classDefNode.className + "." + classDefNode.className, true, irVoidType);
                    irProgram.functions.put(func.irFuncName, func);
                    classType.constructor = func;
                }
                for (var function : classDefNode.functions.values()) {
                    IRFunction classMethod = new IRFunction(function.irFuncName, true, toIRType(function.returnType));
                    irProgram.functions.put(classMethod.irFuncName, classMethod);
                }
            } else if (def instanceof FuncDefNode funcDefNode) {
                IRFunction func = new IRFunction(funcDefNode.irFuncName, false, toIRType(funcDefNode.returnType));
                irProgram.functions.put(func.irFuncName, func);
            }
        }
    }

    Entity getValue(Entity var) {
        if (var instanceof LocalVar localVar) {
            LoadIns load = new LoadIns(localVar);
            currentBlock.addIns(load);
            return load.value;
        } else if (var instanceof GlobalVar globalVar) {
            LoadIns load = new LoadIns(globalVar);
            currentBlock.addIns(load);
            return load.value;
        } else if (var instanceof Literal) {
            return var;
        } else {
            return var;
        }
    }

    Entity getValue(RegVar varPtr, IRType type) {
        assert varPtr.type.equals(irPtrType) : "a exprNode's irPtr is not a irPtrType regVar";
        RegVar value = new RegVar(type, (varPtr.toString() + "_value." + varPtr.getLoadNum()));
        currentBlock.addIns(new LoadIns(varPtr.toString(), type, value.name));
        return value;
    }

    Entity getValue(ExprNode node) {
        if (node.irVal != null) {
            return getValue(node.irVal);
        }
        return getValue(node.irPtr, toIRType(node.type));
    }

    @Override
    public void visit(ProgramNode node)  {
        IRSymbolCollect(node); //todo check是否有必要
        for (var def : node.defNodes) {
            if (def instanceof FuncDefNode) {
                visit((FuncDefNode) def);
            } else if (def instanceof ClassDefNode) {
                visit((ClassDefNode) def);
            } else {
                visit((VarDefNode) def);
            }
        }
        if (!irProgram.initFunction.entryBlock.instructions.isEmpty()) {
            irProgram.functions.put(irProgram.initFunction.irFuncName, irProgram.initFunction);
            IRFunction mainFunc = irProgram.functions.get("@main");
            mainFunc.entryBlock.instructions.add(0, new CallIns(irVoidType, "@global_init", null));
        }
        for (var block : irProgram.initFunction.blocks) {
            if (block.exitInstruction != null) {
                block.instructions.add(block.exitInstruction);
            }
        }
        try {
            out1 = new PrintWriter(irFile, StandardCharsets.UTF_8);
            irProgram.Print(out1);
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        currentFunction = irProgram.functions.get(node.irFuncName);
        currentBlock = currentFunction.entryBlock;
        if (node.funcName.equals("main")) {
            currentBlock.exitInstruction = new ReturnIns(new IntLiteral(0));
        } else if (node.returnType.isVoid) {
            currentBlock.exitInstruction = new ReturnIns(new voidLiteral());
        }
        if (!node.functionParameterList.isEmpty()) {
            visit(node.functionParameterList);
        }
        visit(node.blockStmt);
        for (var block : currentFunction.blocks) {
            if (block.exitInstruction != null) {
                block.instructions.add(block.exitInstruction);
            }
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        currentClass = irProgram.classes.get("%class." + node.className);
        if (node.constructor != null) visit(node.constructor);
        for (var function : node.functions.values()) {
            visitClassMethod(function);
        }
    }

    @Override
    public void visit(ClassConstructorNode node) {
        currentFunction = irProgram.functions.get("@" + node.className + "." + node.className);
        currentBlock = currentFunction.entryBlock;
        currentFunction.addParaThis();
        LocalVar thisPtr = new LocalVar("%this", irPtrType);
        currentFunction.addLocalVar(thisPtr);
        currentBlock.addIns(new AllocaIns(thisPtr));
        currentBlock.addIns(new StoreIns(currentFunction.parameters.get(0), thisPtr.name)); //添加局部变量this指针
        visit(node.blockStmt);
    }

    public void visitClassMethod(FuncDefNode funcDefNode) {
        currentFunction = irProgram.functions.get(funcDefNode.irFuncName);
        currentBlock = currentFunction.entryBlock;
        currentFunction.addParaThis();
        LocalVar thisPtr = new LocalVar("%this", irPtrType);
        currentFunction.addLocalVar(thisPtr);
        currentBlock.addIns(new AllocaIns(thisPtr));
        currentBlock.addIns(new StoreIns(currentFunction.parameters.get(0), thisPtr.name)); //添加局部变量this指针
        if (!funcDefNode.functionParameterList.isEmpty()) {
            funcDefNode.functionParameterList.accept(this);
        }
        visit(funcDefNode.blockStmt);
    }

    @Override
    public void visit(VarDefNode node) {
        for (var singleVar : node.vars) {
            visit(singleVar);
        }
    }

    @Override
    public void visit(SingleVarDefNode node) {
        if (node.irVarName.startsWith("@")) {
            GlobalVar var = new GlobalVar(node.irVarName, toIRType(node.type));
            irProgram.globalVars.put(var.name, var);
            if (node.expr != null) {
                if (node.expr instanceof ConstExprNode) {
                    node.expr.accept(this);
                    var.initVal = getValue(node.expr);
                } else {
                    var.initVal = defaultVal(var.type);
                    IR.IRFunction tmpFunc = currentFunction;
                    BasicBlock tmpBlock = currentBlock;
                    currentFunction = irProgram.initFunction;
                    currentBlock = irProgram.initFunction.entryBlock;
                    node.expr.accept(this);
                    currentBlock.addIns(new StoreIns(getValue(node.expr), var.name));
                    currentBlock = tmpBlock;
                    currentFunction = tmpFunc;
                }
            } else {
                var.initVal = defaultVal(var.type);
            }
        } else {
            LocalVar var = new LocalVar(node.irVarName, toIRType(node.type));
            currentFunction.addLocalVar(var);
            currentBlock.addIns(new AllocaIns(var));
            if (node.expr != null) {
                node.expr.accept(this);
                currentBlock.addIns(new StoreIns(getValue(node.expr), var.name));
            }
        }
    }

    @Override
    public void visit(FuncParameterListNode node) {
        for (var para : node.parameters) {
            RegVar irPara = currentFunction.addPara(para);  //加入函数的参数列表
            LocalVar inPara = new LocalVar(para.irVarName, toIRType(para.type));  //创建局部变量
            currentFunction.addLocalVar(inPara);
            currentBlock.addIns(new AllocaIns(inPara));
            currentBlock.addIns(new StoreIns(irPara, inPara.name));
        }
    }


    @Override
    public void visit(BlockStmtNode node) {
        for (var stmt : node.stmts) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(IfStmtNode node) {
        int ifStmtNum = ++currentFunction.ifStmtNum;
        node.condition.accept(this);
        Entity condVal = getValue(node.condition);
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("if_end." + ifStmtNum);
        nextBlock.exitInstruction = currentBlock.exitInstruction;
        BasicBlock trueBlock = new BasicBlock("if_true." + ifStmtNum);
        trueBlock.exitInstruction = new BranchIns(nextBlock);
        currentFunction.addBlock(trueBlock);
        currentBlock = trueBlock;
        node.trueStmts.accept(this);
        if (node.falseStmts != null) {
            BasicBlock falseBlock = new BasicBlock("if_false." + ifStmtNum);
            falseBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(falseBlock);
            currentBlock = falseBlock;
            node.falseStmts.accept(this);
            tmpBlock.exitInstruction = new BranchIns(condVal, trueBlock, falseBlock);
        } else {
            tmpBlock.exitInstruction = new BranchIns(condVal, trueBlock, nextBlock);
        }
        currentFunction.addBlock(nextBlock);
        currentBlock = nextBlock;
    }

    @Override
    public void visit(WhileStmtNode node) {
        int whileStmtNum = ++currentFunction.whileStmtNum;
        node.condition.accept(this);
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("while_end." + whileStmtNum);
        BasicBlock condBlock = new BasicBlock("while_condition." + whileStmtNum);
        BasicBlock bodyBlock = new BasicBlock("while_body." + whileStmtNum);
        BasicBlock lastContinue = continueToBlock;
        BasicBlock lastBreak = breakToBlock;
        breakToBlock = nextBlock;
        continueToBlock = condBlock;
        nextBlock.exitInstruction = tmpBlock.exitInstruction;
        tmpBlock.exitInstruction = new BranchIns(condBlock);
        currentBlock = condBlock;
        currentFunction.addBlock(condBlock);
        BranchIns branchIns = new BranchIns(null, bodyBlock, nextBlock);
        condBlock.exitInstruction = branchIns;
        node.condition.accept(this);
        branchIns.condition = getValue(node.condition);
        currentBlock = bodyBlock;
        currentFunction.addBlock(bodyBlock);
        bodyBlock.exitInstruction = new BranchIns(condBlock);
        node.stmts.accept(this);
        currentBlock = nextBlock;
        currentFunction.addBlock(nextBlock);
        continueToBlock = lastContinue;
        breakToBlock = lastBreak;
    }

    @Override
    public void visit(ForStmtNode node) {
        int forStmtNum = ++currentFunction.forStmtNum;
        if (node.initVarDef != null) {
            node.initVarDef.accept(this);
        } else if (node.initExpr != null) {
            node.initExpr.accept(this);
        }
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("for_end." + forStmtNum);
        BasicBlock condBlock = new BasicBlock("for_condition." + forStmtNum);
        BasicBlock bodyBlock = new BasicBlock("for_body." + forStmtNum);
        BasicBlock stepBlock = new BasicBlock("for_step." + forStmtNum);
        BasicBlock lastContinue = continueToBlock;
        BasicBlock lastBreak = breakToBlock;
        continueToBlock = stepBlock;
        breakToBlock = nextBlock;
        nextBlock.exitInstruction = tmpBlock.exitInstruction;
        tmpBlock.exitInstruction = new BranchIns(condBlock);
        currentBlock = condBlock;
        currentFunction.addBlock(condBlock);
        if (node.condition != null) {
            BranchIns branch = new BranchIns(null, bodyBlock, nextBlock);
            condBlock.exitInstruction = branch;
            node.condition.accept(this);
            branch.condition = getValue(node.condition);
        } else {
            condBlock.exitInstruction = new BranchIns(bodyBlock);
        }
        currentBlock = bodyBlock;
        currentFunction.addBlock(bodyBlock);
        bodyBlock.exitInstruction = new BranchIns(stepBlock);
        node.stmts.accept(this);
        currentBlock = stepBlock;
        currentFunction.addBlock(stepBlock);
        stepBlock.exitInstruction = new BranchIns(condBlock);
        if (node.step != null) {
            node.step.accept(this);
        }
        currentBlock = nextBlock;
        currentFunction.addBlock(nextBlock);
        continueToBlock = lastContinue;
        breakToBlock = lastBreak;
    }

    @Override
    public void visit(ContinueStmtNode node) {
        currentBlock.exitInstruction = new BranchIns(continueToBlock);
    }

    @Override
    public void visit(BreakStmtNode node) {
        currentBlock.exitInstruction = new BranchIns(breakToBlock);
    }

    @Override
    public void visit(ReturnStmtNode node) {
        if (node.expr != null) {
            ReturnIns returnIns = new ReturnIns(null);
            currentBlock.exitInstruction = returnIns;
            node.expr.accept(this);
            Entity returnVal = getValue(node.expr);
            returnIns.type = returnVal.type;
            returnIns.value = returnVal;
        } else {
            currentBlock.exitInstruction = new ReturnIns(new voidLiteral());
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        if (node.expr != null) {
            node.expr.accept(this);
        }
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.varDefNode.accept(this);
    }

    @Override
    public void visit(ParenExprNode node) {
        node.exprNode.accept(this);
        node.irVal = node.exprNode.irVal;
        node.irPtr = node.exprNode.irPtr;
    }

    @Override
    public void visit(ConstExprNode node) {
        if (node.value instanceof IntConst intConst) {
            node.irVal = new IntLiteral(intConst);
        } else if (node.value instanceof BoolConst boolConst) {
            node.irVal = new BoolLiteral(boolConst);
        } else if (node.value instanceof Null) {
            node.irVal = new NullLiteral();
        } else if (node.value instanceof This) {
            node.irVal = currentFunction.localVars.get("%this"); //必在类函数内，在类函数的开头已经定义过局部变量%this
        } else { //string literal
            StringLiteral str = new StringLiteral((StringConst) node.value);
            irProgram.stringLiterals.add(str);
            node.irVal = str;
        }
    }

    @Override
    public void visit(VarExprNode node) {
        if (!node.isFunction) { //如果这个varExpr是函数，那么irFuncName已经在type checker中处理好了
            if (!node.isMember) {
                if (node.irVarName.startsWith("@")) {
                    node.irVal = irProgram.globalVars.get(node.irVarName);
                } else {
                    node.irVal = currentFunction.localVars.get(node.irVarName);
                }
            } else { //是this的一个数据成员,建立一个局部变量，存入指向该元素的指针
                RegVar memberPtr = new RegVar(irPtrType, "%this." + node.varName + "." + currentClass.getGEPtime(node.varName));
                LoadIns loadIns = new LoadIns(currentFunction.localVars.get("%this"));
                int idx2 = currentClass.getMemberNum(node.varName);
                GetElementPtrIns getIns = new GetElementPtrIns(currentClass, loadIns.value, new IntLiteral(0), new IntLiteral(idx2), memberPtr.name); //把指向这个成员的指针存入var
                currentBlock.addIns(loadIns);
                currentBlock.addIns(getIns);
                node.irPtr = memberPtr;
                node.irVal = null;
            }
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        int arithNum = ++currentFunction.arithNum;
        node.lhs.accept(this);
        if (node.op.equals("&&") || node.op.equals("||")) {     //shortcut
            ++currentFunction.shortCutNum;
            BasicBlock nextBlock = new BasicBlock("shortCut_next." + currentFunction.shortCutNum);
            BasicBlock rhsBlock = new BasicBlock("shortCut_rhs." + currentFunction.shortCutNum);
            BasicBlock trueBlock = new BasicBlock("shortCut_true." + currentFunction.shortCutNum);
            BasicBlock falseBlock = new BasicBlock("shortCut_false." + currentFunction.shortCutNum);
            nextBlock.exitInstruction = currentBlock.exitInstruction;
            if (node.op.equals("&&")) {
                currentBlock.exitInstruction = new BranchIns(getValue(node.lhs), rhsBlock, falseBlock);
            } else {
                currentBlock.exitInstruction = new BranchIns(getValue(node.lhs), trueBlock, rhsBlock);
            }
            currentFunction.addBlock(rhsBlock);
            currentBlock = rhsBlock;
            BranchIns branchIns = new BranchIns(null, trueBlock, falseBlock);
            currentBlock.exitInstruction = branchIns;
            node.rhs.accept(this);
            branchIns.condition = getValue(node.rhs);
            currentFunction.addBlock(trueBlock);
            currentBlock = trueBlock;
            currentBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(falseBlock);
            currentBlock = falseBlock;
            currentBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(nextBlock);
            currentBlock = nextBlock;
            RegVar result = new RegVar(irBoolType, "%shortCut_result." + currentFunction.shortCutNum);
            PhiIns phiIns = new PhiIns(result);
            phiIns.addPair(new BoolLiteral(true), trueBlock);
            phiIns.addPair(new BoolLiteral(false), falseBlock);
            node.irVal = result;
            currentBlock.addIns(phiIns);
        } else {
            node.rhs.accept(this);
            if (node.lhs.type.equals(stringType) || node.rhs.type.equals(stringType)) {
                String funcName;
                int callNum;
                funcName = switch (node.op) {
                    case "+" -> "@string.add";
                    case "==" -> "@string.eq";
                    case "!=" -> "@string.ne";
                    case ">" -> "@string.gt";
                    case "<" -> "@string.lt";
                    case "<=" -> "@string.le";
                    default -> "@string.ge";
                };
                callNum = irProgram.getBuildInCallTime(funcName);
                if (node.op.equals("+")) {
                    node.irVal = new RegVar(irPtrType, "%" + funcName.substring(1) + "_result." + callNum);
                } else {
                    node.irVal = new RegVar(irBoolType, "%" + funcName.substring(1) + "_result." + callNum);
                }
                currentBlock.addIns(new CallIns(funcName, node.irVal, getValue(node.lhs), getValue(node.rhs)));
            } else {
                if (node.op.equals("*") || node.op.equals("/") || node.op.equals("%") || node.op.equals("+") || node.op.equals("-") || node.op.equals("<<") || node.op.equals(">>") || node.op.equals("&") || node.op.equals("^") || node.op.equals("|")) {
                    node.irVal = new RegVar(irIntType, "%arith_result." + arithNum);
                    switch (node.op) {
                        case "+" -> currentBlock.addIns(new Add(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "-" -> currentBlock.addIns(new Sub(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "*" -> currentBlock.addIns(new Mul(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "/" -> currentBlock.addIns(new Sdiv(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "%" -> currentBlock.addIns(new Srem(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "<<" -> currentBlock.addIns(new Shl(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case ">>" -> currentBlock.addIns(new Ashr(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "&" -> currentBlock.addIns(new And(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "|" -> currentBlock.addIns(new Or(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "^" -> currentBlock.addIns(new Xor(getValue(node.lhs), getValue(node.rhs), node.irVal));
                    }
                } else {
                    node.irVal = new RegVar(irBoolType, "%arith_result." + arithNum);
                    switch (node.op) {
                        case "==" -> currentBlock.addIns(new Eq(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "!=" -> currentBlock.addIns(new Ne(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case ">" -> currentBlock.addIns(new Sgt(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "<" -> currentBlock.addIns(new Slt(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case ">=" -> currentBlock.addIns(new Sge(getValue(node.lhs), getValue(node.rhs), node.irVal));
                        case "<=" -> currentBlock.addIns(new Sle(getValue(node.lhs), getValue(node.rhs), node.irVal));
                    }
                }
            }
        }
    }

    @Override
    public void visit(UnaryExprNode node) {
        int arithNum = ++currentFunction.arithNum;
        node.exprNode.accept(this);
        RegVar result;
        if (node.op.equals("!")) {
            result = new RegVar(irBoolType, "%unary_result." + arithNum);
        } else {
            result = new RegVar(irIntType, "%unary_result." + arithNum);
        }
        switch (node.op) {
            case "++" -> {
                node.irVal = getValue(node.exprNode);
                currentBlock.addIns(new Add(node.irVal, new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal != null ? node.exprNode.irVal.toString() : node.exprNode.irPtr.toString()));
            }
            case "--" -> {
                node.irVal = getValue(node.exprNode);
                currentBlock.addIns(new Sub(node.irVal, new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal != null ? node.exprNode.irVal.toString() : node.exprNode.irPtr.toString()));
            }
            case "-" -> {
                currentBlock.addIns(new Sub(new IntLiteral(0), getValue(node.exprNode), result));
                node.irVal = result;
            }
            case "+" -> node.irVal = getValue(node.exprNode);
            case "~" -> {
                currentBlock.addIns(new Xor(new IntLiteral(-1), getValue(node.exprNode), result));
                node.irVal = result;
            }
            case "!" -> {
                currentBlock.addIns(new Xor(new BoolLiteral(true), getValue(node.exprNode), result));
                node.irVal = result;
            }
        }
    }

    @Override
    public void visit(PreOpExprNode node) {
        int arithNum = ++currentFunction.arithNum;
        node.expr.accept(this);
        RegVar result = new RegVar(irIntType, "%preop_result." + arithNum);
        switch (node.op) {
            case "++" -> {
                currentBlock.addIns(new Add(getValue(node.expr), new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.expr.irVal != null ? node.expr.irVal.toString() : node.expr.irPtr.toString()));
                node.irVal = node.expr.irVal;
                node.irPtr = node.expr.irPtr;
            }
            case "--" -> {
                currentBlock.addIns(new Sub(getValue(node.expr), new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.expr.irVal != null ? node.expr.irVal.toString() : node.expr.irPtr.toString()));
                node.irVal = node.expr.irVal;
                node.irPtr = node.expr.irPtr;
            }
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.rhs.accept(this);
        node.lhs.accept(this);
        currentBlock.addIns(new StoreIns(getValue(node.rhs), (node.lhs.irVal != null ? node.lhs.irVal.toString() : node.lhs.irPtr.toString())));
        node.irVal = node.lhs.irVal;
        node.irPtr = node.lhs.irPtr;
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.func.accept(this);
        String irFuncName = node.func.irFuncName;
        CallIns callIns;
        int buildInCallTime = irProgram.getBuildInCallTime(irFuncName);
        if (buildInCallTime != -1) {  //build-in function
            node.irVal = new RegVar(toIRType(node.func.function.returnType), "%" + irFuncName.substring(1) + "_result." + buildInCallTime);
            callIns = new CallIns(irFuncName, node.irVal);
            if (node.func.function.equals(size)) {
                Entity var = getValue(((MemberExprNode) node.func).obj);
                callIns.args.add(var);
            } else if (node.func.function.equals(length) || node.func.function.equals(substring) || node.func.function.equals(parseInt) || node.func.function.equals(ord)) {
                Entity var = getValue(((MemberExprNode) node.func).obj);
                callIns.args.add(var);
            }
            for (var arg : node.args) {
                arg.accept(this);
                callIns.args.add(getValue(arg));
            }
        } else {
            IRFunction function = irProgram.functions.get(irFuncName);
            function.callNum++;
            node.irVal = new RegVar(function.returnType, "%" + irFuncName.substring(1) + "_result." + function.callNum);
            callIns = new CallIns(irFuncName, node.irVal);
            if (function.isClassMethod) {
                if (node.func instanceof MemberExprNode memberExprNode) {
                    callIns.args.add(getValue(memberExprNode.obj));
                } else {
                    Entity this_val = getValue(currentFunction.localVars.get("%this"));
                    callIns.args.add(this_val);
                }
            }
            for (var arg : node.args) {
                arg.accept(this);
                callIns.args.add(getValue(arg));
            }
        }
        currentBlock.addIns(callIns);
    }

    @Override
    public void visit(ArrayExprNode node) {
        int arrayNum = ++currentFunction.arrayNum;
        node.array.accept(this);
        node.index.accept(this);
        Entity startPtr = getValue(node.array);
        RegVar ptr = new RegVar(irPtrType, "%array_ptr." + arrayNum);
        GetElementPtrIns get = new GetElementPtrIns(toIRType(node.type), startPtr, getValue(node.index), null, ptr.name);
        currentBlock.addIns(get);
        node.irPtr = ptr;
        node.irVal = null;
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        if (node.function == null) { //如果node.function != null,已经在typecheck中处理好了irFuncName
            Entity ptr = getValue(node.obj);
            IRClassType classType = irProgram.classes.get(node.irClassName);
            int num = classType.getMemberNum(node.memberName);
            String name = "%" + node.irClassName.substring(7) + "." + node.memberName + "_ptr." + classType.getGEPtime(node.memberName);
            RegVar memberPtr = new RegVar(irPtrType, name);
            GetElementPtrIns getIns = new GetElementPtrIns(classType, ptr, new IntLiteral(0), new IntLiteral(num), name);
            currentBlock.addIns(getIns);
            node.irPtr = memberPtr;
            node.irVal = null;
        }
    }


    RegVar getNewArray(int at, int dim, ArrayList<Entity> lengthVal, IRType baseType) {
        CallIns callIns;
        RegVar array = new RegVar(irPtrType, "%new_array_" + at + "." + currentFunction.newNum);
        if (at == dim - 1) {
            if (baseType.equals(irIntType)) {
                callIns = new CallIns("@_newIntArray", array, lengthVal.get(at));
            } else if (baseType.equals(irBoolType)) {
                callIns = new CallIns("@_newBoolArray", array, lengthVal.get(at));
            } else {
                callIns = new CallIns("@_newPtrArray", array, lengthVal.get(at));
            }
        } else {
            callIns = new CallIns("@_newPtrArray", array, lengthVal.get(at));
        }
        currentBlock.addIns(callIns);
        return array;
    }

    Entity newFunction(int at, int dim, ArrayList<Entity> lengthVal, IRType baseType) {
        RegVar startValue = getNewArray(at, dim, lengthVal, baseType);
        if (at != lengthVal.size() - 1) {
            LocalVar idx = new LocalVar("%new_idx" + dim + "." + currentFunction.newNum, irIntType);
            currentBlock.addIns(new AllocaIns(idx));
            currentBlock.addIns(new StoreIns(new IntLiteral(0), idx.name));
            BasicBlock nextBlock = new BasicBlock("new_for_end_" + dim + "." + currentFunction.newNum);
            BasicBlock condBlock = new BasicBlock("new_for_condition_" + dim + "." + currentFunction.newNum);
            BasicBlock bodyBlock = new BasicBlock("new_for_body_" + dim + "." + currentFunction.newNum);
            BasicBlock stepBlock = new BasicBlock("new_forstep__" + dim + "." + currentFunction.newNum);
            nextBlock.exitInstruction = currentBlock.exitInstruction;
            currentBlock.exitInstruction = new BranchIns(condBlock);

            currentBlock = condBlock;
            currentFunction.addBlock(condBlock);
            RegVar condition = new RegVar(irBoolType, "%new_condition_" + dim + "." + currentFunction.newNum);
            RegVar idxVal = new RegVar(irIntType, "%new_idx_val" + dim + "." + currentFunction.newNum);
            condBlock.addIns(new LoadIns(idx.name, irIntType, idxVal.name));
            currentBlock.addIns(new Slt(idxVal, lengthVal.get(at), condition));
            condBlock.exitInstruction = new BranchIns(condition, bodyBlock, nextBlock);

            currentBlock = bodyBlock;
            currentFunction.addBlock(bodyBlock);
            Entity nextDim = newFunction(at + 1, dim, lengthVal, baseType);
            GetElementPtrIns getElementPtrIns = new GetElementPtrIns(irPtrType, startValue, idxVal, null, "%new_array_" + (at - 1) + "." + currentFunction.newNum);
            currentBlock.addIns(getElementPtrIns);
            currentBlock.addIns(new StoreIns(nextDim, getElementPtrIns.valuePtrName));
            bodyBlock.exitInstruction = new BranchIns(stepBlock);

            currentBlock = stepBlock;
            currentFunction.addBlock(stepBlock);
            RegVar newIdxVal = new RegVar(irIntType, "%new_idx_val_add" + dim + "." + currentFunction.newNum);
            currentBlock.addIns(new Add(idxVal, new IntLiteral(1), newIdxVal));
            currentBlock.addIns(new StoreIns(newIdxVal, idx.name));
            stepBlock.exitInstruction = new BranchIns(condBlock);

            currentBlock = nextBlock;
            currentFunction.addBlock(nextBlock);
//            for (int idx = 0; idx < lengthVal.get(at); ++idx) {
//                Entity nextDim = newFunction(at + 1, dim, lengthVal, baseType);
//                GetElementPtrIns getElementPtrIns = new GetElementPtrIns(irPtrType, startValue, new IntLiteral(i), new IntLiteral(0), "%new_array_" + (at - 1) + "_" + i + "." + currentFunction.newNum);
//                currentBlock.addIns(getElementPtrIns);
//                currentBlock.addIns(new StoreIns(nextDim, getElementPtrIns.value.name));
//            }
        }
        return startValue;
    }

    @Override
    public void visit(NewExprNode node) {
        currentFunction.newNum++;
        if (!node.isArray) {
            IRClassType classType = irProgram.getClassType(node.typeName.typeName);
            node.irVal = new RegVar(irPtrType, "%new_class_" + node.typeName.typeName + "." + classType.gerNewTime());
            int classSize = (classType.bitSize);
            currentBlock.addIns(new CallIns("@malloc", node.irVal, new IntLiteral(classSize)));
            if (classType.constructor != null) {
                currentBlock.addIns(new CallIns(irVoidType, classType.constructor.irFuncName, null));
            }
        } else {
            if (node.lengths.isEmpty()) {
                node.irVal = new NullLiteral();
            } else {
                ArrayList<Entity> lengths = new ArrayList<>();
                for (var lengthNode : node.lengths) {
                    lengthNode.accept(this);
                    lengths.add(getValue(lengthNode));
                }
                node.irVal = newFunction(0, node.arrDim, lengths, toIRType(node.typeName));
            }

        }
    }

    @Override
    public void visit(TernaryExprNode node) {
        boolean isVoid = node.mExpr.type.isVoid;
        Entity value1 = null, value2 = null;
        int ternaryNum = ++currentFunction.ternaryNum;
        node.lExpr.accept(this);
        BasicBlock nextBlock = new BasicBlock("ternary_end." + ternaryNum);
        BasicBlock Block1 = new BasicBlock("ternary_first." + ternaryNum);
        BasicBlock Block2 = new BasicBlock("ternary_second." + ternaryNum);
        nextBlock.exitInstruction = currentBlock.exitInstruction;
        currentBlock.exitInstruction = new BranchIns(getValue(node.lExpr), Block1, Block2);
        currentBlock = Block1;
        currentFunction.addBlock(Block1);
        currentBlock.exitInstruction = new BranchIns(nextBlock);
        node.mExpr.accept(this);
        if (!isVoid) {
            value1 = getValue(node.mExpr);
        }
        BasicBlock endBlock1 = currentBlock;
        currentBlock = Block2;
        currentFunction.addBlock(Block2);
        currentBlock.exitInstruction = new BranchIns(nextBlock);
        node.rExpr.accept(this);
        if (!isVoid) {
            value2 = getValue(node.rExpr);
        }
        BasicBlock endBlock2 = currentBlock;
        currentBlock = nextBlock;
        currentFunction.addBlock(nextBlock);
        if (!isVoid) {
            node.irVal = new RegVar(toIRType(node.mExpr.type), "%ternary_value." + ternaryNum);
            PhiIns phi = new PhiIns(node.irVal);
            phi.addPair(value1, endBlock1);
            phi.addPair(value2, endBlock2);
            currentBlock.addIns(phi);
        }
    }
}
