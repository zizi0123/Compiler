package semantic;

import ast.*;
import ast.def.ClassDefNode;
import ast.def.FuncDefNode;
import ast.def.VarDefNode;
import ast.expr.*;
import ast.stmt.*;
import util.error.SemanticError;
import util.scope.GlobalScope;
import util.scope.Scope;
import util.type.Type;

import java.util.Objects;

public class SemanticChecker implements ASTVisitor {
    GlobalScope globalScope;
    Scope currentScope;

    public SemanticChecker(GlobalScope globalScope) {
        this.globalScope = globalScope;
        this.currentScope.members = globalScope.members;
    }

    @Override
    public void visit(ProgramNode node) {
        FuncDefNode main = globalScope.getFunc("main");
        if (main == null) {
            throw new SemanticError("missing" + "main" + "function");
        }
        if (!main.functionParameterList.parameters.isEmpty()) {
            throw new SemanticError("main" + "function parameter error");
        }
        if (main.returnType != intType) {
            throw new SemanticError("main" + "function return type error");
        }
        if (main.blockStmt.isEmpty()) {
            throw new SemanticError("function" + "main" + "missing function body");
        }
        for (var defNode : node.defNodes) {
            defNode.accept(this);
        }

    }

    @Override
    public void visit(FuncDefNode node) {
        globalScope.checkType(node.returnType);
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
            if (currentScope.hasReturned && currentScope.returnType != voidType) {
                throw new RuntimeException("function " + node.funcName + " did not return a " + node.returnType + " value");
            }
        } else {
            if (Objects.equals(node.funcName, "main") && currentScope.hasReturned && currentScope.returnType != intType && currentScope.returnType != voidType) {
                throw new RuntimeException("function main return " + currentScope.returnType.typeName.typeName);
            } else {
                if (!currentScope.hasReturned || currentScope.returnType != node.returnType) {
                    throw new RuntimeException("function " + node.funcName + " did not return a " + node.returnType + " value");
                }
            }
        }
        currentScope = currentScope.parentScope;
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
        globalScope.checkType(node.type);
        if (globalScope.functions.containsKey(node.name)) {
            throw new SemanticError("duplicate variable name and function name: " + node.name);
        }
        if (currentScope.containInThisScope(node.name)) {
            throw new SemanticError("duplicate variable name: " + node.name);
        }
        if (node.expr != null) {
            node.expr.accept(this);
        }
        currentScope.addMember(node.name, node.type);
    }

    @Override
    public void visit(FuncParameterListNode node) {
        for (var para : node.parameters) {
            globalScope.checkType(para.type);
            currentScope.addMember(para.varName, para.type);
        }
    }

    @Override
    public void visit(ClassConstructorNode node) {
        currentScope = new Scope(currentScope);
        currentScope.isInFunction = true;
        node.blockStmt.accept(this);
        currentScope = currentScope.parentScope;
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
        if (node.condition.type != boolType) {
            throw new SemanticError("expression type error: condition expression type is not bool");
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
        if (node.condition.type != boolType) {
            throw new SemanticError("expression type error: condition expression type is not bool");
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
            if (node.condition.type != boolType) {
                throw new SemanticError("expression type error: condition expression type is not bool");
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
            throw new SemanticError("stmt continue in not a loop");
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        if (!currentScope.isInLoop) {
            throw new SemanticError("stmt break in not a loop");
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
            } else if (scope.returnType != node.returnType) {
                throw new SemanticError("different return types in a function");
            }
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        if (node.expr != null) node.expr.accept(this);
    }

    @Override
    public void visit(ParenExprNode node) {
        node.exprNode.accept(this);
    }

    @Override
    public void visit(ConstExprNode node) {
        node.type = node.value.GetType();
        if (node.type.equals(thisType) && !currentScope.isInClass) {
            throw new SemanticError("this pointer outside a class");
        }
    }

    @Override
    public void visit(VarExprNode node) {
        if (currentScope.getTypeInAllScope(node.varName) != null) {
            node.type = currentScope.getTypeInAllScope(node.varName);
        } else if (globalScope.getFunc(node.varName) != null) {
            node.type = new Type("function");
            node.function = globalScope.getFunc(node.varName);
        } else {
            throw new SemanticError("undefined variable " + node.varName);
        }
    }

    @Override
    public void visit(NewExprNode node) {
        if (!node.isArray) {
            if (globalScope.getClass(node.typeName.typeName) == null) {
                throw new SemanticError("class " + node.typeName.typeName + " is not defined");
            }
        } else {
            globalScope.checkTypeName(node.typeName);
            for (var expr : node.exprs) {
                expr.accept(this);
                if (expr.type != intType) {
                    throw new SemanticError("expression type error: size expression type is not int");
                }
            }
        }
        node.type = new Type(node.typeName, false, node.isArray, node.arrDim);
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (node.lhs.type.equals(voidType) || node.rhs.type.equals(voidType)) {
            throw new SemanticError("void type in binary expression");
        }
        if (node.lhs.type.equals(nullType) || node.rhs.type.equals(nullType)) {
            if (!Objects.equals(node.op, "==") && !Objects.equals(node.op, "!=")) {
                throw new SemanticError("invalid null type in binary expression");
            } else if ((node.lhs.type != nullType && node.lhs.type.isNonReferenceType()) || (node.rhs.type != nullType && node.rhs.type.isNonReferenceType())) {
                throw new SemanticError("a null type and a non reference type in comparison expression");
            } else {
                node.type = boolType;
            }
        } else {
            if (node.lhs.type != node.rhs.type) {
                throw new SemanticError("lhs type different from rhs type in a binary expression");
            }
            if (node.lhs.type.equals(stringType)) {
                if (Objects.equals(node.op, "+")) {
                    node.type = stringType;
                } else if (Objects.equals(node.op, "==") || Objects.equals(node.op, "!=") || Objects.equals(node.op, "<") || Objects.equals(node.op, ">") || Objects.equals(node.op, "<=") || Objects.equals(node.op, ">=")) {
                    node.type = boolType;
                } else {
                    throw new SemanticError("invalid operation with two string");
                }
            } else if (node.lhs.type.equals(intType)) {
                if (Objects.equals(node.op, "||") || Objects.equals(node.op, "&&")) {
                    throw new SemanticError("invalid operation with two int");
                }
                if (Objects.equals(node.op, "<=") || Objects.equals(node.op, ">=") || Objects.equals(node.op, "<") || Objects.equals(node.op, ">")) {
                    node.type = boolType;
                } else {
                    node.type = intType;
                }
            } else if (node.lhs.type.equals(boolType)) {
                if (Objects.equals(node.op, "&&") || Objects.equals(node.op, "||")) {
                    node.type = boolType;
                } else {
                    throw new SemanticError("invalid operation with two bool");
                }
            } else {
                throw new SemanticError("invalid type in a binary expression");
            }
        }
    }

    @Override
    public void visit(UnaryExprNode node) {
        node.exprNode.accept(this);
        if (Objects.equals(node.op, "++") || Objects.equals(node.op, "--")) {
            if (node.exprNode.type != intType || !node.exprNode.isLeftValue()) {
                throw new SemanticError("invalid type (int and left value expected) in unary expression");
            }
            node.type = intType;
        } else if (Objects.equals(node.op, "+") || Objects.equals(node.op, "-") || Objects.equals(node.op, "~")) {
            if (node.exprNode.type != intType) {
                throw new SemanticError("invalid type (int expected) in unary expression");
            }
            node.type = intType;
        } else {
            if (node.exprNode.type != boolType) {
                throw new SemanticError("invalid type (bool expected) in unary expression");
            }
            node.type = boolType;
        }
    }

    @Override
    public void visit(PreOpExprNode node) {
        node.expr.accept(this);
        if (node.expr.type != intType || !node.expr.isLeftValue()) {
            throw new SemanticError("invalid type (int and left value expected) in unary expression");
        }
        node.type = intType;
    }

    @Override
    public void visit(AssignExprNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (!node.lhs.isLeftValue()) {
            throw new SemanticError("lhs of an assign expression is not a left value");
        }
        if (node.lhs.type.equals(voidType) || node.rhs.type.equals(voidType)) {
            throw new SemanticError("void type in assign expression");
        }
        if (node.lhs.type.equals(nullType)) {
            throw new SemanticError("lhs of an assign expression is null");
        }
        if (node.rhs.type.equals(nullType) && node.lhs.type.isNonReferenceType()) {
            throw new SemanticError("assign null to a non reference type");
        }
        if (node.lhs.type != node.rhs.type) {
            throw new SemanticError("different type in two sides of an assign expression");
        }
        node.type = node.lhs.type;
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.func.accept(this);
        if (!node.func.type.isFunction) {
            throw new SemanticError("invalid function expression");
        }
        FuncDefNode func = node.func.function;
        for (var arg : node.args) {
            arg.accept(this);
        }
        if (func.functionParameterList.parameters.size() != node.args.size()) {
            throw new SemanticError("arguments mismatching in a func" + func.funcName + "call expression");
        }
        for (int i = 0; i < node.args.size(); ++i) {
            if (func.functionParameterList.parameters.get(i).type.equals(node.args.get(i).type)) {
                throw new SemanticError("arguments type mismatching in a func " + func.funcName + "call expression");
            }
        }
        node.type = func.returnType;
    }

    @Override
    public void visit(ArrayExprNode node) {
        node.array.accept(this);
        node.index.accept(this);
        if (!node.type.isArray) {
            throw new SemanticError("invalid array expression");
        }
        if (!node.index.type.equals(intType)) {
            throw new SemanticError("invalid index expression");
        }
        if (node.array.type.arrayDim == 1) {
            node.type = new Type(node.array.type.typeName, false, false, 0);
        } else {
            node.type = new Type(node.array.type.typeName, false, true, node.array.type.arrayDim - 1);
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        node.obj.accept(this);
        if (node.type.equals(thisType)) {
            if (currentScope.containInThisScope(node.memberName)) {
                node.type = currentScope.members.get(node.memberName);
            } else if (currentScope.classDefNode.functions.containsKey(node.memberName)) {
                node.function = currentScope.classDefNode.functions.get(node.memberName);
                node.type = new Type("function");
            } else {
                throw new SemanticError("class member " + node.memberName + "is not defined");
            }
            return;
        }
        if (node.obj.type.typeName.isClass) {
            if (globalScope.getClass(node.obj.type.typeName.typeName) == null) {
                throw new SemanticError("class " + node.obj.type.typeName.typeName + "is not defined");
            }
            ClassDefNode classDefNode = globalScope.getClass(node.obj.type.typeName.typeName);
            if (classDefNode.members.containsKey(node.memberName)) {
                node.type = classDefNode.members.get(node.memberName).type;
            } else if (classDefNode.functions.containsKey(node.memberName)) {
                node.function = classDefNode.functions.get(node.memberName);
                node.type = new Type("function");
            } else {
                throw new SemanticError("class " + node.obj.type.typeName.typeName + "does not have member " + node.memberName);
            }
            return;
        }
        if (node.obj.type.equals(stringType)) {
            switch (node.memberName) {
                case "length" -> {
                    node.function = GlobalScope.length;
                    node.type = new Type("function");
                }
                case "substring" -> {
                    node.function = GlobalScope.substring;
                    node.type = new Type("function");
                }
                case "parseInt" -> {
                    node.function = GlobalScope.parseInt;
                    node.type = new Type("function");
                }
                case "ord" -> {
                    node.function = GlobalScope.ord;
                    node.type = new Type("function");
                }
                default -> throw new SemanticError("function " + node.memberName + "is not defined for string");
            }
            return;
        }
        if (node.obj.type.isArray) {
            if (node.memberName.equals("size")) {
                node.function = GlobalScope.size;
                node.type = new Type("function");
            }
            return;
        }
        throw new SemanticError("invalid member expression");
    }

    @Override
    public void visit(TernaryExprNode node) {
        node.lExpr.accept(this);
        node.mExpr.accept(this);
        node.rExpr.accept(this);
        if(!node.lExpr.type.equals(boolType)){
            throw new SemanticError("type of the lExpr of a ternaryExpr is not bool");
        }
        if(!node.mExpr.type.equals(node.rExpr.type)){
            throw new SemanticError("type of the mExpr and lExpr of a ternaryExpr is different");
        }
        node.type = node.mExpr.type;
    }
}
