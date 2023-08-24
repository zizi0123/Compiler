package semantic;

import ast.*;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import ast.util.error.SemanticError;
import ast.util.scope.GlobalScope;
import ast.util.scope.Scope;
import ast.util.type.ASTType;

import java.util.Objects;

public class TypeChecker implements ASTVisitor {
    GlobalScope globalScope;
    Scope currentScope;

    public TypeChecker(GlobalScope globalScope) {
        this.globalScope = globalScope;
        this.currentScope = new Scope(null);
        this.currentScope.vars = globalScope.vars;
    }

    @Override
    public void visit(ProgramNode node) {
        FuncDefNode main = globalScope.getFunc("main");
        if (main == null) {
            throw new SemanticError("missing main" + "function", node.pos);
        }
        if (!main.functionParameterList.parameters.isEmpty()) {
            throw new SemanticError("main function parameter error", node.pos);
        }
        if (!main.returnType.equals(intType)) {
            throw new SemanticError("main function return type error", node.pos);
        }
        for (var defNode : node.defNodes) {
            defNode.accept(this);
        }

    }

    @Override
    public void visit(FuncDefNode node) {
        globalScope.checkType(node.returnType, node.pos);
        currentScope = new Scope(currentScope);
        currentScope.isInFunction = true;
        if (!node.functionParameterList.isEmpty()) {
            node.functionParameterList.accept(this);
        }
        if (!node.blockStmt.isEmpty()) {
            node.blockStmt.accept(this);
        }
        //检查一下，在遍历了每一个语句以后，函数是否顺利返回
        if (node.returnType.equals(voidType)) {
            if (currentScope.hasReturned && !currentScope.returnType.equals(voidType)) {
                throw new RuntimeException("function " + node.funcName + " did not return a " + node.returnType + " value");
            }
        } else {
            if (Objects.equals(node.funcName, "main")) {
                if (currentScope.hasReturned && !currentScope.returnType.equals(intType) && !currentScope.returnType.equals(voidType)) {
                    throw new RuntimeException("function main return " + currentScope.returnType.typeName.typeName);
                }
            } else {
                if (!currentScope.hasReturned || !currentScope.returnType.equalsNull(node.returnType, currentScope)) {
                    throw new RuntimeException("function " + node.funcName + " did not return a " + node.returnType + " value");
                }
            }
        }
    }

    @Override
    public void visit(ClassDefNode node) {
        currentScope = new Scope(currentScope);
        currentScope.isInClass = true;
        currentScope.classDefNode = node;
        //members
        for (var member : node.members.values()) {
            if (member.expr != null) {
                throw new RuntimeException("class " + node.className + " member " + member.name + " has default initialization expression");
            }
            member.accept(this);
        }
        //constructor
        if (node.functions.containsKey(node.className)) {
            throw new RuntimeException("constructor error");
        }
        if (node.constructor != null) {
            if (!Objects.equals(node.constructor.className, node.className)) {
                throw new RuntimeException("constructor error");
            }
            node.constructor.accept(this);
        }
        //functions
        for (var func : node.functions.values()) {
            func.accept(this);
            func.irFuncName = "@" + node.className + "." + func.funcName;
        }
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(VarDefNode node) {
        for (var singleVar : node.vars) {
            singleVar.accept(this);
        }
    }

    @Override
    public void visit(SingleVarDefNode node) {
        globalScope.checkType(node.type, node.pos);
        if (globalScope.functions.containsKey(node.name)) {
            throw new SemanticError("duplicate variable name and function name: " + node.name, node.pos);
        }
        if (currentScope.containInThisScope(node.name)) {
            throw new SemanticError("duplicate variable name: " + node.name, node.pos);
        }
        if (node.expr != null) {
            node.expr.accept(this);
            if (node.expr.type.equals(nullType)) {
                if (!node.type.isReferenceType()) {
                    throw new SemanticError("assign null to a non reference type", node.pos);
                }
            } else if (!node.type.equals(node.expr.type, currentScope)) {
                throw new SemanticError("different type in two sides of an assign expression", node.pos);
            }
        }
        currentScope.addVar(node.name, node.type);
        if (currentScope.parentScope == null) {
            node.irVarName = "@" + node.name;
        } else {
            node.irVarName = "%" + node.name + currentScope.scopeNum;
        }
    }

    @Override
    public void visit(FuncParameterListNode node) {
        for (var para : node.parameters) {
            globalScope.checkType(para.type, node.pos);
            para.irVarName = "%" + para.varName + "." + currentScope.scopeNum;
            currentScope.addVar(para.varName, para.type);
        }
    }

    @Override
    public void visit(ClassConstructorNode node) {
        currentScope = new Scope(currentScope);
        currentScope.isInFunction = true;
        node.blockStmt.accept(this);
        if (currentScope.hasReturned && !currentScope.returnType.equals(voidType)) {
            throw new SemanticError("cannot return any value in a constructor", node.pos);
        }
        currentScope = currentScope.parentScope;
        node.irFuncName = "@" + node.className + "." + node.className;
    }

    @Override
    public void visit(BlockStmtNode node) {
        currentScope = new Scope(currentScope);
        for (var stmt : node.stmts) {
            stmt.accept(this);
        }
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(IfStmtNode node) {
        node.condition.accept(this);
        if (!node.condition.type.equals(boolType)) {
            throw new SemanticError("expression type error: condition expression type is not bool", node.condition.pos);
        }
        currentScope = new Scope(currentScope);
        node.trueStmts.accept(this);
        currentScope = currentScope.parentScope;
        if (node.falseStmts != null) {
            currentScope = new Scope(currentScope);
            node.falseStmts.accept(this);
            currentScope = currentScope.parentScope;
        }
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.condition.accept(this);
        if (!node.condition.type.equals(boolType)) {
            throw new SemanticError("expression type error: condition expression type is not bool", node.condition.pos);
        }
        currentScope = new Scope(currentScope);
        currentScope.isInLoop = true;
        node.stmts.accept(this);
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(ForStmtNode node) {
        currentScope = new Scope(currentScope);
        currentScope.isInLoop = true;
        if (node.initExpr != null) {
            node.initExpr.accept(this);
        }
        if (node.initVarDef != null) {
            node.initVarDef.accept(this);
        }
        if (node.condition != null) {
            node.condition.accept(this);
            if (!node.condition.type.equals(boolType)) {
                throw new SemanticError("expression type error: condition expression type is not bool", node.condition.pos);
            }
        }
        if (node.step != null) {
            node.step.accept(this);
        }
        node.stmts.accept(this);
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if (!currentScope.isInLoop) {
            throw new SemanticError("stmt continue in not a loop", node.pos);
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        if (!currentScope.isInLoop) {
            throw new SemanticError("stmt break in not a loop", node.pos);
        }
    }

    //给每一个外层（包括本层）的scope的returnType赋值
    @Override
    public void visit(ReturnStmtNode node) {
        if (node.expr == null) {
            node.returnType = voidType;
        } else {
            node.expr.accept(this);
            node.returnType = node.expr.type;
        }
        for (Scope scope = currentScope; scope.isInFunction && scope.parentScope != null; scope = scope.parentScope) {
            if (!scope.hasReturned) {
                scope.returnType = node.returnType;
                scope.hasReturned = true;
            } else if (!scope.returnType.equalsNull(node.returnType, currentScope)) {
                throw new SemanticError("different return types in a function", node.pos);
            }
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        if (node.expr != null) node.expr.accept(this);
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.varDefNode.accept(this);
    }

    @Override
    public void visit(ParenExprNode node) {
        node.exprNode.accept(this);
        node.type = node.exprNode.type;
    }

    @Override
    public void visit(ConstExprNode node) {
        node.type = node.value.GetType();
        if (node.type.equals(thisType, currentScope) && !currentScope.isInClass) {
            throw new SemanticError("this pointer outside a class", node.pos);
        }
    }

    @Override
    public void visit(VarExprNode node) {
        if (node.isFunction) {
            if (currentScope.isInClass && currentScope.classDefNode.functions.containsKey(node.varName)) {
                node.type = functionType;
                node.function = currentScope.classDefNode.functions.get(node.varName);
                node.irVarName = "@" + currentScope.classDefNode.className + "." + node.function.funcName;
                node.irFuncName = node.irVarName;
            } else if (globalScope.getFunc(node.varName) != null) {
                node.type = functionType;
                node.function = globalScope.getFunc(node.varName);
                node.irVarName = "@" + node.function.funcName;
                node.irFuncName = node.irVarName;
            } else {
                throw new SemanticError("undefined function " + node.varName, node.pos);
            }
        } else {
            if (currentScope.getTypeInAllScope(node.varName) != null) {
                node.type = currentScope.getTypeInAllScope(node.varName);
                node.irVarName = currentScope.getIRNameInAllScope(node.varName);
                if (currentScope.isInClass && currentScope.classDefNode.members.containsKey(node.varName)) {
                    node.isMember = true;
                }
            } else {
                throw new SemanticError("undefined variable " + node.varName, node.pos);
            }
        }
    }

    @Override
    public void visit(NewExprNode node) {
        if (!node.isArray) {
            if (globalScope.getClass(node.typeName.typeName) == null) {
                throw new SemanticError("class " + node.typeName.typeName + " is not defined", node.pos);
            }
        } else {
            globalScope.checkTypeName(node.typeName, node.pos);
            for (var expr : node.exprs) {
                expr.accept(this);
                if (!expr.type.equals(intType)) {
                    throw new SemanticError("expression type error: size expression type is not int", expr.pos);
                }
            }
        }
        node.type = new ASTType(node.typeName, false, node.isArray, node.arrDim);
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (node.lhs.type.equals(voidType) || node.rhs.type.equals(voidType)) {
            throw new SemanticError("void type in binary expression", node.pos);
        }
        if (node.lhs.type.equals(nullType) || node.rhs.type.equals(nullType)) {
            if (!Objects.equals(node.op, "==") && !Objects.equals(node.op, "!=")) {
                throw new SemanticError("invalid null type in binary expression", node.lhs.pos);
            } else if ((!node.lhs.type.equals(nullType) && !node.lhs.type.isReferenceType()) || (!node.rhs.type.equals(nullType) && !node.rhs.type.isReferenceType())) {
                throw new SemanticError("a null type and a non reference type in comparison expression", node.pos);
            } else {
                node.type = boolType;
            }
        } else {
            if (!node.lhs.type.equals(node.rhs.type, currentScope)) {
                throw new SemanticError("lhs type different from rhs type in a binary expression", node.pos);
            }
            if (node.lhs.type.equals(stringType)) {
                if (Objects.equals(node.op, "+")) {
                    node.type = stringType;
                } else if (Objects.equals(node.op, "==") || Objects.equals(node.op, "!=") || Objects.equals(node.op, "<") || Objects.equals(node.op, ">") || Objects.equals(node.op, "<=") || Objects.equals(node.op, ">=")) {
                    node.type = boolType;
                } else {
                    throw new SemanticError("invalid operation with two string", node.pos);
                }
            } else if (node.lhs.type.equals(intType)) {
                if (Objects.equals(node.op, "||") || Objects.equals(node.op, "&&")) {
                    throw new SemanticError("invalid operation with two int", node.pos);
                }
                if (Objects.equals(node.op, "<=") || Objects.equals(node.op, ">=") || Objects.equals(node.op, "<") || Objects.equals(node.op, ">") || Objects.equals(node.op, "==") || Objects.equals(node.op, "!=")) {
                    node.type = boolType;
                } else {
                    node.type = intType;
                }
            } else if (node.lhs.type.equals(boolType)) {
                if (Objects.equals(node.op, "&&") || Objects.equals(node.op, "||") || Objects.equals(node.op, "==") || Objects.equals(node.op, "!=")) {
                    node.type = boolType;
                } else {
                    throw new SemanticError("invalid operation with two bool", node.pos);
                }
            } else if (node.lhs.type.isArray || node.lhs.type.typeName.isClass) {
                if (Objects.equals(node.op, "==") || Objects.equals(node.op, "!=")) {
                    node.type = boolType;
                } else {
                    throw new SemanticError("invalid operation with two array", node.pos);
                }
            } else {
                throw new SemanticError("invalid type in a binary expression", node.pos);
            }
        }
    }

    @Override
    public void visit(UnaryExprNode node) {
        node.exprNode.accept(this);
        if (Objects.equals(node.op, "++") || Objects.equals(node.op, "--")) {
            if (!node.exprNode.type.equals(intType) || !node.exprNode.isLeftValue()) {
                throw new SemanticError("invalid type (int and left value expected) in unary expression", node.pos);
            }
            node.type = intType;
        } else if (Objects.equals(node.op, "+") || Objects.equals(node.op, "-") || Objects.equals(node.op, "~")) {
            if (!node.exprNode.type.equals(intType)) {
                throw new SemanticError("invalid type (int expected) in unary expression", node.pos);
            }
            node.type = intType;
        } else {
            if (!node.exprNode.type.equals(boolType)) {
                throw new SemanticError("invalid type (bool expected) in unary expression", node.exprNode.pos);
            }
            node.type = boolType;
        }
    }

    @Override
    public void visit(PreOpExprNode node) {
        node.expr.accept(this);
        if (!node.expr.type.equals(intType) || !node.expr.isLeftValue()) {
            throw new SemanticError("invalid type (int and left value expected) in unary expression", node.expr.pos);
        }
        node.type = intType;
    }

    @Override
    public void visit(AssignExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (!node.lhs.isLeftValue()) {
            throw new SemanticError("lhs of an assign expression is not a left value", node.lhs.pos);
        }
        if (node.lhs.type.equals(voidType) || node.rhs.type.equals(voidType)) {
            throw new SemanticError("void type in assign expression", node.pos);
        }
        if (node.lhs.type.equals(nullType)) {
            throw new SemanticError("lhs of an assign expression is null", node.lhs.pos);
        }
        if (node.rhs.type.equals(nullType)) {
            if (!node.lhs.type.isReferenceType()) {
                throw new SemanticError("assign null to a non reference type", node.pos);
            }
        } else if (!node.lhs.type.equals(node.rhs.type, currentScope)) {
            throw new SemanticError("different type in two sides of an assign expression", node.pos);
        }
        node.type = node.rhs.type;
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.func.accept(this);
        if (!node.func.type.isFunction) {
            throw new SemanticError("invalid function expression ", node.func.pos);
        }
        FuncDefNode func = node.func.function;
        for (var arg : node.args) {
            arg.accept(this);
        }
        if (func.functionParameterList.parameters.size() != node.args.size()) {
            throw new SemanticError("arguments mismatching in a func" + func.funcName + "call expression", func.pos);
        }
        for (int i = 0; i < node.args.size(); ++i) {
            if (!func.functionParameterList.parameters.get(i).type.equalsNull(node.args.get(i).type, currentScope)) {
                throw new SemanticError("arguments type mismatching in a func " + func.funcName + "call expression", node.args.get(i).pos);
            }
        }
        node.type = func.returnType;
    }

    @Override
    public void visit(ArrayExprNode node) {
        node.array.accept(this);
        node.index.accept(this);
        if (!node.array.type.isArray) {
            throw new SemanticError("invalid array expression", node.array.pos);
        }
        if (!node.index.type.equals(intType)) {
            throw new SemanticError("invalid index expression", node.index.pos);
        }
        if (node.array.type.arrayDim == 1) {
            node.type = new ASTType(node.array.type.typeName, false, false, 0);
        } else {
            node.type = new ASTType(node.array.type.typeName, false, true, node.array.type.arrayDim - 1);
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        if (node.obj.type.equals(thisType)) {
            node.irClassName = "%class."+currentScope.classDefNode.className;
            if (currentScope.classDefNode.members.containsKey(node.memberName)) {
                node.type = currentScope.classDefNode.members.get(node.memberName).type;
            } else if (currentScope.classDefNode.functions.containsKey(node.memberName)) {
                node.function = currentScope.classDefNode.functions.get(node.memberName);
                node.type = functionType;
                node.irFuncName = "@" + currentScope.classDefNode.className + "." + node.function.funcName;
            } else {
                throw new SemanticError("class member " + node.memberName + "is not defined", node.pos);
            }
            return;
        }
        if (node.obj.type.typeName.isClass && !node.obj.type.isArray) {
            if (globalScope.getClass(node.obj.type.typeName.typeName) == null) {
                throw new SemanticError("class " + node.obj.type.typeName.typeName + "is not defined", node.obj.pos);
            }
            ClassDefNode classDefNode = globalScope.getClass(node.obj.type.typeName.typeName);
            node.irClassName = "%class."+classDefNode.className;
            if (classDefNode.members.containsKey(node.memberName)) {
                node.type = classDefNode.members.get(node.memberName).type;
            } else if (classDefNode.functions.containsKey(node.memberName)) {
                node.function = classDefNode.functions.get(node.memberName);
                node.type = functionType;
                node.irFuncName = "@" + classDefNode.className + "." + node.function.funcName;
            } else {
                throw new SemanticError("class " + node.obj.type.typeName.typeName + " does not have member " + node.memberName, node.pos);
            }
            return;
        }
        if (node.obj.type.equals(stringType)) {
            switch (node.memberName) {
                case "length" -> {
                    node.function = GlobalScope.length;
                    node.type = functionType;
                    node.irFuncName = "@string_" + node.function.funcName;
                }
                case "substring" -> {
                    node.function = GlobalScope.substring;
                    node.type = functionType;
                    node.irFuncName = "@string_" + node.function.funcName;
                }
                case "parseInt" -> {
                    node.function = GlobalScope.parseInt;
                    node.type = functionType;
                    node.irFuncName = "@string_" + node.function.funcName;
                }
                case "ord" -> {
                    node.function = GlobalScope.ord;
                    node.type = functionType;
                    node.irFuncName = "@string_" + node.function.funcName;
                }
                default ->
                        throw new SemanticError("function " + node.memberName + "is not defined for string", node.pos);
            }
            return;
        }
        if (node.obj.type.isArray) {
            if (node.memberName.equals("size")) {
                node.function = GlobalScope.size;
                node.type = functionType;
                node.irFuncName = "@array_" + node.function.funcName;
            }
            return;
        }
        throw new SemanticError("invalid member expression", node.pos);
    }

    @Override
    public void visit(TernaryExprNode node) {
        node.lExpr.accept(this);
        node.mExpr.accept(this);
        node.rExpr.accept(this);
        if (!node.lExpr.type.equals(boolType)) {
            throw new SemanticError("type of the lExpr of a ternaryExpr is not bool", node.lExpr.pos);
        }
        if (!node.mExpr.type.equalsNull(node.rExpr.type, currentScope)) {
            throw new SemanticError("type of the mExpr and lExpr of a ternaryExpr is different", node.pos);
        }
        node.type = node.mExpr.type;
    }
}
