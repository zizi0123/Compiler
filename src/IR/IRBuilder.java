package IR;

import IR.instruction.*;
import IR.instruction.arithmetic.*;
import IR.instruction.icmp.*;
import IR.literal.*;
import IR.type.IRClassType;
import IR.type.IRPtrType;
import IR.type.IRType;
import IR.variable.GlobalVar;
import IR.variable.LocalVar;
import IR.variable.RegVar;
import ast.*;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import ast.util.constValue.*;
import ast.util.error.SemanticError;
import ast.util.scope.GlobalScope;
import ast.util.scope.Scope;
import semantic.SymbolCollector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

import static IR.literal.Literal.defaultVal;
import static IR.type.IRType.toIRType;
import static IR.type.IRTypes.*;
import static ast.util.scope.GlobalScope.*;

public class IRBuilder implements ASTVisitor {
    GlobalScope globalScope;
    Scope currentScope;

    IRClassType currentClass;
    Program program;
    BasicBlock currentBlock, continueToBlock, breakToBlock;
    IR.IRFunction currentFunction;

    public IRBuilder(GlobalScope globalScope) { //直接传入经过semantic check的globalScope
        this.program = new Program();
        this.globalScope = globalScope;
        this.currentScope = new Scope(null);
        this.currentScope.vars = globalScope.vars;
    }

    void IRSymbolCollect(ProgramNode node) {  //todo check是否需要提前处理
        for (var def : node.defNodes) {
            if (def instanceof ClassDefNode classDefNode) {
                IRClassType classType = new IRClassType("%class." + classDefNode.className, classDefNode.members.size() << 2);
                for (var member : classDefNode.members.values()) {
                    classType.addType(member);
                }
                program.classes.put(classType.typeName, classType);
            }
        }
    }

    Entity getValue(Entity var, IRType type) {
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

    Entity getValue(ExprNode node) {
        return getValue(node.irVal, toIRType(node.type));
    }

    @Override
    public void visit(ProgramNode node) {
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
        if (!program.initFunction.entryBlock.instructions.isEmpty()) {
            program.functions.put(program.initFunction.irFuncName, program.initFunction);
            IRFunction mainFunc = program.functions.get("@main");
            mainFunc.entryBlock.instructions.add(0, new CallIns(irVoidType, "@global_init", null));
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        currentFunction = new IRFunction(node.irFuncName, false, toIRType(node.returnType));
        currentBlock = currentFunction.entryBlock;
        if (node.funcName.equals("main")) {
            currentBlock.exitInstruction = new ReturnIns(new IntLiteral(0));
        }
        if (!node.functionParameterList.isEmpty()) {
            visit(node.functionParameterList);
        }
        visit(node.blockStmt);
        program.functions.put(currentFunction.irFuncName, currentFunction);
    }

    @Override
    public void visit(ClassDefNode node) {
        currentClass = program.classes.get(node.className);
        if (node.constructor != null) visit(node.constructor);
        for (var function : node.functions.values()) {
            visitClassMethod(node.className, function);
        }
    }

    @Override
    public void visit(ClassConstructorNode node) {
        currentFunction = new IRFunction("@" + node.className + "." + node.className, true, irVoidType);
        currentBlock = currentFunction.entryBlock;
        currentClass.constructor = currentFunction;
        currentFunction.addParaThis();
        program.functions.put(currentFunction.irFuncName, currentFunction);
        visit(node.blockStmt);
    }

    public void visitClassMethod(String className, FuncDefNode funcDefNode) {
        currentFunction = new IRFunction(funcDefNode.irFuncName, true, toIRType(funcDefNode.returnType));
        currentBlock = currentFunction.entryBlock;
        currentFunction.addParaThis();
        if (!funcDefNode.functionParameterList.isEmpty()) {
            funcDefNode.functionParameterList.accept(this);
        }
        program.functions.put(currentFunction.irFuncName, currentFunction);
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
            program.globalVars.put(var.name, var);
            if (node.expr != null) {
                if (node.expr instanceof ConstExprNode expr) {
                    node.expr.accept(this);
                    var.initVal = getValue(node.expr);
                } else {
                    var.initVal = defaultVal(var.type);
                    IR.IRFunction tmpFunc = currentFunction;
                    BasicBlock tmpBlock = currentBlock;
                    currentFunction = program.initFunction;
                    currentBlock = program.initFunction.entryBlock;
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
        LocalVar thisPtr = new LocalVar("%this", irPtrType);
        currentFunction.addLocalVar(thisPtr);
        currentBlock.addIns(new AllocaIns(thisPtr));
        currentBlock.addIns(new StoreIns(currentFunction.parameters.get(0), thisPtr.name)); //添加局部变量this指针
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
        ++currentFunction.ifStmtNum;
        node.condition.accept(this);
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("end_if." + currentFunction.ifStmtNum);
        nextBlock.exitInstruction = currentBlock.exitInstruction;
        BasicBlock trueBlock = new BasicBlock("true_if." + currentFunction.ifStmtNum);
        trueBlock.exitInstruction = new BranchIns(nextBlock);
        currentFunction.addBlock(trueBlock);
        currentBlock = trueBlock;
        node.trueStmts.accept(this);
        if (node.falseStmts != null) {
            BasicBlock falseBlock = new BasicBlock("false_if." + currentFunction.ifStmtNum);
            falseBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(falseBlock);
            currentBlock = falseBlock;
            node.falseStmts.accept(this);
            tmpBlock.exitInstruction = new BranchIns(getValue(node.condition), trueBlock, falseBlock);
        } else {
            tmpBlock.exitInstruction = new BranchIns(getValue(node.condition), trueBlock, nextBlock);
        }
        currentFunction.addBlock(nextBlock);
        currentBlock = nextBlock;
    }

    @Override
    public void visit(WhileStmtNode node) {
        ++currentFunction.whileStmtNum;
        node.condition.accept(this);
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("end_while." + currentFunction.whileStmtNum);
        BasicBlock condBlock = new BasicBlock("condition_while." + currentFunction.whileStmtNum);
        BasicBlock bodyBlock = new BasicBlock("body_while." + currentFunction.whileStmtNum);
        BasicBlock lastContinue = continueToBlock;
        BasicBlock lastBreak = breakToBlock;
        breakToBlock = nextBlock;
        continueToBlock = condBlock;
        nextBlock.exitInstruction = tmpBlock.exitInstruction;
        tmpBlock.exitInstruction = new BranchIns(condBlock);
        currentBlock = condBlock;
        currentFunction.addBlock(condBlock);
        node.condition.accept(this);
        condBlock.exitInstruction = new BranchIns(getValue(node.condition), bodyBlock, nextBlock);
        currentBlock = bodyBlock;
        currentFunction.addBlock(bodyBlock);
        node.stmts.accept(this);
        bodyBlock.exitInstruction = new BranchIns(condBlock);
        currentBlock = nextBlock;
        currentFunction.addBlock(nextBlock);
        continueToBlock = lastContinue;
        breakToBlock = lastBreak;
    }

    @Override
    public void visit(ForStmtNode node) {
        ++currentFunction.forStmtNum;
        if (node.initVarDef != null) {
            node.initVarDef.accept(this);
        } else {
            node.initExpr.accept(this);
        }
        BasicBlock tmpBlock = currentBlock;
        BasicBlock nextBlock = new BasicBlock("end_for." + currentFunction.forStmtNum);
        BasicBlock condBlock = new BasicBlock("condition_for." + currentFunction.forStmtNum);
        BasicBlock bodyBlock = new BasicBlock("body_for." + currentFunction.forStmtNum);
        BasicBlock stepBlock = new BasicBlock("step_for" + currentFunction.forStmtNum);
        BasicBlock lastContinue = continueToBlock;
        BasicBlock lastBreak = breakToBlock;
        continueToBlock = stepBlock;
        breakToBlock = nextBlock;
        nextBlock.exitInstruction = tmpBlock.exitInstruction;
        tmpBlock.exitInstruction = new BranchIns(condBlock);
        currentBlock = condBlock;
        currentFunction.addBlock(condBlock);
        if (node.condition != null) {
            node.condition.accept(this);
            condBlock.exitInstruction = new BranchIns(getValue(node.condition), bodyBlock, nextBlock);
        } else {
            condBlock.exitInstruction = new BranchIns(bodyBlock);
        }
        currentBlock = bodyBlock;
        currentFunction.addBlock(bodyBlock);
        node.stmts.accept(this);
        bodyBlock.exitInstruction = new BranchIns(stepBlock);
        currentBlock = stepBlock;
        currentFunction.addBlock(stepBlock);
        if (node.step != null) {
            node.step.accept(this);
        }
        stepBlock.exitInstruction = new BranchIns(condBlock);
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
        currentFunction.retNum++;
        if (node.expr != null) {
            node.expr.accept(this);
            currentBlock.exitInstruction = new ReturnIns(getValue(node.expr));
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
            program.stringLiterals.add(str);
            node.irVal = str;
        }
    }

    @Override
    public void visit(VarExprNode node) {
        if (!node.isFunction) { //如果这个varExpr是函数，那么irFuncName已经在type checker中处理好了
            if (!node.isMember) {
                if (node.irVarName.startsWith("@")) {
                    node.irVal = program.globalVars.get(node.irVarName);
                } else {
                    node.irVal = currentFunction.localVars.get(node.irVarName);
                }
            } else { //是this的一个数据成员,建立一个局部变量，存入指向该元素的指针
                node.irVal = currentFunction.localVars.get("%this." + node.varName);
                if (node.irVal == null) { //此成员第一次出现，将它加入localVar;
                    LocalVar var = new LocalVar("%this." + node.varName, toIRType(node.type));
                    LoadIns loadIns = new LoadIns(currentFunction.localVars.get("%this"));
                    int idx2 = currentClass.getMemberNum(node.varName);
                    GetElementPtrIns getIns = new GetElementPtrIns(var.type, loadIns.value, 0, idx2, var.name); //把指向这个成员的指针存入var
                    currentBlock.addIns(loadIns);
                    currentBlock.addIns(getIns);
                    currentFunction.addLocalVar(var);
                    node.irVal = var;
                }
            }
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        ++currentFunction.arithNum;
        node.lhs.accept(this);
        if (node.op.equals("&&") || !node.op.equals("||")) { //circuit
            RegVar result = new RegVar(irPtrType, "shortCut_result." + currentFunction.shortCutNum);
            currentBlock.addIns(new AllocaIns(result, irBoolType));
            ++currentFunction.shortCutNum;
            BasicBlock nextBlock = new BasicBlock("shortCut_next." + currentFunction.shortCutNum);
            BasicBlock rhsBlock = new BasicBlock("shortCut_rhs." + currentFunction.shortCutNum);
            BasicBlock trueBlock = new BasicBlock("shortCut_true." + currentFunction.shortCutNum);
            BasicBlock falseBlock = new BasicBlock("shortCut_false." + currentFunction.shortCutNum);
            BasicBlock tmpBlock = currentBlock;
            nextBlock.exitInstruction = tmpBlock.exitInstruction;
            if (node.op.equals("&&")) {
                tmpBlock.exitInstruction = new BranchIns(getValue(node.lhs), rhsBlock, falseBlock);
            } else {
                tmpBlock.exitInstruction = new BranchIns(getValue(node.lhs), trueBlock, rhsBlock);
            }
            currentFunction.addBlock(rhsBlock);
            currentBlock = rhsBlock;
            node.rhs.accept(this);
            rhsBlock.exitInstruction = new BranchIns(getValue(node.rhs), trueBlock, falseBlock);
            currentFunction.addBlock(trueBlock);
            currentBlock = trueBlock;
            currentBlock.addIns(new StoreIns(new BoolLiteral(true), result.name));
            currentBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(falseBlock);
            currentBlock = falseBlock;
            currentBlock.addIns(new StoreIns(new BoolLiteral(false), result.name));
            currentBlock.exitInstruction = new BranchIns(nextBlock);
            currentFunction.addBlock(nextBlock);
            currentBlock = nextBlock;
            node.irVal = result;
        } else {
            node.rhs.accept(this);
            if (node.lhs.type.equals(stringType) || node.rhs.type.equals(stringType)) {
                String funcName;
                int callNum;
                switch (node.op) {
                    case "+":
                        funcName = "@string_add";
                        break;
                    case "==":
                        funcName = "@string_eq";
                        break;
                    case "!=":
                        funcName = "@string_ne";
                        break;
                    case ">":
                        funcName = "@string_gt";
                        break;
                    case "<":
                        funcName = "@string_lt";
                        break;
                    case "<=":
                        funcName = "@string_le";
                        break;
                    default:
                        funcName = "@string_ge";
                        break;
                }
                callNum = program.getBuildInCallTime(funcName);
                node.irVal = new RegVar(irBoolType, funcName + "_result." + callNum);
                currentBlock.addIns(new CallIns(funcName, node.irVal, getValue(node.lhs), getValue(node.rhs)));
            } else {
                if (node.op.equals("*") || node.op.equals("/") || node.op.equals("%") || node.op.equals("+") || node.op.equals("-") || node.op.equals("<<") || node.op.equals(">>") || node.op.equals("&") || node.op.equals("^") || node.op.equals("|")) {
                    node.irVal = new RegVar(irIntType, "arith_result." + currentFunction.arithNum);
                    switch (node.op) {
                        case "+":
                            currentBlock.addIns(new Add(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "-":
                            currentBlock.addIns(new Sub(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "*":
                            currentBlock.addIns(new Mul(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "/":
                            currentBlock.addIns(new Sdiv(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "%":
                            currentBlock.addIns(new Srem(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "<<":
                            currentBlock.addIns(new Shl(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case ">>":
                            currentBlock.addIns(new Ashr(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "&":
                            currentBlock.addIns(new And(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "|":
                            currentBlock.addIns(new Or(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "^":
                            currentBlock.addIns(new Xor(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                    }
                } else {
                    node.irVal = new RegVar(irBoolType, "arith_result." + currentFunction.arithNum);
                    switch (node.op) {
                        case "==":
                            currentBlock.addIns(new Eq(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "!=":
                            currentBlock.addIns(new Ne(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case ">":
                            currentBlock.addIns(new Sgt(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "<":
                            currentBlock.addIns(new Slt(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case ">=":
                            currentBlock.addIns(new Sge(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                        case "<=":
                            currentBlock.addIns(new Sle(getValue(node.lhs), getValue(node.rhs), node.irVal));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void visit(UnaryExprNode node) {
        ++currentFunction.arithNum;
        node.exprNode.accept(this);
        RegVar result = new RegVar(irIntType, "tmp_result." + currentFunction.arithNum);
        switch (node.op) {
            case "++":
                node.irVal = getValue(node.exprNode);
                currentBlock.addIns(new Add(node.irVal, new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal.getName()));
                break;
            case "--":
                node.irVal = getValue(node.exprNode);
                currentBlock.addIns(new Sub(node.irVal, new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal.getName()));
                break;
            case "-":
                currentBlock.addIns(new Sub(new IntLiteral(0), getValue(node.exprNode), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal.getName()));
                node.irVal = result;
                break;
            case "+":
                node.irVal = getValue(node.exprNode);
                break;
            case "~":
                currentBlock.addIns(new Xor(new IntLiteral(-1), getValue(node.exprNode), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal.getName()));
                node.irVal = result;
                break;
            case "!":
                currentBlock.addIns(new Xor(new BoolLiteral(true), getValue(node.exprNode), result));
                currentBlock.addIns(new StoreIns(result, node.exprNode.irVal.getName()));
                node.irVal = result;
                break;
        }
    }

    @Override
    public void visit(PreOpExprNode node) {
        ++currentFunction.arithNum;
        node.expr.accept(this);
        RegVar result = new RegVar(irIntType, "tmp_result." + currentFunction.arithNum);
        switch (node.op) {
            case "++":
                currentBlock.addIns(new Add(getValue(node.expr), new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.expr.irVal.getName()));
                node.irVal = node.expr.irVal;
                break;
            case "--":
                currentBlock.addIns(new Sub(getValue(node.expr), new IntLiteral(1), result));
                currentBlock.addIns(new StoreIns(result, node.expr.irVal.getName()));
                node.irVal = node.expr.irVal;
                break;
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.rhs.accept(this);
        node.lhs.accept(this);
        currentBlock.addIns(new StoreIns(getValue(node.rhs), node.lhs.irVal.getName()));
        node.irVal = node.rhs.irVal;
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.func.accept(this);
        CallIns callIns;
        int buildInCallTime = program.getBuildInCallTime(node.irFuncName);
        if (buildInCallTime != -1) {  //build-in function
            node.irVal = new RegVar(toIRType(node.function.returnType), node.irFuncName + "_result." + buildInCallTime);
            callIns = new CallIns(node.irFuncName, node.irVal);
            if (node.func.function.equals(size)) { //todo 此处的array是否需要什么特殊操作？？？
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
            IRFunction function = program.functions.get(node.irFuncName);
            function.callNum++;
            node.irVal = new RegVar(toIRType(node.function.returnType), node.irFuncName + "_result." + function.callNum);
            callIns = new CallIns(node.irFuncName, node.irVal);
            if (function.isClassMethod) {
                if (node.func instanceof MemberExprNode memberExprNode) {
                    callIns.args.add(getValue(memberExprNode.obj));
                } else {
                    Entity this_val = getValue(currentFunction.localVars.get("%this"), irPtrType);
                    callIns.args.add(this_val);
                }
            }
            for (var arg : node.args) {
                arg.accept(this);
                callIns.args.add(getValue(arg));
            }
        }
        currentBlock.addIns(callIns);
        //todo 在打印函数的时候，如果是void，那么不需要打印result
    }

    @Override
    public void visit(ArrayExprNode node) {
        ++currentFunction.arrayNum;
        node.array.accept(this);
        node.index.accept(this);
        Entity startPtr = getValue(node.array);
        RegVar value = new RegVar(irPtrType, "%array_ptr." + currentFunction.arrayNum);
        currentBlock.addIns(new GetElementPtrIns(toIRType(node.type), startPtr, getValue(node.index), new IntLiteral(0), value.name));
        if (node.type.isArray) { //还需要再次load,才能得到指向数组开头位置的指针
            ++currentFunction.arrayNum;
            LoadIns load = new LoadIns(value, irPtrType, "%array_ptr." + currentFunction.arrayNum);
            currentBlock.addIns(load);
            node.irVal = load.value;
        } else {
            node.irVal = value;
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        if (node.function == null) { //如果node.function != null,已经在typecheck中处理好了irFuncName
            Entity ptr = getValue(node.obj);
            IRClassType classType = program.classes.get(node.irClassName);
            int num = classType.getMemberNum(node.memberName);
            String name = node.irClassName + "." + node.memberName + classType.getGEPtime(node.memberName);
            GetElementPtrIns getIns = new GetElementPtrIns(classType, ptr, new IntLiteral(0), new IntLiteral(num), name);
            node.irVal = getIns.value;
        }
    }

    @Override
    public void visit(NewExprNode node) {
        currentFunction.newNum++;
        IRType baseType = toIRType(node.typeName, program);
        if (!node.isArray) {
            IRClassType classType = (IRClassType) baseType;
            node.irVal = new RegVar(irPtrType, "%new_class." + currentFunction.newNum);
            int classSize = (classType.bitSize);
            currentBlock.addIns(new CallIns("@malloc", node.irVal, new IntLiteral(classSize)));
            if (classType.constructor != null) {
                currentBlock.addIns(new CallIns("@" + node.typeName.typeName + "." + node.typeName.typeName, null, node.irVal));
            }
        } else {
            if (node.exprs.isEmpty()) {
                node.irVal = new NullLiteral();
            }
            if (node.arrDim == 1) {
                node.irVal = new RegVar(irPtrType, "%new_array." + currentFunction.newNum);
                if (baseType.equals(irIntType)) {
                    currentBlock.addIns(new CallIns("@_newBoolArray", node.irVal, getValue(node.exprs.get(0))));
                } else if (baseType.equals(irBoolType)) {
                    currentBlock.addIns(new CallIns("@_newIntArray", node.irVal, getValue(node.exprs.get(0))));
                } else { //string&class
                    currentBlock.addIns(new CallIns("@_newPtrArray", node.irVal, getValue(node.exprs.get(0))));
                }
            }
        }

    }

    @Override
    public void visit(TernaryExprNode node) {
        ++currentFunction.ternaryNum;
        node.lExpr.accept(this);
        BasicBlock nextBlock = new BasicBlock("end_ternary." + currentFunction.ternaryNum);
        BasicBlock Block1 = new BasicBlock("first_ternary." + currentFunction);
        BasicBlock Block2 = new BasicBlock("second_ternary." + currentFunction);
        BasicBlock tmpBlock = currentBlock;
        nextBlock.exitInstruction = tmpBlock.exitInstruction;
        tmpBlock.exitInstruction = new BranchIns(getValue(node.lExpr), Block1, Block2);
        currentBlock = Block1;
        currentFunction.addBlock(Block1);
        node.mExpr.accept(this);
        Entity value1 = getValue(node.mExpr);
        currentBlock.exitInstruction = new BranchIns(nextBlock);
        currentBlock = Block2;
        currentFunction.addBlock(Block2);
        node.rExpr.accept(this);
        Entity value2 = getValue(node.rExpr);
        currentBlock.exitInstruction = new BranchIns(nextBlock);
        currentBlock = nextBlock;
        node.irVal = new RegVar(toIRType(node.mExpr.type), "%value_ternary." + currentFunction.ternaryNum);
        PhiIns phi = new PhiIns(node.irVal);
        phi.addPair(value1, Block1);
        phi.addPair(value2, Block2);
        currentBlock.addIns(phi);
    }
}
